package service.contract;

import db.MaturityNoticeMapper;
import db.MyBatisSessionFactory;
import enums.DeliveryMethod;
import model.contract.MaturityNotice;
import org.apache.ibatis.session.SqlSession;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// 만기계약 관리 서비스 — 재계약 의사 처리, DB 저장 담당
public class MaturityContractService {

    public static MaturityNotice createMaturityNotice(DeliveryMethod deliveryMethod) {
        MaturityNotice maturityNotice = new MaturityNotice(deliveryMethod, LocalDateTime.now());
        maturityNotice.sendNotice();
        return maturityNotice;
    }

    // 재계약 의사 결과 처리
    public static String processRenewalIntention(String intention) {
        switch (intention) {
            case "1": return "재계약 의사 있음 — 재계약 절차를 안내합니다.";
            case "2": return "만기 처리 완료 — 계약상태: '만기종료'";
            case "3": return "회신 기한 초과 — 계약상태: '만기종료'";
            default:  return "[오류] 올바른 선택이 아닙니다.";
        }
    }

    public static String applyRenewalIntention(MaturityNotice maturityNotice, String intention) {
        if (maturityNotice != null) {
            maturityNotice.setCheckedAt(LocalDateTime.now());
            if ("1".equals(intention)) {
                maturityNotice.setRenewalIntention(true);
            } else if ("2".equals(intention)) {
                maturityNotice.setRenewalIntention(false);
            } else {
                maturityNotice.setRenewalIntention(null);
            }
            maturityNotice.checkRenewalIntention();
        }
        return processRenewalIntention(intention);
    }

    // 만기 처리 결과를 DB에 저장하고 생성된 안내번호를 반환한다
    public static String saveMaturityResult(MaturityNotice maturityNotice) {
        if (maturityNotice == null) {
            return null;
        }
        String noticeId = "MTN-" + System.currentTimeMillis();
        String deliveryMethod = resolveDeliveryMethodName(maturityNotice);
        String renewalIntention = resolveRenewalIntention(maturityNotice.getRenewalIntention());
        try (SqlSession s = MyBatisSessionFactory.openSession()) {
            int rows = s.getMapper(MaturityNoticeMapper.class)
                    .insert(maturityNotice, noticeId, deliveryMethod, renewalIntention);
            return rows > 0 ? noticeId : null;
        } catch (Exception e) {
            System.out.println("[DB 오류] 만기 안내 저장 실패: " + e.getMessage());
            return null;
        }
    }

    public static List<MaturityNotice> getMaturityNoticeList() {
        try (SqlSession s = MyBatisSessionFactory.openSession()) {
            return s.getMapper(MaturityNoticeMapper.class).findAll();
        } catch (Exception e) {
            System.out.println("[DB 오류] 만기 안내 목록 조회 실패: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public static MaturityNotice findMaturityNoticeById(String noticeId) {
        try (SqlSession s = MyBatisSessionFactory.openSession()) {
            return s.getMapper(MaturityNoticeMapper.class).findById(noticeId);
        } catch (Exception e) {
            System.out.println("[DB 오류] 만기 안내 조회 실패: " + e.getMessage());
            return null;
        }
    }

    public static String createNoticeDeliverySummary(MaturityNotice maturityNotice) {
        if (maturityNotice == null) {
            return "[시스템] 만기 안내장 발송 정보 없음";
        }
        return "[시스템] 만기 안내장 발송 결과"
                + "\n  발송방법: " + maturityNotice.getDeliveryMethod()
                + "\n  발송일시: " + maturityNotice.getSentAt()
                + "\n  안내: 실제 우편/문자 발송은 현재 콘솔 단계에서 시뮬레이션합니다.";
    }

    public static String createRenewalCheckSummary(MaturityNotice maturityNotice) {
        if (maturityNotice == null) {
            return "[시스템] 재계약 의사 확인 정보 없음";
        }
        return "[시스템] 재계약 의사 확인 결과"
                + "\n  재계약의사: " + resolveRenewalIntentionLabel(maturityNotice.getRenewalIntention())
                + "\n  확인일시: " + maturityNotice.getCheckedAt();
    }

    private static String resolveRenewalIntentionLabel(Boolean renewalIntention) {
        if (renewalIntention == null) {
            return "회신 없음";
        }
        return renewalIntention ? "있음" : "없음";
    }

    private static String resolveDeliveryMethodName(MaturityNotice notice) {
        if (notice == null || notice.getDeliveryMethod() == null) {
            return DeliveryMethod.SMS.name();
        }
        return notice.getDeliveryMethod().name();
    }

    private static String resolveRenewalIntention(Boolean value) {
        if (value == null) return null;
        return value ? "YES" : "NO";
    }
}

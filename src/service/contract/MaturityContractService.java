package service.contract;

import db.MaturityNoticeDBO;
import enums.DeliveryMethod;
import model.contract.MaturityNotice;

import java.time.LocalDateTime;

// 만기계약 관리 서비스 — 재계약 의사 처리 + 영속화 담당
public class MaturityContractService {

    private static final MaturityNoticeDBO maturityNoticeDBO = new MaturityNoticeDBO();

    public static boolean saveMaturityNotice(MaturityNotice notice, String policyNumber) {
        if (notice == null) {
            return false;
        }
        if (notice.getMaturityNoticeId() == null || notice.getMaturityNoticeId().isEmpty()) {
            notice.setMaturityNoticeId("MN-" + System.currentTimeMillis());
        }
        notice.setPolicyNumber(policyNumber);
        return maturityNoticeDBO.save(notice);
    }

    public static boolean updateMaturityNotice(MaturityNotice notice) {
        return maturityNoticeDBO.update(notice);
    }

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
}

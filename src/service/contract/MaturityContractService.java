package service.contract;

import db.MaturityNoticeDBO;
import enums.DeliveryMethod;
import model.contract.MaturityNotice;

import java.time.LocalDateTime;

// 만기계약 관리 서비스 — 재계약 의사 처리, 만기안내 객체 저장 유스케이스 흐름 담당
public class MaturityContractService {

    // 재계약 의사 결과 처리
    public static String processRenewalIntention(String intention) {
        switch (intention) {
            case "1": return "재계약 의사 있음 — 재계약 절차를 안내합니다.";
            case "2": return "만기 처리 완료 — 계약상태: '만기종료'";
            case "3": return "회신 기한 초과 — 계약상태: '만기종료'";
            default:  return "[오류] 올바른 선택이 아닙니다.";
        }
    }

    // MaturityNotice 객체 생성 및 저장
    public static void saveMaturityNotice(String intention) {
        MaturityNotice notice = new MaturityNotice(DeliveryMethod.SMS, LocalDateTime.now());
        notice.setRenewalIntention(
            "1".equals(intention) ? Boolean.TRUE :
            "2".equals(intention) ? Boolean.FALSE : null
        );
        notice.setCheckedAt(LocalDateTime.now());
        new MaturityNoticeDBO().save(notice);
    }
}

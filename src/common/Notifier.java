package common;

// 외부 알림(SIU 위임, 미납안내, 만기안내, 거절 알림 등) 발송 추상
public interface Notifier {

    // 수신처(부서/사번/연락처)에 메시지 전달
    void notify(String recipient, String message);
}

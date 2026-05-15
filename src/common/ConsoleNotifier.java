package common;

/**
 * Notifier 콘솔 출력 구현 — [알림] 포맷으로 한 줄 출력.
 * TODO: Replace with SMS/Email/Slack-backed implementation when external channels are wired.
 */
public class ConsoleNotifier implements Notifier {

    @Override
    public void notify(String recipient, String message) {
        System.out.println("  [알림] (→ " + recipient + ") " + message);
    }
}

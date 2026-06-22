package webstack.common.constant;

/**
 * 通知类型
 */
public final class NoticeType {

    private NoticeType() {}

    /** 系统广播通知 */
    public static final int SYSTEM = 1;

    /** 评论回复通知 */
    public static final int REPLY = 2;

    /** 系统通知发送者 ID（虚拟） */
    public static final int SYSTEM_SENDER_ID = 0;

    /** 系统通知发送者名称 */
    public static final String SYSTEM_SENDER_NAME = "系统";
}

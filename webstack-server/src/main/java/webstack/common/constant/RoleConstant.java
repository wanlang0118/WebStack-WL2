package webstack.common.constant;

/**
 * 系统角色常量
 */
public final class RoleConstant {

    private RoleConstant() {}

    /** 管理员：可访问后台所有管理接口 */
    public static final String ADMIN = "ADMIN";

    /** 普通用户：仅可访问与自身相关的接口 */
    public static final String USER = "USER";
}

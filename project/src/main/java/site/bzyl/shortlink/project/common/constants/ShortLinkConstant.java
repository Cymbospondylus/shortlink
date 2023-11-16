package site.bzyl.shortlink.project.common.constants;

/**
 * 短链接常量类
 */
public class ShortLinkConstant {
    /**
     * 默认域名
     */
    public static final String DEFAULT_DOMAIN = "ljq.fan";

    /**
     *  默认协议
     */
    public static final String DEFAULT_PROTOCOL = "http://";

    /**
     * 短链接创建最大重试次数
     */
    public static final Integer SHORT_LINK_CREATION_MAX_RETRY = 10;

    /**
     * 启用状态标识
     */
    public static final Integer ENABLE_STATUS = 0;

    /**
     * 禁用状态标识
     */
    public static final Integer DISABLE_STATUS = 1;

    /**
     * 永久有效短链接默认缓存过期时间(一个月)
     */
    public static Long DEFAULT_PERMANENT_LINK_EXPIRE_TIME = 2629800000l;

    /**
     * 短链接不存在跳转页面
     */
    public static String SHORT_LINK_NOT_FOUND_PAGE = "/page/notfound";
}

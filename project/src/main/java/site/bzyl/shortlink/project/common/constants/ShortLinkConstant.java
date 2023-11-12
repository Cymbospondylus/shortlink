package site.bzyl.shortlink.project.common.constants;

/**
 * 短链接常量类
 */
public class ShortLinkConstant {
    /**
     * 默认域名
     */
    public static final String DEFAULT_DOMAIN = "www.bzyl.site";

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
}

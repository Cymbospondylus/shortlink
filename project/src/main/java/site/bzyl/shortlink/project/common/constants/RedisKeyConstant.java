package site.bzyl.shortlink.project.common.constants;

/**
 * Redis缓存Key常量类
 */
public class RedisKeyConstant {
    /**
     * 短链接重定向key,key=完整短链接,value=原始链接
     */
    public static final String SHORT_LINK_REDIRECT_KEY = "short-link_redirect:%s";

    public static final String LOCK_SHORT_LINK_REDIRECT = "lock_short_link_redirect:%s";
}

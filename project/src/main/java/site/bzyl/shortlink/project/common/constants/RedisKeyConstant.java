package site.bzyl.shortlink.project.common.constants;

/**
 * Redis缓存Key常量类
 */
public class RedisKeyConstant {
    /**
     * 短链接重定向key,key=完整短链接,value=原始链接
     */
    public static final String SHORT_LINK_REDIRECT_KEY = "short-link_redirect:%s";

    /**
     * 短链接空值缓存, 防止缓存穿透
     */
    public static final String SHORT_LINK_NULL_VALUE_KEY = "short-link_null:%s";

    /**
     * 短链接查询时加锁，防止缓存击穿
     */
    public static final String LOCK_SHORT_LINK_REDIRECT = "lock_short_link_redirect:%s";

    /**
     *  短链接监控统计 UV
     */
    public static final String SHORT_LINK_STATS_UV = "short-link:stats:uv:%s";

    /**
     *  短链接监控统计 UIP
     */
    public static final String SHORT_LINK_STATS_UIP = "short-link:stats:uip:%s";
}

package site.bzyl.shortlink.admin.remote.dto.resp;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 短链接分页响应参数
 */
@Data
@Builder
public class ShortLinkPageRespDTO {

    /**
     * 域名
     */
    private String domain;

    /**
     * 短链接
     */
    private String shortUri;

    /**
     * 完整短链接
     */
    private String fullShortUri;

    /**
     * 原始链接
     */
    private String originUri;

    /**
     * 点击量
     */
    private Integer clickNum;

    /**
     * 分组标识
     */
    private String gid;

    /**
     * 网站图片
     */
    private String favicon;

    /**
     * 有效期类型 0：永久有效 1：用户自定义
     */
    private Integer validDateType;

    /**
     * 有效期
     */
    private LocalDateTime validDate;

    /**
     * 描述
     */
    private String description;

    /**
     * 历史PV
     */
    private Integer totalPv;

    /**
     * 今日PV
     */
    private Integer toDayPv;

    /**
     * 历史UV
     */
    private Integer totalUv;

    /**
     * 今日UV
     */
    private Integer toDayUv;

    /**
     * 历史UIP
     */
    private Integer totalUIp;

    /**
     * 今日UIP
     */
    private Integer toDayUIp;
}

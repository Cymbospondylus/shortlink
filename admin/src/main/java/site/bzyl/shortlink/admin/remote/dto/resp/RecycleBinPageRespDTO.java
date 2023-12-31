package site.bzyl.shortlink.admin.remote.dto.resp;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 回收站短链接分页查询响应参数
 */
@Data
@Builder
public class RecycleBinPageRespDTO {
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

}

package site.bzyl.shortlink.project.dto.resp;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ShortLinkCreateRespDTO {

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
     * 分组标识
     */
    private String gid;

    /**
     * 有效期
     */
    private LocalDateTime validDate;

    /**
     * 描述
     */
    private String description;
}

package site.bzyl.shortlink.admin.dto.resp;

import lombok.Data;

/**
 * 短链接分返回参数响应
 */
@Data
public class ShortLinkGroupRespDTO {
    /**
     * 分组标识
     */
    private String gid;

    /**
     * 分组名称
     */
    private String name;

    /**
     * 分组排序
     */
    private Integer sortOrder;

    /**
     * 分组下的短链接数
     */
    private Integer shortLinkCount;
}

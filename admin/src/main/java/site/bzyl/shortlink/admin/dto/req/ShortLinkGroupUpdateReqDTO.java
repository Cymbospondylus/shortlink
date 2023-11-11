package site.bzyl.shortlink.admin.dto.req;

import lombok.Data;

/**
 * 短链接分组修改请求参数
 */
@Data
public class ShortLinkGroupUpdateReqDTO {
    /**
     * 分组标识
     */
    private String gid;

    /**
     * 分组名称
     */
    private String name;

    /**
     * 创建分组用户标识
     */
    private String username;

    /**
     * 分组排序
     */
    private Integer sortOrder;
}

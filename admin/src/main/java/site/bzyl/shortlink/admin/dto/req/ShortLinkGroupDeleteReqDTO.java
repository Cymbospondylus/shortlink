package site.bzyl.shortlink.admin.dto.req;

import lombok.Data;

/**
 * 短链接分组删除请求参数
 */
@Data
public class ShortLinkGroupDeleteReqDTO {
    /**
     * 分组标识
     */
    private String gid;

    /**
     * 创建分组用户标识
     */
    private String username;
}

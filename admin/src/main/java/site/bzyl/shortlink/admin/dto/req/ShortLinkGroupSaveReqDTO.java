package site.bzyl.shortlink.admin.dto.req;

import lombok.Data;

/**
 * 短链接分组新增请求参数
 */
@Data
public class ShortLinkGroupSaveReqDTO {
    /**
    * 分组名称
    */
    private String name;
}
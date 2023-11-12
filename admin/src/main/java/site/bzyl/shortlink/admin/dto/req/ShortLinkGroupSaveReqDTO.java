package site.bzyl.shortlink.admin.dto.req;

import lombok.*;

/**
 * 短链接分组新增请求参数
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShortLinkGroupSaveReqDTO {
    /**
    * 分组名称
    */
    private String name;
}
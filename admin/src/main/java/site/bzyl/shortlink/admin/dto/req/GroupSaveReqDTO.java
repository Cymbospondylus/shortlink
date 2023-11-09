package site.bzyl.shortlink.admin.dto.req;

import lombok.Data;

/**
 * 短链接分组新增请求参数
 */
@Data
public class GroupSaveReqDTO {
    /**
    * 分组名称
    */
    private String name;

    /**
    * 创建分组用户标识
    */
    private String username;
}
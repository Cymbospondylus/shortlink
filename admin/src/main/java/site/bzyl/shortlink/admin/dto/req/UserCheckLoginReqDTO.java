package site.bzyl.shortlink.admin.dto.req;

import lombok.Data;

/**
 * 用户检查登录请求参数
 */
@Data
public class UserCheckLoginReqDTO {
    /**
     * 用户名
     */
    private String username;

    /**
     * token
     */
    private String token;
}

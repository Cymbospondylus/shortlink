package site.bzyl.shortlink.admin.remote.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 分组内短链接计数返回响应
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShortLinkCountRespDTO {
    /**
     * 分组标识
     */
    private String gid;

    /**
     * 分组下的短链接数
     */
    private Integer shortLinkCount;
}

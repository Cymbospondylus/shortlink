package site.bzyl.shortlink.admin.remote.dto.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 回收站恢复短链接请求参数
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecycleBinRestoreReqDTO {
    /**
     * 完整短链接
     */
    private String fullShortUri;

    /**
     * 分组标识
     */
    private String gid;
}

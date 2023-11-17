package site.bzyl.shortlink.admin.remote.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 短链接加入回收站请求参数
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecycleBinSaveReqDTO {
    /**
     * 完整短链接
     */
    private String fullShortUri;

    /**
     * 分组标识
     */
    private String gid;
}

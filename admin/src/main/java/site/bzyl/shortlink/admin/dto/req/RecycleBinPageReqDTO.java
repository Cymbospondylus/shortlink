package site.bzyl.shortlink.admin.dto.req;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 短链接回收站分页查询请求参数
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecycleBinPageReqDTO extends Page {
    /**
     * 分组标识
     */
    private String gid;
}

package site.bzyl.shortlink.project.dto.req;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import site.bzyl.shortlink.project.dao.entity.ShortLinkDO;

/**
 * 短链接回收站分页查询请求参数
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecycleBinPageReqDTO extends Page<ShortLinkDO> {
    /**
     * 分组标识
     */
    private String gid;
}

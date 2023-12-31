package site.bzyl.shortlink.admin.remote.dto.req;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

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
    private List<String> gidList;

    /**
     * 排序标识
     */
    private String orderTag;
}

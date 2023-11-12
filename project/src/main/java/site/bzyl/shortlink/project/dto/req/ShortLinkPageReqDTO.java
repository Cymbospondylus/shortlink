package site.bzyl.shortlink.project.dto.req;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;

/**
 * 短链接分页请求参数
 */

@Data
public class ShortLinkPageReqDTO extends Page {
    /**
     * 短链接分组id
     */
    private String gid;
}

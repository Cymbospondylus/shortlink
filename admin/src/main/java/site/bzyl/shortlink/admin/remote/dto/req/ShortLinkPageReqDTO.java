package site.bzyl.shortlink.admin.remote.dto.req;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;

import java.util.List;

/**
 * 短链接分页请求参数
 */
@Data
public class ShortLinkPageReqDTO extends Page {
    /**
     * 短链接分组id
     */
    private List<String> gidList;
}


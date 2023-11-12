package site.bzyl.shortlink.admin.remote.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import site.bzyl.shortlink.admin.common.convention.result.Result;
import site.bzyl.shortlink.admin.remote.dto.req.ShortLinkCreateReqDTO;
import site.bzyl.shortlink.admin.remote.dto.req.ShortLinkPageReqDTO;
import site.bzyl.shortlink.admin.remote.dto.resp.ShortLinkCreateRespDTO;
import site.bzyl.shortlink.admin.remote.dto.resp.ShortLinkPageRespDTO;
import site.bzyl.shortlink.admin.remote.service.ShortLinkRemoteService;

/**
 * 短链接中台远程调用接口
 */
@RestController
public class ShortLinkController {
    ShortLinkRemoteService shortLinkRemoteService = new ShortLinkRemoteService(){};
    /**
     * 分页查询短链接
     * @param requestParam 短链接分页查询请求参数
     * @return 短链接分页查询响应参数
     */
    @GetMapping("/api/short-link/admin/v1/page")
    public Result<IPage<ShortLinkPageRespDTO>> pageShortLink(ShortLinkPageReqDTO requestParam) {
        return shortLinkRemoteService.pageShortLink(requestParam);
    }

    /**
     * 创建短链接接口
     * @param requestParam 短链接创建请求参数
     * @return 短链接创建返回响应
     */
    @PostMapping("/api/short-link/admin/v1/create")
    public Result<ShortLinkCreateRespDTO> createShortLink(@RequestBody ShortLinkCreateReqDTO requestParam) {
        return shortLinkRemoteService.createShortLink(requestParam);
    }
}


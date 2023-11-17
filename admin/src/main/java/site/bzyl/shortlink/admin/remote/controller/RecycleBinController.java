package site.bzyl.shortlink.admin.remote.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import site.bzyl.shortlink.admin.common.convention.result.Result;
import site.bzyl.shortlink.admin.common.convention.result.Results;
import site.bzyl.shortlink.admin.remote.dto.req.RecycleBinPageReqDTO;
import site.bzyl.shortlink.admin.remote.dto.req.RecycleBinSaveReqDTO;
import site.bzyl.shortlink.admin.remote.dto.resp.RecycleBinPageRespDTO;
import site.bzyl.shortlink.admin.remote.service.RecycleBinRemoteService;
import site.bzyl.shortlink.admin.remote.service.ShortLinkRecycleBinService;

/**
 * 短链接回收站远程调用接口
 */
@RestController
@RequiredArgsConstructor
public class RecycleBinController {
    RecycleBinRemoteService recycleBinRemoteService = new RecycleBinRemoteService(){};
    private final ShortLinkRecycleBinService shortLinkRecycleBinService;

    /**
     * 短链接移入回收站接口
     * @param requestParam 短链接加入回收站请求参数
     */
    @PostMapping("/api/short-link/admin/v1/recycle-bin/save")
    public Result<Void> addShortLinkToRecycleBin(@RequestBody RecycleBinSaveReqDTO requestParam) {
        recycleBinRemoteService.addShortLinkToRecycleBin(requestParam);
        return Results.success();
    }

    /**
     * 短链接回收站分页查询接口
     * @param requestParam 短链接分页查询请求参数
     * @return 短链接分页查询响应参数
     */
    @GetMapping("/api/short-link/admin/v1/recycle-bin/page")
    public Result<IPage<RecycleBinPageRespDTO>> pageRecycleBinLink(RecycleBinPageReqDTO requestParam) {
        return shortLinkRecycleBinService.pageRecycleBinLink(requestParam);
    }
}

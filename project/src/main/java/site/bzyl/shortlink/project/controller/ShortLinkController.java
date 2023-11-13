package site.bzyl.shortlink.project.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import site.bzyl.shortlink.project.common.convention.result.Result;
import site.bzyl.shortlink.project.common.convention.result.Results;
import site.bzyl.shortlink.project.dto.req.ShortLinkCreateReqDTO;
import site.bzyl.shortlink.project.dto.req.ShortLinkPageReqDTO;
import site.bzyl.shortlink.project.dto.resp.ShortLinkCountRespDTO;
import site.bzyl.shortlink.project.dto.resp.ShortLinkCreateRespDTO;
import site.bzyl.shortlink.project.dto.resp.ShortLinkPageRespDTO;
import site.bzyl.shortlink.project.service.ShortLinkService;

import java.util.List;

/**
 * 短链接相关接口
 */
@RestController
@RequiredArgsConstructor
public class ShortLinkController {

    private final ShortLinkService shortLinkService;

    /**
     * 创建短链接接口
     * @param requestParam 短链接创建请求参数
     * @return 短链接创建返回响应
     */
    @PostMapping("/api/short-link/v1/create") // todo uri先这么写, 为了后面引入前端工程联调方便，之后再自己改成习惯的写法
    public Result<ShortLinkCreateRespDTO> createShortLink(@RequestBody ShortLinkCreateReqDTO requestParam) {
        return Results.success(shortLinkService.createShortLink(requestParam));
    }

    /**
     * 分页查询短链接
     * @param requestParam 短链接分页查询请求参数
     * @return 短链接分页查询响应参数
     */
    @GetMapping("/api/short-link/v1/page")
    public Result<IPage<ShortLinkPageRespDTO>> pageShortLink(ShortLinkPageReqDTO requestParam) {
        return Results.success(shortLinkService.pageShortLink(requestParam));
    }

    /**
     * 查询分组中短链接的数量
     * @param requestParam gidList数组
     * @return 分组内短链接计数返回响应
     */
    @GetMapping("/api/short-link/v1/count")
    public Result<List<ShortLinkCountRespDTO>> countShortLink(@RequestParam List<String> requestParam) {
        return Results.success(shortLinkService.countShortLink(requestParam));
    }
}

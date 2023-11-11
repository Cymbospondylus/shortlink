package site.bzyl.shortlink.project.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import site.bzyl.shortlink.project.common.convention.result.Result;
import site.bzyl.shortlink.project.common.convention.result.Results;
import site.bzyl.shortlink.project.dto.req.ShortLinkCreateReqDTO;
import site.bzyl.shortlink.project.dto.resp.ShortLinkCreateRespDTO;
import site.bzyl.shortlink.project.service.ShortLinkService;

@RestController
@RequiredArgsConstructor
public class ShortLinkController {

    private final ShortLinkService shortLinkService;

    @PostMapping("/api/short-link/project/v1/link")
    public Result<ShortLinkCreateRespDTO> createShortLink(@RequestBody ShortLinkCreateReqDTO requestParam) {
        return Results.success(shortLinkService.createShortLink(requestParam));
    }
}

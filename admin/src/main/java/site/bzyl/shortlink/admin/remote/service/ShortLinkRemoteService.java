package site.bzyl.shortlink.admin.remote.service;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.baomidou.mybatisplus.core.metadata.IPage;
import site.bzyl.shortlink.admin.common.convention.result.Result;
import site.bzyl.shortlink.admin.remote.dto.ShortLinkCountRespDTO;
import site.bzyl.shortlink.admin.remote.dto.req.ShortLinkCreateReqDTO;
import site.bzyl.shortlink.admin.remote.dto.req.ShortLinkPageReqDTO;
import site.bzyl.shortlink.admin.remote.dto.resp.ShortLinkCreateRespDTO;
import site.bzyl.shortlink.admin.remote.dto.resp.ShortLinkPageRespDTO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 短链接中台远程调用服务
 */
public interface ShortLinkRemoteService {

    default Result<IPage<ShortLinkPageRespDTO>> pageShortLink(ShortLinkPageReqDTO requestParam) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("gid", requestParam.getGid());
        paramMap.put("current", requestParam.getCurrent());
        paramMap.put("size", requestParam.getSize());
        String pageJsonStr = HttpUtil.get("http://127.0.0.1:8001/api/short-link/v1/page", paramMap);
        return JSON.parseObject(pageJsonStr, new TypeReference<>() {
        });
    }

    default Result<ShortLinkCreateRespDTO> createShortLink(ShortLinkCreateReqDTO requestParam) {
        String resultBodyJsonStr = HttpUtil.post("http://127.0.0.1:8001/api/short-link/v1/create", JSON.toJSONString(requestParam));
        return JSON.parseObject(resultBodyJsonStr, new TypeReference<>() {
        });
    }

    default Result<List<ShortLinkCountRespDTO>> countShortLink(List<String> gidList) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("gidList", gidList);
        String pageJsonStr = HttpUtil.get("http://127.0.0.1:8001/api/short-link/v1/count", paramMap);
        return JSON.parseObject(pageJsonStr, new TypeReference<>() {
        });
    }
}


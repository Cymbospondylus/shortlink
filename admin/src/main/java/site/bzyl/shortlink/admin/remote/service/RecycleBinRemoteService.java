package site.bzyl.shortlink.admin.remote.service;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.baomidou.mybatisplus.core.metadata.IPage;
import site.bzyl.shortlink.admin.common.convention.result.Result;
import site.bzyl.shortlink.admin.remote.dto.req.RecycleBinDeleteReqDTO;
import site.bzyl.shortlink.admin.remote.dto.req.RecycleBinPageReqDTO;
import site.bzyl.shortlink.admin.remote.dto.req.RecycleBinRestoreReqDTO;
import site.bzyl.shortlink.admin.remote.dto.req.RecycleBinSaveReqDTO;
import site.bzyl.shortlink.admin.remote.dto.resp.RecycleBinPageRespDTO;

import java.util.HashMap;
import java.util.Map;

/**
 * 短链接回收站远程调用服务
 */
public interface RecycleBinRemoteService {
    default Result<Void> addShortLinkToRecycleBin(RecycleBinSaveReqDTO requestParam) {
        String resultBodyJsonStr = HttpUtil.post("http://127.0.0.1:8001/api/short-link/v1/recycle-bin/save", JSON.toJSONString(requestParam));
        return JSON.parseObject(resultBodyJsonStr, new TypeReference<>() {
        });
    }

    default Result<IPage<RecycleBinPageRespDTO>> pageRecycleBinLink(RecycleBinPageReqDTO requestParam) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("gidList", requestParam.getGidList());
        paramMap.put("current", requestParam.getCurrent());
        paramMap.put("size", requestParam.getSize());
        String pageJsonStr = HttpUtil.get("http://127.0.0.1:8001/api/short-link/v1/recycle-bin/page", paramMap);
        return JSON.parseObject(pageJsonStr, new TypeReference<>() {
        });
    }

    default Result<Void> restoreShortLinkFromRecycleBin(RecycleBinRestoreReqDTO requestParam) {
        String resultBodyJsonStr = HttpUtil.post("http://127.0.0.1:8001/api/short-link/v1/recycle-bin/restore", JSON.toJSONString(requestParam));
        return JSON.parseObject(resultBodyJsonStr, new TypeReference<>() {
        });
    }

    default Result<Void> deleteShortLinkInRecycleBin(RecycleBinDeleteReqDTO requestParam) {
        // todo HttpUtil没有Delete方法, 留着重构feign调用的时候再写
        return null;
    }
}

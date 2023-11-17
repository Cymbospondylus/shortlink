package site.bzyl.shortlink.admin.remote.service;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import site.bzyl.shortlink.admin.common.convention.result.Result;
import site.bzyl.shortlink.admin.remote.dto.RecycleBinSaveReqDTO;

/**
 * 短链接回收站远程调用服务
 */
public interface RecycleBinRemoteService {
    default Result<Void> addShortLinkToRecycleBin(RecycleBinSaveReqDTO requestParam) {
        String resultBodyJsonStr = HttpUtil.post("http://127.0.0.1:8001/api/short-link/v1/recycle-bin/save", JSON.toJSONString(requestParam));
        return JSON.parseObject(resultBodyJsonStr, new TypeReference<>() {
        });
    }
}

package site.bzyl.shortlink.project.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.google.common.base.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import site.bzyl.shortlink.project.dao.entity.LinkAccessStatsDO;
import site.bzyl.shortlink.project.dao.entity.LinkLocaleStatsDO;
import site.bzyl.shortlink.project.dao.mapper.*;
import site.bzyl.shortlink.project.dto.req.ShortLinkStatsReqDTO;
import site.bzyl.shortlink.project.dto.resp.ShortLinkStatsAccessDailyRespDTO;
import site.bzyl.shortlink.project.dto.resp.ShortLinkStatsLocaleCNRespDTO;
import site.bzyl.shortlink.project.dto.resp.ShortLinkStatsOsRespDTO;
import site.bzyl.shortlink.project.dto.resp.ShortLinkStatsRespDTO;
import site.bzyl.shortlink.project.service.ShortLinkStatsService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ShortLinkStatsServiceImpl implements ShortLinkStatsService {
    private final linkAccessStatsMapper linkAccessStatsMapper;
    private final LinkLocaleStatsMapper linkLocaleStatsMapper;
    private final LinkAccessLogsMapper linkAccessLogsMapper;
    private final LinkBrowserStatsMapper linkBrowserStatsMapper;
    private final LinkOsStatsMapper linkOsStatsMapper;
    private final LinkDeviceStatsMapper linkDeviceStatsMapper;
    private final LinkNetworkStatsMapper linkNetworkStatsMapper;

    @Override
    public ShortLinkStatsRespDTO oneShortLinkStats(ShortLinkStatsReqDTO requestParam) {
        // 基础信息统计,pv,uv,uip
        List<LinkAccessStatsDO> listStatsByShortLink = linkAccessStatsMapper.listStatsByShortLink(requestParam);

        // 地区访问统计
        List<LinkLocaleStatsDO> listLocaleByShortLink = linkLocaleStatsMapper.listLocaleByShortLink(requestParam);
        int localCnSum = listLocaleByShortLink.stream()
                .mapToInt(LinkLocaleStatsDO::getCnt)
                .sum();
        List<ShortLinkStatsLocaleCNRespDTO> localeCnStats = listLocaleByShortLink.stream()
                .map(each -> {
                    double ratio = (double) each.getCnt() / localCnSum;
                    double actualRatio = Math.round(ratio * 100.0) / 100.0;
                    return ShortLinkStatsLocaleCNRespDTO
                            .builder()
                            .cnt(each.getCnt())
                            .locale(each.getProvince())
                            .ratio(actualRatio)
                            .build();
                })
                .collect(Collectors.toList());

        // 小时访问统计
        List<LinkAccessStatsDO> listHourStatsByShortLink = linkAccessStatsMapper.listHourStatsByShortLink(requestParam);
        List<Integer> hourStats = Stream.iterate(0, i -> i + 1).limit(24).map(i -> listHourStatsByShortLink.stream()
                        .filter(each -> Objects.equal(each.getHour(), i))
                        .findFirst()
                        .map(LinkAccessStatsDO::getPv)
                        .orElse(0))
                .collect(Collectors.toList());


        // 操作系统访问监控
        List<ShortLinkStatsOsRespDTO> osStats = new ArrayList<>();
        List<HashMap<String, Object>> listOsStatsByShortLink = linkOsStatsMapper.listOsStatsByShortLink(requestParam);
        int osSum = listOsStatsByShortLink.stream()
                .mapToInt(each -> Integer.parseInt(each.get("count").toString()))
                .sum();
        listOsStatsByShortLink.forEach(each -> {
            double ratio = (double) Integer.parseInt(each.get("count").toString()) / osSum;
            double actualRatio = Math.round(ratio * 100.0) / 100.0;
            ShortLinkStatsOsRespDTO osRespDTO = ShortLinkStatsOsRespDTO.builder()
                    .cnt(Integer.parseInt(each.get("count").toString()))
                    .os(each.get("os").toString())
                    .ratio(actualRatio)
                    .build();
            osStats.add(osRespDTO);
        });


        return ShortLinkStatsRespDTO
                .builder()
                .daily(BeanUtil.copyToList(listStatsByShortLink, ShortLinkStatsAccessDailyRespDTO.class))
                .localeCnStats(localeCnStats)
                .hourStats(hourStats)
                .osStats(osStats)
                .build();
    }
}

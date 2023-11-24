package site.bzyl.shortlink.project.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.text.StrBuilder;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.bzyl.shortlink.project.common.convention.exception.ClientException;
import site.bzyl.shortlink.project.common.convention.exception.ServiceException;
import site.bzyl.shortlink.project.dao.entity.LinkAccessStatsDO;
import site.bzyl.shortlink.project.dao.entity.ShortLinkDO;
import site.bzyl.shortlink.project.dao.entity.ShortLinkGotoDO;
import site.bzyl.shortlink.project.dao.mapper.ShortLinkAccessStatsMapper;
import site.bzyl.shortlink.project.dao.mapper.ShortLinkGotoMapper;
import site.bzyl.shortlink.project.dao.mapper.ShortLinkMapper;
import site.bzyl.shortlink.project.dto.req.ShortLinkCreateReqDTO;
import site.bzyl.shortlink.project.dto.req.ShortLinkPageReqDTO;
import site.bzyl.shortlink.project.dto.req.ShortLinkUpdateReqDTO;
import site.bzyl.shortlink.project.dto.resp.ShortLinkCountRespDTO;
import site.bzyl.shortlink.project.dto.resp.ShortLinkCreateRespDTO;
import site.bzyl.shortlink.project.dto.resp.ShortLinkPageRespDTO;
import site.bzyl.shortlink.project.service.ShortLinkService;
import site.bzyl.shortlink.project.toolkit.HashUtil;
import site.bzyl.shortlink.project.toolkit.ShortLinkUtil;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static site.bzyl.shortlink.project.common.constants.RedisKeyConstant.*;
import static site.bzyl.shortlink.project.common.constants.ShortLinkConstant.*;
import static site.bzyl.shortlink.project.common.enums.ShortLinkValidDateType.PERMANENT;

/**
 * 短链接业务层实现
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ShortLinkServiceImpl extends ServiceImpl<ShortLinkMapper, ShortLinkDO> implements ShortLinkService {
    private final RBloomFilter<String> shortLinkCreationCachePenetrationBloomFilter;
    private final ShortLinkGotoMapper shortLinkGotoMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final RedissonClient redissonClient;
    private final ShortLinkAccessStatsMapper shortLinkAccessStatsMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ShortLinkCreateRespDTO createShortLink(ShortLinkCreateReqDTO requestParam) {
        String suffix = generateShortLinkSuffix(requestParam);
        // TODO 这里拿到一个布隆过滤器不存在的 suffix 应该再根据suffix加个分布式锁保证不会重复添加，小概率事件会拿到相同 suffix
        String fullShortUri = generateFullShortUri(DEFAULT_DOMAIN, suffix);
        ShortLinkDO shortLinkDO = ShortLinkDO
                .builder()
                .shortUri(suffix)
                .fullShortUri(fullShortUri)
                .gid(requestParam.getGid())
                .enableStatus(ENABLE_STATUS_ENABLED)
                .domain(DEFAULT_DOMAIN)
                .originUri(requestParam.getOriginUri())
                .createdType(requestParam.getCreatedType())
                .validDateType(requestParam.getValidDateType())
                .validDate(requestParam.getValidDate())
                .description(requestParam.getDescription())
                .build();

        ShortLinkGotoDO shortLinkGotoDO = ShortLinkGotoDO
                .builder()
                .fullShortUri(fullShortUri)
                .gid(requestParam.getGid())
                .build();
        int insertGoto = shortLinkGotoMapper.insert(shortLinkGotoDO);
        if (insertGoto < 1) {
            ServiceException.cast("短链接路由表创建失败");
        }

        int insert = baseMapper.insert(shortLinkDO);
        if (insert < 1) {
            ServiceException.cast("短链接创建失败");
        }
        shortLinkCreationCachePenetrationBloomFilter.add(fullShortUri);
        // 缓存预热防止缓存雪崩
        stringRedisTemplate.opsForValue().set(String.format(SHORT_LINK_REDIRECT_KEY, fullShortUri),
                shortLinkDO.getOriginUri(),
                ShortLinkUtil.getLinkExpireTimeMillis(requestParam.getValidDate()),
                TimeUnit.MILLISECONDS);

        return ShortLinkCreateRespDTO
                .builder()
                .shortUri(shortLinkDO.getShortUri())
                .fullShortUri(shortLinkDO.getFullShortUri())
                .originUri(shortLinkDO.getOriginUri())
                .domain(shortLinkDO.getDomain())
                .gid(shortLinkDO.getGid())
                .validDate(shortLinkDO.getValidDate())
                .description(shortLinkDO.getDescription())
                .build();
    }

    @Override
    public IPage<ShortLinkPageRespDTO> pageShortLink(ShortLinkPageReqDTO requestParam) {
        // todo 短链接分组和回收站分组都存在查询用户所有的问题, 虽然可以通过gid来区分, 最好还是加一层DTO根据分组和List返回
        LambdaQueryWrapper<ShortLinkDO> queryWrapper = Wrappers.lambdaQuery(ShortLinkDO.class)
                .in(ShortLinkDO::getGid, requestParam.getGidList())
                .eq(ShortLinkDO::getEnableStatus, ENABLE_STATUS_ENABLED)
                .orderByDesc(ShortLinkDO::getUpdateTime);
        IPage<ShortLinkDO> resultPage = baseMapper.selectPage(requestParam, queryWrapper);
        return resultPage.convert(each -> BeanUtil.toBean(each, ShortLinkPageRespDTO.class));
    }

    @Override
    public List<ShortLinkCountRespDTO> countShortLink(List<String> requestParam) {
        List<ShortLinkCountRespDTO> resultList = requestParam.stream()
                .map(each -> {
                    LambdaQueryWrapper<ShortLinkDO> queryWrapper = Wrappers.lambdaQuery(ShortLinkDO.class)
                            .eq(ShortLinkDO::getGid, each);
                    Integer shortLinkCount = Math.toIntExact(baseMapper.selectCount(queryWrapper));
                    return ShortLinkCountRespDTO
                            .builder()
                            .gid(each)
                            .shortLinkCount(shortLinkCount)
                            .build();
                })
                .collect(Collectors.toList());
        return resultList;
    }

    @Override
    @Transactional
    public void updateShortLink(ShortLinkUpdateReqDTO requestParam) {
        LambdaQueryWrapper<ShortLinkDO> queryWrapper = Wrappers.lambdaQuery(ShortLinkDO.class)
                .eq(ShortLinkDO::getFullShortUri, requestParam.getFullShortUri());
        ShortLinkDO hasShortLink = baseMapper.selectOne(queryWrapper);
        Optional.ofNullable(hasShortLink).orElseThrow(() -> new ClientException("短链接记录不存在"));

        ShortLinkDO shortLinkDO = ShortLinkDO
                .builder()
                .domain(hasShortLink.getDomain())
                .shortUri(hasShortLink.getShortUri())
                .clickNum(hasShortLink.getClickNum())
                .gid(requestParam.getGid())
                .fullShortUri(requestParam.getFullShortUri())
                .favicon(requestParam.getFavicon())
                .originUri(requestParam.getOriginUri())
                .enableStatus(requestParam.getEnableStatus())
                .validDateType(requestParam.getValidDateType())
                .validDate(requestParam.getValidDate())
                .description(requestParam.getDescription())
                .createdType(hasShortLink.getCreatedType())
                .build();
        // gid 相等表示没有修改分组，只是修改短链接信息
        if (Objects.equals(hasShortLink.getGid(), shortLinkDO.getGid())) {
            LambdaUpdateWrapper<ShortLinkDO> updateWrapper = Wrappers.lambdaUpdate(ShortLinkDO.class)
                    .eq(ShortLinkDO::getGid, shortLinkDO.getGid())
                    .eq(ShortLinkDO::getFullShortUri, shortLinkDO.getFullShortUri())
                    // 根据有效期类型判断, 如果是永久有效则把有效期设置为 null
                    .set(shortLinkDO.getValidDateType().equals(PERMANENT.getType()), ShortLinkDO::getValidDate, null);
            int update = baseMapper.update(shortLinkDO, updateWrapper);
            if (update < 1) {
                log.error("修改短链接信息失败, gid:{}, fullShortUri:{}", requestParam.getGid(), requestParam.getFullShortUri());
                ServiceException.cast("修改短链接信息失败");
            }
        } // gid 不相等, 表示当前短链接移动到了新的分组
        else {
            LambdaQueryWrapper<ShortLinkDO> deleteQueryWrapper = Wrappers.lambdaQuery(ShortLinkDO.class)
                    .eq(ShortLinkDO::getGid, hasShortLink.getGid())
                    .eq(ShortLinkDO::getFullShortUri, hasShortLink.getFullShortUri());
            int insert = baseMapper.insert(shortLinkDO);
            int delete = baseMapper.delete(deleteQueryWrapper);
            if (insert < 1 || delete < 1) {
                log.error("修改短链接信息失败, gid:{}, fullShortUri:{}", requestParam.getGid(), requestParam.getFullShortUri());
                ServiceException.cast("修改短链接信息失败");
            }
        }
    }

    @SneakyThrows
    @Override
    public void shortLinkRedirect(String shortUri, HttpServletRequest request, HttpServletResponse response) {
        String fullShortUrl = StrBuilder
                .create()
                // 获取请求方式是http还是https
                .append(request.getScheme())
                .append("://")
                // 获取域名
                .append(request.getServerName())
                .append("/")
                .append(shortUri)
                .toString();
        String originalLink = stringRedisTemplate.opsForValue().get(String.format(SHORT_LINK_REDIRECT_KEY, fullShortUrl));
        if (StrUtil.isNotBlank(originalLink)) {

            shortLinkAccessStats(fullShortUrl, null, request, response);


            response.sendRedirect(originalLink);
            return;
        }

        if (!shortLinkCreationCachePenetrationBloomFilter.contains(fullShortUrl)) {
            response.sendRedirect(SHORT_LINK_NOT_FOUND_PAGE);
            return;
        }

        String shortLinkNullValue = stringRedisTemplate.opsForValue().get(String.format(SHORT_LINK_NULL_VALUE_KEY, fullShortUrl));
        if (StrUtil.isNotBlank(shortLinkNullValue)) {
            response.sendRedirect(SHORT_LINK_NOT_FOUND_PAGE);
            return;
        }

        RLock lock = redissonClient.getLock(String.format(LOCK_SHORT_LINK_REDIRECT, fullShortUrl));
        lock.lock();
        try {
            originalLink = stringRedisTemplate.opsForValue().get(String.format(SHORT_LINK_REDIRECT_KEY, fullShortUrl));
            if (StrUtil.isNotBlank(originalLink)) {
                shortLinkAccessStats(fullShortUrl, null, request, response);
                response.sendRedirect(originalLink);
                return;
            }
            LambdaQueryWrapper<ShortLinkGotoDO> linkGotoQueryWrapper = Wrappers.lambdaQuery(ShortLinkGotoDO.class)
                    .eq(ShortLinkGotoDO::getFullShortUri, fullShortUrl);
            ShortLinkGotoDO shortLinkGotoDO = shortLinkGotoMapper.selectOne(linkGotoQueryWrapper);
            if (shortLinkGotoDO == null) {
                // 使用 "-", 表示缓存空对象，用于前面使用StrUtil判断缓存是否为空(没有缓存空对象)
                stringRedisTemplate.opsForValue().set(String.format(SHORT_LINK_NULL_VALUE_KEY, fullShortUrl),
                        "-",
                        30,
                        TimeUnit.SECONDS
                );
                response.sendRedirect(SHORT_LINK_NOT_FOUND_PAGE);
                return;
            }
            LambdaQueryWrapper<ShortLinkDO> queryWrapper = Wrappers.lambdaQuery(ShortLinkDO.class)
                    .eq(ShortLinkDO::getGid, shortLinkGotoDO.getGid())
                    .eq(ShortLinkDO::getEnableStatus, ENABLE_STATUS_ENABLED)
                    .eq(ShortLinkDO::getFullShortUri, fullShortUrl);
            ShortLinkDO shortLinkDO = baseMapper.selectOne(queryWrapper);

            if (shortLinkDO == null) {
                stringRedisTemplate.opsForValue().set(String.format(SHORT_LINK_NULL_VALUE_KEY, fullShortUrl),
                        "-",
                        30,
                        TimeUnit.SECONDS
                );
                response.sendRedirect(SHORT_LINK_NOT_FOUND_PAGE);
                return;
            }

            shortLinkAccessStats(fullShortUrl, shortLinkDO.getGid(), request, response);

            stringRedisTemplate.opsForValue().set(String.format(SHORT_LINK_REDIRECT_KEY, fullShortUrl),
                    shortLinkDO.getOriginUri(),
                    ShortLinkUtil.getLinkExpireTimeMillis(shortLinkDO.getValidDate()),
                    TimeUnit.MILLISECONDS);
            response.sendRedirect(shortLinkDO.getOriginUri());
        } finally {
            lock.unlock();
        }
    }

    private void shortLinkAccessStats(String fullShortUrl, String gid, HttpServletRequest request, HttpServletResponse response) {
        // Lambda 表达式中需要使用原子变量
        AtomicBoolean uvFirstFlag = new AtomicBoolean();
        Runnable addCookieTask = () -> {
            String uv = UUID.fastUUID().toString();
            Cookie uvCookie = new Cookie("uv", uv);
            // Cookie 有效期 30 天
            uvCookie.setMaxAge(60 * 60 * 24 * 30);
            // Cookie 作用域为当前域名下的短链接
            uvCookie.setPath(StrUtil.sub(fullShortUrl, fullShortUrl.lastIndexOf("/"), fullShortUrl.length()));
            response.addCookie(uvCookie);
            Long added = stringRedisTemplate.opsForSet().add(String.format(SHORT_LINK_STATS_UV, fullShortUrl), uv);
            uvFirstFlag.set(added != null && added > 0L);
        };
        Cookie[] cookies = request.getCookies();
        if (ArrayUtil.isNotEmpty(cookies)) {
            Arrays.stream(cookies)
                    .filter(each -> Objects.equals(each.getName(), "uv"))
                    .findFirst()
                    .map(Cookie::getValue)
                    .ifPresentOrElse(each -> {
                        Long uvAdded = stringRedisTemplate.opsForSet().add(String.format(SHORT_LINK_STATS_UV, fullShortUrl), each);
                        uvFirstFlag.set(uvAdded != null && uvAdded > 0L);
                    }, addCookieTask);
        }
        else {
            addCookieTask.run();
        }

        String clientIp = ShortLinkUtil.getClientIp(request);
        Long uipAdded = stringRedisTemplate.opsForSet().add(String.format(SHORT_LINK_STATS_UIP, fullShortUrl), clientIp);
        boolean uipFirstFlag = uipAdded != null && uipAdded > 0L;


        if (StrUtil.isBlank(gid)) {
            LambdaQueryWrapper<ShortLinkGotoDO> queryWrapper = Wrappers.lambdaQuery(ShortLinkGotoDO.class)
                    .eq(ShortLinkGotoDO::getFullShortUri, fullShortUrl);
            ShortLinkGotoDO shortLinkGotoDO = shortLinkGotoMapper.selectOne(queryWrapper);
            gid = shortLinkGotoDO.getGid();
        }
        LinkAccessStatsDO linkAccessStatsDO = LinkAccessStatsDO
                .builder()
                .fullShortUrl(fullShortUrl)
                .gid(gid)
                .date(LocalDate.now())
                .pv(1)
                .uv(uvFirstFlag.get()? 1 : 0)
                .uip(uipFirstFlag ? 1 : 0)
                .hour(DateUtil.hour(new Date(), true))
                .weekday(DateUtil.dayOfWeek(new Date()))
                .build();
        shortLinkAccessStatsMapper.accessShortLink(linkAccessStatsDO);
    }

    private String generateShortLinkSuffix(ShortLinkCreateReqDTO requestParam) {
        int retryCount = 0;
        while (true) {
            // 使用 原始长链接 + 系统当前毫秒数, 每次尝试获取hash值都不同
            String suffix = HashUtil.hashToBase62(requestParam.getOriginUri() + System.currentTimeMillis());
            String fullShortUri = generateFullShortUri(DEFAULT_DOMAIN, suffix);
            if (!shortLinkCreationCachePenetrationBloomFilter.contains(fullShortUri)) {
                return suffix;
            }
            retryCount++;
            if (retryCount >= SHORT_LINK_CREATION_MAX_RETRY) {
                ServiceException.cast("短链接创建重试次数过多");
            }
        }
    }

    private String generateFullShortUri(String domain, String shortUri) {
        return StrBuilder.create()  // new StringBuilder()
                .append(DEFAULT_PROTOCOL)
                .append(domain)
                .append("/")
                .append(shortUri)
                .toString();
    }
}

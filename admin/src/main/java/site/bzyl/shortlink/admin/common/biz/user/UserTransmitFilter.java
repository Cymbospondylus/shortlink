package site.bzyl.shortlink.admin.common.biz.user;

import com.alibaba.fastjson2.JSON;
import com.google.common.collect.Lists;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.data.redis.core.StringRedisTemplate;
import site.bzyl.shortlink.admin.common.constants.UserConstant;

import java.io.IOException;
import java.util.List;

import static site.bzyl.shortlink.admin.common.constants.RedisCacheConstant.USER_LOGIN_PREFIX;

/**
 * 用户信息传输过滤器
 */
@RequiredArgsConstructor
public class UserTransmitFilter implements Filter {

    public final StringRedisTemplate stringRedisTemplate;

    private static List<String> IGNORE_URI = Lists.newArrayList(
            "/api/short-link/v1/user/login",
            "/api/short-link/v1/user/check-login"
    );

    @Override
    @SneakyThrows
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        String requestURI = httpServletRequest.getRequestURI();
        if (!inIgnoreList(requestURI, IGNORE_URI)) {
            String username = httpServletRequest.getHeader(UserConstant.USER_NAME_KEY);
            String token = httpServletRequest.getHeader(UserConstant.USER_TOKEN_KEY);
            Object userInfoJsonStr = stringRedisTemplate.opsForHash().get(USER_LOGIN_PREFIX + username, token);
            if (userInfoJsonStr != null) {
                UserInfoDTO userInfoDTO = JSON.parseObject(userInfoJsonStr.toString(), UserInfoDTO.class);
                UserContext.setUser(userInfoDTO);
            }
        }
        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            // 防止内存泄露
            UserContext.removeUser();
        }
    }

    private Boolean inIgnoreList(String uri, List<String> ignoreList) {
        for (String ignoreListURI : ignoreList) {
            if (ignoreListURI.equals(uri))
                return true;
        }
        return false;
    }
}
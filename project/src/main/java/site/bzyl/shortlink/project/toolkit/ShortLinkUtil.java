package site.bzyl.shortlink.project.toolkit;

import cn.hutool.core.date.LocalDateTimeUtil;

import java.time.LocalDateTime;
import java.util.Optional;

import static java.time.temporal.ChronoUnit.MILLIS;
import static site.bzyl.shortlink.project.common.constants.ShortLinkConstant.DEFAULT_PERMANENT_LINK_EXPIRE_TIME;

public class ShortLinkUtil {
    public static long getLinkExpireTimeMillis(LocalDateTime expireDateTime) {
        return Optional
                .ofNullable(expireDateTime)
                .map(each -> LocalDateTimeUtil.between(LocalDateTime.now(), each, MILLIS))
                .orElse(DEFAULT_PERMANENT_LINK_EXPIRE_TIME);
    }
}

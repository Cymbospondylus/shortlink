package site.bzyl.shortlink.project.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 短链接有效期类型枚举
 */
@RequiredArgsConstructor
@Getter
public enum ShortLinkValidDateType {
    /**
     * 永久有效
     */
    PERMANENT(0),
    /**
     * 用户自定义
     */
    CUSTOMIZATION(1);


    private final Integer type;
}

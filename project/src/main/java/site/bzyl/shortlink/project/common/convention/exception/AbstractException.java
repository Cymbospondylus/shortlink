package site.bzyl.shortlink.project.common.convention.exception;

import lombok.Getter;
import org.springframework.util.StringUtils;
import site.bzyl.shortlink.project.common.convention.errorcode.IErrorCode;

import java.util.Optional;

/**
 * 抽象项目中的三类异常体系
 * @see ClientException
 * @see ServiceException
 * @see RemoteException
 */
@Getter
public abstract class AbstractException extends RuntimeException {

    public final String errorCode;

    public final String errorMessage;

    public static void cast(IErrorCode code){}

    public AbstractException(String message, Throwable throwable, IErrorCode errorCode) {
        super(message, throwable);
        this.errorCode = errorCode.code();
        this.errorMessage = Optional.ofNullable(StringUtils.hasLength(message) ? message : null).orElse(errorCode.message());
    }


}
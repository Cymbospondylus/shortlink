package site.bzyl.shortlink.admin.common.convention.exception;

import site.bzyl.shortlink.admin.common.convention.errorcode.BaseErrorCode;
import site.bzyl.shortlink.admin.common.convention.errorcode.IErrorCode;

import java.util.Optional;

/**
 * 服务端异常
 */
public class ServiceException extends AbstractException {

    public ServiceException(String message) {
        this(message, null, BaseErrorCode.SERVICE_ERROR);
    }

    public ServiceException(IErrorCode errorCode) {
        this(null, errorCode);
    }

    public ServiceException(String message, IErrorCode errorCode) {
        this(message, null, errorCode);
    }

    public ServiceException(String message, Throwable throwable, IErrorCode errorCode) {
        super(Optional.ofNullable(message).orElse(errorCode.message()), throwable, errorCode);
    }

    public static void cast(IErrorCode code) {
        throw new ServiceException(code);
    }

    public static void cast(String message) {
        throw new ServiceException(message, null, BaseErrorCode.SERVICE_ERROR);
    }

    @Override
    public String toString() {
        return "ServiceException{" +
                "code='" + errorCode + "'," +
                "message='" + errorMessage + "'" +
                '}';
    }
}
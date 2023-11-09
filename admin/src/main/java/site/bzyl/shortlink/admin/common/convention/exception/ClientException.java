package site.bzyl.shortlink.admin.common.convention.exception;

import site.bzyl.shortlink.admin.common.convention.errorcode.BaseErrorCode;
import site.bzyl.shortlink.admin.common.convention.errorcode.IErrorCode;

/**
 * 客户端异常
 */
public class ClientException extends AbstractException {

    public ClientException(IErrorCode errorCode) {
        this(null, null, errorCode);
    }

    public ClientException(String message) {
        this(message, null, BaseErrorCode.CLIENT_ERROR);
    }

    public ClientException(String message, IErrorCode errorCode) {
        this(message, null, errorCode);
    }

    public ClientException(String message, Throwable throwable, IErrorCode errorCode) {
        super(message, throwable, errorCode);
    }

    public static void cast(IErrorCode code) {
        throw new ClientException(code);
    }
    public static void cast(String message) {
        throw new ClientException(message, null, BaseErrorCode.CLIENT_ERROR);
    }


    @Override
    public String toString() {
        return "ClientException{" +
                "code='" + errorCode + "'," +
                "message='" + errorMessage + "'" +
                '}';
    }
}
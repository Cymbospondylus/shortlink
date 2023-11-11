package site.bzyl.shortlink.project.common.convention.exception;


import site.bzyl.shortlink.project.common.convention.errorcode.BaseErrorCode;
import site.bzyl.shortlink.project.common.convention.errorcode.IErrorCode;

/**
 * 远程服务调用异常
 */
public class RemoteException extends AbstractException {

    public RemoteException(IErrorCode errorCode) {
        this(null, null, errorCode);
    }
    public RemoteException(String message) {
        this(message, null, BaseErrorCode.REMOTE_ERROR);
    }

    public RemoteException(String message, IErrorCode errorCode) {
        this(message, null, errorCode);
    }

    public RemoteException(String message, Throwable throwable, IErrorCode errorCode) {
        super(message, throwable, errorCode);
    }

    public static void cast(IErrorCode code) {
        throw new RemoteException(code);
    }

    public static void cast(String message) {
        throw new RemoteException(message, null, BaseErrorCode.REMOTE_ERROR);
    }

    @Override
    public String toString() {
        return "RemoteException{" +
                "code='" + errorCode + "'," +
                "message='" + errorMessage + "'" +
                '}';
    }
}
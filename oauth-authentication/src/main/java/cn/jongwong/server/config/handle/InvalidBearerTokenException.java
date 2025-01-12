package cn.jongwong.server.config.handle;

public class InvalidBearerTokenException extends RuntimeException {
    public InvalidBearerTokenException(String message) {
        super(message);
    }

    public InvalidBearerTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}
package org.eveforge.config;

/**
 * 业务异常类
 */
public class BusiException extends RuntimeException {
    private int code;
    private String message;

    public BusiException(int code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public BusiException(String message) {
        super(message);
        this.code = 500;
        this.message = message;
    }

    public BusiException(String message, Throwable cause) {
        super(message, cause);
        this.code = 500;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
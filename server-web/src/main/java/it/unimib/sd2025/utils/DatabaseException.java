package it.unimib.sd2025.utils;

public class DatabaseException extends Exception {

    private final int errorCode;

    public DatabaseException(String message, int errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }
}

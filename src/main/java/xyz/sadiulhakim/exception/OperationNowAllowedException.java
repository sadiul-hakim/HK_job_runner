package xyz.sadiulhakim.exception;

public class OperationNowAllowedException extends RuntimeException {
    public OperationNowAllowedException(String message) {
        super(message);
    }
}

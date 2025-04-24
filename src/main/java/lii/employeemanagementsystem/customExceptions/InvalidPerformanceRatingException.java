package lii.employeemanagementsystem.customExceptions;

public class InvalidPerformanceRatingException extends RuntimeException {
    public InvalidPerformanceRatingException(String message) {
        super(message);
    }

    public InvalidPerformanceRatingException(String message, Throwable cause) {
        super(message, cause);
    }
}
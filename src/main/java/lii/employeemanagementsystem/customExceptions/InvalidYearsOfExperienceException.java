package lii.employeemanagementsystem.customExceptions;

public class InvalidYearsOfExperienceException extends RuntimeException {
    public InvalidYearsOfExperienceException(String message) {
        super(message);
    }

    public InvalidYearsOfExperienceException(String message, Throwable cause) {
        super(message, cause);
    }
}
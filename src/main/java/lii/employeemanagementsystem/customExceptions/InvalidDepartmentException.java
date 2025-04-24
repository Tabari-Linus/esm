package lii.employeemanagementsystem.customExceptions;

public class InvalidDepartmentException extends RuntimeException{

    public InvalidDepartmentException(String message) {
        super(message);
    }

    public InvalidDepartmentException(String message, Throwable cause) {
        super(message, cause);
    }
}

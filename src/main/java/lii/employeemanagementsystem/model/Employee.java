package lii.employeemanagementsystem.model;

import javafx.beans.property.*;
import lii.employeemanagementsystem.customExceptions.InvalidDepartmentException;
import lii.employeemanagementsystem.customExceptions.InvalidPerformanceRatingException;
import lii.employeemanagementsystem.customExceptions.InvalidSalaryException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public class Employee<T> implements Comparable<Employee<T>> {
    private final ObjectProperty<T> employeeId = new SimpleObjectProperty<>();
    private final StringProperty name = new SimpleStringProperty();
    private final StringProperty department = new SimpleStringProperty();
    private final DoubleProperty salary = new SimpleDoubleProperty();
    private final DoubleProperty performanceRating = new SimpleDoubleProperty();
    private final IntegerProperty yearsOfExperience = new SimpleIntegerProperty();
    private final BooleanProperty active = new SimpleBooleanProperty();
    private final StringProperty profilePicture = new SimpleStringProperty();

    // Employee Constructor
    public Employee(T employeeId, String name, String department, double salary, double performanceRating,
                    int yearsOfExperience, boolean active, String profilePicture) {
        this.employeeId.set(employeeId);
        this.name.set(name);
        this.department.set(department);
        this.salary.set(salary);
        this.performanceRating.set(roundToOneDecimalPlace(performanceRating));
        this.yearsOfExperience.set(yearsOfExperience);
        this.active.set(active);
        this.profilePicture.set(profilePicture);
    }

    private double roundToOneDecimalPlace(double value) {
        return BigDecimal.valueOf(value)
                .setScale(1, RoundingMode.HALF_UP)
                .doubleValue();
    }
    // Getters and Setters
    public ObjectProperty<T> employeeIdProperty() {
        return employeeId;
    }

    public StringProperty nameProperty() {
        return name;
    }

    public StringProperty departmentProperty() {
        return department;
    }

    public DoubleProperty salaryProperty() {
        return salary;
    }

    public DoubleProperty performanceRatingProperty() {
        return performanceRating;
    }

    public IntegerProperty yearsOfExperienceProperty() {
        return yearsOfExperience;
    }

    public BooleanProperty activeProperty() {
        return active;
    }

    public StringProperty profilePictureProperty() {
        return profilePicture;
    }

    public T getEmployeeId() {
        return employeeId.get();
    }

    public void setEmployeeId(T employeeId) {
        this.employeeId.set(employeeId);
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty.");
        }
        this.name.set(name);
    }

    public String getDepartment() {
        return department.get();
    }

    public void setDepartment(String department) {
        if (department == null || department.trim().isEmpty()) {
            throw new InvalidDepartmentException("Department cannot be null or empty.");
        }

        this.department.set(department);
    }

    public double getSalary() {
        return roundToOneDecimalPlace(salary.get());
    }

    public void setSalary(double salary) {
        if (salary < 0) {
            throw new InvalidSalaryException("Salary cannot be negative.");
        }
        this.salary.set(salary);
    }


    public double getPerformanceRating() {
        return performanceRating.get();
    }

    public void setPerformanceRating(double performanceRating) {
        if (performanceRating < 0 || performanceRating > 5) {
            throw new InvalidPerformanceRatingException("Performance rating must be between 0 and 5.");
        }
        this.performanceRating.set(roundToOneDecimalPlace(performanceRating));
    }

    public int getYearsOfExperience() {
        return yearsOfExperience.get();
    }

    public void setYearsOfExperience(int yearsOfExperience) {
        this.yearsOfExperience.set(yearsOfExperience);
    }

    public boolean isActive() {
        return active.get();
    }

    public void setActive(boolean active) {
        this.active.set(active);
    }

    public String getProfilePicture() {
        return profilePicture.get();
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture.set(profilePicture);
    }

    // Implement Comparable to sort by years of experience (descending order)
    @Override
    public int compareTo(Employee<T> other) {
        return Integer.compare(other.yearsOfExperience.get(), this.yearsOfExperience.get());
    }

    // Override equals and hashCode for proper comparison and usage in collections
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Employee<?> employee = (Employee<?>) o;
        return Objects.equals(employeeId, employee.employeeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(employeeId);
    }

    // Override toString for better readability
    @Override
    public String toString() {
        return "Employee{" +
                "employeeId=" + employeeId +
                ", name='" + name + '\'' +
                ", department='" + department + '\'' +
                ", salary=" + salary +
                ", performanceRating=" + performanceRating +
                ", yearsOfExperience=" + yearsOfExperience +
                ", isActive=" + active +
                ", profilePicture='" + profilePicture + '\'' +
                '}';
    }
}
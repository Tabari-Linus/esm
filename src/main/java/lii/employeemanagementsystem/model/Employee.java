package lii.employeemanagementsystem.model;

import javafx.beans.property.*;

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

    // Constructor
    public Employee(T employeeId, String name, String department, double salary, double performanceRating,
                    int yearsOfExperience, boolean active, String profilePicture) {
        this.employeeId.set(employeeId);
        this.name.set(name);
        this.department.set(department);
        this.salary.set(salary);
        this.performanceRating.set(performanceRating);
        this.yearsOfExperience.set(yearsOfExperience);
        this.active.set(active);
        this.profilePicture.set(profilePicture);
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
        this.name.set(name);
    }

    public String getDepartment() {
        return department.get();
    }

    public void setDepartment(String department) {
        this.department.set(department);
    }

    public double getSalary() {
        return salary.get();
    }

    public void setSalary(double salary) {
        this.salary.set(salary);
    }

    public double getPerformanceRating() {
        return performanceRating.get();
    }

    public void setPerformanceRating(double performanceRating) {
        this.performanceRating.set(performanceRating);
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
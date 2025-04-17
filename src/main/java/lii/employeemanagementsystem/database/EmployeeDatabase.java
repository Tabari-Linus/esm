package lii.employeemanagementsystem.database;

import lii.employeemanagementsystem.model.Employee;

import java.util.*;
import java.util.stream.Collectors;

public class EmployeeDatabase<T> {
    private final Map<T, Employee<T>> employeeMap = new HashMap<>();

    // Add a new employee
    public void addEmployee(Employee<T> employee) {
        if (employeeMap.containsKey(employee.getEmployeeId())) {
            throw new IllegalArgumentException("Employee with ID " + employee.getEmployeeId() + " already exists.");
        }
        employeeMap.put(employee.getEmployeeId(), employee);
    }

    // Remove an employee by ID
    public void removeEmployee(T employeeId) {
        if (!employeeMap.containsKey(employeeId)) {
            throw new IllegalArgumentException("Employee with ID " + employeeId + " does not exist.");
        }
        employeeMap.remove(employeeId);
    }

    // Update an employee's details dynamically
    public void updateEmployeeDetails(T employeeId, String field, Object newValue) {
        Employee<T> employee = employeeMap.get(employeeId);
        if (employee == null) {
            throw new IllegalArgumentException("Employee with ID " + employeeId + " does not exist.");
        }

        switch (field.toLowerCase()) {
            case "name":
                employee.setName((String) newValue);
                break;
            case "department":
                employee.setDepartment((String) newValue);
                break;
            case "salary":
                employee.setSalary((Double) newValue);
                break;
            case "performancerating":
                employee.setPerformanceRating((Double) newValue);
                break;
            case "yearsofexperience":
                employee.setYearsOfExperience((Integer) newValue);
                break;
            case "isactive":
                employee.setActive((Boolean) newValue);
                break;
            case "profilepicture":
                employee.setProfilePicture((String) newValue);
                break;
            default:
                throw new IllegalArgumentException("Invalid field: " + field);
        }
    }

    // Method to give a salary raise to employees with high performance ratings
    public void giveSalaryRaise(double percentage, double minRating) {
        employeeMap.values().stream()
                .filter(employee -> employee.getPerformanceRating() >= minRating)
                .forEach(employee -> {
                    double newSalary = employee.getSalary() * (1 + percentage / 100);
                    employee.setSalary(newSalary);
                });
    }

    // Method to retrieve the top 5 highest-paid employees
    public List<Employee<T>> getTop5HighestPaidEmployees() {
        return employeeMap.values().stream()
                .sorted(Comparator.comparingDouble(Employee<T>::getSalary).reversed())
                .limit(5)
                .collect(Collectors.toList());
    }

    // Method to calculate the average salary of employees in a specific department
    public double calculateAverageSalaryByDepartment(String department) {
        return employeeMap.values().stream()
                .filter(employee -> employee.getDepartment().equalsIgnoreCase(department))
                .mapToDouble(Employee::getSalary)
                .average()
                .orElse(0.0); // Return 0.0 if no employees are found in the department
    }

    // Retrieve all employees
    public Collection<Employee<T>> getAllEmployees() {
        return employeeMap.values();
    }

    // Search employees by department
    public List<Employee<T>> searchByDepartment(String department) {
        return employeeMap.values().stream()
                .filter(employee -> employee.getDepartment().equalsIgnoreCase(department))
                .collect(Collectors.toList());
    }

    // Search employees by name (contains search term, case-insensitive)
    public List<Employee<T>> searchByName(String searchTerm) {
        return employeeMap.values().stream()
                .filter(employee -> employee.getName().toLowerCase().contains(searchTerm.toLowerCase()))
                .collect(Collectors.toList());
    }

    // Search employees by minimum performance rating
    public List<Employee<T>> searchByMinimumPerformanceRating(double minRating) {
        return employeeMap.values().stream()
                .filter(employee -> employee.getPerformanceRating() >= minRating)
                .collect(Collectors.toList());
    }

    // Search employees by salary range
    public List<Employee<T>> searchBySalaryRange(double minSalary, double maxSalary) {
        return employeeMap.values().stream()
                .filter(employee -> employee.getSalary() >= minSalary && employee.getSalary() <= maxSalary)
                .collect(Collectors.toList());
    }

    // Iterator to traverse all employees
    public Iterator<Employee<T>> getEmployeeIterator() {
        return employeeMap.values().iterator();
    }


    // Display employees using a for-each loop
    public void displayEmployeesForEach() {
        System.out.println("Employee Details (For-Each Loop):");
        System.out.println("------------------------------------------------------------");
        for (Employee<T> employee : employeeMap.values()) {
            System.out.printf("ID: %-10s Name: %-15s Department: %-10s Salary: $%-10.2f%n",
                    employee.getEmployeeId(), employee.getName(), employee.getDepartment(), employee.getSalary());
        }
        System.out.println("------------------------------------------------------------");
    }

    // Display employees using Stream API
    public void displayEmployeesStream() {
        System.out.println("Employee Details (Stream API):");
        System.out.println("------------------------------------------------------------");
        String report = employeeMap.values().stream()
                .map(employee -> String.format("ID: %-10s Name: %-15s Department: %-10s Salary: $%-10.2f",
                        employee.getEmployeeId(), employee.getName(), employee.getDepartment(), employee.getSalary()))
                .collect(Collectors.joining("\n"));
        System.out.println(report);
        System.out.println("------------------------------------------------------------");
    }
}
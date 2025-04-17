package lii.employeemanagementsystem.model;

import java.util.Comparator;

public class EmployeeSalaryComparator<T> implements Comparator<Employee<T>> {
    @Override
    public int compare(Employee<T> e1, Employee<T> e2) {
        return Double.compare(e2.getSalary(), e1.getSalary()); // Descending order
    }
}
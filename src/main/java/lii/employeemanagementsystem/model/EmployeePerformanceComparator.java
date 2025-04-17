package lii.employeemanagementsystem.model;

import java.util.Comparator;

public class EmployeePerformanceComparator<T> implements Comparator<Employee<T>> {
    @Override
    public int compare(Employee<T> e1, Employee<T> e2) {
        return Double.compare(e2.getPerformanceRating(), e1.getPerformanceRating()); // Descending order
    }
}
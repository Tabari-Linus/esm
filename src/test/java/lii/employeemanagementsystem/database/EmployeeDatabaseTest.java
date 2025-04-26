package lii.employeemanagementsystem.database;

import lii.employeemanagementsystem.customExceptions.*;
import lii.employeemanagementsystem.model.Employee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class EmployeeDatabaseTest {
    private EmployeeDatabase<UUID> employeeDatabase;
    private Employee<UUID> employee1;
    private Employee<UUID> employee2;

    @BeforeEach
    void setUp() {
        employeeDatabase = new EmployeeDatabase<>();
        employee1 = new Employee<>(UUID.randomUUID(), "Dan Mark", "IT", 50000.0, 5.0, 4, true, null);
        employee2 = new Employee<>(UUID.randomUUID(), "Owusu Dennis", "HR", 60000.0, 7.0, 4, true, null);
    }

    @Test
    void addEmployeeValidEmployeeTest() {
        employeeDatabase.addEmployee(employee1);
        employeeDatabase.addEmployee(new Employee<>(UUID.randomUUID(), "Ben Johnson", "IT", 1000.0, 5.0, 4, true, null));
        assertEquals(2, employeeDatabase.getAllEmployees().size());

    }

    @Test
    void addEmployeeInvalidSalaryTest() {
        Employee<UUID> invalidEmployee = new Employee<>(UUID.randomUUID(), "Ben Johnson", "IT", -1000.0, 5.0, 4, true, null);
        assertThrows(InvalidSalaryException.class, () -> employeeDatabase.addEmployee(invalidEmployee));
    }

    @Test
    void addEmployeeDuplicateEmployeeTest() {
        employeeDatabase.addEmployee(employee1);
        assertThrows(IllegalArgumentException.class, () -> employeeDatabase.addEmployee(employee1));
    }

    @Test
    void removeEmployeeValidEmployeeTest() {
        employeeDatabase.addEmployee(employee1);
        employeeDatabase.removeEmployee(employee1.getEmployeeId());
        assertEquals(0, employeeDatabase.getAllEmployees().size());
    }

    @Test
    void removeEmployeeNonExistentEmployeeTest() {
        assertThrows(EmployeeNotFoundException.class, () -> employeeDatabase.removeEmployee(UUID.randomUUID()));
    }

    @Test
    void updateEmployeeDetailsValidUpdateTest() {
        employeeDatabase.addEmployee(employee1);
        employeeDatabase.updateEmployeeDetails(employee1.getEmployeeId(), "name", "Dan Markatte");
        assertEquals("Dan Markatte", employee1.getName());
    }

    @Test
    void updateEmployeeDetailsInvalidFieldTest() {
        employeeDatabase.addEmployee(employee1);
        assertThrows(IllegalArgumentException.class, () -> employeeDatabase.updateEmployeeDetails(employee1.getEmployeeId(), "Address", "Ayeduase"));
    }

    @Test
    void giveSalaryRaiseValidRaiseTest() {
        employeeDatabase.addEmployee(employee1);
        employeeDatabase.giveSalaryRaise(10, 4.0);
        assertEquals(55000.0, employee1.getSalary());
    }

    @Test
    void giveSalaryRaiseInvalidPercentageTest() {
        assertThrows(IllegalArgumentException.class, () -> employeeDatabase.giveSalaryRaise(-10, 4.0));
    }

    @Test
    void getTop5HighestPaidEmployeesTest() {
        employeeDatabase.addEmployee(employee1);
        employeeDatabase.addEmployee(employee2);
        List<Employee<UUID>> top5 = employeeDatabase.getTop5HighestPaidEmployees();
        assertEquals(2, top5.size());
        assertEquals(employee2, top5.getFirst());
    }

    @Test
    void calculateAverageSalaryByDepartmentValidDepartmentTest() {
        employeeDatabase.addEmployee(employee1);
        employeeDatabase.addEmployee(employee2);
        double averageSalary = employeeDatabase.calculateAverageSalaryByDepartment("IT");
        assertEquals(50000.0, averageSalary);
    }

    @Test
    void calculateAverageSalaryByDepartmentInvalidDepartmentTest() {
        assertThrows(InvalidDepartmentException.class, () -> employeeDatabase.calculateAverageSalaryByDepartment("Admin"));
    }

    @Test
    void searchByTermValidSearchTest() {
        employeeDatabase.addEmployee(employee1);
        employeeDatabase.addEmployee(employee2);
        List<Employee<UUID>> results = employeeDatabase.searchByTerm("Dan");
        assertEquals(1, results.size());
        assertEquals(employee1, results.getFirst());
    }

    @Test
    void searchByTermNullSearchTermTest() {
        assertThrows(IllegalArgumentException.class, () -> employeeDatabase.searchByTerm(null));
    }

    @Test
    void searchBySalaryRangeValidRangeTest() {
        employeeDatabase.addEmployee(employee1);
        employeeDatabase.addEmployee(employee2);
        List<Employee<UUID>> results = employeeDatabase.searchBySalaryRange(40000.0, 55000.0);
        assertEquals(1, results.size());
        assertEquals(employee1, results.getFirst());
    }

    @Test
    void searchBySalaryRangeInvalidRangeTest() {
        assertThrows(InvalidSalaryException.class, () -> employeeDatabase.searchBySalaryRange(60000.0, 50000.0));
    }
}
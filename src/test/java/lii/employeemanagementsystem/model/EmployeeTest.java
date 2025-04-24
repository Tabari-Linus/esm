package lii.employeemanagementsystem.model;

import lii.employeemanagementsystem.customExceptions.InvalidDepartmentException;
import lii.employeemanagementsystem.customExceptions.InvalidPerformanceRatingException;
import lii.employeemanagementsystem.customExceptions.InvalidSalaryException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class EmployeeTest {

    @Test
    void constructorValidInputsTest() {
        UUID employeeId = UUID.randomUUID();
        Employee<UUID> employee = new Employee<>(employeeId, "David Damptey", "IT", 50000.0, 4.5, 5, true, "profile.jpg");
        assertEquals(employeeId, employee.getEmployeeId());
        assertEquals("David Damptey", employee.getName());
        assertEquals("IT", employee.getDepartment());
        assertEquals(50000.0, employee.getSalary());
        assertEquals(4.5, employee.getPerformanceRating());
        assertEquals(5, employee.getYearsOfExperience());
        assertTrue(employee.isActive());
        assertEquals("profile.jpg", employee.getProfilePicture());
    }

    @Test
    void setNameInvalidNameTest() {
        Employee<UUID> employee = new Employee<>(UUID.randomUUID(), "David Damptey", "IT", 50000.0, 4.5, 5, true, "profile.jpg");
        assertThrows(IllegalArgumentException.class, () -> employee.setName(null));
        assertThrows(IllegalArgumentException.class, () -> employee.setName(""));
    }

    @Test
    void setDepartmentInvalidDepartmentTest() {
        Employee<UUID> employee = new Employee<>(UUID.randomUUID(), "David Damptey", "IT", 50000.0, 4.5, 5, true, "profile.jpg");
        assertThrows(InvalidDepartmentException.class, () -> employee.setDepartment(null));
        assertThrows(InvalidDepartmentException.class, () -> employee.setDepartment(""));
    }

    @Test
    void setSalaryInvalidSalaryTest() {
        Employee<UUID> employee = new Employee<>(UUID.randomUUID(), "David Damptey", "IT", 50000.0, 4.5, 5, true, "profile.jpg");
        assertThrows(InvalidSalaryException.class, () -> employee.setSalary(-1000.0));
    }

    @Test
    void setPerformanceRatingInvalidRatingTest() {
        Employee<UUID> employee = new Employee<>(UUID.randomUUID(), "David Damptey", "IT", 50000.0, 4.5, 5, true, "profile.jpg");
        assertThrows(InvalidPerformanceRatingException.class, () -> employee.setPerformanceRating(-1.0));
        assertThrows(InvalidPerformanceRatingException.class, () -> employee.setPerformanceRating(6.0));
    }

    @Test
    void compareToTest() {
        Employee<UUID> employee1 = new Employee<>(UUID.randomUUID(), "David Damptey", "IT", 50000.0, 4.5, 5, true, "profile.jpg");
        Employee<UUID> employee2 = new Employee<>(UUID.randomUUID(), "Jane Doe", "HR", 60000.0, 4.0, 10, true, "profile2.jpg");
        assertTrue(employee1.compareTo(employee2) > 0);
        assertTrue(employee2.compareTo(employee1) < 0);
    }


}
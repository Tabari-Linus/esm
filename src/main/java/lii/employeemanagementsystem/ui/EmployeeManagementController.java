package lii.employeemanagementsystem.ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import lii.employeemanagementsystem.database.EmployeeDatabase;
import lii.employeemanagementsystem.model.Employee;
import lii.employeemanagementsystem.service.UniqueIdGenerator;

import java.util.UUID;
import java.io.File;

public class EmployeeManagementController {

    @FXML
    private TableView<Employee<UUID>> employeeTable;
    @FXML
    private TableColumn<Employee<Integer>, Integer> idColumn;
    @FXML
    private TableColumn<Employee<Integer>, String> nameColumn;
    @FXML
    private TableColumn<Employee<Integer>, String> departmentColumn;
    @FXML
    private TableColumn<Employee<Integer>, Double> salaryColumn;
    @FXML
    private TableColumn<Employee<Integer>, Double> ratingColumn;
    @FXML
    private Label statusLabel;

    private final EmployeeDatabase<UUID> employeeDatabase = new EmployeeDatabase<>();
    private final ObservableList<Employee<UUID>> employees = FXCollections.observableArrayList();
    private final ListView<Employee<UUID>> employeeListView = new ListView<>();
    private final ComboBox<String> sortOptions = new ComboBox<>();

    @FXML
    public void initialize() {
        // Bind table columns to Employee properties
        idColumn.setCellValueFactory(data -> data.getValue().employeeIdProperty());
        nameColumn.setCellValueFactory(data -> data.getValue().nameProperty());
        departmentColumn.setCellValueFactory(data -> data.getValue().departmentProperty());
        salaryColumn.setCellValueFactory(data -> data.getValue().salaryProperty().asObject());
        ratingColumn.setCellValueFactory(data -> data.getValue().performanceRatingProperty().asObject());

        // Set the table's items
        employeeTable.setItems(employees);

        // Load initial data
        loadSampleData();
    }

    private void loadSampleData() {
        Employee<UUID> emp1 = new Employee<>(UniqueIdGenerator.generateUniqueId(), "Alice Johnson", "IT", 75000, 4.3, 5, true, "file:images/alice.jpg");
        Employee<UUID> emp2 = new Employee<>(UniqueIdGenerator.generateUniqueId(), "Bob Smith", "Finance", 88000, 3.9, 8, true, "file:images/bob.jpg");
        Employee<UUID> emp3 = new Employee<>(UniqueIdGenerator.generateUniqueId(), "Clara Lee", "HR", 62000, 4.7, 3, true, "file:images/clara.jpg");

        employeeDatabase.addEmployee(emp1);
        employeeDatabase.addEmployee(emp2);
        employeeDatabase.addEmployee(emp3);

        employees.setAll(employeeDatabase.getAllEmployees());
    }

    @FXML
    private void onAddEmployee() {
        showAddDialog();
        // Logic to add a new employee (e.g., show a dialog to input details)
        statusLabel.setText("Add Employee clicked.");
    }

    @FXML
    private void onUpdateEmployee() {
        // Logic to update selected employee details
        Employee<UUID> selectedEmployee = employeeTable.getSelectionModel().getSelectedItem();
        if (selectedEmployee != null) {
            statusLabel.setText("Update Employee: " + selectedEmployee.getName());
        } else {
            statusLabel.setText("No employee selected for update.");
        }
    }

    @FXML
    private void onRemoveEmployee() {
        // Logic to remove the selected employee
        Employee<UUID> selectedEmployee = employeeTable.getSelectionModel().getSelectedItem();
        if (selectedEmployee != null) {
            employeeDatabase.removeEmployee(selectedEmployee.getEmployeeId());
            employees.remove(selectedEmployee);
            statusLabel.setText("Removed Employee: " + selectedEmployee.getName());
        } else {
            statusLabel.setText("No employee selected for removal.");
        }
    }

    @FXML
    private void onRaiseSalary() {
        // Logic to raise salary for employees with high performance
        employeeDatabase.giveSalaryRaise(10, 4.5);
        employees.setAll(employeeDatabase.getAllEmployees());
        statusLabel.setText("Raised salary for high-performing employees.");
    }


    private void showAddDialog() {
        Stage dialog = new Stage();
        dialog.setTitle("Add New Employee");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10));
        grid.setHgap(10);
        grid.setVgap(10);

        TextField nameField = new TextField();
        TextField deptField = new TextField();
        TextField salaryField = new TextField();
        TextField ratingField = new TextField();
        TextField experienceField = new TextField();
        CheckBox activeBox = new CheckBox("Active");
        Button uploadBtn = new Button("Upload Image");
        final String[] imagePath = {"file:images/placeholder.jpg"};

        uploadBtn.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            File selected = fileChooser.showOpenDialog(dialog);
            if (selected != null) {
                imagePath[0] = selected.toURI().toString();
            }
        });

        Button saveBtn = new Button("Save");
        saveBtn.setOnAction(e -> {
            Employee<UUID> newEmp = new Employee<>(
                    UniqueIdGenerator.generateUniqueId(),
                    nameField.getText(),
                    deptField.getText(),
                    Double.parseDouble(salaryField.getText()),
                    Double.parseDouble(ratingField.getText()),
                    Integer.parseInt(experienceField.getText()),
                    activeBox.isSelected(),
                    imagePath[0]
            );
            employeeDatabase.addEmployee(newEmp);
            employees.setAll(employeeDatabase.getAllEmployees());
            dialog.close();
        });

        grid.addRow(0, new Label("Name:"), nameField);
        grid.addRow(1, new Label("Department:"), deptField);
        grid.addRow(2, new Label("Salary:"), salaryField);
        grid.addRow(3, new Label("Performance Rating:"), ratingField);
        grid.addRow(4, new Label("Experience (yrs):"), experienceField);
        grid.addRow(5, activeBox, uploadBtn);
        grid.add(saveBtn, 1, 6);

        Scene scene = new Scene(grid, 400, 300);
        dialog.setScene(scene);
        dialog.show();
    }
}
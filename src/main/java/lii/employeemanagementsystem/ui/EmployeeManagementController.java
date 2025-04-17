package lii.employeemanagementsystem.ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lii.employeemanagementsystem.database.EmployeeDatabase;
import lii.employeemanagementsystem.model.Employee;
import lii.employeemanagementsystem.service.UniqueIdGenerator;

import java.io.File;
import java.util.*;

public class EmployeeManagementController {

    @FXML
    private TableView<Employee<UUID>> employeeTable;
    @FXML
    private TableColumn<Employee<UUID>, UUID> idColumn;
    @FXML
    private TableColumn<Employee<UUID>, String> nameColumn;
    @FXML
    private TableColumn<Employee<UUID>, String> departmentColumn;
    @FXML
    private TableColumn<Employee<UUID>, Double> salaryColumn;
    @FXML
    private TableColumn<Employee<UUID>, Double> ratingColumn;
    @FXML
    private TableColumn<Employee<UUID>, Integer> experienceColumn;
    @FXML
    private TextField searchField;
    @FXML
    private ComboBox<String> sortOptions;
    @FXML
    private Label statusLabel;

    private final EmployeeDatabase<UUID> employeeDatabase = new EmployeeDatabase<>();
    private final ObservableList<Employee<UUID>> employees = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Bind table columns to Employee properties
        idColumn.setCellValueFactory(data -> data.getValue().employeeIdProperty());
        nameColumn.setCellValueFactory(data -> data.getValue().nameProperty());
        departmentColumn.setCellValueFactory(data -> data.getValue().departmentProperty());
        salaryColumn.setCellValueFactory(data -> data.getValue().salaryProperty().asObject());
        ratingColumn.setCellValueFactory(data -> data.getValue().performanceRatingProperty().asObject());
        experienceColumn.setCellValueFactory(data -> data.getValue().yearsOfExperienceProperty().asObject());

        // Set the table's items
        employeeTable.setItems(employees);

        // Initialize sort options
        sortOptions.setItems(FXCollections.observableArrayList(
                "Sort by Experience",
                "Sort by Salary",
                "Sort by Performance Rating"
        ));
        sortOptions.setValue("Sort by Experience");
        sortOptions.setOnAction(e -> sortEmployees());

        // Add search functionality
        searchField.textProperty().addListener((obs, oldVal, newVal) -> filterEmployees(newVal));

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
        Stage dialog = new Stage();
        dialog.setTitle("Add New Employee");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField nameField = new TextField();
        TextField deptField = new TextField();
        TextField salaryField = new TextField();
        TextField experienceField = new TextField();
        CheckBox activeBox = new CheckBox("Active");
        ChoiceBox<Double> ratingChoiceBox = new ChoiceBox<>(FXCollections.observableArrayList(0.0, 1.0, 2.0, 3.0, 4.0, 5.0));
        ratingChoiceBox.setValue(0.0);

        Button uploadBtn = new Button("Upload Image");
        final String[] imagePath = {"file:images/placeholder.jpg"};
        uploadBtn.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            File selectedFile = fileChooser.showOpenDialog(dialog);
            if (selectedFile != null) {
                imagePath[0] = selectedFile.toURI().toString();
            }
        });

        Button saveBtn = new Button("Save");
        saveBtn.setOnAction(e -> {
            try {
                Employee<UUID> newEmployee = new Employee<>(
                        UniqueIdGenerator.generateUniqueId(),
                        nameField.getText(),
                        deptField.getText(),
                        Double.parseDouble(salaryField.getText()),
                        ratingChoiceBox.getValue(),
                        Integer.parseInt(experienceField.getText()),
                        activeBox.isSelected(),
                        imagePath[0]
                );
                employeeDatabase.addEmployee(newEmployee);
                employees.setAll(employeeDatabase.getAllEmployees());
                statusLabel.setText("Employee added successfully.");
                dialog.close();
            } catch (NumberFormatException ex) {
                statusLabel.setText("Invalid input. Please check the fields.");
            }
        });

        grid.addRow(0, new Label("Name:"), nameField);
        grid.addRow(1, new Label("Department:"), deptField);
        grid.addRow(2, new Label("Salary:"), salaryField);
        grid.addRow(3, new Label("Experience (yrs):"), experienceField);
        grid.addRow(4, new Label("Performance Rating:"), ratingChoiceBox);
        grid.addRow(5, activeBox, uploadBtn);
        grid.add(saveBtn, 1, 6);

        Scene scene = new Scene(grid, 400, 300);
        dialog.setScene(scene);
        dialog.show();
    }

    @FXML
    private void onDeleteById() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Delete Employee");
        dialog.setHeaderText("Enter the Employee ID to delete:");
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(idStr -> {
            try {
                UUID id = UUID.fromString(idStr);
                employeeDatabase.removeEmployee(id);
                employees.setAll(employeeDatabase.getAllEmployees());
                statusLabel.setText("Employee deleted successfully.");
            } catch (IllegalArgumentException e) {
                statusLabel.setText("Invalid UUID format.");
            }
        });
    }

    private void sortEmployees() {
        String selected = sortOptions.getValue();
        List<Employee<UUID>> sorted = new ArrayList<>(employees);

        if (selected.equals("Sort by Salary")) {
            sorted.sort(Comparator.comparingDouble(Employee<UUID>::getSalary).reversed());
        } else if (selected.equals("Sort by Performance Rating")) {
            sorted.sort(Comparator.comparingDouble(Employee<UUID>::getPerformanceRating).reversed());
        } else {
            sorted.sort(Comparator.comparingInt(Employee<UUID>::getYearsOfExperience).reversed());
        }

        employees.setAll(sorted);
    }

    private void filterEmployees(String keyword) {
        keyword = keyword.toLowerCase();
        List<Employee<UUID>> filtered = new ArrayList<>();
        for (Employee<UUID> emp : employeeDatabase.getAllEmployees()) {
            if (emp.getName().toLowerCase().contains(keyword) || emp.getDepartment().toLowerCase().contains(keyword)) {
                filtered.add(emp);
            }
        }
        employees.setAll(filtered);
    }
}
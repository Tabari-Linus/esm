package lii.employeemanagementsystem.ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lii.employeemanagementsystem.database.EmployeeDatabase;
import lii.employeemanagementsystem.model.Employee;
import lii.employeemanagementsystem.model.EmployeePerformanceComparator;
import lii.employeemanagementsystem.model.EmployeeSalaryComparator;
import lii.employeemanagementsystem.service.UniqueIdGenerator;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class EmployeeManagementController {

    @FXML
    private TableView<Employee<UUID>> employeeTable;
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
    private TableColumn<Employee<UUID>, Void> actionColumn;
    @FXML
    private TextField searchField;
    @FXML
    private ComboBox<String> sortOptions;
    @FXML
    private Label statusLabel;
    @FXML
    private RadioButton filterByRating;
    @FXML
    private RadioButton filterBySalary;
    @FXML
    private Slider ratingFilterSlider;
    @FXML
    private HBox salaryFilterBox;
    @FXML
    private TextField minSalaryField;
    @FXML
    private TextField maxSalaryField;
    @FXML
    private ToggleGroup filterToggleGroup;
    @FXML
    private TextField raisePercentageField;
    @FXML
    private TextField departmentField;
    @FXML
    private TextField minRatingField;



    private final EmployeeDatabase<UUID> employeeDatabase = new EmployeeDatabase<>();
    private final ObservableList<Employee<UUID>> employees = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        nameColumn.setCellValueFactory(data -> data.getValue().nameProperty());
        departmentColumn.setCellValueFactory(data -> data.getValue().departmentProperty());
        salaryColumn.setCellValueFactory(data -> data.getValue().salaryProperty().asObject());
        ratingColumn.setCellValueFactory(data -> data.getValue().performanceRatingProperty().asObject());
        experienceColumn.setCellValueFactory(data -> data.getValue().yearsOfExperienceProperty().asObject());

        employeeTable.setItems(employees);
        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button deleteButton = new Button("Delete");
            private final Button viewButton = new Button("View");

            {
                deleteButton.setOnAction(event -> {
                    Employee<UUID> employee = getTableView().getItems().get(getIndex());
                    confirmAndDeleteEmployee(employee);
                });

                viewButton.setOnAction(event -> {
                    Employee<UUID> employee = getTableView().getItems().get(getIndex());
                    showEmployeeDetails(employee);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox buttons = new HBox(10, viewButton, deleteButton);
                    setGraphic(buttons);
                }
            }
        });

        sortOptions.setItems(FXCollections.observableArrayList(
                "Sort by Experience",
                "Sort by Salary",
                "Sort by Performance Rating"
        ));
        sortOptions.setValue("Sort by Experience");
        sortOptions.setOnAction(e -> sortEmployees());

        searchField.textProperty().addListener((obs, oldVal, newVal) -> filterEmployees(newVal));

        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button deleteButton = new Button("Delete");
            private final Button viewButton = new Button("View");

            {
                deleteButton.setOnAction(event -> {
                    Employee<UUID> employee = getTableView().getItems().get(getIndex());
                    confirmAndDeleteEmployee(employee);
                });

                viewButton.setOnAction(event -> {
                    Employee<UUID> employee = getTableView().getItems().get(getIndex());
                    showEmployeeDetails(employee);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox buttons = new HBox(10, viewButton, deleteButton);
                    setGraphic(buttons);
                }
            }
        });

        filterToggleGroup = new ToggleGroup();
        filterByRating.setToggleGroup(filterToggleGroup);
        filterBySalary.setToggleGroup(filterToggleGroup);


        filterByRating.setOnAction(e -> {
            if (filterByRating.isSelected()) {
                salaryFilterBox.setVisible(false);
                ratingFilterSlider.setVisible(true);
            }
        });
        filterBySalary.setOnAction(e -> {
            if (filterBySalary.isSelected()) {
                salaryFilterBox.setVisible(true);
                ratingFilterSlider.setVisible(false);
            }
        });
        filterByRating.setSelected(true);
        salaryFilterBox.setVisible(false);
        ratingFilterSlider.setVisible(true);
        ratingFilterSlider.setMin(0);



        ratingFilterSlider.setOnMouseReleased(e -> onApplyFilter());
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

        Slider ratingSlider = new Slider(0, 5, 0);
        ratingSlider.setShowTickLabels(true);
        ratingSlider.setShowTickMarks(true);
        ratingSlider.setMajorTickUnit(1);
        ratingSlider.setBlockIncrement(0.1);

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
                        ratingSlider.getValue(),
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
        grid.addRow(4, new Label("Performance Rating:"), ratingSlider);
        grid.addRow(5, activeBox, uploadBtn);
        grid.add(saveBtn, 1, 6);

        VBox container = new VBox(grid);
        container.setPadding(new Insets(20)); // Add padding around the content

        Scene scene = new Scene(container, 400, 300);
        dialog.setScene(scene);
        dialog.show();
    }

    @FXML
    private void onApplyFilter() {
        try {
            // Initialize filtered list with all employees
            List<Employee<UUID>> filteredList = new ArrayList<>(employeeDatabase.getAllEmployees());

            // Apply rating filter if slider value is greater than 0
            double minRating = ratingFilterSlider.getValue();
            if (minRating > 0) {
                filteredList = employeeDatabase.searchByMinimumPerformanceRating(minRating);
            }

            // Apply salary filter if both fields are filled
            String minSalaryText = minSalaryField.getText();
            String maxSalaryText = maxSalaryField.getText();
            if (!minSalaryText.isEmpty() && !maxSalaryText.isEmpty()) {
                double minSalary = Double.parseDouble(minSalaryText);
                double maxSalary = Double.parseDouble(maxSalaryText);
                filteredList = filteredList.stream()
                        .filter(employee -> employeeDatabase.searchBySalaryRange(minSalary, maxSalary).contains(employee))
                        .collect(Collectors.toList());
            }

            // Update the table with the filtered list
            employees.setAll(filteredList);
            statusLabel.setText("Filters applied successfully.");
        } catch (NumberFormatException ex) {
            statusLabel.setText("Invalid input. Please check the salary fields.");
        }
    }


    private void confirmAndDeleteEmployee(Employee<UUID> employee) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText("Are you sure you want to delete " + employee.getName() + "?");
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            employeeDatabase.removeEmployee(employee.getEmployeeId());
            employees.setAll(employeeDatabase.getAllEmployees());
            statusLabel.setText("Employee deleted successfully.");
        }
    }

    private void showEmployeeDetails(Employee<UUID> employee) {
        Stage dialog = new Stage();
        dialog.setTitle("Employee Details");

        VBox card = new VBox(10);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: #f9f9f9; -fx-border-color: #ccc; -fx-border-radius: 10; -fx-background-radius: 10;");

        Label nameLabel = new Label("Name: " + employee.getName());
        Label deptLabel = new Label("Department: " + employee.getDepartment());
        Label salaryLabel = new Label("Salary: $" + employee.getSalary());
        Label ratingLabel = new Label("Performance Rating: " + employee.getPerformanceRating());
        Label experienceLabel = new Label("Experience: " + employee.getYearsOfExperience() + " years");
        Label activeLabel = new Label("Active: " + (employee.isActive() ? "Yes" : "No"));

        Button editButton = new Button("Edit");
        editButton.setOnAction(e -> {
            dialog.close();
            showEditDialog(employee);
        });

        Button deleteButton = new Button("Delete");
        deleteButton.setOnAction(e -> {
            dialog.close();
            confirmAndDeleteEmployee(employee);
        });

        HBox actions = new HBox(10, editButton, deleteButton);
        card.getChildren().addAll(nameLabel, deptLabel, salaryLabel, ratingLabel, experienceLabel, activeLabel, actions);

        Scene scene = new Scene(card, 400, 300);
        dialog.setScene(scene);
        dialog.show();
    }

    private void showEditDialog(Employee<UUID> employee) {
        Stage dialog = new Stage();
        dialog.setTitle("Edit Employee");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField nameField = new TextField(employee.getName());
        TextField deptField = new TextField(employee.getDepartment());
        TextField salaryField = new TextField(String.valueOf(employee.getSalary()));
        TextField experienceField = new TextField(String.valueOf(employee.getYearsOfExperience()));
        Slider ratingSlider = new Slider(0, 5, employee.getPerformanceRating());
        ratingSlider.setShowTickLabels(true);
        ratingSlider.setShowTickMarks(true);
        ratingSlider.setMajorTickUnit(1);
        ratingSlider.setBlockIncrement(0.1);
        CheckBox activeBox = new CheckBox("Active");
        activeBox.setSelected(employee.isActive());

        Button saveButton = new Button("Save");
        saveButton.setOnAction(e -> {
            try {
                employee.setName(nameField.getText());
                employee.setDepartment(deptField.getText());
                employee.setSalary(Double.parseDouble(salaryField.getText()));
                employee.setYearsOfExperience(Integer.parseInt(experienceField.getText()));
                employee.setPerformanceRating(ratingSlider.getValue());
                employee.setActive(activeBox.isSelected());
                employees.setAll(employeeDatabase.getAllEmployees());
                statusLabel.setText("Employee updated successfully.");
                dialog.close();
            } catch (NumberFormatException ex) {
                statusLabel.setText("Invalid input. Please check the fields.");
            }
        });

        grid.addRow(0, new Label("Name:"), nameField);
        grid.addRow(1, new Label("Department:"), deptField);
        grid.addRow(2, new Label("Salary:"), salaryField);
        grid.addRow(3, new Label("Experience (yrs):"), experienceField);
        grid.addRow(4, new Label("Performance Rating:"), ratingSlider);
        grid.addRow(5, new Label("Active:"), activeBox);
        grid.add(saveButton, 1, 6);

        VBox container = new VBox(grid);
        container.setPadding(new Insets(20)); // Add padding around the content

        Scene scene = new Scene(container, 400, 300);
        dialog.setScene(scene);
        dialog.show();
    }

    private void sortEmployees() {
        String selected = sortOptions.getValue();
        List<Employee<UUID>> sorted = new ArrayList<>(employees);

        if (selected.equals("Sort by Salary")) {
            sorted.sort(new EmployeeSalaryComparator<>());
        } else if (selected.equals("Sort by Performance Rating")) {
            sorted.sort(new EmployeePerformanceComparator<>());
        } else {
            // Use the compareTo method from the Employee class
            sorted.sort(Employee::compareTo);
        }

        employees.setAll(sorted);
    }

    private void filterEmployees(String keyword) {
        keyword = keyword.toLowerCase();
        employees.setAll(employeeDatabase.searchByTerm(keyword));
    }

    @FXML
    private void onApplySalaryRaise() {
        try {
            double percentage = Double.parseDouble(raisePercentageField.getText());
            double minRating = Double.parseDouble(minRatingField.getText());

            employeeDatabase.giveSalaryRaise(percentage, minRating); // Apply raise for employees with rating ≥ minRating
            employees.setAll(employeeDatabase.getAllEmployees()); // Refresh the table
            showAlert("Success", "Salary raise applied successfully.");
        } catch (NumberFormatException e) {
            showAlert("Error", "Invalid input. Please enter valid numbers.");
        }
    }

    @FXML
    private void onViewTop5HighestPaid() {
        List<Employee<UUID>> top5Employees = employeeDatabase.getTop5HighestPaidEmployees();
        StringBuilder message = new StringBuilder("Top 5 Highest Paid Employees:\n");
        for (Employee<UUID> employee : top5Employees) {
            message.append(String.format("- %s: $%.2f\n", employee.getName(), employee.getSalary()));
        }
        showAlert("Top 5 Highest Paid Employees", message.toString());
    }

    @FXML
    private void onCalculateAverageSalary() {
        String department = departmentField.getText();
        if (department.isEmpty()) {
            showAlert("Error", "Please enter a department.");
            return;
        }
        double averageSalary = employeeDatabase.calculateAverageSalaryByDepartment(department);
        if (averageSalary > 0) {
            showAlert("Average Salary", String.format("Average salary in %s: $%.2f", department, averageSalary));
        } else {
            showAlert("Average Salary", "No employees found in the specified department.");
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
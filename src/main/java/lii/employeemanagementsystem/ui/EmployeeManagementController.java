package lii.employeemanagementsystem.ui;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lii.employeemanagementsystem.customExceptions.EmployeeNotFoundException;
import lii.employeemanagementsystem.customExceptions.InvalidDepartmentException;
import lii.employeemanagementsystem.customExceptions.InvalidPerformanceRatingException;
import lii.employeemanagementsystem.customExceptions.InvalidSalaryException;
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
    private ComboBox<String> departmentField;
    @FXML
    private TextField minRatingField;
    @FXML
    private ComboBox<String> filterActiveComboBox;

    @FXML
    private Label totalEmployeesLabel;
    @FXML
    private Label visibleEmployeesLabel;


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

        });
        loadData();
        totalEmployeesLabel.setText(String.valueOf(employeeDatabase.getAllEmployees().size()));

        //listener to update employee count on table changes due to filtering or sorting
        employees.addListener((ListChangeListener<Employee<UUID>>) change -> {
            visibleEmployeesLabel.setText(String.valueOf(employees.size()));
            totalEmployeesLabel.setText(String.valueOf(employeeDatabase.getAllEmployees().size()));

        });


        visibleEmployeesLabel.setText(String.valueOf(employees.size()));

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

    }

    private void loadData() {
        Employee<UUID> emp1 = new Employee<>(UniqueIdGenerator.generateUniqueId(), "Mr Lii", "Admin", 100000, 4.8, 15, true, "file:images/lord.jpg");

        employeeDatabase.addEmployee(emp1);

        employees.setAll(employeeDatabase.getAllEmployees());
    }

    @FXML
    private void onAddEmployee() {
        try {
            Stage dialog = new Stage();
            dialog.setTitle("Add New Employee");

            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);

            TextField nameField = new TextField();
            ComboBox<String> deptComboBox = new ComboBox<>();
            deptComboBox.setItems(FXCollections.observableArrayList("IT", "Finance", "HR", "Engineering", "Marketing"));
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
                try {
                    FileChooser fileChooser = new FileChooser();
                    File selectedFile = fileChooser.showOpenDialog(dialog);
                    if (selectedFile != null) {
                        imagePath[0] = selectedFile.toURI().toString();
                    }
                } catch (Exception ex) {
                    showAlert("Error", "Failed to upload image: " + ex.getMessage());
                }
            });

            Button saveBtn = new Button("Save");
            saveBtn.setOnAction(e -> {
                try {
                    if (nameField.getText().isEmpty() ) {
                        showAlert("Error", "Name field cannot be empty.");
                        return;
                    } else if (deptComboBox.getValue() == null) {
                        showAlert("Error", "Department field cannot be empty.");
                        return;
                    } else if (salaryField.getText().isEmpty()) {
                    showAlert("Error", "Salary field cannot be empty.");
                    return;
                } else if (experienceField.getText().isEmpty()) {
                        showAlert("Error", "Experience field cannot be empty.");
                        return;
                    }

                } catch (Exception ex) {
                    showAlert("Error", "Failed to add employee: " + ex.getMessage());
                }

                try {
                    if (Double.parseDouble(salaryField.getText()) < 0) {
                        showAlert("Error", "Salary cannot be negative.");
                        return;
                    }
                } catch (NumberFormatException ex) {
                    showAlert("Error", "Salary must be a valid number.");
                    return;
                }

                try {
                    if (Integer.parseInt(experienceField.getText()) < 0) {
                        showAlert("Error", "Experience cannot be negative.");
                        return;
                    }
                } catch (NumberFormatException ex) {
                    showAlert("Error", "Experience must be a valid number.");
                    return;
                }



                Employee<UUID> newEmployee = new Employee<>(
                        UniqueIdGenerator.generateUniqueId(),
                        nameField.getText(),
                        deptComboBox.getValue(),
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

            });

            grid.addRow(0, new Label("Name:"), nameField);
            grid.addRow(1, new Label("Department:"), deptComboBox);
            grid.addRow(2, new Label("Salary:"), salaryField);
            grid.addRow(3, new Label("Experience (yrs):"), experienceField);
            grid.addRow(4, new Label("Performance Rating:"), ratingSlider);
            grid.addRow(5, activeBox, uploadBtn);
            grid.add(saveBtn, 1, 6);

            VBox container = new VBox(grid);
            container.setPadding(new Insets(20));

            Scene scene = new Scene(container, 400, 300);
            dialog.setScene(scene);
            dialog.show();
            totalEmployeesLabel.setText(String.valueOf(employeeDatabase.getAllEmployees().size()));
            visibleEmployeesLabel.setText(String.valueOf(employees.size()));
        } catch (Exception ex) {
            showAlert("Error", "Failed to open Add Employee dialog: " + ex.getMessage());
        }
    }

    @FXML
    private void onApplyFilter() {
        try {
            List<Employee<UUID>> filteredList = new ArrayList<>(employeeDatabase.getAllEmployees());

            double minRating = ratingFilterSlider.getValue();
            if (minRating >= 0) {
                filteredList = employeeDatabase.searchByMinimumPerformanceRating(minRating);
            }

            String minSalaryText = minSalaryField.getText();
            String maxSalaryText = maxSalaryField.getText();
            if (!minSalaryText.isEmpty() && !maxSalaryText.isEmpty()) {
                try {
                    double minSalary = Double.parseDouble(minSalaryText);
                    double maxSalary = Double.parseDouble(maxSalaryText);

                    filteredList = filteredList.stream()
                            .filter(employee -> employeeDatabase.searchBySalaryRange(minSalary, maxSalary).contains(employee))
                            .collect(Collectors.toList());
                } catch (NumberFormatException e) {
                    showAlert("Error", "Salary input ranges are Invalid. Please enter valid numbers.");
                } catch (InvalidSalaryException e) {
                    showAlert("Error", e.getMessage());
                }
            }

            employees.setAll(filteredList);
            statusLabel.setText("Filters applied successfully.");
        } catch (Exception ex) {
            showAlert("Error", "Failed to apply filters: " + ex.getMessage());
        }
    }


    private void confirmAndDeleteEmployee(Employee<UUID> employee) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText("Are you sure you want to delete " + employee.getName() + "?");
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                employeeDatabase.removeEmployee(employee.getEmployeeId());
                employees.setAll(employeeDatabase.getAllEmployees());
                statusLabel.setText("Employee deleted successfully.");
                totalEmployeesLabel.setText(String.valueOf(employeeDatabase.getAllEmployees().size()));
                visibleEmployeesLabel.setText(String.valueOf(employees.size()));
            } catch (EmployeeNotFoundException e){
                showAlert("Error", e.getMessage());
            }
            catch (Exception ex) {
                showAlert("Error", "Failed to delete employee: " + ex.getMessage());
            }
        }
    }

    private void showEmployeeDetails(Employee<UUID> employee) {
        Stage dialog = new Stage();
        dialog.setTitle("Employee Details");

        VBox card = new VBox(10);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: #f9f9f9; -fx-border-color: #ccc; -fx-border-radius: 10; -fx-background-radius: 10;");

        // Profile Picture
        ImageView profilePicture = new ImageView(employee.getProfilePicture());
        profilePicture.setFitWidth(100);
        profilePicture.setFitHeight(100);
        profilePicture.setStyle("-fx-border-radius: 100; -fx-background-radius: 50; -fx-effect: dropshadow(gaussian, gray, 5, 0.5, 0, 0);");

        // Employee Details
        Label nameLabel = new Label("Name: " + employee.getName());
        Label deptLabel = new Label("Department: " + employee.getDepartment());
        Label salaryLabel = new Label("Salary: $" + employee.getSalary());
        Label ratingLabel = new Label("Performance Rating: " + employee.getPerformanceRating());
        Label experienceLabel = new Label("Experience: " + employee.getYearsOfExperience() + " years");
        Label activeLabel = new Label("Active: " + (employee.isActive() ? "Yes" : "No"));

        // Buttons
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

        // Layout
        HBox header = new HBox(10, new VBox(10, nameLabel, deptLabel, salaryLabel, ratingLabel, experienceLabel, activeLabel, actions), profilePicture);
        header.setSpacing(20);
        header.setPadding(new Insets(10));
        header.setStyle("-fx-alignment: top-right;");

        card.getChildren().add(header);

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
        ComboBox<String> deptComboBox = new ComboBox<>();
        deptComboBox.setItems(FXCollections.observableArrayList("IT", "Finance", "HR", "Engineering", "Marketing"));
        deptComboBox.setValue(employee.getDepartment());
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
                if (nameField.getText().isEmpty() ) {
                    showAlert("Error", "Name field cannot be empty.");
                    return;
                } else if (deptComboBox.getValue() == null) {
                    showAlert("Error", "Department field cannot be empty.");
                    return;
                } else if (salaryField.getText().isEmpty()) {
                    showAlert("Error", "Salary field cannot be empty.");
                    return;
                } else if (experienceField.getText().isEmpty()) {
                    showAlert("Error", "Experience field cannot be empty.");
                    return;
                }

                employee.setName(nameField.getText());
                employee.setDepartment(deptComboBox.getValue());
                employee.setSalary(Double.parseDouble(salaryField.getText()));
                employee.setYearsOfExperience(Integer.parseInt(experienceField.getText()));
                employee.setPerformanceRating(ratingSlider.getValue());
                employee.setActive(activeBox.isSelected());
                employees.setAll(employeeDatabase.getAllEmployees());
                statusLabel.setText("Employee updated successfully.");
                dialog.close();
            }
            catch (NumberFormatException ex) {
                showAlert("Error", "Invalid input. Please check the fields.");
            }
            catch (Exception ex) {
                showAlert("Error",  ex.getMessage());
            }
        });

        grid.addRow(0, new Label("Name:"), nameField);
        grid.addRow(1, new Label("Department:"), deptComboBox);
        grid.addRow(2, new Label("Salary:"), salaryField);
        grid.addRow(3, new Label("Experience (yrs):"), experienceField);
        grid.addRow(4, new Label("Performance Rating:"), ratingSlider);
        grid.addRow(5, new Label("Active:"), activeBox);
        grid.add(saveButton, 1, 6);

        VBox container = new VBox(grid);
        container.setPadding(new Insets(20));

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

            employeeDatabase.giveSalaryRaise(percentage, minRating);
            employees.setAll(employeeDatabase.getAllEmployees());


            showAlert("Success", "Salary raise applied successfully.");
            raisePercentageField.setText("");
            minRatingField.setText("");
        } catch (NumberFormatException ex) {
            showAlert("Error", "Invalid input. Please enter valid numbers.");
        } catch (Exception ex) {
            showAlert("Error", ex.getMessage());
        }
    }

    @FXML
    private void onViewTop5HighestPaid() {
        try {
            List<Employee<UUID>> top5Employees = employeeDatabase.getTop5HighestPaidEmployees();
            StringBuilder message = new StringBuilder("Top 5 Highest Paid Employees:\n");
            for (Employee<UUID> employee : top5Employees) {
                message.append(String.format("- %s: $%.2f\n", employee.getName(), employee.getSalary()));
            }
            showAlert("Top 5 Highest Paid Employees", message.toString());
        } catch (Exception ex) {
            showAlert("Error", "Failed to retrieve top 5 highest-paid employees: " + ex.getMessage());
        }
    }

    @FXML
    private void onCalculateAverageSalary() {
        try {
            String department = departmentField.getValue();
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
        } catch (Exception ex) {
            showAlert("Error",  ex.getMessage());
        }
    }


    @FXML
    private void onFilterActiveComboBox() {
        try {
            String selectedFilter = filterActiveComboBox.getValue();
            List<Employee<UUID>> filteredEmployees;

            if ("Active Employees".equals(selectedFilter)) {
                filteredEmployees = new ArrayList<>();
                Iterator<Employee<UUID>> iterator = employeeDatabase.getEmployeeIterator();
                while (iterator.hasNext()) {
                    Employee<UUID> employee = iterator.next();
                    if (employee.isActive()) {
                        filteredEmployees.add(employee);
                    }
                }
            } else if ("Inactive Employees".equals(selectedFilter)) {
                filteredEmployees = new ArrayList<>();
                Iterator<Employee<UUID>> iterator = employeeDatabase.getEmployeeIterator();
                while (iterator.hasNext()) {
                    Employee<UUID> employee = iterator.next();
                    if (!employee.isActive()) {
                        filteredEmployees.add(employee);
                    }
                }
            } else {
                // Default to showing all employees
                filteredEmployees = new ArrayList<>(employeeDatabase.getAllEmployees());
            }

            employees.setAll(filteredEmployees);
        } catch (Exception ex) {
            showAlert("Error", "Failed to apply filter: " + ex.getMessage());
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
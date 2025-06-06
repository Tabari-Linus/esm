package lii.employeemanagementsystem;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import lii.employeemanagementsystem.model.User;
import lii.employeemanagementsystem.ui.LoginController;

public class MainApp extends Application {

    public static User authenticatedUser;

    @Override
    public void start(Stage primaryStage) {
        showLoginScreen(primaryStage);
    }

    private void showLoginScreen(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/lii/employeemanagementsystem/ui/Login.fxml"));
            Scene scene = new Scene(loader.load());
            LoginController loginController = loader.getController();


            loginController.setOnLoginSuccess(user -> {
                authenticatedUser = user;
                primaryStage.close();
                showMainApplication();
            });

            primaryStage.setScene(scene);
            primaryStage.setTitle("Login");
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showMainApplication() {
        try {
            Stage mainStage = new Stage();
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/lii/employeemanagementsystem/ui/EmployeeManagementUI.fxml"));

            VBox root = loader.load();

            // Crating a header to display user information
            HBox header = new HBox(10);
            header.setStyle("-fx-padding: 10; -fx-alignment: center-left;");

            // Profile picture
            ImageView profilePicture = new ImageView(new Image(authenticatedUser.getProfilePicture()));
            profilePicture.setFitWidth(50);
            profilePicture.setFitHeight(50);
            mainStage.setTitle("Employee Management System");

            // User name
            Text userName = new Text("Welcome, " + authenticatedUser.getUsername());
            userName.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

            // Logout button
            Button logoutButton = new Button("Logout");
            logoutButton.setStyle("-fx-background-color: #ff4d4d; -fx-text-fill: white; -fx-font-weight: bold;");
            logoutButton.setOnAction(event -> {
                mainStage.close(); // Close the main application window
            });

            header.getChildren().addAll(profilePicture, userName, logoutButton);


            root.getChildren().add(0, header);

            Scene scene = new Scene(root);
            mainStage.setScene(scene);

            mainStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
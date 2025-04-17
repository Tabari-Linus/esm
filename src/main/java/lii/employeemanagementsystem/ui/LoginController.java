package lii.employeemanagementsystem.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import lii.employeemanagementsystem.model.User;
import lii.employeemanagementsystem.service.LoginService;

import java.util.function.Consumer;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    private final LoginService loginService = new LoginService();
    private Consumer<User> onLoginSuccess;

    @FXML
    private void onLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        User authenticatedUser = loginService.authenticate(username, password);
        if (authenticatedUser != null) {
            if (onLoginSuccess != null) {
                onLoginSuccess.accept(authenticatedUser); // Notify the main app
            }
        } else {
            showAlert("Login Failed", "Invalid username or password.");
        }
    }

    public void setOnLoginSuccess(Consumer<User> onLoginSuccess) {
        this.onLoginSuccess = onLoginSuccess;
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
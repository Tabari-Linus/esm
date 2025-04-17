package lii.employeemanagementsystem.service;

import lii.employeemanagementsystem.model.User;

import java.util.HashMap;
import java.util.Map;

public class LoginService {
    private final Map<String, User> userDatabase = new HashMap<>();

    public LoginService() {
        // Add sample users with profile pictures
        userDatabase.put("admin", new User("admin", "admin123", "file:D:/space/GA/Amalitech/row/ems/src/main/resources/images/Luffy.png"));
}

    public User authenticate(String username, String password) {
        User user = userDatabase.get(username);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }
}
package com.motorph.service;

import com.motorph.data.DataStore;
import com.motorph.model.User;
import java.util.List;

public class AuthenticationService {
    private DataStore dataStore;

    public AuthenticationService() {
        this.dataStore = DataStore.getInstance();
    }

    public User authenticate(String username, String password) {
        List<User> users = dataStore.getUsers();
        
        for (User user : users) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                return user;
            }
        }
        return null;
    }
}

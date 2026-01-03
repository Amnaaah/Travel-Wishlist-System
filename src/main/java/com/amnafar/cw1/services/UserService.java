package com.amnafar.cw1.services;

import com.amnafar.cw1.model.User;
import com.amnafar.cw1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // Fetch user by ID returns null if not found
    public User getUserById(int id) {
        return userRepository.findById(id).orElse(null);
    }

    // Get all users in the system
//    public List<User> getAllUsers() {
//        return userRepository.findAll();
//    }

    // Register a new user
    public User addUser(User user) {
        return userRepository.save(user);
    }

    // Update user details
    public User updateUser(int id, User user) {
        User existing = userRepository.findById(id).orElse(null);
        if (existing != null) {
            existing.setUsername(user.getUsername());
            existing.setPassword(user.getPassword());
            existing.setEmail(user.getEmail());
            return userRepository.save(existing);
        }
        return null;
    }

    // Delete user
    public boolean deleteUser(int id) {
        User existing = userRepository.findById(id).orElse(null);
        if (existing != null) {
            userRepository.delete(existing);
            return true;
        }
        return false;
    }

    // Simple login
    public User login(String username, String password) {
        return userRepository.findByUsernameAndPassword(username, password);
    }
}

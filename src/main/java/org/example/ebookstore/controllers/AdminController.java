package org.example.ebookstore.controllers;

import org.example.ebookstore.entities.dtos.UserDto;
import org.example.ebookstore.services.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class AdminController {
    private final UserService userService;

    @Autowired
    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/admin-panel")
    public String displayAdminPanel(Model model) {
        List<UserDto> userDtos = this.userService.findAll();
        model.addAttribute("users", userDtos);

        return "admin-panel";
    }

    @PostMapping("/admin-panel/{userId}")
    public ResponseEntity<?> addAdminRoleToUser(Model model, @PathVariable("userId") Long userId) {
        try {
            this.userService.addAdminRoleToUser(model, userId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @DeleteMapping("/admin-panel/{userId}")
    public ResponseEntity<?> removeAdminRoleFromUser(Model model, @PathVariable("userId") Long userId) {
        try {
            this.userService.removeAdminRoleFromUser(model, userId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }
}

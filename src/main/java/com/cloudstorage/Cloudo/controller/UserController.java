package com.cloudstorage.Cloudo.controller;


import com.cloudstorage.Cloudo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import com.cloudstorage.Cloudo.models.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser() {
        User user = userService.getCurrentUser();
        return ResponseEntity.ok(user);

    }

}

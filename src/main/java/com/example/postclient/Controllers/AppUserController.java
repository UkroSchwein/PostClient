package com.example.postclient.Controllers;

import com.example.postclient.Models.AppUser;
import com.example.postclient.Models.EmailPasswordPair;
import com.example.postclient.Service.AppUserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
public class AppUserController {

    private final AppUserService appUserService;

    @PostMapping("/add")
    public ResponseEntity<AppUser> createAppUser(@RequestBody AppUser appUser) {
        AppUser savedAppUser = appUserService.saveAppUser(appUser);
        return new ResponseEntity<>(savedAppUser, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppUser> getAppUserById(@PathVariable Long id) {
        AppUser appUser = appUserService.getAppUserById(id);
        return appUser != null ? new ResponseEntity<>(appUser, HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping
    public ResponseEntity<List<AppUser>> getAllAppUsers() {
        List<AppUser> appUsers = appUserService.getAllAppUsers();
        return new ResponseEntity<>(appUsers, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAppUser(@PathVariable Long id) {
        appUserService.deleteAppUser(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AppUser> updateAppUser(@PathVariable Long id, @RequestBody AppUser updatedUser) {
        AppUser updatedAppUser = appUserService.updateAppUser(id, updatedUser);
        return updatedAppUser != null ? new ResponseEntity<>(updatedAppUser, HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
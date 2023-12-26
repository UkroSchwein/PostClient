package com.example.postclient.Service;

import com.example.postclient.Models.AppUser;
import com.example.postclient.Models.EmailPasswordPair;
import com.example.postclient.Repository.AppUserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class AppUserService {


    private AppUserRepository appUserRepository;
    private PasswordEncoder passwordEncoder;

    public AppUser saveAppUser(AppUser appUser) {
        appUser.setPassword(passwordEncoder.encode(appUser.getPassword()));
        return appUserRepository.save(appUser);
    }

    public AppUser getAppUserById(Long id) {
        return appUserRepository.findById(id).orElse(null);
    }

    public List<AppUser> getAllAppUsers() {
        return appUserRepository.findAll();
    }

    public void deleteAppUser(Long id) {
        appUserRepository.deleteById(id);
    }

    public AppUser updateAppUser(Long id, AppUser updatedUser) {
        AppUser existingUser = getAppUserById(id);

        if (existingUser != null) {
            existingUser.setUsername(updatedUser.getUsername());
            existingUser.setPhoneNumber(updatedUser.getPhoneNumber());
            existingUser.setPassword(updatedUser.getPassword());
            existingUser.setRoles(updatedUser.getRoles());

            List<EmailPasswordPair> updatedPairs = updatedUser.getEmailPasswordPairs();
            if (updatedPairs != null) {
                existingUser.getEmailPasswordPairs().clear();
                existingUser.getEmailPasswordPairs().addAll(updatedPairs);
            }

            return appUserRepository.save(existingUser);
        } else {
            return null;
        }
    }


    public Optional<List<EmailPasswordPair>> getEmailPasswordPairsByUsername(String username) {
        Optional<AppUser> appUserOptional = appUserRepository.findByUsername(username);
        return appUserOptional.map(AppUser::getEmailPasswordPairs);
    }

    public Optional<EmailPasswordPair> getEmailPasswordPairById(String username, Long id) {
        Optional<AppUser> appUserOptional = appUserRepository.findByUsername(username);
        return appUserOptional.flatMap(appUser -> appUser.getEmailPasswordPairs().stream()
                .filter(pair -> pair.getId().equals(id))
                .findFirst());
    }

}



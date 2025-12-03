package org.example.ebookstore.entities.dtos;

import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.validation.constraints.*;
import org.example.ebookstore.entities.Picture;

public class UserRegistrationDto {
    @Size(min = 4, max = 30)
    @NotNull
    private String username;
    @Email(message = "Invalid email.")
    @NotNull
    private String email;
    @Size(min = 4, max = 128)
    @NotNull
    private String password;
    @Size(min = 4, max = 128)
    @NotNull
    private String confirmPassword;
    @Size(min = 2, max = 50)
    @NotNull
    private String firstName;
    @Size(min = 2, max = 50)
    @NotNull
    private String lastName;
    @Positive
    @Max(150)
    @NotNull
    private int age;
    private Picture picture;

    public UserRegistrationDto() {
    }

    public UserRegistrationDto(String username, String email, String password, String confirmPassword, String firstName, String lastName, int age, Picture picture) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.confirmPassword = confirmPassword;
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.picture = picture;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Picture getPicture() {
        return picture;
    }

    public void setPicture(Picture picture) {
        this.picture = picture;
    }
}

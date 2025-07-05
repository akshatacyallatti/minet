package com.example.minet.dto;


import java.time.LocalDateTime;

public class UserBaseDto {

    private String name;
    private String email;
    private LocalDateTime createdDate;

    public UserBaseDto() {
    }

    public UserBaseDto(String name, String email,LocalDateTime createdDate) {
        this.name = name;
        this.email = email;
        this.createdDate = createdDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }
}

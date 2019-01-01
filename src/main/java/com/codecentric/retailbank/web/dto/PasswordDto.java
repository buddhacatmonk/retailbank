package com.codecentric.retailbank.web.dto;

import com.codecentric.retailbank.validation.ValidPassword;

import javax.validation.constraints.NotNull;

public class PasswordDto {
    @NotNull
    private String oldPassword;

    @NotNull
    @ValidPassword
    private String newPassword;


    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}

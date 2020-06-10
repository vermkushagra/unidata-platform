package com.unidata.mdm.backend.common.dto.security;

public class PasswordDTO extends BaseSecurityDTO {
    private final UserDTO user;
    private final String hashedText;
    private final String text;
    private final boolean isActive;

    public PasswordDTO(UserDTO user, String hashedText, boolean isActive) {
        this.user = user;
        this.hashedText = hashedText;
        this.isActive = isActive;
        this.text = null;
    }

    public PasswordDTO(UserDTO user, String hashedText, String text, boolean isActive) {
        this.user = user;
        this.hashedText = hashedText;
        this.text = text;
        this.isActive = isActive;
    }

    public UserDTO getUser() {
        return user;
    }

    public String getHashedText() {
        return hashedText;
    }

    public String getText() {
        return text;
    }

    public boolean isActive() {
        return isActive;
    }
}

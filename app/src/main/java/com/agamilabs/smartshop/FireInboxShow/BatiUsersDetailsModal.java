package com.agamilabs.smartshop.FireInboxShow;

import java.util.List;

public class BatiUsersDetailsModal {
   String name, email, photo;

    public BatiUsersDetailsModal() {
    }

    public BatiUsersDetailsModal(String name, String email, String photo) {
        this.name = name;
        this.email = email;
        this.photo = photo;
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

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}

package com.kustacks.kuring.worker.client.staff.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TestStaffDTO {

    @JsonProperty("name")
    private String name;

    @JsonProperty("major")
    private String major;

    @JsonProperty("lab")
    private String lab;

    @JsonProperty("phone")
    private String phone;

    @JsonProperty("email")
    private String email;

    public TestStaffDTO() {

    }

    public TestStaffDTO(String name, String major, String lab, String phone, String email) {
        this.name = name;
        this.major = major;
        this.lab = lab;
        this.phone = phone;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public String getMajor() {
        return major;
    }

    public String getLab() {
        return lab;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }
}

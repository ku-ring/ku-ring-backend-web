package com.kustacks.kuring.user.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Embeddable;
import jakarta.persistence.OneToMany;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Embeddable
@NoArgsConstructor
public class Devices {

    @OneToMany(mappedBy = "user", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<Device> devices = new ArrayList<>();

    public void add(Device device) {
        this.devices.add(device);
    }

    public void remove(Device device) {
        this.devices.remove(device);
    }

    public List<Device> getDevices() {
        return devices;
    }
}

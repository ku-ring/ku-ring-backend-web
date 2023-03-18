package com.kustacks.kuring.worker.staff.deptinfo.veterinary_medicine;

import org.springframework.stereotype.Component;

@Component
public class VeterinaryMedicineDept extends VeterinaryMedicineCollege {

    public VeterinaryMedicineDept() {
        super("105091", "수의학과", "42372");
    }
}

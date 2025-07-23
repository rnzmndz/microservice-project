package com.renzomendoza.employee_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmergencyContact {

    @Column(name = "emergency_contact_first_name")
    private String firstName;

    @Column(name = "emergency_contact_last_name")
    private String lastName;

    @Column(name = "emergency_contact_phone_number")
    private String phoneNumber;
}

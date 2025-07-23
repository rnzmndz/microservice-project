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
public class ContactInformation {

    @Column(name = "phone_number")
    private String phoneNumber;
    private String email;
}

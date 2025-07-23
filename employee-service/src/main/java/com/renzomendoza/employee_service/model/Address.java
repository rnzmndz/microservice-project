package com.renzomendoza.employee_service.model;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address {

    private String street;
    private String city;
    private String state;
    private String zipCode;
}

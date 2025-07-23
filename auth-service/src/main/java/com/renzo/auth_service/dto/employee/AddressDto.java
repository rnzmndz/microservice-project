package com.renzo.auth_service.dto.employee;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Address information")
public class AddressDto {

    @NotBlank(message = "Street is required")
    @Size(max = 100, message = "Street must be less than 100 characters")
    @Schema(description = "Street address", example = "123 Main St")
    private String street;

    @NotBlank(message = "City is required")
    @Size(max = 50, message = "City must be less than 50 characters")
    @Schema(description = "City", example = "New York")
    private String city;

    @NotBlank(message = "State is required")
    @Size(max = 50, message = "State must be less than 50 characters")
    @Schema(description = "State or province", example = "NY")
    private String state;

    @NotBlank(message = "Zip code is required")
    @Pattern(regexp = "^[0-9]{5}(?:-[0-9]{4})?$", message = "Invalid zip code format")
    @Schema(description = "Postal/Zip code", example = "10001")
    private String zipCode;
}
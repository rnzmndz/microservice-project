package com.renzo.auth_service.client;

import com.renzo.auth_service.dto.employee.employee.EmployeeCreateDto;
import com.renzo.auth_service.dto.employee.employee.EmployeeResponse;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "employee-service")
public interface EmployeeClient {

    @PostMapping("/api/v1/employees")
    EmployeeResponse createEmployee(@Valid @RequestBody EmployeeCreateDto employeeUpdateDto);
}

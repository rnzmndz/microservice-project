package com.renzomendoza.employee_service.dto;

import com.renzomendoza.employee_service.dto.employee.EmployeeList;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Schema(name = "EmployeePage")
public class EmployeePage extends PageImpl<EmployeeList> {
    public EmployeePage(List<EmployeeList> content, Pageable pageable, long total) {
        super(content, pageable, total);
    }
}


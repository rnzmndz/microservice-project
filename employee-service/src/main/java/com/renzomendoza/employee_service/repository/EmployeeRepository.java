package com.renzomendoza.employee_service.repository;

import com.renzomendoza.employee_service.model.EmployeeProfile;
import io.micrometer.observation.ObservationFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EmployeeRepository extends JpaRepository<EmployeeProfile, UUID> {

    Page<EmployeeProfile> findByJobTitle(String jobTitle, Pageable pageable);

    Page<EmployeeProfile> findByFirstNameContainingOrLastNameContaining(String name, String name1, Pageable pageable);
}

package reservation.api.domain.employee.repository

import org.springframework.data.jpa.repository.JpaRepository
import reservation.api.domain.employee.entity.Employee

interface EmployeeRepository : JpaRepository<Employee?, Long?>
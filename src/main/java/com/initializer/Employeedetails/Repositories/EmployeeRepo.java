package com.initializer.Employeedetails.Repositories;

import com.initializer.Employeedetails.Tables.Employee;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface EmployeeRepo extends CrudRepository<Employee, Integer> {

   Employee findById(int id);
   String deleteById(int id);
   List<Employee> findAllByPID(int id,Sort sort);
   Employee findByDesi(String str);
   Employee findByJid(int id);
}


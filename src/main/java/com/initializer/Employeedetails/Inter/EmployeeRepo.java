package com.initializer.Employeedetails.Inter;

import com.initializer.Employeedetails.GS.Employee;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface EmployeeRepo extends CrudRepository<Employee, Integer> {

   Employee findById(int id);
   String deleteById(int id);
   List<Employee> findAllByPID(int id);
//   Employee findByPID(int id);
//   Employee findAllByDesi(String str);
//   Employee findByName(String str);
}


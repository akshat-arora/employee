package com.initializer.Employeedetails.Inter;

import com.initializer.Employeedetails.GS.Employee;
import net.bytebuddy.TypeCache;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface EmployeeRepo extends CrudRepository<Employee, Integer> {

   Employee findById(int id);
   String deleteById(int id);
   List<Employee> findAllByPID(int id,Sort sort);
}


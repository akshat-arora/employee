package com.initializer.Employeedetails.Repositories;

import com.initializer.Employeedetails.Tables.Employee;
import net.bytebuddy.matcher.ElementMatcher;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface EmployeeRepo extends CrudRepository<Employee, Integer> {

   Employee findById(int id);
   String deleteById(String id);
   List<Employee> findAllByManagerId(int id,Sort sort);
   Employee findByJid(Integer id);
   List<Employee> findAll(Sort sort);
   Employee findByName(String str);

}


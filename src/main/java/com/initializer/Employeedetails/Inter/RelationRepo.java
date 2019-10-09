package com.initializer.Employeedetails.Inter;

import com.initializer.Employeedetails.GS.Relation;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;

public interface RelationRepo extends CrudRepository<Relation,Integer> {

    Relation findByDesi(String desi);

}

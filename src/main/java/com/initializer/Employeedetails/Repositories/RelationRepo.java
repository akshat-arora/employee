package com.initializer.Employeedetails.Repositories;

import com.initializer.Employeedetails.Tables.Relation;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;

public interface RelationRepo extends CrudRepository<Relation,Integer> {

    Relation findByJobTitle(String desi);
    Iterable<Relation> findAllByJobTitle(String desi);
    Relation findByJid(int id, Sort sort);

}

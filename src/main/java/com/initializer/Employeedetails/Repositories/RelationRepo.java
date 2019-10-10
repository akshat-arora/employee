package com.initializer.Employeedetails.Repositories;

import com.initializer.Employeedetails.Tables.Relation;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;

public interface RelationRepo extends CrudRepository<Relation,Integer> {

    Relation findByDesi(String desi);
    Iterable<Relation> findAllByDesi(String desi);
    Relation findByJid(Relation relation, Sort sort);

}

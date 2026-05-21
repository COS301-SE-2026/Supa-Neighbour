package com.app.api.repositories;

import com.app.api.models.Dependent;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DependentRepository extends CrudRepository<Dependent, Integer>
{
    Dependent  findByUserId(int userId);
}
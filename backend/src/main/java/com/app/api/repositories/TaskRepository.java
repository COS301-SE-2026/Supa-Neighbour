package com.app.api.repositories;

import com.app.api.models.Task;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends CrudRepository<Task, Integer>
{
    // basically our "WHERE residentID = 3..." filter in sql

    // The below will be used by the GET /users/userId/tasks endpoint in my tasks view 
    Iterable<Task> findByHelperId(int helperId);

    // Will be used by GET /tasks indashboard
    Iterable<Task> findByDependentId(int dependentId);

    // Will be used by GET /tasks?category=.. 
    Iterable<Task> findByTaskTypeId(int taskTypeId);
}
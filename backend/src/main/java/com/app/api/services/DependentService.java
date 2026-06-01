package com.app.api.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.api.models.Dependent;
import com.app.api.repositories.DependentRepository;

@Service
public class DependentService {

    @Autowired
    private DependentRepository dependentRepository;

    // Get all
    public List<Dependent> getAllDependents() {
        return dependentRepository.findAll();
    }

    // Get by id
    public Dependent getDependentById(int id) {
        return dependentRepository.findById(id).orElse(null);
    }

    // Create
    public Dependent saveDependent(Dependent dependent) {
        if(dependent == null) return null;
        return dependentRepository.save(dependent);
    }

    // Update
    public Dependent updateDependent(int id, Dependent updated) {
        Dependent existing = dependentRepository.findById(id).orElse(null);
        if (existing == null) return null;

        existing.setUserid(updated.getUserid());
        existing.setTaskTypeid(updated.getTaskTypeid());

        return dependentRepository.save(existing);
    }

    // Delete
    public void deleteDependent(int id) {
        dependentRepository.deleteById(id);
    }
}
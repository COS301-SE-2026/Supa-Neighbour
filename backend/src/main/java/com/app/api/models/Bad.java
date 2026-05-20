package com.app.api.models;

import java.sql.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "badgestable")
public class Bad {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "badge_name")
    private String badgeName;

    @Column(name = "description")
    private String description;

    public Bad() {
    }

    public Bad(String badgeName, String description) {
        this.badgeName = badgeName;
        this.description = description; 
    }
    
}

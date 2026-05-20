package com.app.api.models;
import java.sql.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tasktypetable")
public class TaskType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tasktypeid")
    private int tasktypeid;
    @Column(name = "associatedbadheid")
    private String typeName;
    @Column(name = "typedescription")
    private String description;
    @Column(name = "needsspecialist")
    private boolean needsSpecialist;
    @Column(name = "xpworth")
    private int xpWorth;

    public TaskType() {
    }

    public TaskType(String typeName, String description, boolean needsSpecialist, int xpWorth) {
        this.typeName = typeName;
        this.description = description;
        this.needsSpecialist = needsSpecialist;
        this.xpWorth = xpWorth;
    }

        public int getTasktypeid() {
            return tasktypeid;
        }
        public String getTypeName() {
            return typeName;
        }
        public String getDescription() {
            return description;
        }
        public boolean isNeedsSpecialist() {
            return needsSpecialist;
        }
        public int getXpWorth() {
            return xpWorth;
        }
        public void setTypeName(String typeName) {
            this.typeName = typeName;
        }
        public void setDescription(String description) {
            this.description = description;
        }
        public void setNeedsSpecialist(boolean needsSpecialist) {
            this.needsSpecialist = needsSpecialist;
        }
        public void setXpWorth(int xpWorth) {
            this.xpWorth = xpWorth;
        }

}

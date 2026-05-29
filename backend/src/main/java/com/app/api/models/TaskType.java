package com.app.api.models;
import java.sql.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Entity
@Table(name = "task_type_table")
public class TaskType {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "task_type_id_seq")
    @Column(name = "task_type_id")
    private int tasktypeid;

    @Column(name = "associated_badge_id")
    private Badges badgeid;

    @Column(name = "type_description")
    private String description;

    @Column(name = "needs_specialist")
    private boolean needsSpecialist;

    @Column(name = "xp_worth")
    private int xpWorth;

    public TaskType() {
    }

    public TaskType(int tasktypeid,Badges badgeid, String description, boolean needsSpecialist, int xpWorth) {
        this.tasktypeid = tasktypeid;
        this.badgeid=badgeid;
        this.description = description;
        this.needsSpecialist = needsSpecialist;
        this.xpWorth = xpWorth;
    }

        public int getTasktypeid() {
            return tasktypeid;
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

        public Badges getBadgeid(){
            return badgeid;
        }

        public void setBadgeid(Badges badgeid){
            this.badgeid=badgeid;
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

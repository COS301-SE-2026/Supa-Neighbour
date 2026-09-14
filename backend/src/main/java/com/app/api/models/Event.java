package com.app.api.models;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.checkerframework.checker.units.qual.C;

import com.google.auto.value.AutoValue.Builder;

@Data 
@Builder 
@Entity
@Table(name = "events_table")
public class Event {
    

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    private Integer enventId;

    @ManyToOne()
    @JoinColumn(name = "user_id",nullable=false)
    private User userId;

    @Column(name = "event_title",nullable = false)
    private String eventTitle;

    @Column(name ="description",nullable = false)
    private String description;

    @Column(name = "event_created_at",nullable = false)
    private LocalDateTime eventCreatedAt;

    @Column(name = "event_start_date_time",nullable = false)
    private LocalDateTime eventStartDateTime;

    @Column(name ="event_end_date_time",nullable = false)
    private LocalDateTime eventEndDateTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    private Location eventLocation;

    @Column(name = "max_participants")
    private Integer maxParticipants;

    @Column(name = "isLive",nullable = false)
    private boolean isLive = false;

    public Event() {

    }

    public Event(Integer eventId,User userId,String eventTitle,String description,LocalDateTime eventCreatedAt,LocalDateTime eventStartDateTime,LocalDateTime eventEndDateTime,Location eventLocation,Integer maxParticipants,Boolean isLive) {
        this.description=description;
        this.enventId=eventId;
        this.eventCreatedAt=eventEndDateTime;
        this.eventEndDateTime=eventEndDateTime;
        this.eventLocation=eventLocation;
        this.isLive=isLive;
        this.eventStartDateTime=eventStartDateTime;
        this.maxParticipants=maxParticipants;
        this.eventTitle=eventTitle;
    }

    
}

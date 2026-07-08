package com.app.api.models;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.app.api.models.User;

@Data
@Builder
@Entity
@Table(name = "availability_table")
public class Availability {

    /* The unique identifier for the availability entry. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "availability_id")
    private int availabilityid;

    /* The user associated with this availability entry. */
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user_id;

    /* The day of the week for this availability entry. */
    @Column(name = "day_of_week")
    private String dayofweek;
    /* The time window for this availability entry. */
    @Column(name = "time_window")
    private String timewindow;
    /* Indicates whether this availability entry is active. */
    @Column(name = "is_active")
    private boolean isactive;
    /**
     * Default constructor required by JPA.
     */
    public Availability() {
    }
    /**
     * Constructs an Availability entry with all fields specified.
     *
     * @param availabilityid the unique identifier for the availability entry
     * @param user           the user associated with this availability entry
     * @param dayofweek      the day of the week for this availability entry
     * @param timewindow     the time window for this availability entry
     * @param isactive       indicates whether this availability entry is active
     */
    public Availability(int availabilityid, User user_id, String dayofweek, String timewindow, boolean isactive) {
        this.availabilityid = availabilityid;
        this.user_id = user_id;
        this.dayofweek = dayofweek;
        this.timewindow = timewindow;
        this.isactive = isactive;
    }
}

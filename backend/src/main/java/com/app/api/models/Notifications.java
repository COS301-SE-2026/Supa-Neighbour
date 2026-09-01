package com.app.api.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "notification_table")
public class Notifications {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private int notificationid;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "notification_type")
    private String notificationtype;

    @Column(name = "entity_id")
    private String entityid;

    @Column(name = "notification_title")
    private String notificationtitle;

    @Column(name = "notification_body")
    private String notificationbody;

    @Column(name = "is_read")
    private boolean isread;

    @Column(name = "created_at")
    private LocalDateTime createdat;
}
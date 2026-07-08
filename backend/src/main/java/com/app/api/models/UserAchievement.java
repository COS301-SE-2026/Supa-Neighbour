package com.app.api.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;
import com.app.api.models.User;
import com.app.api.models.Badges;
import java.util.Date;
/**
 * Represents a user achievement within the application.
 * <p>
 * This entity maps to the {@code user_achievement_table} database table and
 * stores references to a user, achievement, and associated metadata.
 * </p>
 */
@Data
@Builder
@Entity
@Table(name = "user_achievement_table")
public class UserAchievement {
    
    /** the unique identifier of the user achievement */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_achievement_id")
    private int userAchievementId;

    /** the user associated with the achievement */
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User userId;

    /** the badge associated with the achievement */
    @ManyToOne
    @JoinColumn(name = "badge_id")
    private Badges badgeId;

    /** the date when the achievement was awarded */
    @ManyToOne
    @JoinColumn(name = "awarded_on")
    private Date awardedOn;

    /** the current progress towards the achievement */
    @Column(name = "progress_current")
    private int progressCurrent;

    /** the target progress required for the achievement */
    @Column(name = "progress_target")
    private int progressTarget;

    /** the default constructor required by JPA */
    public UserAchievement() {
    }

    /**
     * Constructs a user achievement with all fields.
     *
     * @param userAchievementId the unique identifier of the user achievement
     * @param userId the associated user
     * @param badgeId the associated badge
     * @param awardedOn the date when the achievement was awarded
     * @param progressCurrent the current progress towards the achievement
     * @param progressTarget the target progress required for the achievement
     */
    public UserAchievement(int userAchievementId, User userId, Badges badgeId, Date awardedOn, int progressCurrent, int progressTarget) {
        this.userAchievementId = userAchievementId;
        this.userId = userId;
        this.badgeId = badgeId;
        this.awardedOn = awardedOn;
        this.progressCurrent = progressCurrent;
        this.progressTarget = progressTarget;
    }
}

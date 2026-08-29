package com.app.api.events;

public class UserBannedEvent {
    private final int userId;
    private final String reportId;
    private final String reason;

        /**
     * Constructs a new UserBannedEvent with the specified details.
     * 
     * @param userId   the unique identifier of the user being permanently banned
     * @param reportId the unique identifier of the report that triggered this ban
     * @param reason   the reason for issuing the permanent ban
     * @throws IllegalArgumentException if userId is less than or equal to 0,
     *                                  or if reportId or reason is null or empty
    **/
    public UserBannedEvent(int userId, String reportId, String reason){
        this.userId = userId;
        this.reportId = reportId;
        this.reason = reason;
    }

    /**
     * Returns the unique identifier of the user who has been permanently banned.
     * 
     * @return the user ID as an integer
     */
    public int getUserId(){
        return userId;
    }

     /**
     * Returns the unique identifier of the report that triggered this ban event.
     * 
     * @return the report ID as a string
     */
    public String getReportId(){
        return reportId;
    }

    /**
     * Returns the reason for issuing the permanent ban to the user.
     * 
     * @return the ban reason as a string
     */
    public String getReason(){
        return reason;
    }
}

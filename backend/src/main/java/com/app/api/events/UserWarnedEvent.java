package com.app.api.events;

public class UserWarnedEvent {
    private final int userId;
    private final String reportId;
    private final String reason;

    /**
     * Constructs a new UserWarnedEvent with the specified details.
     * 
     * @param userId   the unique identifier of the user being warned
     * @param reportId the unique identifier of the report that triggered this warning
     * @param reason   the reason for issuing the warning
     * @throws IllegalArgumentException if userId is less than or equal to 0,
     *                                  or if reportId or reason is null or empty
     */
    public UserWarnedEvent(int userId, String reportId, String reason){
        this.userId = userId;
        this.reportId = reportId;
        this.reason = reason;
    }

    /**
     * Returns the unique identifier of the user who received the warning.
     * 
     * @return the user ID as an integer
     */
    public int getUserId(){
        return userId;
    }

    /**
     * Returns the unique identifier of the report that triggered this warning event.
     * 
     * @return the report ID as a string
     */
    public String getReportId(){
        return reportId;
    }

    /**
     * Returns the reason for issuing the warning to the user.
     * 
     * @return the warning reason as a string
     */
    public String getReason(){
        return reason;
    }
}

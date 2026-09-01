package com.app.api.events;

public class UserSuspendedEvent {
    private final int userId;
    private final String reportId;
    private final String reason;

    /**
     * Constructs a new UserSuspendedEvent with the specified details.
     * 
     * @param userId   the unique identifier of the user being temporarily suspended
     * @param reportId the unique identifier of the report that triggered this suspension
     * @param reason   the reason for issuing the temporary suspension
     * @throws IllegalArgumentException if userId is less than or equal to 0,
     *                                  or if reportId or reason is null or empty
     */
    public UserSuspendedEvent(int userId, String reportId, String reason){
        this.userId = userId;
        this.reportId = reportId;
        this.reason = reason;
    }

    /**
     * Returns the unique identifier of the user who has been temporarily suspended.
     * 
     * @return the user ID as an integer
     */
    public int getUserId(){
        return userId;
    }

    /**
     * Returns the unique identifier of the report that triggered this suspension event.
     * 
     * @return the report ID as a string
     */
    public String getReportId(){
        return reportId;
    }

    /**
     * Returns the reason for issuing the temporary suspension to the user.
     * 
     * @return the suspension reason as a string
     */
    public String getReason(){
        return reason;
    }
}

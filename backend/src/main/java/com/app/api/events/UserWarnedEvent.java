package com.app.api.events;

public class UserWarnedEvent {
    private final int userId;
    private final String reportId;
    private final String reason;

    public UserWarnedEvent(int userId, String reportId, String reason){
        this.userId = userId;
        this.reportId = reportId;
        this.reason = reason;
    }

    public int getUserId(){
        return userId;
    }

    public String getReportId(){
        return reportId;
    }

    public String getReason(){
        return reason;
    }
}

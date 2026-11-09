package com.scc.model;

import java.sql.Timestamp;

public class ActivityLog {
    private int id;
    private int userId;
    private String actionType;
    private String description;
    private Timestamp timestamp;

    public ActivityLog(int id, int userId, String actionType, String description, Timestamp timestamp) {
        this.id = id;
        this.userId = userId;
        this.actionType = actionType;
        this.description = description;
        this.timestamp = timestamp;
    }

    public int getId() { return id; }
    public int getUserId() { return userId; }
    public String getActionType() { return actionType; }
    public String getDescription() { return description; }
    public Timestamp getTimestamp() { return timestamp; }
}
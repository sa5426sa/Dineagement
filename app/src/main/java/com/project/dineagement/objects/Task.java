package com.project.dineagement.objects;

/**
 * An {@link Object} representation of a task.
 * @author Shaked Awad
 * @version 1.0
 * @since 3/20/2025
 */
public class Task {
    private int serialNum;
    private String taskName, taskDesc, dateCreated, dateDue, forUser;
    private int priority; // 0 - not important nor urgent, 1 - not important but urgent, 10 - important but not urgent, 11 - important and urgent
    private String createdByUser;

    /**
     * Instantiates a new Task.
     */
    public Task() {
    }

    /**
     * Instantiates a new Task.
     * @param serialNum The serial number
     * @param taskName The name
     * @param taskDesc The description
     * @param dateCreated The creation date
     * @param dateDue The due date
     * @param forUser The directed user
     * @param priority The priority
     * @param createdByUser The creator
     */
    public Task(int serialNum, String taskName, String taskDesc, String dateCreated, String dateDue, String forUser, int priority, String createdByUser) {
        this.serialNum = serialNum;
        this.taskName = taskName;
        this.taskDesc = taskDesc;
        this.dateCreated = dateCreated;
        this.dateDue = dateDue;
        this.forUser = forUser;
        this.priority = priority;
        this.createdByUser = createdByUser;
    }

    // getters and setters for each value
    public int getSerialNum() {
        return serialNum;
    }

    public void setSerialNum(int serialNum) {
        this.serialNum = serialNum;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskDesc() {
        return taskDesc;
    }

    public void setTaskDesc(String taskDesc) {
        this.taskDesc = taskDesc;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getDateDue() {
        return dateDue;
    }

    public void setDateDue(String dateDue) {
        this.dateDue = dateDue;
    }

    public String getForUser() {
        return forUser;
    }

    public void setForUser(String forUser) {
        this.forUser = forUser;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getCreatedByUser() {
        return createdByUser;
    }

    public void setCreatedByUser(String createdByUser) {
        this.createdByUser = createdByUser;
    }
}
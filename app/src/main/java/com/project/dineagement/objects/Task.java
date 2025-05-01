package com.project.dineagement.objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Objects;

public class Task {
    private int serialNum;
    private String taskName, taskDesc, dateCreated, dateDue, forUser;
    private int priority; // 0 - not important nor urgent, 1 - not important but urgent, 10 - important but not urgent, 11 - important and urgent
    private String createdByUser;

    public Task() {
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Task)) return false;
        Task task = (Task) o;
        return serialNum == task.serialNum && priority == task.priority && Objects.equals(taskName, task.taskName) && Objects.equals(taskDesc, task.taskDesc) && Objects.equals(dateCreated, task.dateCreated) && Objects.equals(dateDue, task.dateDue) && Objects.equals(forUser, task.forUser) && Objects.equals(createdByUser, task.createdByUser);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serialNum, taskName, taskDesc, dateCreated, dateDue, forUser, priority, createdByUser);
    }
}
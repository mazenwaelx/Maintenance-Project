package com.LMS.Learning_Management_System.entity;

import jakarta.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Entity
@Table(name ="notifications" )
public class Notifications {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int notificationsId;


    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "userId", referencedColumnName = "userId")
    private Users userId;

    private String message;

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private Date createdTime;
    public Notifications() {

    }
    public Notifications(int notificationsId, Users userId, String message, Date createdTime) {
        this.notificationsId = notificationsId;
        this.userId = userId;
        this.message = message;
    }
    public int getNotificationsId() {
        return notificationsId;
    }
    public void setNotificationsId(int notificationsId) {
        this.notificationsId = notificationsId;
    }
    public Users getUserId() {
        return userId;
    }
    public void setUserId(Users userId) {
        this.userId = userId;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public Date getCreatedTime() {
        return createdTime;
    }
    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    @Override
    public String toString() {
        return "Notifications{" +
                "notificationsId=" + notificationsId +
                ", userId=" + userId +
                ", message='" + message + '\'' +
                ", createdTime=" + createdTime +
                '}';
    }
}

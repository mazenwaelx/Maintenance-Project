package com.LMS.Learning_Management_System.entity;


import jakarta.persistence.*;

@Entity
@Table(name = "instructor")
public class Instructor {
    @Id
    private int userAccountId;

    @OneToOne
    @JoinColumn(name = "user_account_id")
    @MapsId
    private Users userId;

    private String firstName;
    private String lastName;
    public Instructor() {

    }
    public Instructor(int userAccountId, Users userId, String firstName, String lastName) {
        this.userAccountId = userAccountId;
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Instructor(Users savedUser) {
        this.userId = savedUser;
    }

    @Override
    public String toString() {
        return "Instructor{" +
                "userAccountId=" + userAccountId +
                ", userId=" + userId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }

}

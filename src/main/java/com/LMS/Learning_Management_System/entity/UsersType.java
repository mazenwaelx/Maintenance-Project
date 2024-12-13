package com.LMS.Learning_Management_System.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "users_type")
public class UsersType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int userTypeId;

    @Column(nullable = false, unique = true)
    private String userTypeName;

//    orphanRemoval = true is an option used in the @OneToMany or @OneToOne JPA annotations
//    that allows for automatic removal of child entities (orphaned entities)
//    when they are no longer referenced by their parent entity.
    @OneToMany(targetEntity = Users.class, mappedBy = "userType", cascade = CascadeType.ALL , orphanRemoval = true)
    private List<Users> users;

    public UsersType() {
    }

    public UsersType(int userTypeId, String userTypeName, List<Users> users) {
        this.userTypeId = userTypeId;
        this.userTypeName = userTypeName;
        this.users = users;
    }

    public int getUserTypeId() {
        return userTypeId;
    }

    public void setUserTypeId(int userTypeId) {
        this.userTypeId = userTypeId;
    }

    public String getUserTypeName() {
        return userTypeName;
    }

    public void setUserTypeName(String userTypeName) {
        this.userTypeName = userTypeName;
    }

    public List<Users> getUsers() {
        return users;
    }

    public void setUsers(List<Users> users) {
        this.users = users;
    }

    @Override
    public String toString() {
        return "UsersType{" +
                "userTypeId=" + userTypeId +
                ", userTypeName='" + userTypeName + '\'' +
                '}';
    }
}

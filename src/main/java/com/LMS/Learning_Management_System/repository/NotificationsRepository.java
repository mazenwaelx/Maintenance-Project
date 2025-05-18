package com.LMS.Learning_Management_System.repository;

import com.LMS.Learning_Management_System.entity.Notifications;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationsRepository extends JpaRepository<Notifications, Integer> {
    List<Notifications> findByUser_UserId(int userId);
}

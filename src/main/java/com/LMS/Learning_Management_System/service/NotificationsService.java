package com.LMS.Learning_Management_System.service;

import com.LMS.Learning_Management_System.entity.Notifications;
import com.LMS.Learning_Management_System.entity.Users;
import com.LMS.Learning_Management_System.repository.NotificationsRepository;
import com.LMS.Learning_Management_System.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class NotificationsService {

    private final NotificationsRepository notificationsRepository;
    private final UsersRepository usersRepository;

    @Autowired
    public NotificationsService(NotificationsRepository notificationsRepository, UsersRepository usersRepository) {
        this.notificationsRepository = notificationsRepository;
        this.usersRepository = usersRepository;
    }

    public List<String> getAllNotifications(int userId) {
        List<Notifications> notificationsList = notificationsRepository.findAll();

        List<String> notificationsMessage = new ArrayList<>();

        for (Notifications notification : notificationsList) {
            if (notification.getUser().getUserId() == userId) {
                notification.setRead(true);
                notificationsMessage.add(notification.getMessage());
                notificationsRepository.save(notification);
            }
        }

        return notificationsMessage;
    }

    public List<String> getAllUnreadNotifications(int userId) {
        List<Notifications> notificationsList = notificationsRepository.findAll();

        List<String> notificationsMessage = new ArrayList<>();

        for (Notifications notification : notificationsList) {
            if (notification.getUser().getUserId() == userId && !notification.isRead()) {
                notification.setRead(true);
                notificationsMessage.add(notification.getMessage());
                notificationsRepository.save(notification);
            }
        }

        return notificationsMessage;
    }

    public void sendNotification(String message, int id) {
        Users user = usersRepository.findById(id).get();
        Notifications enrollmentNotification = new Notifications();
        enrollmentNotification.setUser(user);
        enrollmentNotification.setRead(false);
        enrollmentNotification.setCreatedAt(new Date());
        enrollmentNotification.setMessage(message);
        notificationsRepository.save(enrollmentNotification);

    }


}

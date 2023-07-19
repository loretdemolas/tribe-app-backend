package com.savvato.tribeapp.controllers;

import com.savvato.tribeapp.dto.NotificationDTO;
import com.savvato.tribeapp.entities.Notification;
import com.savvato.tribeapp.services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/api/notifications")
public class NotificationAPIController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/{id}")
    public ResponseEntity<NotificationDTO> getNotificationById(@PathVariable Long id) {
        NotificationDTO notification = notificationService.getNotificationDTOById(id);
        return notification != null ? ResponseEntity.ok(notification) : ResponseEntity.notFound().build();
    }
    @PutMapping
    public ResponseEntity<String> updateNotification(@RequestBody Long id) {
        boolean isRead = notificationService.checkNotificationReadStatus(id);
        if (isRead) {
            return ResponseEntity.ok("Notification is already read");
        } else {
            notificationService.updateNotificationReadStatus(id);
            return ResponseEntity.ok("Notification read status updated");
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteNotification(@PathVariable Long id) {
        boolean exists = notificationService.checkNotificationExists(id);
        if (exists) {
            notificationService.deleteNotification(id);
            return ResponseEntity.ok("Notification deleted");
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @GetMapping("/user/{user_id}")
    public ResponseEntity<List<NotificationDTO>> getUserNotifications(@PathVariable Long user_id) {
        List<Notification> notifications = notificationService.getNotificationsByUserId(user_id);
        List<NotificationDTO> userNotificationDTOs = new ArrayList<>();

        for (Notification notification : notifications) {
            userNotificationDTOs.add(notificationService.getNotificationDTOById(notification.getId()));
        }

        return ResponseEntity.ok(userNotificationDTOs);
    }
}

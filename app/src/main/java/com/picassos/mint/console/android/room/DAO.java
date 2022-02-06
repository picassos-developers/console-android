package com.picassos.mint.console.android.room;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.picassos.mint.console.android.entities.NotificationEntity;

import java.util.List;

@Dao
public interface DAO {
    // notifications
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void requestInsertNotification(NotificationEntity notification);

    @Query("DELETE FROM notification WHERE id = :id")
    void requestDeleteNotification(long id);

    @Query("DELETE FROM notification")
    void requestDeleteAllNotification();

    @Query("SELECT * FROM notification ORDER BY created_at DESC")
    List<NotificationEntity> requestAllNotifications();

    @Query("SELECT * FROM notification WHERE id = :id LIMIT 1")
    NotificationEntity requestNotification(long id);

    @Query("SELECT COUNT(id) FROM notification WHERE read = 0")
    Integer requestNotificationUnreadCount();

    @Query("SELECT COUNT(id) FROM notification")
    Integer requestNotificationCount();
}

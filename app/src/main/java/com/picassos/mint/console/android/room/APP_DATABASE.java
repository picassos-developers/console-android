package com.picassos.mint.console.android.room;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

import com.picassos.mint.console.android.entities.AccountEntity;
import com.picassos.mint.console.android.entities.NotificationEntity;

@Database(entities = {NotificationEntity.class, AccountEntity.class}, version = 1, exportSchema = false)
public abstract class APP_DATABASE extends RoomDatabase {
    public abstract DAO requestDAO();

    private static APP_DATABASE INSTANCE;

    public static APP_DATABASE requestDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE =
                    Room.databaseBuilder(context, APP_DATABASE.class, "console_database")
                            .allowMainThreadQueries()
                            .fallbackToDestructiveMigration()
                            .build();
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }
}
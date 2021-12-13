package com.picassos.mint.console.android.room;

import android.database.Cursor;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.picassos.mint.console.android.entities.NotificationEntity;
import java.lang.Class;
import java.lang.Integer;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings({"unchecked", "deprecation"})
public final class DAO_Impl implements DAO {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<NotificationEntity> __insertionAdapterOfNotificationEntity;

  private final SharedSQLiteStatement __preparedStmtOfRequestDeleteNotification;

  private final SharedSQLiteStatement __preparedStmtOfRequestDeleteAllNotification;

  public DAO_Impl(RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfNotificationEntity = new EntityInsertionAdapter<NotificationEntity>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR REPLACE INTO `notification` (`id`,`title`,`content`,`obj_id`,`read`,`created_at`) VALUES (?,?,?,?,?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, NotificationEntity value) {
        if (value.id == null) {
          stmt.bindNull(1);
        } else {
          stmt.bindLong(1, value.id);
        }
        if (value.title == null) {
          stmt.bindNull(2);
        } else {
          stmt.bindString(2, value.title);
        }
        if (value.content == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindString(3, value.content);
        }
        if (value.obj_id == null) {
          stmt.bindNull(4);
        } else {
          stmt.bindLong(4, value.obj_id);
        }
        final Integer _tmp;
        _tmp = value.read == null ? null : (value.read ? 1 : 0);
        if (_tmp == null) {
          stmt.bindNull(5);
        } else {
          stmt.bindLong(5, _tmp);
        }
        if (value.created_at == null) {
          stmt.bindNull(6);
        } else {
          stmt.bindLong(6, value.created_at);
        }
      }
    };
    this.__preparedStmtOfRequestDeleteNotification = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "DELETE FROM notification WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfRequestDeleteAllNotification = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "DELETE FROM notification";
        return _query;
      }
    };
  }

  @Override
  public void requestInsertNotification(final NotificationEntity notification) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfNotificationEntity.insert(notification);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void requestDeleteNotification(final long id) {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOfRequestDeleteNotification.acquire();
    int _argIndex = 1;
    _stmt.bindLong(_argIndex, id);
    __db.beginTransaction();
    try {
      _stmt.executeUpdateDelete();
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
      __preparedStmtOfRequestDeleteNotification.release(_stmt);
    }
  }

  @Override
  public void requestDeleteAllNotification() {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOfRequestDeleteAllNotification.acquire();
    __db.beginTransaction();
    try {
      _stmt.executeUpdateDelete();
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
      __preparedStmtOfRequestDeleteAllNotification.release(_stmt);
    }
  }

  @Override
  public List<NotificationEntity> requestAllNotifications() {
    final String _sql = "SELECT * FROM notification ORDER BY created_at DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
      final int _cursorIndexOfContent = CursorUtil.getColumnIndexOrThrow(_cursor, "content");
      final int _cursorIndexOfObjId = CursorUtil.getColumnIndexOrThrow(_cursor, "obj_id");
      final int _cursorIndexOfRead = CursorUtil.getColumnIndexOrThrow(_cursor, "read");
      final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
      final List<NotificationEntity> _result = new ArrayList<NotificationEntity>(_cursor.getCount());
      while(_cursor.moveToNext()) {
        final NotificationEntity _item;
        _item = new NotificationEntity();
        if (_cursor.isNull(_cursorIndexOfId)) {
          _item.id = null;
        } else {
          _item.id = _cursor.getLong(_cursorIndexOfId);
        }
        if (_cursor.isNull(_cursorIndexOfTitle)) {
          _item.title = null;
        } else {
          _item.title = _cursor.getString(_cursorIndexOfTitle);
        }
        if (_cursor.isNull(_cursorIndexOfContent)) {
          _item.content = null;
        } else {
          _item.content = _cursor.getString(_cursorIndexOfContent);
        }
        if (_cursor.isNull(_cursorIndexOfObjId)) {
          _item.obj_id = null;
        } else {
          _item.obj_id = _cursor.getLong(_cursorIndexOfObjId);
        }
        final Integer _tmp;
        if (_cursor.isNull(_cursorIndexOfRead)) {
          _tmp = null;
        } else {
          _tmp = _cursor.getInt(_cursorIndexOfRead);
        }
        _item.read = _tmp == null ? null : _tmp != 0;
        if (_cursor.isNull(_cursorIndexOfCreatedAt)) {
          _item.created_at = null;
        } else {
          _item.created_at = _cursor.getLong(_cursorIndexOfCreatedAt);
        }
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public NotificationEntity requestNotification(final long id) {
    final String _sql = "SELECT * FROM notification WHERE id = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
      final int _cursorIndexOfContent = CursorUtil.getColumnIndexOrThrow(_cursor, "content");
      final int _cursorIndexOfObjId = CursorUtil.getColumnIndexOrThrow(_cursor, "obj_id");
      final int _cursorIndexOfRead = CursorUtil.getColumnIndexOrThrow(_cursor, "read");
      final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
      final NotificationEntity _result;
      if(_cursor.moveToFirst()) {
        _result = new NotificationEntity();
        if (_cursor.isNull(_cursorIndexOfId)) {
          _result.id = null;
        } else {
          _result.id = _cursor.getLong(_cursorIndexOfId);
        }
        if (_cursor.isNull(_cursorIndexOfTitle)) {
          _result.title = null;
        } else {
          _result.title = _cursor.getString(_cursorIndexOfTitle);
        }
        if (_cursor.isNull(_cursorIndexOfContent)) {
          _result.content = null;
        } else {
          _result.content = _cursor.getString(_cursorIndexOfContent);
        }
        if (_cursor.isNull(_cursorIndexOfObjId)) {
          _result.obj_id = null;
        } else {
          _result.obj_id = _cursor.getLong(_cursorIndexOfObjId);
        }
        final Integer _tmp;
        if (_cursor.isNull(_cursorIndexOfRead)) {
          _tmp = null;
        } else {
          _tmp = _cursor.getInt(_cursorIndexOfRead);
        }
        _result.read = _tmp == null ? null : _tmp != 0;
        if (_cursor.isNull(_cursorIndexOfCreatedAt)) {
          _result.created_at = null;
        } else {
          _result.created_at = _cursor.getLong(_cursorIndexOfCreatedAt);
        }
      } else {
        _result = null;
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public Integer requestNotificationUnreadCount() {
    final String _sql = "SELECT COUNT(id) FROM notification WHERE read = 0";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final Integer _result;
      if(_cursor.moveToFirst()) {
        final Integer _tmp;
        if (_cursor.isNull(0)) {
          _tmp = null;
        } else {
          _tmp = _cursor.getInt(0);
        }
        _result = _tmp;
      } else {
        _result = null;
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public Integer requestNotificationCount() {
    final String _sql = "SELECT COUNT(id) FROM notification";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final Integer _result;
      if(_cursor.moveToFirst()) {
        final Integer _tmp;
        if (_cursor.isNull(0)) {
          _tmp = null;
        } else {
          _tmp = _cursor.getInt(0);
        }
        _result = _tmp;
      } else {
        _result = null;
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}

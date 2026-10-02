package com.hightech.foodguard.data;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class MealGuidelineDao_Impl implements MealGuidelineDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<MealGuideline> __insertionAdapterOfMealGuideline;

  private final Converters __converters = new Converters();

  private final EntityDeletionOrUpdateAdapter<MealGuideline> __deletionAdapterOfMealGuideline;

  private final EntityDeletionOrUpdateAdapter<MealGuideline> __updateAdapterOfMealGuideline;

  private final SharedSQLiteStatement __preparedStmtOfDeleteById;

  private final SharedSQLiteStatement __preparedStmtOfClearAll;

  public MealGuidelineDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfMealGuideline = new EntityInsertionAdapter<MealGuideline>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `meal_guidelines` (`id`,`mealType`,`title`,`components`,`guidelines`,`dayOrTag`,`orderIndex`) VALUES (nullif(?, 0),?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final MealGuideline entity) {
        statement.bindLong(1, entity.getId());
        final String _tmp = __converters.fromMealType(entity.getMealType());
        statement.bindString(2, _tmp);
        statement.bindString(3, entity.getTitle());
        statement.bindString(4, entity.getComponents());
        statement.bindString(5, entity.getGuidelines());
        statement.bindString(6, entity.getDayOrTag());
        statement.bindLong(7, entity.getOrderIndex());
      }
    };
    this.__deletionAdapterOfMealGuideline = new EntityDeletionOrUpdateAdapter<MealGuideline>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `meal_guidelines` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final MealGuideline entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfMealGuideline = new EntityDeletionOrUpdateAdapter<MealGuideline>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `meal_guidelines` SET `id` = ?,`mealType` = ?,`title` = ?,`components` = ?,`guidelines` = ?,`dayOrTag` = ?,`orderIndex` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final MealGuideline entity) {
        statement.bindLong(1, entity.getId());
        final String _tmp = __converters.fromMealType(entity.getMealType());
        statement.bindString(2, _tmp);
        statement.bindString(3, entity.getTitle());
        statement.bindString(4, entity.getComponents());
        statement.bindString(5, entity.getGuidelines());
        statement.bindString(6, entity.getDayOrTag());
        statement.bindLong(7, entity.getOrderIndex());
        statement.bindLong(8, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteById = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM meal_guidelines WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfClearAll = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM meal_guidelines";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final MealGuideline item, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfMealGuideline.insertAndReturnId(item);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertAll(final List<MealGuideline> items,
      final Continuation<? super List<Long>> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<List<Long>>() {
      @Override
      @NonNull
      public List<Long> call() throws Exception {
        __db.beginTransaction();
        try {
          final List<Long> _result = __insertionAdapterOfMealGuideline.insertAndReturnIdsList(items);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final MealGuideline item, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfMealGuideline.handle(item);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final MealGuideline item, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfMealGuideline.handle(item);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteById(final long id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteById.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteById.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object clearAll(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfClearAll.acquire();
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfClearAll.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<MealGuideline>> observeAll() {
    final String _sql = "SELECT * FROM meal_guidelines ORDER BY mealType, id";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"meal_guidelines"}, new Callable<List<MealGuideline>>() {
      @Override
      @NonNull
      public List<MealGuideline> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfMealType = CursorUtil.getColumnIndexOrThrow(_cursor, "mealType");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfComponents = CursorUtil.getColumnIndexOrThrow(_cursor, "components");
          final int _cursorIndexOfGuidelines = CursorUtil.getColumnIndexOrThrow(_cursor, "guidelines");
          final int _cursorIndexOfDayOrTag = CursorUtil.getColumnIndexOrThrow(_cursor, "dayOrTag");
          final int _cursorIndexOfOrderIndex = CursorUtil.getColumnIndexOrThrow(_cursor, "orderIndex");
          final List<MealGuideline> _result = new ArrayList<MealGuideline>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final MealGuideline _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final MealType _tmpMealType;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfMealType);
            _tmpMealType = __converters.toMealType(_tmp);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpComponents;
            _tmpComponents = _cursor.getString(_cursorIndexOfComponents);
            final String _tmpGuidelines;
            _tmpGuidelines = _cursor.getString(_cursorIndexOfGuidelines);
            final String _tmpDayOrTag;
            _tmpDayOrTag = _cursor.getString(_cursorIndexOfDayOrTag);
            final int _tmpOrderIndex;
            _tmpOrderIndex = _cursor.getInt(_cursorIndexOfOrderIndex);
            _item = new MealGuideline(_tmpId,_tmpMealType,_tmpTitle,_tmpComponents,_tmpGuidelines,_tmpDayOrTag,_tmpOrderIndex);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<MealGuideline>> observeByType(final MealType type) {
    final String _sql = "SELECT * FROM meal_guidelines WHERE mealType = ? ORDER BY id";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    final String _tmp = __converters.fromMealType(type);
    _statement.bindString(_argIndex, _tmp);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"meal_guidelines"}, new Callable<List<MealGuideline>>() {
      @Override
      @NonNull
      public List<MealGuideline> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfMealType = CursorUtil.getColumnIndexOrThrow(_cursor, "mealType");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfComponents = CursorUtil.getColumnIndexOrThrow(_cursor, "components");
          final int _cursorIndexOfGuidelines = CursorUtil.getColumnIndexOrThrow(_cursor, "guidelines");
          final int _cursorIndexOfDayOrTag = CursorUtil.getColumnIndexOrThrow(_cursor, "dayOrTag");
          final int _cursorIndexOfOrderIndex = CursorUtil.getColumnIndexOrThrow(_cursor, "orderIndex");
          final List<MealGuideline> _result = new ArrayList<MealGuideline>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final MealGuideline _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final MealType _tmpMealType;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfMealType);
            _tmpMealType = __converters.toMealType(_tmp_1);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpComponents;
            _tmpComponents = _cursor.getString(_cursorIndexOfComponents);
            final String _tmpGuidelines;
            _tmpGuidelines = _cursor.getString(_cursorIndexOfGuidelines);
            final String _tmpDayOrTag;
            _tmpDayOrTag = _cursor.getString(_cursorIndexOfDayOrTag);
            final int _tmpOrderIndex;
            _tmpOrderIndex = _cursor.getInt(_cursorIndexOfOrderIndex);
            _item = new MealGuideline(_tmpId,_tmpMealType,_tmpTitle,_tmpComponents,_tmpGuidelines,_tmpDayOrTag,_tmpOrderIndex);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getAll(final Continuation<? super List<MealGuideline>> $completion) {
    final String _sql = "SELECT * FROM meal_guidelines ORDER BY mealType, id";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<MealGuideline>>() {
      @Override
      @NonNull
      public List<MealGuideline> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfMealType = CursorUtil.getColumnIndexOrThrow(_cursor, "mealType");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfComponents = CursorUtil.getColumnIndexOrThrow(_cursor, "components");
          final int _cursorIndexOfGuidelines = CursorUtil.getColumnIndexOrThrow(_cursor, "guidelines");
          final int _cursorIndexOfDayOrTag = CursorUtil.getColumnIndexOrThrow(_cursor, "dayOrTag");
          final int _cursorIndexOfOrderIndex = CursorUtil.getColumnIndexOrThrow(_cursor, "orderIndex");
          final List<MealGuideline> _result = new ArrayList<MealGuideline>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final MealGuideline _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final MealType _tmpMealType;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfMealType);
            _tmpMealType = __converters.toMealType(_tmp);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpComponents;
            _tmpComponents = _cursor.getString(_cursorIndexOfComponents);
            final String _tmpGuidelines;
            _tmpGuidelines = _cursor.getString(_cursorIndexOfGuidelines);
            final String _tmpDayOrTag;
            _tmpDayOrTag = _cursor.getString(_cursorIndexOfDayOrTag);
            final int _tmpOrderIndex;
            _tmpOrderIndex = _cursor.getInt(_cursorIndexOfOrderIndex);
            _item = new MealGuideline(_tmpId,_tmpMealType,_tmpTitle,_tmpComponents,_tmpGuidelines,_tmpDayOrTag,_tmpOrderIndex);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}

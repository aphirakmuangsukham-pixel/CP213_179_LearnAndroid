package com.example.flashcard.data.local;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Exception;
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
public final class StudyStatDao_Impl implements StudyStatDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<StudyStat> __insertionAdapterOfStudyStat;

  public StudyStatDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfStudyStat = new EntityInsertionAdapter<StudyStat>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `study_stats` (`date`,`categoryId`,`cardsStudied`,`correctAnswers`,`studyMinutes`) VALUES (?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final StudyStat entity) {
        statement.bindString(1, entity.getDate());
        statement.bindLong(2, entity.getCategoryId());
        statement.bindLong(3, entity.getCardsStudied());
        statement.bindLong(4, entity.getCorrectAnswers());
        statement.bindLong(5, entity.getStudyMinutes());
      }
    };
  }

  @Override
  public Object insertOrUpdateStat(final StudyStat stat,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfStudyStat.insert(stat);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<StudyStat>> getAllStats() {
    final String _sql = "SELECT * FROM study_stats ORDER BY date DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"study_stats"}, new Callable<List<StudyStat>>() {
      @Override
      @NonNull
      public List<StudyStat> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfCategoryId = CursorUtil.getColumnIndexOrThrow(_cursor, "categoryId");
          final int _cursorIndexOfCardsStudied = CursorUtil.getColumnIndexOrThrow(_cursor, "cardsStudied");
          final int _cursorIndexOfCorrectAnswers = CursorUtil.getColumnIndexOrThrow(_cursor, "correctAnswers");
          final int _cursorIndexOfStudyMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "studyMinutes");
          final List<StudyStat> _result = new ArrayList<StudyStat>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final StudyStat _item;
            final String _tmpDate;
            _tmpDate = _cursor.getString(_cursorIndexOfDate);
            final int _tmpCategoryId;
            _tmpCategoryId = _cursor.getInt(_cursorIndexOfCategoryId);
            final int _tmpCardsStudied;
            _tmpCardsStudied = _cursor.getInt(_cursorIndexOfCardsStudied);
            final int _tmpCorrectAnswers;
            _tmpCorrectAnswers = _cursor.getInt(_cursorIndexOfCorrectAnswers);
            final int _tmpStudyMinutes;
            _tmpStudyMinutes = _cursor.getInt(_cursorIndexOfStudyMinutes);
            _item = new StudyStat(_tmpDate,_tmpCategoryId,_tmpCardsStudied,_tmpCorrectAnswers,_tmpStudyMinutes);
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
  public Flow<List<StudyStat>> getStatsByCategory(final int categoryId) {
    final String _sql = "SELECT * FROM study_stats WHERE categoryId = ? ORDER BY date DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, categoryId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"study_stats"}, new Callable<List<StudyStat>>() {
      @Override
      @NonNull
      public List<StudyStat> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfCategoryId = CursorUtil.getColumnIndexOrThrow(_cursor, "categoryId");
          final int _cursorIndexOfCardsStudied = CursorUtil.getColumnIndexOrThrow(_cursor, "cardsStudied");
          final int _cursorIndexOfCorrectAnswers = CursorUtil.getColumnIndexOrThrow(_cursor, "correctAnswers");
          final int _cursorIndexOfStudyMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "studyMinutes");
          final List<StudyStat> _result = new ArrayList<StudyStat>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final StudyStat _item;
            final String _tmpDate;
            _tmpDate = _cursor.getString(_cursorIndexOfDate);
            final int _tmpCategoryId;
            _tmpCategoryId = _cursor.getInt(_cursorIndexOfCategoryId);
            final int _tmpCardsStudied;
            _tmpCardsStudied = _cursor.getInt(_cursorIndexOfCardsStudied);
            final int _tmpCorrectAnswers;
            _tmpCorrectAnswers = _cursor.getInt(_cursorIndexOfCorrectAnswers);
            final int _tmpStudyMinutes;
            _tmpStudyMinutes = _cursor.getInt(_cursorIndexOfStudyMinutes);
            _item = new StudyStat(_tmpDate,_tmpCategoryId,_tmpCardsStudied,_tmpCorrectAnswers,_tmpStudyMinutes);
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
  public Object getStatByDateAndCategory(final String date, final int categoryId,
      final Continuation<? super StudyStat> $completion) {
    final String _sql = "SELECT * FROM study_stats WHERE date = ? AND categoryId = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, date);
    _argIndex = 2;
    _statement.bindLong(_argIndex, categoryId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<StudyStat>() {
      @Override
      @Nullable
      public StudyStat call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfCategoryId = CursorUtil.getColumnIndexOrThrow(_cursor, "categoryId");
          final int _cursorIndexOfCardsStudied = CursorUtil.getColumnIndexOrThrow(_cursor, "cardsStudied");
          final int _cursorIndexOfCorrectAnswers = CursorUtil.getColumnIndexOrThrow(_cursor, "correctAnswers");
          final int _cursorIndexOfStudyMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "studyMinutes");
          final StudyStat _result;
          if (_cursor.moveToFirst()) {
            final String _tmpDate;
            _tmpDate = _cursor.getString(_cursorIndexOfDate);
            final int _tmpCategoryId;
            _tmpCategoryId = _cursor.getInt(_cursorIndexOfCategoryId);
            final int _tmpCardsStudied;
            _tmpCardsStudied = _cursor.getInt(_cursorIndexOfCardsStudied);
            final int _tmpCorrectAnswers;
            _tmpCorrectAnswers = _cursor.getInt(_cursorIndexOfCorrectAnswers);
            final int _tmpStudyMinutes;
            _tmpStudyMinutes = _cursor.getInt(_cursorIndexOfStudyMinutes);
            _result = new StudyStat(_tmpDate,_tmpCategoryId,_tmpCardsStudied,_tmpCorrectAnswers,_tmpStudyMinutes);
          } else {
            _result = null;
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

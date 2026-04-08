package com.example.flashcard.data.local;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
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
public final class FlashCardDao_Impl implements FlashCardDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<FlashCard> __insertionAdapterOfFlashCard;

  private final EntityDeletionOrUpdateAdapter<FlashCard> __deletionAdapterOfFlashCard;

  private final EntityDeletionOrUpdateAdapter<FlashCard> __updateAdapterOfFlashCard;

  public FlashCardDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfFlashCard = new EntityInsertionAdapter<FlashCard>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `flashcards` (`id`,`categoryId`,`frontText`,`backText`) VALUES (nullif(?, 0),?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final FlashCard entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getCategoryId());
        statement.bindString(3, entity.getFrontText());
        statement.bindString(4, entity.getBackText());
      }
    };
    this.__deletionAdapterOfFlashCard = new EntityDeletionOrUpdateAdapter<FlashCard>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `flashcards` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final FlashCard entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfFlashCard = new EntityDeletionOrUpdateAdapter<FlashCard>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `flashcards` SET `id` = ?,`categoryId` = ?,`frontText` = ?,`backText` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final FlashCard entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getCategoryId());
        statement.bindString(3, entity.getFrontText());
        statement.bindString(4, entity.getBackText());
        statement.bindLong(5, entity.getId());
      }
    };
  }

  @Override
  public Object insertCard(final FlashCard flashCard,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfFlashCard.insert(flashCard);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteCard(final FlashCard flashCard,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfFlashCard.handle(flashCard);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateCard(final FlashCard flashCard,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfFlashCard.handle(flashCard);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<FlashCard>> getCardsByCategory(final int categoryId) {
    final String _sql = "SELECT * FROM flashcards WHERE categoryId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, categoryId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"flashcards"}, new Callable<List<FlashCard>>() {
      @Override
      @NonNull
      public List<FlashCard> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfCategoryId = CursorUtil.getColumnIndexOrThrow(_cursor, "categoryId");
          final int _cursorIndexOfFrontText = CursorUtil.getColumnIndexOrThrow(_cursor, "frontText");
          final int _cursorIndexOfBackText = CursorUtil.getColumnIndexOrThrow(_cursor, "backText");
          final List<FlashCard> _result = new ArrayList<FlashCard>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final FlashCard _item;
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final int _tmpCategoryId;
            _tmpCategoryId = _cursor.getInt(_cursorIndexOfCategoryId);
            final String _tmpFrontText;
            _tmpFrontText = _cursor.getString(_cursorIndexOfFrontText);
            final String _tmpBackText;
            _tmpBackText = _cursor.getString(_cursorIndexOfBackText);
            _item = new FlashCard(_tmpId,_tmpCategoryId,_tmpFrontText,_tmpBackText);
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}

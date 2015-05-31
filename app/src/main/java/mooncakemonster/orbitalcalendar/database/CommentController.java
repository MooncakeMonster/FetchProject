package mooncakemonster.orbitalcalendar.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class CommentController
{
    // Database fields
    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;
    private String[] allColumns = { DatabaseHelper.COLUMN_ID,
                                    DatabaseHelper.EVENT,
                                    DatabaseHelper.DATE,
                                    DatabaseHelper.LOCATION,
                                    DatabaseHelper.NOTES,
                                    DatabaseHelper.REMIND,
                                  };

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Comment createComment(String event, int date, String location, String notes, String remind) {
        ContentValues values = new ContentValues();
        //Insert key-value in ContentValues
        values.put(DatabaseHelper.EVENT, event);
        values.put(DatabaseHelper.DATE, date);
        values.put(DatabaseHelper.LOCATION, location);
        values.put(DatabaseHelper.NOTES, notes);
        values.put(DatabaseHelper.REMIND, remind);

        long insertId = database.insert(DatabaseHelper.DATABASE_NAME, null, values);
        Cursor cursor = database.query(DatabaseHelper.DATABASE_NAME, allColumns,
                DatabaseHelper.COLUMN_ID + " = " + insertId, null, null, null, null);
        //Sort the table after insertion
        //database.rawQuery()
        cursor.moveToFirst();
        Comment newComment = cursorToComment(cursor);
        cursor.close();
        return newComment;
    }

    public void deleteComment(Comment comment) {
        long id = comment.getId();
        System.out.println("Comment deleted with id: " + id);
        database.delete(DatabaseHelper.DATABASE_NAME, DatabaseHelper.COLUMN_ID + " = " + id, null);
    }

    public List<Comment> getAllAppointment() {
        List<Comment> comments = new ArrayList<Comment>();

        Cursor cursor = database.query(DatabaseHelper.DATABASE_NAME,allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Comment comment = cursorToComment(cursor);
            comments.add(comment);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return comments;
    }

    private Comment cursorToComment(Cursor cursor) {
        Comment comment = new Comment();
        comment.setId(cursor.getLong(0));
        comment.setComment(cursor.getString(1));
        return comment;
    }

}

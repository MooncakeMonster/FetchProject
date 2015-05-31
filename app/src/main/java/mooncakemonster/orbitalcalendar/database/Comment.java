package mooncakemonster.orbitalcalendar.database;

/*
 * This class is our model and contains the data we will save in the database and show in the user interface.
 * Adapted from: http://www.vogella.com/tutorials/AndroidSQLite/article.html#sqliteoverview_sqliteopenhelper
 */

public class Comment {
    private long id;
    private String comment;
    private int number;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getInt() {
        return number;
    }

    public void setInt(int number) {
        this.number = number;
    }

    // Will be used by the ArrayAdapter in the ListView
    @Override
    public String toString() {
        if(comment == null)
        {
            return "" + number;
        }

        else
            return comment;
    }
}

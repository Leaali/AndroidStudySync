package ch.lw.myapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class DbHelper extends SQLiteOpenHelper {
    private final Context context;
    private static final String DATABASE_NAME = "Grade.db";
    private static final int DATABASE_VERSION = 2;

    //Semester Table
    private static final String TABLE_NAME = "my_semester";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_TITLE = "semester_name";

    //Subject Table
    private static final String TABLE_SUBJECTS = "my_subjects";
    private static final String COLUMN_SUBJECT_ID = "subject_id";
    private static final String COLUMN_SEMESTER_ID = "semester_id";
    private static final String COLUMN_SUBJECT_TITLE = "subject_title";
    private static final String COLUMN_SUBJECT_GRADES = "subject_grades";
    private static final String COLUMN_SUBJECT_AVERAGE = "subject_average";


    public DbHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME +
                " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TITLE + " TEXT);";
        db.execSQL(query);
        String subjectTableQuery = "CREATE TABLE " + TABLE_SUBJECTS +
                " (" + COLUMN_SUBJECT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_SEMESTER_ID + " INTEGER, " +
                COLUMN_SUBJECT_TITLE + " TEXT, " +
                COLUMN_SUBJECT_GRADES + " TEXT, " +
                COLUMN_SUBJECT_AVERAGE + " REAL);";
        db.execSQL(subjectTableQuery);
    }

    //----------------------------------------Semester-------------------------------------------
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    void addSemester(String title) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_TITLE, title);
        long result = db.insert(TABLE_NAME, null, cv);
        if (result == -1) {
            Toast.makeText(context, "Fehler, das Semester konnte nicht hinzugefügt werden.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Erfolgreich hinzugefügt!", Toast.LENGTH_SHORT).show();
        }
    }

    Cursor readAllData() {
        String query = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }

    void updateData(String row_id, String title) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_TITLE, title);

        long result = db.update(TABLE_NAME, cv, "_id=?", new String[]{row_id}); // update command für SLQLite
        if (result == -1) {
            Toast.makeText(context, "Fehler, das Semester konnte nicht angepasst werden.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Erfolgreich angepasst!", Toast.LENGTH_SHORT).show();
        }
    }

    void deleteOneRow(String row_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete(TABLE_NAME, "_id=?", new String[]{row_id});
        if (result == -1) {
            Toast.makeText(context, "Fehler, das Semester konnte nicht gelöscht werden.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Erfolgreich gelöscht.", Toast.LENGTH_SHORT).show();
        }
    }


    //----------------------------------------Subject-------------------------------------------
    void addSubject(long semesterId, String title) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_SEMESTER_ID, semesterId);
        cv.put(COLUMN_SUBJECT_TITLE, title);
        cv.put(COLUMN_SUBJECT_GRADES, "");
        cv.put(COLUMN_SUBJECT_AVERAGE, 0.0);

        long result = db.insert(TABLE_SUBJECTS, null, cv);
        if (result == -1) {
            Toast.makeText(context, "Fehler, das Fach konnte nicht hinzugefügt werden.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Erfolgreich hinzugefügt!", Toast.LENGTH_SHORT).show();
        }
    }

    Cursor readAllSubjectData(int semesterId) {
        String query = "SELECT * FROM " + TABLE_SUBJECTS +
                " WHERE " + COLUMN_SEMESTER_ID + " = " + semesterId;
        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery(query, null);
    }

    void updateSubject(int subjectId, String title, String grades, double average) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_SUBJECT_TITLE, title);
        cv.put(COLUMN_SUBJECT_GRADES, grades);
        cv.put(COLUMN_SUBJECT_AVERAGE, average);

        long result = db.update(TABLE_SUBJECTS, cv, COLUMN_SUBJECT_ID + "=?", new String[]{String.valueOf(subjectId)});
        if (result == -1) {
            Toast.makeText(context, "Fehler, das Fach konnte nicht aktualisiert werden.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Erfolgreich aktualisiert!", Toast.LENGTH_SHORT).show();
        }
    }

    void deleteSubject(int subjectId) {
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete(TABLE_SUBJECTS, COLUMN_SUBJECT_ID + "=?", new String[]{String.valueOf(subjectId)});
        if (result == -1) {
            Toast.makeText(context, "Fehler, das Fach konnte nicht gelöscht werden.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Erfolgreich gelöscht.", Toast.LENGTH_SHORT).show();
        }
    }
}

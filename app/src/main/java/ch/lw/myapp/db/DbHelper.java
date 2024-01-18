package ch.lw.myapp.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DbHelper extends SQLiteOpenHelper {
    private final Context context;
    private static final String DATABASE_NAME = "Grade.db";
    private static final int DATABASE_VERSION = 4;

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

    //Grade Table
    private static final String TABLE_GRADES = "my_grades";
    public static final String COLUMN_GRADE_ID = "grade_id";
    public static final String COLUMN_SUBJECT_ID_GRADE = "subject_id";
    public static final String COLUMN_GRADE_TITLE = "grade_title";
    public static final String COLUMN_GRADE_WEIGHTING = "grade_weighting";
    public static final String COLUMN_GRADE_VALUE = "grade_value";

    // Exam Table
    private static final String TABLE_EXAMS = "my_exams";
    public static final String COLUMN_EXAM_ID = "exam_id";
    public static final String COLUMN_EXAM_DATE = "exam_date";
    public static final String COLUMN_EXAM_SUBJECT = "exam_subject";
    public static final String COLUMN_EXAM_DESCRIPTION = "exam_description";


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
        String gradeTableQuery = "CREATE TABLE " + TABLE_GRADES +
                " (" + COLUMN_GRADE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_SUBJECT_ID_GRADE + " INTEGER, " +
                COLUMN_GRADE_TITLE + " TEXT, " +
                COLUMN_GRADE_WEIGHTING + " REAL, " +
                COLUMN_GRADE_VALUE + " REAL);";
        db.execSQL(gradeTableQuery);
        String examTableQuery = "CREATE TABLE " + TABLE_EXAMS +
                " (" + COLUMN_EXAM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_EXAM_DATE + " DATE, " +
                COLUMN_EXAM_SUBJECT + " TEXT, " +
                COLUMN_EXAM_DESCRIPTION + " TEXT);";
        db.execSQL(examTableQuery);
    }

    //----------------------------------------Semester-------------------------------------------
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SUBJECTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GRADES);
        onCreate(db);
    }

    public void addSemester(String title) {
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

    public Cursor readAllData() {
        String query = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }

    public void updateData(int row_id, String title) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_TITLE, title);

        long result = db.update(TABLE_NAME, cv, "_id=?", new String[]{String.valueOf(row_id)}); // update command für SLQLite
        if (result == -1) {
            Toast.makeText(context, "Fehler, das Semester konnte nicht angepasst werden.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Erfolgreich angepasst!", Toast.LENGTH_SHORT).show();
        }
    }

    public void deleteOneRow(int row_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        List<Integer> subjectIds = getAllSubjectsInSemester(row_id);
        for (int subjectId : subjectIds) {
            deleteSubject(subjectId);
        }
        long result = db.delete(TABLE_NAME, "_id=?", new String[]{String.valueOf(row_id)});
        if (result == -1) {
            Toast.makeText(context, "Fehler, das Semester konnte nicht gelöscht werden.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Erfolgreich gelöscht.", Toast.LENGTH_SHORT).show();
        }
    }

    List<Integer> getAllSubjectsInSemester(int semesterId) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Integer> subjectIds = new ArrayList<>();

        Cursor cursor = db.query(
                TABLE_SUBJECTS,
                new String[]{COLUMN_SUBJECT_ID},
                COLUMN_SEMESTER_ID + "=?",
                new String[]{String.valueOf(semesterId)},
                null,
                null,
                null
        );

        if (cursor != null) {
            int columnIndex = cursor.getColumnIndex(COLUMN_SUBJECT_ID);

            if (columnIndex != -1 && cursor.moveToFirst()) {
                do {
                    subjectIds.add(cursor.getInt(columnIndex));
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return subjectIds;
    }


    //----------------------------------------Subject-------------------------------------------
    public void addSubject(int semesterId, String title) {
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

    public Cursor readAllSubjectData(int semesterId) {
        String query = "SELECT * FROM " + TABLE_SUBJECTS +
                " WHERE " + COLUMN_SEMESTER_ID + " = " + semesterId;
        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery(query, null);
    }

    public void updateSubject(int subjectId, String title) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_SUBJECT_TITLE, title);

        long result = db.update(TABLE_SUBJECTS, cv, COLUMN_SUBJECT_ID + "=?", new String[]{String.valueOf(subjectId)});
        if (result == -1) {
            Toast.makeText(context, "Fehler, das Fach konnte nicht aktualisiert werden.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Erfolgreich aktualisiert!", Toast.LENGTH_SHORT).show();
        }
    }

    public void deleteSubject(int subjectId) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_GRADES, COLUMN_SUBJECT_ID + "=?", new String[]{String.valueOf(subjectId)});
        long result = db.delete(TABLE_SUBJECTS, COLUMN_SUBJECT_ID + "=?", new String[]{String.valueOf(subjectId)});
        if (result == -1) {
            Toast.makeText(context, "Fehler, das Fach konnte nicht gelöscht werden.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Erfolgreich gelöscht.", Toast.LENGTH_SHORT).show();
        }
    }

    public void updateSubjectAverage(int subjectId, double average) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_SUBJECT_AVERAGE, average);

        long result = db.update(TABLE_SUBJECTS, cv, COLUMN_SUBJECT_ID + "=?", new String[]{String.valueOf(subjectId)});
        if (result == -1) {
            Toast.makeText(context, "Fehler, der Durchschnitt konnte nicht aktualisiert werden.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Erfolgreich aktualisiert!", Toast.LENGTH_SHORT).show();
        }
    }
    //----------------------------------------Grade-------------------------------------------

    public long addGrade(long subjectId, String title, double weighting, double value) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_SUBJECT_ID_GRADE, subjectId);
        cv.put(COLUMN_GRADE_TITLE, title);
        cv.put(COLUMN_GRADE_WEIGHTING, weighting);
        cv.put(COLUMN_GRADE_VALUE, value);

        return db.insert(TABLE_GRADES, null, cv);
    }

    public Cursor readAllGrades(int subjectId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_GRADES +
                " WHERE " + COLUMN_SUBJECT_ID_GRADE + " = " + subjectId;
        return db.rawQuery(query, null);
    }

    public void deleteGrade(int gradeId) {
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete(TABLE_GRADES, COLUMN_GRADE_ID + "=?", new String[]{String.valueOf(gradeId)});
        if (result == -1) {
            Toast.makeText(context, "Fehler, die Note konnte nicht gelöscht werden.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Erfolgreich gelöscht.", Toast.LENGTH_SHORT).show();
        }
    }

    public void updateGrade(int gradeId, String newTitle, double newWeighting, double newValue) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_GRADE_TITLE, newTitle);
        cv.put(COLUMN_GRADE_WEIGHTING, newWeighting);
        cv.put(COLUMN_GRADE_VALUE, newValue);

        long result = db.update(TABLE_GRADES, cv, COLUMN_GRADE_ID + "=?", new String[]{String.valueOf(gradeId)});

        if (result == -1) {
            Toast.makeText(context, "Fehler, die Note konnte nicht aktualisiert werden.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Erfolgreich aktualisiert.", Toast.LENGTH_SHORT).show();
        }
    }

    //----------------------------------------Exam-------------------------------------------
    public long addExam(String examDate, String examSubject, String examDescription) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_EXAM_DATE, examDate);
        cv.put(COLUMN_EXAM_SUBJECT, examSubject);
        cv.put(COLUMN_EXAM_DESCRIPTION, examDescription);

        return db.insert(TABLE_EXAMS, null, cv);
    }

    public Cursor readAllExam() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_EXAMS;
        return db.rawQuery(query, null);
    }

    public void deleteExam(int examId) {
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete(TABLE_EXAMS, COLUMN_EXAM_ID + "=?", new String[]{String.valueOf(examId)});
        if (result == -1) {
            Toast.makeText(context, "Fehler, die Prüfung konnte nicht gelöscht werden.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Erfolgreich gelöscht.", Toast.LENGTH_SHORT).show();
        }
    }

    public void updateExam(int examId, String examDate, String examSubject, String examDescription) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_EXAM_DATE, examDate);
        cv.put(COLUMN_EXAM_SUBJECT, examSubject);
        cv.put(COLUMN_EXAM_DESCRIPTION, examDescription);

        long result = db.update(TABLE_EXAMS, cv, COLUMN_EXAM_ID + "=?", new String[]{String.valueOf(examId)});

        if (result == -1) {
            Toast.makeText(context, "Fehler, die Prüfung konnte nicht aktualisiert werden.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Erfolgreich aktualisiert.", Toast.LENGTH_SHORT).show();
        }
    }
}

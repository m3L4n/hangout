package com.example.ft_hangout.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.io.ByteArrayOutputStream;


public class MydataBaseHelper extends SQLiteOpenHelper {

    private Context context;
    private static final String DATABASE_NAME = "contactstore.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME_MSG = "msg";
    private static final String COLUMN_MSG_ID = "_id";
    private static final String COLUMN_MSG_TEL = "_senderNumber";
    private static final String COLUMN_MSG_SENDER = "_isender";
    private static final String COLUMN_MSG_TIME = "_timestamp";
    private static final String COLUMN_MSG_MSG = "_msg";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_PHOTO = "_photo";

    private static final String TABLE_NAME = "contacts";
    private static final String COLUMN_NAME = "_name";
    private static final String COLUMN_LASTNAME = "_lastName";
    private static final String COLUMN_EMAIL = "_email";
    private static final String COLUMN_NUMBER = "_number";
    private static final String COLUMN_BIRTHDAY = "_birthday";


    public MydataBaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String databasePath = context.getDatabasePath(DATABASE_NAME).getPath();
         String query = "CREATE TABLE " + TABLE_NAME + " ("
                 + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                 + COLUMN_NAME + " TEXT, "
                 + COLUMN_LASTNAME + " TEXT, "
                 + COLUMN_EMAIL + " TEXT, "
                 + COLUMN_NUMBER + " TEXT, "
                 + COLUMN_PHOTO + " BLOB, "
                 + COLUMN_BIRTHDAY + " TEXT);";
         db.execSQL(query);
        String msg = "CREATE TABLE " + TABLE_NAME_MSG + " ("
                + COLUMN_MSG_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_MSG_TEL + " TEXT, "
                + COLUMN_MSG_SENDER + " TEXT, "
                + COLUMN_MSG_MSG + " TEXT, "
                + COLUMN_MSG_TIME + " TEXT);";
        db.execSQL(msg);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_MSG);
        onCreate(db);
    }

    public void add_Contact(String name, String lastName, String email, String number, String birthday, byte[] image_profile){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_NAME, name);
        cv.put(COLUMN_LASTNAME, lastName);
        cv.put(COLUMN_EMAIL, email);
        cv.put(COLUMN_NUMBER, number);
        cv.put(COLUMN_BIRTHDAY, birthday);
        cv.put(COLUMN_PHOTO, image_profile);

        long result = db.insert(TABLE_NAME, null, cv);
        if ( result == -1)
        {
            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(context, "success", Toast.LENGTH_SHORT).show();
        }
    }
    public  void add_msg( String numeroContact, String time, String content){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_MSG_TEL, numeroContact);
        cv.put(COLUMN_MSG_SENDER, "true");
        cv.put(COLUMN_MSG_TIME, time);
        cv.put(COLUMN_MSG_MSG, content);
        long result = db.insert(TABLE_NAME_MSG, null, cv);
    }
    public void add_receive_msg(String Number, String time, String content){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_MSG_TEL, Number);
        cv.put(COLUMN_MSG_SENDER, "false");
        cv.put(COLUMN_MSG_TIME, time);
        cv.put(COLUMN_MSG_MSG, content);
        long result = db.insert(TABLE_NAME_MSG, null, cv);
    }

    public void isNeedToEnregister(String Number, String time, String content){
        if (!numberExists(Number))
        {
            add_Contact(Number, Number, "", Number, "", null);
        }
            add_receive_msg(Number, time, content);
    }
    public Cursor readAllData(){
        String Query = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        if (db != null) {
           cursor = db.rawQuery(Query, null);
        }
        return  cursor;
    }
    public boolean numberExists(String number) {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_NUMBER + " = ?";
        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(query, new String[] {number });
        }
        if (cursor != null && cursor.moveToFirst()) {
            boolean exists = (cursor.getCount() > 0);
            cursor.close();
            return exists;
        }
        return false;

    }
    public Cursor readAllMsg(String numeroContact){
        String query = "SELECT * FROM "+ TABLE_NAME_MSG + " WHERE " + COLUMN_MSG_TEL + " = ?";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        if (db != null) {
             cursor = db.rawQuery(query, new String[] {numeroContact });
        }
        return  cursor;
    }

    public void updateData(String row_id, String name, String lastName, String email, String number, String birthday,byte [] imagePath){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_NAME, name);
        cv.put(COLUMN_LASTNAME, lastName);
        cv.put(COLUMN_EMAIL, email);
        cv.put(COLUMN_NUMBER, number);
        cv.put(COLUMN_BIRTHDAY, birthday);
        cv.put(COLUMN_PHOTO, imagePath);
        long result = db.update(TABLE_NAME, cv, "_id=?", new String[]{row_id});
        if ( result == -1)
        {
            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(context, "success", Toast.LENGTH_SHORT).show();
        }
    }
    public void deleteOneRow(String row_id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            int result = db.delete(TABLE_NAME, "_id=?", new String[]{row_id});
            if (result == -1) {
                Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "success", Toast.LENGTH_SHORT).show();
                db.setTransactionSuccessful();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }

    }


    public void deleteAllData(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME);
    }
}

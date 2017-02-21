package net.muellersites.depicture.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import net.muellersites.depicture.Objects.User;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "MTools";
    private static final String TABLE_NAME = "Users";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_TOKEN = "token";

    private static final int DATABASE_VERSION = 2;
    private static final String USER_TABLE_CREATE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_ID + " INTEGER, " +
                    COLUMN_NAME + " TEXT, " +
                    COLUMN_EMAIL + " TEXT, " +
                    COLUMN_PASSWORD + " TEXT, " +
                    COLUMN_TOKEN + " TEXT);";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(USER_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DELETE FROM User");
        db.close();
    }

    public void insertUser(User user){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id", user.getId());
        values.put("name", user.getName());
        values.put("email", user.getEmail());
        values.put("password", user.getPassword());
        values.put("token", user.getToken());
        db.insert("Users", null, values);
        db.close();
    }

    public void updateUser(User user){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id", user.getId());
        values.put("name", user.getName());
        values.put("email", user.getEmail());
        values.put("password", user.getPassword());
        values.put("token", user.getToken());
        db.update("Users", values, "id=" + user.getId(), null);
        db.close();
    }

    public User getUser() {
        User user = new User();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM Users";
        Cursor cursor = db.rawQuery(query, null);
        if( cursor != null && cursor.moveToFirst() ){
            user.setId(cursor.getInt(0));
            user.setName(cursor.getString(1));
            user.setEmail(cursor.getString(2));
            user.setPassword(cursor.getString(3));
            user.setToken(cursor.getString(4));
            cursor.close();
        }else {
            user.setName("FAILURE");
        }
        db.close();
        return user;
    }

    public void clearUsers(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM Users");
        db.close();
    }

}

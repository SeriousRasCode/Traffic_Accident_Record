package manage.traffic.zga.rec;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "AccidentRecords.db";
    public static final String ACCIDENTS_TABLE_NAME = "accidents";
    public static final String USERS_TABLE_NAME = "users";

    // Accidents Column Names
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_DRIVER = "driver_name";
    public static final String COLUMN_TYPE = "accident_type";
    public static final String COLUMN_PLATE = "vplate";
    public static final String COLUMN_MODEL = "model";
    public static final String COLUMN_CITY = "city";
    public static final String COLUMN_COUNTRY = "country";
    public static final String COLUMN_DATE = "date";

    // Users Column Names
    public static final String USER_COLUMN_ID = "id";
    public static final String USER_COLUMN_USERNAME = "username";
    public static final String USER_COLUMN_PASSWORD = "password";
    public static final String USER_COLUMN_IS_ADMIN = "isAdmin";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table " + ACCIDENTS_TABLE_NAME + " " +
                        "(id integer primary key autoincrement, driver_name text, accident_type text, " +
                        "vplate text, model text, city text, country text, date text)"
        );
        db.execSQL(
                "create table " + USERS_TABLE_NAME + " " +
                        "(id integer primary key autoincrement, username text, password text, isAdmin integer)"
        );
        // Seed Super Admin
        ContentValues contentValues = new ContentValues();
        contentValues.put(USER_COLUMN_USERNAME, "admin");
        contentValues.put(USER_COLUMN_PASSWORD, "admin");
        contentValues.put(USER_COLUMN_IS_ADMIN, 1);
        db.insert(USERS_TABLE_NAME, null, contentValues);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ACCIDENTS_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + USERS_TABLE_NAME);
        onCreate(db);
    }

    // REGISTER / INSERT DATA
    public boolean insertAccident(String driver, String type, String plate, String model, String city, String country, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_DRIVER, driver);
        contentValues.put(COLUMN_TYPE, type);
        contentValues.put(COLUMN_PLATE, plate);
        contentValues.put(COLUMN_MODEL, model);
        contentValues.put(COLUMN_CITY, city);
        contentValues.put(COLUMN_COUNTRY, country);
        contentValues.put(COLUMN_DATE, date);
        db.insert(ACCIDENTS_TABLE_NAME, null, contentValues);
        return true;
    }

    // VIEW DATA BY ID
    public Cursor getData(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("select * from " + ACCIDENTS_TABLE_NAME + " where id=" + id, null);
    }

    // UPDATE DATA
    public boolean updateAccident(Integer id, String driver, String type, String plate, String model, String city, String country, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_DRIVER, driver);
        contentValues.put(COLUMN_TYPE, type);
        contentValues.put(COLUMN_PLATE, plate);
        contentValues.put(COLUMN_MODEL, model);
        contentValues.put(COLUMN_CITY, city);
        contentValues.put(COLUMN_COUNTRY, country);
        contentValues.put(COLUMN_DATE, date);
        db.update(ACCIDENTS_TABLE_NAME, contentValues, "id = ? ", new String[]{Integer.toString(id)});
        return true;
    }

    // DELETE DATA
    public Integer deleteAccident(Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(ACCIDENTS_TABLE_NAME, "id = ? ", new String[]{Integer.toString(id)});
    }

    // VIEW ALL DATA (As ListMap for Easy Adapter Binding)
    public ArrayList<HashMap<String, Object>> getAllAccidents() {
        ArrayList<HashMap<String, Object>> listmap = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " + ACCIDENTS_TABLE_NAME, null);

        if (res.moveToFirst()) {
            do {
                HashMap<String, Object> map = new HashMap<>();
                map.put(COLUMN_ID, res.getInt(res.getColumnIndex(COLUMN_ID)));
                map.put(COLUMN_DRIVER, res.getString(res.getColumnIndex(COLUMN_DRIVER)));
                map.put(COLUMN_TYPE, res.getString(res.getColumnIndex(COLUMN_TYPE)));
                map.put(COLUMN_PLATE, res.getString(res.getColumnIndex(COLUMN_PLATE)));
                map.put(COLUMN_MODEL, res.getString(res.getColumnIndex(COLUMN_MODEL)));
                map.put(COLUMN_CITY, res.getString(res.getColumnIndex(COLUMN_CITY)));
                map.put(COLUMN_COUNTRY, res.getString(res.getColumnIndex(COLUMN_COUNTRY)));
                map.put(COLUMN_DATE, res.getString(res.getColumnIndex(COLUMN_DATE)));

                listmap.add(map);
            } while (res.moveToNext());
        }
        res.close();
        return listmap;
    }

    public int numberOfRows() {
        SQLiteDatabase db = this.getReadableDatabase();
        return (int) DatabaseUtils.queryNumEntries(db, ACCIDENTS_TABLE_NAME);
    }

    public boolean insertUser(String username, String password, boolean isAdmin) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(USER_COLUMN_USERNAME, username);
        contentValues.put(USER_COLUMN_PASSWORD, password);
        contentValues.put(USER_COLUMN_IS_ADMIN, isAdmin ? 1 : 0);
        long result = db.insert(USERS_TABLE_NAME, null, contentValues);
        return result != -1;
    }

    public int checkUserRole(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + USERS_TABLE_NAME + " WHERE " + USER_COLUMN_USERNAME + " = ? AND " + USER_COLUMN_PASSWORD + " = ?", new String[]{username, password});
        int role = -1; // -1 means login failed
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            role = cursor.getInt(cursor.getColumnIndex(USER_COLUMN_IS_ADMIN));
        }
        cursor.close();
        return role;
    }

    public ArrayList<HashMap<String, Object>> getAllUsers() {
        ArrayList<HashMap<String, Object>> listmap = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " + USERS_TABLE_NAME, null);

        if (res.moveToFirst()) {
            do {
                HashMap<String, Object> map = new HashMap<>();
                map.put(USER_COLUMN_ID, res.getInt(res.getColumnIndex(USER_COLUMN_ID)));
                map.put(USER_COLUMN_USERNAME, res.getString(res.getColumnIndex(USER_COLUMN_USERNAME)));
                map.put(USER_COLUMN_IS_ADMIN, res.getInt(res.getColumnIndex(USER_COLUMN_IS_ADMIN)));
                listmap.add(map);
            } while (res.moveToNext());
        }
        res.close();
        return listmap;
    }

    public boolean updateUserRole(int id, boolean isAdmin) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(USER_COLUMN_IS_ADMIN, isAdmin ? 1 : 0);
        db.update(USERS_TABLE_NAME, contentValues, "id = ?", new String[]{Integer.toString(id)});
        return true;
    }

    public boolean checkUserExists(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + USERS_TABLE_NAME + " WHERE " + USER_COLUMN_USERNAME + " = ?", new String[]{username});
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;
    }
}

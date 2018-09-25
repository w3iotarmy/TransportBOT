package org.droidplanner.android.activities.dronedb;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;
import android.provider.BaseColumns;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DroneDB extends SQLiteOpenHelper implements BaseColumns {

    public static final String DATABASE_NAME = "dronedetails.db";
    public static final String TABLE_NAME = "drone";
    public static final String COL_NAME = "Name";
    public static final String COL_DISTANCE = "Distance";
    public static final String COL_Flight = "FlightTime";
    public static final String COL_WP = "Waypoints";
    public static final String COL_TIME_STAMP = "TimeStamp";
    public static final String CREATE_TABLE = "CREATE TABLE "
            + TABLE_NAME +
            " ( " + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + " "
            + COL_NAME + " TEXT, " + COL_TIME_STAMP + " "
            + "TEXT" + ", " + COL_DISTANCE + " TEXT, "
            + COL_Flight + " TEXT, " + COL_WP + " TEXT" + " );";

    public static final String WP_TABLE = "wptable";
    public static final String WP_LAT = "Latitude";
    public static final String WP_LON = "Longitude";
    public static final String WP_ALT = "Altitude";
    public static final String CREATE_WP_TABLE = "CREATE TABLE " + WP_TABLE
            + " ( " + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + WP_LAT + " TEXT, "
            + WP_LON + " TEXT, " + WP_ALT + " TEXT " + ");";

    public DroneDB(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        try {
            sqLiteDatabase.execSQL(CREATE_TABLE);
            sqLiteDatabase.execSQL(CREATE_WP_TABLE);
        } catch (Exception e) {

        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + WP_TABLE);
        this.onCreate(sqLiteDatabase);
    }


    public void getDroneData() {
        SQLiteDatabase db = this.getReadableDatabase();

        String showAllData = "SELECT * FROM drone";
        String rowCount = "SELECT * FROM drone WHERE _id = 9";

        try {
            Cursor cursor = db.rawQuery(showAllData, null);

        } catch (Exception e) {

        }

        try {
            Cursor cursor2 = db.rawQuery(rowCount, null);

        } catch (Exception e) {

        }
    }

    public void addDroneData(String wp) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        String time = sdf.format(new Date());
        values.put(COL_TIME_STAMP, time);
        values.put(COL_WP, wp);


        //String insertQuery = "INSERT INTO " + TABLE_NAME + " (" +  COL_TIME_STAMP + ", " +COL_WP + ")" + " VALUES" + " (" + ts + " );";

        try {
            //sqLiteDatabase.execSQL(insertQuery);
            sqLiteDatabase.insert(TABLE_NAME, null, values);
            sqLiteDatabase.close();

        } catch (Exception e) {

        }
    }

    public void addWpData(double lat, double lon, double alt){
        SQLiteDatabase wpDb = this.getWritableDatabase();
        ContentValues wp_values = new ContentValues();
        wp_values.put(WP_LAT, lat);
        wp_values.put(WP_LON, lon);
        wp_values.put(WP_ALT, alt);

        try{
            wpDb.insert(WP_TABLE, null, wp_values);
            wpDb.close();

        }catch (Exception ex){

        }


    }

    public  void getWpData(){
        SQLiteDatabase db = this.getReadableDatabase();
        String showWp = "SELECT * FROM wptable";

        try {
            Cursor cursor = db.rawQuery(showWp, null);

        } catch (Exception e) {

        }

    }

    public void updateDroneData(int rowId, String timeStamp){
        SQLiteDatabase drone = this.getWritableDatabase();
        ContentValues dr_values = new ContentValues();

        dr_values.put(COL_TIME_STAMP, timeStamp);

    }

}

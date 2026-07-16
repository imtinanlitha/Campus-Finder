package com.example.campusfinder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "CampusFinder.db";
    private static final int DATABASE_VERSION = 1;

    // Tabel & Kolom
    private static final String TABLE_NAME = "locations";
    private static final String COL_ID = "id";
    private static final String COL_NAME = "name";
    private static final String COL_DESC = "description";
    private static final String COL_IMAGE = "image_resource";
    private static final String COL_LAT = "latitude";
    private static final String COL_LONG = "longitude";
    private static final String COL_CATEGORY = "category";
    private static final String COL_FAVORITE = "is_favorite";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // 1. Membuat Tabel saat aplikasi pertama kali diinstall
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_NAME + " TEXT, " +
                COL_DESC + " TEXT, " +
                COL_IMAGE + " INTEGER, " +
                COL_LAT + " REAL, " +
                COL_LONG + " REAL, " +
                COL_CATEGORY + " TEXT, " +
                COL_FAVORITE + " INTEGER DEFAULT 0)";
        db.execSQL(createTable);
    }

    public ArrayList<CampusLocation> getLocationsByCategory(String categoryNeeded) {
        ArrayList<CampusLocation> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COL_CATEGORY + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{categoryNeeded});

        if (cursor.moveToFirst()) {
            do {
                // Ambil ID juga
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME));
                String desc = cursor.getString(cursor.getColumnIndexOrThrow(COL_DESC));
                int img = cursor.getInt(cursor.getColumnIndexOrThrow(COL_IMAGE));
                double lat = cursor.getDouble(cursor.getColumnIndexOrThrow(COL_LAT));
                double lng = cursor.getDouble(cursor.getColumnIndexOrThrow(COL_LONG));

                // Ambil status favorite (SQLite simpan boolean sebagai 1 atau 0)
                boolean isFav = cursor.getInt(cursor.getColumnIndexOrThrow(COL_FAVORITE)) == 1;

                // Gunakan constructor lengkap
                list.add(new CampusLocation(id, name, desc, img, lat, lng, isFav));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    // 2. Update tabel
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // 3. Fungsi Tambah Data (Insert)
    public boolean addLocation(String name, String desc, int imageRes, double lat, double lng, String category) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COL_NAME, name);
        cv.put(COL_DESC, desc);
        cv.put(COL_IMAGE, imageRes);
        cv.put(COL_LAT, lat);
        cv.put(COL_LONG, lng);
        cv.put(COL_CATEGORY, category);

        long result = db.insert(TABLE_NAME, null, cv);
        return result != -1;
    }

    // Fungsi Update Like
    public void updateFavoriteStatus(int locationId, boolean isFavorite) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        // Jika true simpan 1, jika false simpan 0
        cv.put(COL_FAVORITE, isFavorite ? 1 : 0);

        // Update baris
        db.update(TABLE_NAME, cv, COL_ID + "=?", new String[]{String.valueOf(locationId)});
    }

    // Ambil data yang status is_favorite = 1 (true)
    public ArrayList<CampusLocation> getFavoriteLocations() {
        ArrayList<CampusLocation> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Ambil yang kolom favorite-nya 1
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COL_FAVORITE + " = 1";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME));
                String desc = cursor.getString(cursor.getColumnIndexOrThrow(COL_DESC));
                int img = cursor.getInt(cursor.getColumnIndexOrThrow(COL_IMAGE));
                double lat = cursor.getDouble(cursor.getColumnIndexOrThrow(COL_LAT));
                double lng = cursor.getDouble(cursor.getColumnIndexOrThrow(COL_LONG));
                boolean isFav = true;

                list.add(new CampusLocation(id, name, desc, img, lat, lng, isFav));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    // Cek database kosong (untuk mengisi data awal)
    public boolean isDatabaseEmpty() {
        SQLiteDatabase db = this.getReadableDatabase();
        String count = "SELECT count(*) FROM " + TABLE_NAME;
        Cursor cursor = db.rawQuery(count, null);
        cursor.moveToFirst();
        int icount = cursor.getInt(0);
        cursor.close();
        return icount == 0;
    }
    // Cek status favorite dari database
    public boolean isLocationFavorite(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COL_FAVORITE + " FROM " + TABLE_NAME + " WHERE " + COL_ID + " = " + id;
        Cursor cursor = db.rawQuery(query, null);

        boolean result = false;
        if (cursor.moveToFirst()) {
            result = cursor.getInt(0) == 1;
        }
        cursor.close();
        return result;
    }
}
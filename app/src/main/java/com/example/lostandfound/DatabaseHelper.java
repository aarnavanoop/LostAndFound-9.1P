package com.example.lostandfound;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "lostandfound.db";
    private static final int DATABASE_VERSION = 1;


    public static final String TABLE_ITEMS = "items";


    public static final String COL_ID          = "id";
    public static final String COL_TITLE       = "title";
    public static final String COL_DESCRIPTION = "description";
    public static final String COL_CATEGORY    = "category";
    public static final String COL_TYPE        = "type";
    public static final String COL_LOCATION    = "location";
    public static final String COL_CONTACT_NAME  = "contact_name";
    public static final String COL_CONTACT_PHONE = "contact_phone";
    public static final String COL_IMAGE_URI   = "image_uri";
    public static final String COL_DATETIME    = "date_time";

    private static final String CREATE_TABLE =
        "CREATE TABLE " + TABLE_ITEMS + " (" +
            COL_ID           + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COL_TITLE        + " TEXT NOT NULL, " +
            COL_DESCRIPTION  + " TEXT, " +
            COL_CATEGORY     + " TEXT, " +
            COL_TYPE         + " TEXT, " +
            COL_LOCATION     + " TEXT, " +
            COL_CONTACT_NAME + " TEXT, " +
            COL_CONTACT_PHONE+ " TEXT, " +
            COL_IMAGE_URI    + " TEXT, " +
            COL_DATETIME     + " TEXT" +
            ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEMS);
        onCreate(db);
    }


    public long insertItem(Item item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_TITLE,         item.getTitle());
        cv.put(COL_DESCRIPTION,   item.getDescription());
        cv.put(COL_CATEGORY,      item.getCategory());
        cv.put(COL_TYPE,          item.getType());
        cv.put(COL_LOCATION,      item.getLocation());
        cv.put(COL_CONTACT_NAME,  item.getContactName());
        cv.put(COL_CONTACT_PHONE, item.getContactPhone());
        cv.put(COL_IMAGE_URI,     item.getImageUri());
        cv.put(COL_DATETIME,      item.getDateTime());
        long id = db.insert(TABLE_ITEMS, null, cv);
        db.close();
        return id;
    }


    public List<Item> getAllItems() {
        List<Item> items = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_ITEMS, null, null, null,
            null, null, COL_ID + " DESC");
        if (cursor.moveToFirst()) {
            do {
                items.add(cursorToItem(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return items;
    }


    public List<Item> getItemsByCategory(String category) {
        List<Item> items = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_ITEMS, null,
            COL_CATEGORY + "=?", new String[]{category},
            null, null, COL_ID + " DESC");
        if (cursor.moveToFirst()) {
            do {
                items.add(cursorToItem(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return items;
    }


    public List<Item> searchItems(String query, String category) {
        List<Item> items = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String selection;
        String[] selectionArgs;
        String like = "%" + query + "%";

        if (category.equals("All")) {
            selection = "(" + COL_TITLE + " LIKE ? OR " + COL_DESCRIPTION + " LIKE ?)";
            selectionArgs = new String[]{like, like};
        } else {
            selection = "(" + COL_TITLE + " LIKE ? OR " + COL_DESCRIPTION + " LIKE ?) AND "
                + COL_CATEGORY + "=?";
            selectionArgs = new String[]{like, like, category};
        }

        Cursor cursor = db.query(TABLE_ITEMS, null, selection, selectionArgs,
            null, null, COL_ID + " DESC");
        if (cursor.moveToFirst()) {
            do {
                items.add(cursorToItem(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return items;
    }


    public Item getItemById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_ITEMS, null,
            COL_ID + "=?", new String[]{String.valueOf(id)},
            null, null, null);
        Item item = null;
        if (cursor.moveToFirst()) {
            item = cursorToItem(cursor);
        }
        cursor.close();
        db.close();
        return item;
    }


    public int deleteItem(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete(TABLE_ITEMS, COL_ID + "=?",
            new String[]{String.valueOf(id)});
        db.close();
        return rows;
    }

    private Item cursorToItem(Cursor cursor) {
        return new Item(
            cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)),
            cursor.getString(cursor.getColumnIndexOrThrow(COL_TITLE)),
            cursor.getString(cursor.getColumnIndexOrThrow(COL_DESCRIPTION)),
            cursor.getString(cursor.getColumnIndexOrThrow(COL_CATEGORY)),
            cursor.getString(cursor.getColumnIndexOrThrow(COL_TYPE)),
            cursor.getString(cursor.getColumnIndexOrThrow(COL_LOCATION)),
            cursor.getString(cursor.getColumnIndexOrThrow(COL_CONTACT_NAME)),
            cursor.getString(cursor.getColumnIndexOrThrow(COL_CONTACT_PHONE)),
            cursor.getString(cursor.getColumnIndexOrThrow(COL_IMAGE_URI)),
            cursor.getString(cursor.getColumnIndexOrThrow(COL_DATETIME))
        );
    }
}

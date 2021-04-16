package com.agamilabs.smartshop.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.agamilabs.smartshop.controller.AppController;
import com.agamilabs.smartshop.model.NotifyModel;

import java.util.ArrayList;
import java.util.Arrays;

import static android.content.ContentValues.TAG;

public class DatabaseHandler extends SQLiteOpenHelper {


    //-------1
    private static final String TAG_Mgs = "DatabaseHandoler";

    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "NOTIFICATION.DB";
    // notification table name
    private static final String TABLE_USERINFO = "notification";
    private static final String NOTIFY_ID = "Id";
    private static final String NOTIFY_TITLE = "TITLE";
    private static final String NOTIFY_BODY_TEXT = "BODY_TEXT";
    private static final String NOTIFY_TOPIC = "TOPIC";


    // notification table name
    private static final String TABLE_TOKENINFO = "token_table";
    private static final String TOKEN_ID = "id";
    private static final String TOKEN_ENCODE_NO = "encode";
    private static final String TOKEN_SITEURL = "site_url";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //------extends SQLiteOpenHelper- by add--3--
    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            String CREATE_USERINFO = "CREATE TABLE " + TABLE_USERINFO + "("
                    + NOTIFY_ID + "  INTEGER PRIMARY KEY, "
                    + NOTIFY_TITLE + " TEXT, "
                    + NOTIFY_BODY_TEXT + " TEXT, "
                    + NOTIFY_TOPIC + " TEXT"
                    + ")";

            db.execSQL(CREATE_USERINFO);
        } catch (Exception e) {

        }

        try {
            String CREATE_TOKENINFO = "CREATE TABLE " + TABLE_TOKENINFO + "("
                    + TOKEN_ID + "  INTEGER PRIMARY KEY, "
                    + TOKEN_ENCODE_NO + " TEXT, "
                    + TOKEN_SITEURL + " TEXT"
                    + ")";

            db.execSQL(CREATE_TOKENINFO);
        } catch (Exception e) {

        }


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {


        Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS contacts");
        onCreate(db);

    }
    //------extends SQLiteOpenHelper- by add--3--


    // insert data
    public void insertUserInfo(String title, String body_text, String topic) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(NOTIFY_TITLE, title);
        values.put(NOTIFY_BODY_TEXT, body_text);
        values.put(NOTIFY_TOPIC, topic);


        // Inserting Row
        db.insert(TABLE_USERINFO, null, values);
        Log.d("TAG", "Insert success");
        db.close(); // Closing database connection
    }

    // insert token encoded data
    public void insertTokenEncodeDAta(String token_no, String site_url) {
        SQLiteDatabase db1 = this.getWritableDatabase();
        ContentValues values1 = new ContentValues();

        values1.put(TOKEN_ENCODE_NO, token_no);
        values1.put(TOKEN_SITEURL, site_url);


        // Inserting Row
        db1.insert(TABLE_TOKENINFO, null, values1);
        Log.d("TAG", "encode Insert success");
        db1.close(); // Closing database connection
    }


    //----------------------view data


    public ArrayList<NotifyModel> getAllInfo(Context context) {

        ArrayList<NotifyModel> userInfoList = new ArrayList<NotifyModel>();
        try {

            String selectQuery = "SELECT  * FROM  " + TABLE_USERINFO + "";

            SQLiteDatabase db = this.getReadableDatabase();

            Cursor cursor = db.rawQuery(selectQuery, null);

            cursor.moveToFirst();  //Move the cursor to the first row.

            //Returns whether the cursor is pointing to the position after the last row.
            while (cursor.moveToNext()) {

                Log.e("col", Arrays.toString(cursor.getColumnNames()));

                NotifyModel customList = new NotifyModel();

                String Id = cursor.getString(0);

                customList.setId(Id);


                String title = cursor.getString(1);
                customList.setTitle(title);

                String body_text = cursor.getString(2);
                customList.setBody_text(body_text);

                String topic = cursor.getString(3);
                customList.setTopic(topic);

                userInfoList.add(customList);
                //cursor.moveToNext();  //Move the cursor to the next row.

            }
            cursor.close();
            db.close();

        } catch (Exception ex) {
            Log.e("getCampaignId Excep. :", ex.toString());
        }

        return userInfoList;
    }


    //----------------------view data


    public String checkExistingToken(String site_url) {

        String token = null;
        try {
            String selectQuery = "SELECT encode FROM "+ TABLE_TOKENINFO +" WHERE site_url= '"+ site_url +"'" ;

            SQLiteDatabase db = this.getReadableDatabase();

            Cursor cursor = db.rawQuery(selectQuery, null);

            cursor.moveToFirst();

            token = cursor.getString(0) ;
//            AppController.getAppController().getInAppNotifier().log("token", token);


        }

        catch (Exception ex) {
            Log.e("TAG", ex.toString());
        }

//        AppController.getAppController().getInAppNotifier().log("token2", token);

        return token;
    }
    public boolean deleteTokenRow( String site_url ) {
        SQLiteDatabase db1 = this.getWritableDatabase();
        long l = db1.delete(TABLE_TOKENINFO, "site_url=?", new String[]{ site_url });

        if (l != -1) {
            return true;
        } else {
            return false;
        }
    }

}//lb
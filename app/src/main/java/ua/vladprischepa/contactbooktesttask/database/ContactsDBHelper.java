package ua.vladprischepa.contactbooktesttask.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;

import ua.vladprischepa.contactbooktesttask.R;
import ua.vladprischepa.contactbooktesttask.model.Contact;
import ua.vladprischepa.contactbooktesttask.model.EmailAddress;
import ua.vladprischepa.contactbooktesttask.model.PhoneNumber;

/**
 * A helper class to manage database creation and version management.
 * @author Vlad Prischepa
 * @since 02.04.2017
 * @version 1
 */

public class ContactsDBHelper extends SQLiteOpenHelper{

    /**
     * Database version, should be incremented within new version of database;
     */
    private static final int DB_VERSION = 1;
    /**
     * Database name suffix used with database name;
     */
    private static final String DB_NAME_SUFFIX = ".db";

    /**
     * SQLite LEFT OUTER JOIN statement
     */
    private static final String SQL_LEFT_OUTER_JOIN = " CROSS JOIN ";
    private static final String SQL_INNER_JOIN = " INNER JOIN ";

    /**
     * Application Context
     */
    private Context mContext;
    /**
     *  public constructor for {@link ContactsDBHelper}
     * @param context Application context
     * @param account Google Account, used as database name
     */
    public ContactsDBHelper(Context context, String account) {
        super(context, account+DB_NAME_SUFFIX, null, DB_VERSION);
        mContext = context;
    }



    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE_CONTACTS);
        db.execSQL(SQL_CREATE_TABLE_PHONES);
        db.execSQL(SQL_CREATE_TABLE_EMAILS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DROP_TABLE+ ContactsContract.Contacts.TABLE_NAME);
        db.execSQL(SQL_DROP_TABLE+ ContactsContract.Phones.TABLE_NAME);
        db.execSQL(SQL_DROP_TABLE+ ContactsContract.Emails.TABLE_NAME);
        onCreate(db);
    }

    /**
     * SQL Statement for creating "Contacts" table
     */
    private static final String SQL_CREATE_TABLE_CONTACTS = "CREATE TABLE IF NOT EXISTS "
            + ContactsContract.Contacts.TABLE_NAME + "("
            + ContactsContract.Contacts.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + ContactsContract.Contacts.COLUMN_FIRST_NAME + " TEXT NOT NULL, "
            + ContactsContract.Contacts.COLUMN_LAST_NAME + " TEXT NOT NULL" + ");";

    /**
     * SQL Statement for creating "Phones" table
     */
    private static final String SQL_CREATE_TABLE_PHONES = "CREATE TABLE IF NOT EXISTS "
            + ContactsContract.Phones.TABLE_NAME + "("
            + ContactsContract.Phones.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + ContactsContract.Phones.COLUMN_CONTACT_ID + " INTEGER NOT NULL, "
            + ContactsContract.Phones.COLUMN_PHONE_NUMBER + " TEXT NOT NULL, "
            + ContactsContract.Phones.COLUMN_NUMBER_TYPE + " INTEGER NOT NULL DEFAULT 0"
            + ");";

    /**
     * SQL Statement for creating "Emails" table
     */
    private static final String SQL_CREATE_TABLE_EMAILS = "CREATE TABLE IF NOT EXISTS "
            + ContactsContract.Emails.TABLE_NAME + "("
            + ContactsContract.Emails.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + ContactsContract.Emails.COLUMN_CONTACT_ID + " INTEGER NOT NULL, "
            + ContactsContract.Emails.COLUMN_EMAIL + " TEXT NOT NULL, "
            + ContactsContract.Emails.COLUMN_EMAIL_TYPE + " INTEGER NOT NULL DEFAULT 0"
            + ");";

    /**
     * SQL Statement for deleting existing table
     */
    private static final String SQL_DROP_TABLE = "DROP TABLE IF EXISTS ";








}

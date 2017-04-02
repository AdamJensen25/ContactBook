package ua.vladprischepa.contactbooktesttask.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

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
     *  public constructor for {@link ContactsDBHelper}
     * @param context Application context
     * @param account Google Account, used as database name
     */
    public ContactsDBHelper(Context context, GoogleSignInAccount account) {
        super(context, account.getEmail()+DB_NAME_SUFFIX, null, DB_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE_CONTACTS);
        db.execSQL(SQL_CREATE_TABLE_PHONES);
        db.execSQL(SQL_CREATE_TABLE_EMAILS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DROP_TABLE+ContactsContract.ContactsTableEntry.TABLE_NAME);
        db.execSQL(SQL_DROP_TABLE+ContactsContract.PhonesTableEntry.TABLE_NAME);
        db.execSQL(SQL_DROP_TABLE+ContactsContract.EmailTableEntry.TABLE_NAME);
        onCreate(db);
    }

    /**
     * SQL Statement for creating "Contacts" table
     */
    private static final String SQL_CREATE_TABLE_CONTACTS = "CREATE TABLE IF NOT EXISTS"
            + ContactsContract.ContactsTableEntry.TABLE_NAME + "("
            + ContactsContract.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + ContactsContract.ContactsTableEntry.COLUMN_FIRST_NAME + " TEXT NOT NULL, "
            + ContactsContract.ContactsTableEntry.COLUMN_LAST_NAME + " TEXT NOT NULL" + ");";

    /**
     * SQL Statement for creating "Phones" table
     */
    private static final String SQL_CREATE_TABLE_PHONES = "CREATE TABLE IF NOT EXISTS"
            + ContactsContract.PhonesTableEntry.TABLE_NAME + "("
            + ContactsContract.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + ContactsContract.COLUMN_CONTACT_ID + " INTEGER NOT NULL, "
            + ContactsContract.PhonesTableEntry.COLUMN_PHONE_NUMBER + " TEXT NOT NULL, "
            + ContactsContract.PhonesTableEntry.COLUMN_NUMBER_TYPE + " INTEGER NOT NULL DEFAULT 0"
            + ");";

    /**
     * SQL Statement for creating "Emails" table
     */
    private static final String SQL_CREATE_TABLE_EMAILS = "CREATE TABLE IF NOT EXISTS"
            + ContactsContract.EmailTableEntry.TABLE_NAME + "("
            + ContactsContract.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + ContactsContract.COLUMN_CONTACT_ID + " INTEGER NOT NULL, "
            + ContactsContract.EmailTableEntry.COLUMN_EMAIL + " TEXT NOT NULL, "
            + ContactsContract.EmailTableEntry.COLUMN_EMAIL_TYPE + " INTEGER NOT NULL DEFAULT 0"
            + ");";

    /**
     * SQL Statement for deleting existing table
     */
    private static final String SQL_DROP_TABLE = "DROP TABLE IF EXISTS ";

}

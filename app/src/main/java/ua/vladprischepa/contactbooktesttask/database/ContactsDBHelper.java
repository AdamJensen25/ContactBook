package ua.vladprischepa.contactbooktesttask.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import java.util.HashMap;

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
    private static final String SQL_LEFT_OUTER_JOIN = " LEFT OUTER JOIN ";

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
        db.execSQL(SQL_DROP_TABLE+ContactsContract.ContactsTableEntry.TABLE_NAME);
        db.execSQL(SQL_DROP_TABLE+ContactsContract.PhonesTableEntry.TABLE_NAME);
        db.execSQL(SQL_DROP_TABLE+ContactsContract.EmailTableEntry.TABLE_NAME);
        onCreate(db);
    }

    /**
     * SQL Statement for creating "Contacts" table
     */
    private static final String SQL_CREATE_TABLE_CONTACTS = "CREATE TABLE IF NOT EXISTS "
            + ContactsContract.ContactsTableEntry.TABLE_NAME + "("
            + ContactsContract.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + ContactsContract.ContactsTableEntry.COLUMN_FIRST_NAME + " TEXT NOT NULL, "
            + ContactsContract.ContactsTableEntry.COLUMN_LAST_NAME + " TEXT NOT NULL" + ");";

    /**
     * SQL Statement for creating "Phones" table
     */
    private static final String SQL_CREATE_TABLE_PHONES = "CREATE TABLE IF NOT EXISTS "
            + ContactsContract.PhonesTableEntry.TABLE_NAME + "("
            + ContactsContract.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + ContactsContract.COLUMN_CONTACT_ID + " INTEGER NOT NULL, "
            + ContactsContract.PhonesTableEntry.COLUMN_PHONE_NUMBER + " TEXT NOT NULL, "
            + ContactsContract.PhonesTableEntry.COLUMN_NUMBER_TYPE + " INTEGER NOT NULL DEFAULT 0"
            + ");";

    /**
     * SQL Statement for creating "Emails" table
     */
    private static final String SQL_CREATE_TABLE_EMAILS = "CREATE TABLE IF NOT EXISTS "
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

    public long insertContact(Contact contact){
        SQLiteDatabase database = getWritableDatabase();
        ContentValues contactValues = new ContentValues();
        contactValues.put(ContactsContract.ContactsTableEntry.COLUMN_FIRST_NAME, contact.getFirstName());
        contactValues.put(ContactsContract.ContactsTableEntry.COLUMN_LAST_NAME, contact.getLastName());
        long rowId = database.insert(ContactsContract.ContactsTableEntry.TABLE_NAME,
                null, contactValues);
        for (PhoneNumber number : contact.getPhoneNumbers()) {
            ContentValues phoneValues = new ContentValues();
            phoneValues.put(ContactsContract.PhonesTableEntry.COLUMN_PHONE_NUMBER, number.getNumber());
            phoneValues.put(ContactsContract.PhonesTableEntry.COLUMN_NUMBER_TYPE, number.getType());
            phoneValues.put(ContactsContract.COLUMN_CONTACT_ID, rowId);
            database.insert(ContactsContract.PhonesTableEntry.TABLE_NAME,
                    null, phoneValues);
        }
        for (EmailAddress emailAddress : contact.getEmails()) {
            ContentValues emailValues = new ContentValues();
            emailValues.put(ContactsContract.EmailTableEntry.COLUMN_EMAIL, emailAddress.getEmail());
            emailValues.put(ContactsContract.EmailTableEntry.COLUMN_EMAIL_TYPE, emailAddress.getType());
            emailValues.put(ContactsContract.COLUMN_CONTACT_ID, rowId);
            database.insert(ContactsContract.EmailTableEntry.TABLE_NAME,
                    null, emailValues);
        }
        if (rowId<0) throw new IllegalArgumentException(
                mContext.getString(R.string.error_unknown_uri));
        return rowId;
    }

    public int deleteContact(int contact_id){
        SQLiteDatabase database = getWritableDatabase();
        int deletedRows = database.delete(ContactsContract.ContactsTableEntry.TABLE_NAME,
                ContactsContract.COLUMN_ID + " = ?",
                new String[]{String.valueOf(contact_id)});
        deletedRows += database.delete(ContactsContract.PhonesTableEntry.TABLE_NAME,
                ContactsContract.COLUMN_CONTACT_ID + " = ?",
                new String[]{String.valueOf(contact_id)});
        deletedRows += database.delete(ContactsContract.EmailTableEntry.TABLE_NAME,
                ContactsContract.COLUMN_CONTACT_ID + " = ?",
                new String[]{String.valueOf(contact_id)});
        return deletedRows;
    }

    public void getAllContactsQuery(){
        SQLiteDatabase database = getReadableDatabase();
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(ContactsContract.ContactsTableEntry.TABLE_NAME
                + SQL_LEFT_OUTER_JOIN + ContactsContract.PhonesTableEntry.TABLE_NAME + " ON "
                + ContactsContract.COLUMN_ID + " = " + ContactsContract.COLUMN_CONTACT_ID
                + SQL_LEFT_OUTER_JOIN + ContactsContract.EmailTableEntry.TABLE_NAME + " ON "
                + ContactsContract.COLUMN_ID + " = " + ContactsContract.COLUMN_CONTACT_ID);
        Cursor cursor = queryBuilder.query(database, null, null,
                null, null, null, null, null);
        logCursor(cursor);

    }

    // вывод в лог данных из курсора
    void logCursor(Cursor c) {
        if (c != null) {
            if (c.moveToFirst()) {
                String str;
                do {
                    str = "";
                    for (String cn : c.getColumnNames()) {
                        str = str.concat(cn + " = " + c.getString(c.getColumnIndex(cn)) + "; ");
                    }
                    Log.d("Tag", str);
                } while (c.moveToNext());
            }
        } else
            Log.d("Tag", "Cursor is null");
    }


}

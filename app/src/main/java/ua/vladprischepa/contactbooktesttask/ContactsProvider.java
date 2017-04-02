package ua.vladprischepa.contactbooktesttask;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import ua.vladprischepa.contactbooktesttask.database.ContactsContract;
import ua.vladprischepa.contactbooktesttask.database.ContactsDBHelper;

public class ContactsProvider extends ContentProvider {

    private static final String TAG = ContactsProvider.class.getSimpleName();

    private ContactsDBHelper mDbHelper;
    private SQLiteDatabase mDatabase;

    /**
     * Content Authority
     */
    public static final String CONTENT_AUTHORITY = "ua.vladprischepa.contactbooktesttask";

    /**
     * Content URI Object
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_CONTACTS = ContactsContract.ContactsTableEntry.TABLE_NAME;
    public static final String PATH_EMAILS = ContactsContract.EmailTableEntry.TABLE_NAME;
    public static final String PATH_PHONES = ContactsContract.PhonesTableEntry.TABLE_NAME;
    public static final Uri CONTACTS_CONTENT_URI =
            BASE_CONTENT_URI.buildUpon().appendPath(PATH_CONTACTS).build();
    public static final Uri PHONES_CONTENT_URI =
            BASE_CONTENT_URI.buildUpon().appendPath(PATH_PHONES).build();
    public static final Uri EMAILS_CONTENT_URI =
            BASE_CONTENT_URI.buildUpon().appendPath(PATH_EMAILS).build();

    private static final String CONTENT_DIR_URI = "vnd.android.cursor.dir";
    private static final String CONTENT_ITEM_URI = "vnd.android.cursor.dir";

    private static final String CONTENT_TYPE_CONTACT = CONTENT_DIR_URI + "/"
            + ContactsContract.ContactsTableEntry.TABLE_NAME;
    private static final String CONTENT_ITEM_TYPE_CONTACT = CONTENT_ITEM_URI + "/"
            + ContactsContract.ContactsTableEntry.TABLE_NAME;
    private static final String CONTENT_TYPE_EMAIL = CONTENT_DIR_URI + "/"
            + ContactsContract.EmailTableEntry.TABLE_NAME;
    private static final String CONTENT_TYPE_PHONE = CONTENT_ITEM_URI + "/"
            + ContactsContract.PhonesTableEntry.TABLE_NAME;

    private static final String SQL_LEFT_OUTER_JOIN = " LEFT OUTER JOIN ";


    private static final int CONTACTS = 10;
    private static final int CONTACTS_ID = 20;
    private static final int PHONES = 30;
    private static final int PHONE_ID = 40;
    private static final int EMAILS = 50;
    private static final int EMAIL_ID = 60;

    private static UriMatcher sUriMatcher;
    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(CONTENT_AUTHORITY, PATH_CONTACTS, CONTACTS);
        sUriMatcher.addURI(CONTENT_AUTHORITY, PATH_CONTACTS + "/#", CONTACTS_ID);
        sUriMatcher.addURI(CONTENT_AUTHORITY, PATH_PHONES, PHONES);
        sUriMatcher.addURI(CONTENT_AUTHORITY, PATH_PHONES + "/#", PHONE_ID);
        sUriMatcher.addURI(CONTENT_AUTHORITY, PATH_EMAILS, EMAILS);
        sUriMatcher.addURI(CONTENT_AUTHORITY, PATH_EMAILS + "/#", EMAIL_ID);
    }

    public ContactsProvider(GoogleSignInAccount account) {
        mDbHelper = new ContactsDBHelper(getContext(), account);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int rows_count;

        switch(sUriMatcher.match(uri)){
            case CONTACTS:
                rows_count = mDatabase.delete(
                        ContactsContract.ContactsTableEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            case PHONES:
                rows_count = mDatabase.delete(
                        ContactsContract.PhonesTableEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            case EMAILS:
                rows_count = mDatabase.delete(
                        ContactsContract.EmailTableEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            default: throw new IllegalArgumentException(
                    getContext().getString(R.string.error_unknown_uri) + uri);
        }

        // Because null could delete all rows:
        if(selection == null || rows_count != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rows_count;
    }


    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case CONTACTS:
                return CONTENT_TYPE_CONTACT;
            case CONTACTS_ID:
                return CONTENT_ITEM_TYPE_CONTACT;
            case PHONES:
                return CONTENT_TYPE_PHONE;
            case EMAILS:
                return CONTENT_TYPE_EMAIL;
            default:
                throw new IllegalArgumentException(
                        getContext().getString(R.string.error_unknown_uri) + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final int match = sUriMatcher.match(uri);
        long id;
        Uri returnUri;
        switch (match) {
            case CONTACTS:
                id = mDatabase.insert(ContactsContract.ContactsTableEntry.TABLE_NAME,
                        null, values);
                if (id > 0){
                    returnUri = ContentUris.withAppendedId(CONTACTS_CONTENT_URI, id);
                } else {
                    throw new UnsupportedOperationException(
                            getContext().getString(R.string.error_row_insertion) + uri);
                }
                break;
            case PHONES:
                id = mDatabase.insert(ContactsContract.PhonesTableEntry.TABLE_NAME,
                        null, values);
                if (id > 0){
                    returnUri = ContentUris.withAppendedId(PHONES_CONTENT_URI, id);
                } else {
                    throw new UnsupportedOperationException(
                            getContext().getString(R.string.error_row_insertion) + uri);
                }
                break;
            case EMAILS:
                id = mDatabase.insert(ContactsContract.EmailTableEntry.TABLE_NAME,
                        null, values);
                if (id > 0){
                    returnUri = ContentUris.withAppendedId(EMAILS_CONTENT_URI, id);
                } else {
                    throw new UnsupportedOperationException(
                            getContext().getString(R.string.error_row_insertion) + uri);
                }
                break;
            default:
                throw new IllegalArgumentException(
                        getContext().getString(R.string.error_unknown_uri) + uri);
        }
        getContext().getContentResolver().notifyChange(returnUri, null);
        return returnUri;
    }


    @Override
    public boolean onCreate() {
        mDatabase = mDbHelper.getWritableDatabase();
        return (mDatabase == null)? false:true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        switch (sUriMatcher.match(uri)){
            case CONTACTS:
                queryBuilder.setTables(ContactsContract.ContactsTableEntry.TABLE_NAME
                    + SQL_LEFT_OUTER_JOIN + ContactsContract.PhonesTableEntry.TABLE_NAME + " ON "
                    + ContactsContract.COLUMN_ID + " = " + ContactsContract.COLUMN_CONTACT_ID
                    + SQL_LEFT_OUTER_JOIN + ContactsContract.EmailTableEntry.TABLE_NAME + " ON "
                    + ContactsContract.COLUMN_ID + " = " + ContactsContract.COLUMN_CONTACT_ID);
                break;
            case CONTACTS_ID:
                queryBuilder.setTables(ContactsContract.ContactsTableEntry.TABLE_NAME
                        + SQL_LEFT_OUTER_JOIN + ContactsContract.PhonesTableEntry.TABLE_NAME + " ON "
                        + ContactsContract.COLUMN_ID + " = " + ContactsContract.COLUMN_CONTACT_ID
                        + SQL_LEFT_OUTER_JOIN + ContactsContract.EmailTableEntry.TABLE_NAME + " ON "
                        + ContactsContract.COLUMN_ID + " = " + ContactsContract.COLUMN_CONTACT_ID);
                queryBuilder.appendWhere(ContactsContract.COLUMN_ID + " = "
                        + uri.getLastPathSegment());
                break;
            default: throw new IllegalArgumentException(
                    getContext().getString(R.string.error_unknown_uri) + uri);
        }
        Cursor cursor = queryBuilder.query(
                mDatabase,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        int rows_count;

        switch(sUriMatcher.match(uri)){
            case CONTACTS:
                rows_count = mDatabase.delete(
                        ContactsContract.ContactsTableEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            case PHONES:
                rows_count = mDatabase.delete(
                        ContactsContract.PhonesTableEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            case EMAILS:
                rows_count = mDatabase.delete(
                        ContactsContract.EmailTableEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            default: throw new IllegalArgumentException(
                    getContext().getString(R.string.error_unknown_uri) + uri);
        }

        // Because null could delete all rows:
        if(selection == null || rows_count != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rows_count;
    }
}

package ua.vladprischepa.contactbooktesttask.database;

import android.provider.BaseColumns;

/**
 * Contract class for SQLite Database tables
 *
 * @author Vlad Prischepa
 * @since 02.04.2017
 * @version 1
 */

public final class ContactsContract{

    public static final String COLUMN_ID = BaseColumns._ID;
    public static final String COLUMN_CONTACT_ID = "contact_id";

    public static final class ContactsTableEntry{

        public static final String TABLE_NAME = "Contacts";
        public static final String COLUMN_FIRST_NAME = "first_name";
        public static final String COLUMN_LAST_NAME = "last_name";
    }

    public static final class PhonesTableEntry{

        public static final String TABLE_NAME = "Phones";


        public static final String COLUMN_PHONE_NUMBER = "number";
        public static final String COLUMN_NUMBER_TYPE = "type";
    }

    public static final class EmailTableEntry{
        public static final String TABLE_NAME = "Emails";

        public static final String COLUMN_EMAIL = "email";
        public static final String COLUMN_EMAIL_TYPE = "type";
    }


}

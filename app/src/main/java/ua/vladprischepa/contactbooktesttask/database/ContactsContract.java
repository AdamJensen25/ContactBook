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



    public static final class Contacts {
        public static final String COLUMN_ID = BaseColumns._ID;

        public static final String TABLE_NAME = "Contacts";
        public static final String COLUMN_FIRST_NAME = "first_name";
        public static final String COLUMN_LAST_NAME = "last_name";
        public static final String COLUMN_ID_FULL = TABLE_NAME+"."+COLUMN_ID;
        public static final String COLUMN_FIRST_NAME_FULL = TABLE_NAME+"."+COLUMN_FIRST_NAME;
        public static final String COLUMN_LAST_NAME_FULL = TABLE_NAME+"."+COLUMN_LAST_NAME;
    }

    public static final class Phones {

        public static final String TABLE_NAME = "Phones";
        public static final String COLUMN_ID = BaseColumns._ID;
        public static final String COLUMN_CONTACT_ID = "contact_id";


        public static final String COLUMN_PHONE_NUMBER = "number";
        public static final String COLUMN_NUMBER_TYPE = "number_type";
        public static final String COLUMN_ID_FULL = TABLE_NAME+"."+COLUMN_ID;
        public static final String COLUMN_CONTACT_ID_FULL = TABLE_NAME+"."+COLUMN_CONTACT_ID;
        public static final String COLUMN_PHONE_NUMBER_FULL = TABLE_NAME+"."+COLUMN_PHONE_NUMBER;
        public static final String COLUMN_NUMBER_TYPE_FULL = TABLE_NAME+"."+COLUMN_NUMBER_TYPE;
    }

    public static final class Emails {
        public static final String TABLE_NAME = "Emails";
        public static final String COLUMN_ID = BaseColumns._ID;
        public static final String COLUMN_CONTACT_ID = "contact_id";

        public static final String COLUMN_EMAIL = "email";
        public static final String COLUMN_EMAIL_TYPE = "email_type";
        public static final String COLUMN_ID_FULL = TABLE_NAME+"."+COLUMN_ID;
        public static final String COLUMN_CONTACT_ID_FULL = TABLE_NAME+"."+COLUMN_CONTACT_ID;
        public static final String COLUMN_EMAIL_FULL = TABLE_NAME+"."+COLUMN_EMAIL;
        public static final String COLUMN_EMAIL_TYPE_FULL = TABLE_NAME+"."+COLUMN_EMAIL_TYPE;
    }


}

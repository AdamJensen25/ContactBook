package ua.vladprischepa.contactbooktesttask.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import ua.vladprischepa.contactbooktesttask.R;
import ua.vladprischepa.contactbooktesttask.model.Contact;
import ua.vladprischepa.contactbooktesttask.model.EmailAddress;
import ua.vladprischepa.contactbooktesttask.model.PhoneNumber;

/**
 * DAO Class that provides access for contacts database and methods for database manage
 *
 * @author Vlad Prischepa
 * @since 04.04.2017
 * @version 1
 */

public class ContactsDao {

    private Context mContext;
    private SQLiteDatabase mDatabase;
    private ContactsDBHelper mDBHelper;

    public ContactsDao(Context context, String account){
        mContext = context;
        mDBHelper = new ContactsDBHelper(context, account);
    }

    /**
     * Method opens database connection
     * @throws SQLException if database is blocked
     */
    private void open() throws SQLException{
        mDatabase = mDBHelper.getWritableDatabase();
    }

    /**
     * Close database connection
     */
    private void close(){
        mDBHelper.close();
    }

    /**
     * Inserts Contact in contacts database
     * @param contact Contact object that have to be inserted
     * @return Id of inserted row
     */
    public long insertContact(Contact contact){
        open();
        ContentValues contactValues = new ContentValues();
        contactValues.put(ContactsContract.Contacts.COLUMN_FIRST_NAME, contact.getFirstName());
        contactValues.put(ContactsContract.Contacts.COLUMN_LAST_NAME, contact.getLastName());
        long rowId = mDatabase.insert(ContactsContract.Contacts.TABLE_NAME,
                null, contactValues);
        for (PhoneNumber number : contact.getPhoneNumbers()) {
            insertPhoneNumber(number, rowId);
        }
        for (EmailAddress emailAddress : contact.getEmails()) {
            insertEmail(emailAddress, rowId);
        }
        if (rowId<0) throw new IllegalArgumentException(
                mContext.getString(R.string.error_unknown_uri));
        close();
        return rowId;
    }

    private void insertPhoneNumber(PhoneNumber number, long contact_id){
        ContentValues phoneValues = new ContentValues();
        phoneValues.put(ContactsContract.Phones.COLUMN_PHONE_NUMBER, number.getNumber());
        phoneValues.put(ContactsContract.Phones.COLUMN_NUMBER_TYPE, number.getType());
        phoneValues.put(ContactsContract.Phones.COLUMN_CONTACT_ID, contact_id);
        mDatabase.insert(ContactsContract.Phones.TABLE_NAME,
                null, phoneValues);
    }

    private void insertEmail(EmailAddress email, long contact_id){
        ContentValues emailValues = new ContentValues();
        emailValues.put(ContactsContract.Emails.COLUMN_EMAIL, email.getEmail());
        emailValues.put(ContactsContract.Emails.COLUMN_EMAIL_TYPE, email.getType());
        emailValues.put(ContactsContract.Emails.COLUMN_CONTACT_ID, contact_id);
        mDatabase.insert(ContactsContract.Emails.TABLE_NAME,
                null, emailValues);
    }

    /**
     * Updates Contact in Contacts Database
     * @param contact contact Contact object that have to be inserted
     */
    public void updateContact(Contact contact){
        open();
        ContentValues contactValues = new ContentValues();
        contactValues.put(ContactsContract.Contacts.COLUMN_FIRST_NAME, contact.getFirstName());
        contactValues.put(ContactsContract.Contacts.COLUMN_LAST_NAME, contact.getLastName());
        mDatabase.update(ContactsContract.Contacts.TABLE_NAME, contactValues,
                ContactsContract.Contacts.COLUMN_ID + " = ?",
        new String[]{String.valueOf(contact.getId())});
        mDatabase.delete(ContactsContract.Phones.TABLE_NAME,
                ContactsContract.Phones.COLUMN_CONTACT_ID + " = ?",
                new String[]{String.valueOf(contact.getId())});
        mDatabase.delete(ContactsContract.Emails.TABLE_NAME,
                ContactsContract.Emails.COLUMN_CONTACT_ID + " = ?",
                new String[]{String.valueOf(contact.getId())});
        for (PhoneNumber number : contact.getPhoneNumbers()) {
            insertPhoneNumber(number, contact.getId());
        }
        for (EmailAddress emailAddress : contact.getEmails()) {
            insertEmail(emailAddress, contact.getId());
        }
        close();
    }

    /**
     * Deletes Contact from Contacts database
     * @param contact_id Id of Contact that should be deleted
     * @return number of affected rows
     */
    public int deleteContact(int contact_id){
        open();
        int deletedRows = mDatabase.delete(ContactsContract.Contacts.TABLE_NAME,
                ContactsContract.Contacts.COLUMN_ID + " = ?",
                new String[]{String.valueOf(contact_id)});
        deletedRows += mDatabase.delete(ContactsContract.Phones.TABLE_NAME,
                ContactsContract.Phones.COLUMN_CONTACT_ID + " = ?",
                new String[]{String.valueOf(contact_id)});
        deletedRows += mDatabase.delete(ContactsContract.Emails.TABLE_NAME,
                ContactsContract.Emails.COLUMN_CONTACT_ID + " = ?",
                new String[]{String.valueOf(contact_id)});
        close();
        return deletedRows;
    }

    /**
     * Method gets {@link List} with all Contacts from database
     * @return Lists with all contacts
     */
    public List<Contact> getAllContacts(){
        open();
        ArrayList<Contact> contacts = new ArrayList<>();
        String contactQuery = "SELECT * FROM " + ContactsContract.Contacts.TABLE_NAME;
        Cursor contactCursor = mDatabase.rawQuery(contactQuery, null);
        while (contactCursor.moveToNext()){
            Contact contact = new Contact();
            contact.setId(contactCursor.getInt(
                    contactCursor.getColumnIndexOrThrow(ContactsContract.Contacts.COLUMN_ID)));
            contact.setFirstName(contactCursor.getString(
                    contactCursor.getColumnIndexOrThrow(ContactsContract.Contacts.COLUMN_FIRST_NAME)));
            contact.setLastName(contactCursor.getString(
                    contactCursor.getColumnIndexOrThrow(ContactsContract.Contacts.COLUMN_LAST_NAME)));
            contact.setPhoneNumbers(getPhoneNumbers(contact.getId()));
            contact.setEmails(getEmails(contact.getId()));
            contacts.add(contact);
        }
        close();
        return contacts;
    }

    /**
     * Method gets {@link List} with Phone Numbers from database by id
     * @param contact_id Contacts id, whom Phone Numbers belongs
     * @return List with Phone Numbers
     */
    private List<PhoneNumber> getPhoneNumbers(int contact_id){
        String phonesQuery = "SELECT * FROM " + ContactsContract.Phones.TABLE_NAME
                + " WHERE " + ContactsContract.Phones.COLUMN_CONTACT_ID_FULL + " = "
                + contact_id;
        Cursor phonesCursor = mDatabase.rawQuery(phonesQuery, null);
        return getNumbersFromCursor(phonesCursor);
    }

    /**
     * Method gets {@link List} with {@link PhoneNumber} from Cursor
     * @param cursor cursor with Phone Numbers, that is returned from database
     * @return List with phone numbers
     */
    private List<PhoneNumber> getNumbersFromCursor(Cursor cursor){
        ArrayList<PhoneNumber> numbers = new ArrayList<>();
        while (cursor.moveToNext()){
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(ContactsContract.Phones.COLUMN_ID));
            String phone = cursor.getString(
                    cursor.getColumnIndexOrThrow(ContactsContract.Phones.COLUMN_PHONE_NUMBER));
            String type = cursor.getString(
                    cursor.getColumnIndexOrThrow(ContactsContract.Phones.COLUMN_NUMBER_TYPE));
            PhoneNumber phoneNumber = new PhoneNumber(phone, type, id);
            numbers.add(phoneNumber);
        }
        return numbers;
    }

    /**
     * Method gets {@link List} with Email Addresses from database by id
     * @param contact_id Contacts id, whom Email Address belongs
     * @return List with Email Addresses
     */
    private List<EmailAddress> getEmails(int contact_id){
        String emailsQuery = "SELECT * FROM " + ContactsContract.Emails.TABLE_NAME
                + " WHERE " + ContactsContract.Emails.COLUMN_CONTACT_ID_FULL + " = "
                + contact_id;
        Cursor phonesCursor = mDatabase.rawQuery(emailsQuery, null);
        return getEmailsFromCursor(phonesCursor);
    }

    /**
     * Method gets {@link List} with {@link EmailAddress} from Cursor
     * @param cursor cursor with Email Addresses, that is returned from database
     * @return List with emails
     */
    private List<EmailAddress> getEmailsFromCursor(Cursor cursor){
        ArrayList<EmailAddress> emails = new ArrayList<>();
        while (cursor.moveToNext()){
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(ContactsContract.Emails.COLUMN_ID));
            String email_address = cursor.getString(
                    cursor.getColumnIndexOrThrow(ContactsContract.Emails.COLUMN_EMAIL));
            String type = cursor.getString(
                    cursor.getColumnIndexOrThrow(ContactsContract.Emails.COLUMN_EMAIL_TYPE));
            EmailAddress phoneNumber = new EmailAddress(email_address, type, id);
            emails.add(phoneNumber);
        }
        return emails;
    }

    // вывод в лог данных из курсора
    private void logCursor(Cursor c) {
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

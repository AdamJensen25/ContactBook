package ua.vladprischepa.contactbooktesttask.utils;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

import ua.vladprischepa.contactbooktesttask.database.ContactsDao;
import ua.vladprischepa.contactbooktesttask.model.Contact;

/**
 * Loader class for asynchronous loading Contacts List
 * @author Vlad Prischepa
 * @since 04.04.2017
 * @version 1
 */

public class ContactsLoader extends AsyncTaskLoader<List<Contact>>{

    /**
     * Contacts DAO
     */
    private ContactsDao mDao;

    public ContactsLoader(Context context, String account){
        super(context);
        mDao = new ContactsDao(context, account);
    }

    /**
     * Loads Contacts List in background thread
     * @return
     */
    @Override
    public List<Contact> loadInBackground() {
        return mDao.getAllContacts();
    }
}

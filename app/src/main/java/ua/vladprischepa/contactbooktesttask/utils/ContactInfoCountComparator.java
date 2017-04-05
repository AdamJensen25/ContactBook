package ua.vladprischepa.contactbooktesttask.utils;

import java.util.Comparator;

import ua.vladprischepa.contactbooktesttask.model.Contact;

/**
 * Comparator class that implements sorting contacts by count of contact information
 * @author Vlad Prischepa
 * @since 05.04.2017
 * @version 1
 */

public class ContactInfoCountComparator implements Comparator<Contact> {

    private boolean mAscending;

    public ContactInfoCountComparator(boolean ascending){
        mAscending = ascending;
    }

    @Override
    public int compare(Contact o1, Contact o2) {
        int count1 = o1.getEmails().size() + o1.getPhoneNumbers().size();
        int count2 = o2.getEmails().size() + o2.getPhoneNumbers().size();
        if (mAscending){
            return Double.compare(count1, count2);
        } else {
            return Double.compare(count2, count1);
        }
    }
}

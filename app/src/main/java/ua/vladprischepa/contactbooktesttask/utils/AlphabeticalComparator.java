package ua.vladprischepa.contactbooktesttask.utils;

import java.util.Comparator;

import ua.vladprischepa.contactbooktesttask.model.Contact;

/**
 * Comparator class that implements sorting contacts by name
 * @author Vlad Prischepa
 * @since 05.04.2017
 * @version 1
 */

public class AlphabeticalComparator implements Comparator<Contact> {
    private boolean mAscending;

    public AlphabeticalComparator(boolean ascending){
        mAscending = ascending;
    }

    @Override
    public int compare(Contact o1, Contact o2) {
        if (mAscending){
            return o1.getFullName().compareToIgnoreCase(o2.getFullName());
        } else {
            return o2.getFullName().compareToIgnoreCase(o1.getFullName());
        }
    }
}

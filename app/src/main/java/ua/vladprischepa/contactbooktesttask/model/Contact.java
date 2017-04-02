package ua.vladprischepa.contactbooktesttask.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * @author Vlad Prischepa
 * @since 02.04.2017
 * @version 1
 */

public class Contact implements Parcelable{

    private String mFirstName;
    private String mLastName;
    private List<PhoneNumber> mPhoneNumbers;
    private List<EmailAddress> mEmails;
    private int mId;

    public Contact(String firstName, String lastName,
                    List<PhoneNumber> phoneNumbers, List<EmailAddress> emails, int id){
        mFirstName = firstName;
        mLastName = lastName;
        mPhoneNumbers = phoneNumbers;
        mEmails = emails;
        mId = id;
    }

    public Contact(){
    }

    protected Contact(Parcel in) {
        mFirstName = in.readString();
        mLastName = in.readString();
        mPhoneNumbers = in.createTypedArrayList(PhoneNumber.CREATOR);
        mEmails = in.createTypedArrayList(EmailAddress.CREATOR);
        mId = in.readInt();
    }

    public static final Creator<Contact> CREATOR = new Creator<Contact>() {
        @Override
        public Contact createFromParcel(Parcel in) {
            return new Contact(in);
        }

        @Override
        public Contact[] newArray(int size) {
            return new Contact[ size ];
        }
    };

    public String getFirstName() {
        return mFirstName;
    }

    public String getLastName() {
        return mLastName;
    }

    public List<PhoneNumber> getPhoneNumbers() {
        return mPhoneNumbers;
    }

    public List<EmailAddress> getEmails() {
        return mEmails;
    }

    public void setFirstName(String firstName) {
        mFirstName = firstName;
    }

    public void setLastName(String lastName) {
        mLastName = lastName;
    }

    public void setEmails(List<EmailAddress> emails) {
        mEmails = emails;
    }

    public void setPhoneNumbers(List<PhoneNumber> phoneNumbers) {
        mPhoneNumbers = phoneNumbers;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Contact contact = (Contact) o;

        if (! mFirstName.equals(contact.mFirstName)) return false;
        if (! mLastName.equals(contact.mLastName)) return false;
        if (! mPhoneNumbers.equals(contact.mPhoneNumbers)) return false;
        if (mId != contact.mId) return false;
        return mEmails.equals(contact.mEmails);
    }

    @Override
    public int hashCode() {
        int result = mFirstName.hashCode();
        result = 31 * result + mLastName.hashCode();
        result = 31 * result + mPhoneNumbers.hashCode();
        result = 31 * result + mEmails.hashCode();
        result = 31 * result + mId;
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mFirstName);
        dest.writeString(mLastName);
        dest.writeTypedList(mPhoneNumbers);
        dest.writeTypedList(mEmails);
        dest.writeInt(mId);
    }
}

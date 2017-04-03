package ua.vladprischepa.contactbooktesttask.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Email Address class
 * @author Vlad Prischepa
 * @since 02.04.2017
 * @version 1
 */

public class EmailAddress implements Parcelable{
    private final String mEmail;
    private final String mType;
    private final int mId;

    public EmailAddress(String email, String type, int id){
        mEmail = email;
        mType = type;
        mId = id;
    }

    protected EmailAddress(Parcel in) {
        mEmail = in.readString();
        mType = in.readString();
        mId = in.readInt();
    }

    public static final Creator<EmailAddress> CREATOR = new Creator<EmailAddress>() {
        @Override
        public EmailAddress createFromParcel(Parcel in) {
            return new EmailAddress(in);
        }

        @Override
        public EmailAddress[] newArray(int size) {
            return new EmailAddress[ size ];
        }
    };

    public String getType() {
        return mType;
    }

    public String getEmail() {
        return mEmail;
    }

    public int getId() {
        return mId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mEmail);
        dest.writeString(mType);
        dest.writeInt(mId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EmailAddress that = (EmailAddress) o;

        if (mType != that.mType) return false;
        else if (mId != that.mId) return false;
        return mEmail.equals(that.mEmail);
    }

    @Override
    public int hashCode() {
        int result = mEmail.hashCode();
        result = 31 * result + mType.hashCode();
        result = 31 * result + mId;
        return result;
    }
}

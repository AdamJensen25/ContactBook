package ua.vladprischepa.contactbooktesttask.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author Vlad Prischepa
 * @since 02.04.2017
 * @version 1
 */

public class PhoneNumber implements Parcelable{

    private final String mNumber;
    private final String mType;
    private final int mId;

    public PhoneNumber(String number, String type, int id){
        mNumber = number;
        mType = type;
        mId = id;
    }

    protected PhoneNumber(Parcel in) {
        mNumber = in.readString();
        mType = in.readString();
        mId = in.readInt();
    }

    public static final Creator<PhoneNumber> CREATOR = new Creator<PhoneNumber>() {
        @Override
        public PhoneNumber createFromParcel(Parcel in) {
            return new PhoneNumber(in);
        }

        @Override
        public PhoneNumber[] newArray(int size) {
            return new PhoneNumber[ size ];
        }
    };

    public String getType() {
        return mType;
    }

    public String getNumber() {
        return mNumber;
    }

    public int getId() {
        return mId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PhoneNumber that = (PhoneNumber) o;

        if (mType != that.mType) return false;
        else if (mId != that.mId) return false;
        return mNumber.equals(that.mNumber);
    }

    @Override
    public int hashCode() {
        int result = mNumber.hashCode();
        result = 31 * result + mType.hashCode();
        result = 31 * result + mId;
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }



    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mNumber);
        dest.writeString(mType);
        dest.writeInt(mId);
    }
}

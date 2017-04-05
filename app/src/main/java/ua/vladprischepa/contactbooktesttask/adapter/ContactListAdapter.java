package ua.vladprischepa.contactbooktesttask.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ua.vladprischepa.contactbooktesttask.R;
import ua.vladprischepa.contactbooktesttask.model.Contact;
import ua.vladprischepa.contactbooktesttask.utils.AlphabeticalComparator;
import ua.vladprischepa.contactbooktesttask.utils.ContactInfoCountComparator;

/**
 * @author Vlad Prischepa
 * @since 03.04.2017
 * @version 1
 */

public class ContactListAdapter extends RecyclerView.Adapter<ContactListAdapter.ContactsViewHolder>
    implements MultiSelector<Contact>{

    public static final int ALPHABETIC_SORT_ASCENDING = 0;
    public static final int ALPHABETIC_SORT_DESCENDING = 1;
    public static final int CONTACT_INFO_SORT_ASCENDING = 2;
    public static final int CONTACT_INFO_SORT_DESCENDING = 3;
    private Context mContext;
    private SparseBooleanArray mSelectedItems;
    private List<Contact> mContacts;
    private final OnItemClickListener mClickListener;

    private int mSortOrder = ALPHABETIC_SORT_ASCENDING;

    public interface OnItemClickListener{
        void onItemClick(Contact contact, int position);
        void onItemLongClick(int position);
    }

    public ContactListAdapter(Context context, OnItemClickListener listener){
        mContext = context;
        mContacts = new ArrayList<>();
        mClickListener = listener;
        mSelectedItems = new SparseBooleanArray();
    }

    @Override
    public ContactsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View root = inflater.inflate(R.layout.item_contact, parent, false);
        return new ContactsViewHolder(root);
    }

    @Override
    public void onBindViewHolder(ContactsViewHolder holder, int position) {
        Contact contact = mContacts.get(position);
        String fullName = contact.getLastName() + " " + contact.getFirstName();
        holder.mContactName.setText(fullName);
        holder.setListener(contact, position, mClickListener);
        if (mSelectedItems.get(position)){
            holder.itemView.setBackgroundColor(mContext.getResources()
                    .getColor(R.color.colorAccent));
            holder.setThumbnail(contact.getLastName(), true);
        } else {
            holder.itemView.setBackgroundColor(mContext.getResources()
                    .getColor(android.R.color.background_light));
            holder.setThumbnail(contact.getLastName(), false);
        }

    }

    public int getSortOrder(){ return mSortOrder; }

    public void sortContacts(int sortOrderFlag){
        mSortOrder = sortOrderFlag;
        switch (sortOrderFlag){
            case ALPHABETIC_SORT_ASCENDING:
                Collections.sort(mContacts, new AlphabeticalComparator(true));
                notifyDataSetChanged();
                break;
            case ALPHABETIC_SORT_DESCENDING:
                Collections.sort(mContacts, new AlphabeticalComparator(false));
                notifyDataSetChanged();
                break;
            case CONTACT_INFO_SORT_ASCENDING:
                Collections.sort(mContacts, new ContactInfoCountComparator(true));
                notifyDataSetChanged();
                break;
            case CONTACT_INFO_SORT_DESCENDING:
                Collections.sort(mContacts, new ContactInfoCountComparator(false));
                notifyDataSetChanged();
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mContacts.size();
    }

    @Override
    public void toggleSelection(int position) {
        if (mSelectedItems.get(position, false)){
            mSelectedItems.delete(position);
        } else {
            mSelectedItems.put(position, true);
        }
        notifyItemChanged(position);
    }

    @Override
    public void clearSelection() {
        mSelectedItems.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getSelectedItemsCount() {
        return mSelectedItems.size();
    }

    @Override
    public List<Contact> getSelectedItems() {
        List<Contact> selectedContacts = new ArrayList<>();
        for (int i = 0; i < mSelectedItems.size(); i++) {
            selectedContacts.add(mContacts.get(mSelectedItems.keyAt(i)));
        }
        return selectedContacts;
    }

    /**
     * Update dataset if it was changed
     * @param contacts new Contacts List
     */
    public void updateDataSet(List<Contact> contacts){
        mContacts.clear();
        mContacts.addAll(contacts);
        sortContacts(mSortOrder);
        notifyDataSetChanged();
    }

    public class ContactsViewHolder extends RecyclerView.ViewHolder {

        private ColorGenerator mColorGenerator = ColorGenerator.DEFAULT;
        private TextDrawable mDrawableBuilder;
        @BindView(R.id.contactThumbnail)
        ImageView mThumbnail;
        @BindView(R.id.tvContactFullName)
        TextView mContactName;

        public ContactsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void setListener(final Contact contact, final int position, final OnItemClickListener listener){
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(contact, position);
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    listener.onItemLongClick(position);
                    return true;
                }
            });
        }

        public void setThumbnail(String lastName, boolean selected){
            String letter = "A";

            if(lastName != null && !lastName.isEmpty()) {
                letter = lastName.substring(0, 1);
            }
            int color = mColorGenerator.getRandomColor();
            // Create a circular icon consisting of a random background colour and first letter of title
            if (selected){
                mDrawableBuilder = TextDrawable.builder()
                        .buildRound("\u2713",
                                mContext.getResources().getColor(android.R.color.holo_green_dark));
                mThumbnail.setImageDrawable(mDrawableBuilder);
            } else {
                mDrawableBuilder = TextDrawable.builder()
                        .buildRound(letter, color);
                mThumbnail.setImageDrawable(mDrawableBuilder);
            }
        }
    }
}

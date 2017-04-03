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
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ua.vladprischepa.contactbooktesttask.MultiSelector;
import ua.vladprischepa.contactbooktesttask.R;
import ua.vladprischepa.contactbooktesttask.model.Contact;

/**
 * @author Vlad Prischepa
 * @since 03.04.2017
 * @version 1
 */

public class ContactListAdapter extends RecyclerView.Adapter<ContactListAdapter.ContactsViewHolder>
    implements MultiSelector<Contact>{

    private Context mContext;
    private SparseBooleanArray mSelectedItems;
    private List<Contact> mContacts;

    public ContactListAdapter(Context context, List<Contact> contacts){
        mContext = context;
        mContacts = contacts;
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
        holder.setThumbnail(contact.getLastName());
        holder.mContactName.setText(fullName);
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

        public void setThumbnail(String lastName){
            String letter = "A";

            if(lastName != null && !lastName.isEmpty()) {
                letter = lastName.substring(0, 1);
            }
            int color = mColorGenerator.getRandomColor();
            // Create a circular icon consisting of a random background colour and first letter of title
            mDrawableBuilder = TextDrawable.builder()
                    .buildRound(letter, color);
            mThumbnail.setImageDrawable(mDrawableBuilder);
        }
    }
}

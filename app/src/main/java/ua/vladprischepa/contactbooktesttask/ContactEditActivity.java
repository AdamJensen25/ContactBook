package ua.vladprischepa.contactbooktesttask;

import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ua.vladprischepa.contactbooktesttask.database.ContactsContract;
import ua.vladprischepa.contactbooktesttask.database.ContactsDBHelper;
import ua.vladprischepa.contactbooktesttask.model.Contact;
import ua.vladprischepa.contactbooktesttask.model.EmailAddress;
import ua.vladprischepa.contactbooktesttask.model.PhoneNumber;

public class ContactEditActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.etFirsName)
    TextInputEditText mEditFirstName;
    @BindView(R.id.etLastName)
    TextInputEditText mEditLastName;
    @BindView(R.id.layoutFirstName)
    TextInputLayout mLayoutFirstName;
    @BindView(R.id.layoutLastName)
    TextInputLayout mLayoutLastName;
    @BindView(R.id.phoneNumberContainer)
    LinearLayout mPhoneNumberContainer;
    @BindView(R.id.emailContainer)
    LinearLayout mEmailContainer;
    @BindView(R.id.addPhoneNumber)
    TextView mAddPhoneNumber;
    @BindView(R.id.addEmail)
    TextView mAddEmail;

    private String mAccount;
    private List<View> mInflatedPhoneFields;
    private List<View> mInflatedEmailFields;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAccount = getIntent().getStringExtra(ContactListActivity.KEY_GOOGLE_ACCOUNT);
        mInflatedPhoneFields = new ArrayList<>();
        mInflatedEmailFields = new ArrayList<>();
        initViews();
    }

    private void initViews(){
        setContentView(R.layout.activity_contact_edit);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        addPhoneField();
        addEmailField();

    }

    @OnClick(R.id.addEmail)
    void addEmailField(){
        final View inflatedView = LayoutInflater.from(this)
                .inflate(R.layout.item_email, mEmailContainer, false);
        final TextInputEditText etEmail = (TextInputEditText) inflatedView.findViewById(R.id.etEmail);
        ImageView imgClear = (ImageView) inflatedView.findViewById(R.id.imgClear);
        Spinner phoneTypeSpinner = (Spinner) inflatedView.findViewById(R.id.spinnerEmail);
        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(this, R.array.email_types_array,
                android.R.layout.simple_spinner_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        phoneTypeSpinner.setAdapter(arrayAdapter);
        mInflatedEmailFields.add(inflatedView);
        final View.OnClickListener removeListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mInflatedEmailFields.size()<=1){
                    etEmail.setText("");
                } else {
                    ((LinearLayout) inflatedView.getParent()).removeView(inflatedView);
                    mInflatedEmailFields.remove(inflatedView);
                }
            }
        };
        imgClear.setOnClickListener(removeListener);
        mEmailContainer.addView(inflatedView);
    }

    @OnClick(R.id.addPhoneNumber)
    void addPhoneField(){
        final View inflatedView = LayoutInflater.from(this)
                .inflate(R.layout.item_phone_number, mPhoneNumberContainer, false);
        final TextInputEditText etPhone = (TextInputEditText) inflatedView.findViewById(R.id.etPhone);
        ImageView imgClear = (ImageView) inflatedView.findViewById(R.id.imgClear);
        Spinner phoneTypeSpinner = (Spinner) inflatedView.findViewById(R.id.spinnerPhoneType);
        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(this, R.array.phone_types_array,
                android.R.layout.simple_spinner_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        phoneTypeSpinner.setAdapter(arrayAdapter);
        mInflatedPhoneFields.add(inflatedView);
        final View.OnClickListener removeListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mInflatedPhoneFields.size()<=1){
                    etPhone.setText("");
                } else {
                    ((LinearLayout) inflatedView.getParent()).removeView(inflatedView);
                    mInflatedPhoneFields.remove(inflatedView);
                }
            }
        };
        imgClear.setOnClickListener(removeListener);
        mPhoneNumberContainer.addView(inflatedView);
    }

    private void saveContact(){
        String firstName = mEditFirstName.getText().toString();
        String lastName = mEditLastName.getText().toString();

        ContactsDBHelper dbHelper = new ContactsDBHelper(this, mAccount);
        Contact contact = new Contact(firstName, lastName, getPhoneNumbers(), getEmails(), 0);
        dbHelper.insertContact(contact);

    }

    private List<PhoneNumber> getPhoneNumbers(){
        List<PhoneNumber> phoneNumbers = new ArrayList<>();
        for (int i = 0; i < mPhoneNumberContainer.getChildCount(); i++) {
            LinearLayout layoutRoot = ((LinearLayout) mPhoneNumberContainer.getChildAt(i));
            TextInputEditText editTextPhoneNumber =
                    (TextInputEditText) layoutRoot.findViewById(R.id.etPhone);
            String phoneNumber = editTextPhoneNumber.getText().toString();
            Spinner spinner = ((Spinner) layoutRoot.findViewById(R.id.spinnerPhoneType));
            String type = spinner.getSelectedItem().toString();
            phoneNumbers.add(new PhoneNumber(phoneNumber, type, 0));
        }
        return phoneNumbers;
    }

    private List<EmailAddress> getEmails(){
        List<EmailAddress> emails = new ArrayList<>();
        for (int i = 0; i < mEmailContainer.getChildCount(); i++) {
            LinearLayout layoutRoot = ((LinearLayout) mEmailContainer.getChildAt(i));
            TextInputEditText editTextEmail =
                    (TextInputEditText) layoutRoot.findViewById(R.id.etEmail);
            String email = editTextEmail.getText().toString();
            Spinner spinner = (Spinner) layoutRoot.findViewById(R.id.spinnerEmail);
            String type = spinner.getSelectedItem().toString();
            emails.add(new EmailAddress(email, type, 0));
        }
        return emails;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_contact_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.save) {
            saveContact();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

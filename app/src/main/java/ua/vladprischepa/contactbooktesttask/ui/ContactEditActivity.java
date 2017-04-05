package ua.vladprischepa.contactbooktesttask.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ua.vladprischepa.contactbooktesttask.R;
import ua.vladprischepa.contactbooktesttask.database.ContactsDao;
import ua.vladprischepa.contactbooktesttask.model.Contact;
import ua.vladprischepa.contactbooktesttask.model.EmailAddress;
import ua.vladprischepa.contactbooktesttask.model.PhoneNumber;

public class ContactEditActivity extends AppCompatActivity implements View.OnClickListener {

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
    private int mFlag;
    private Contact mContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAccount = getIntent().getStringExtra(ContactListActivity.KEY_GOOGLE_ACCOUNT);
        mFlag = getIntent().getIntExtra(ContactListActivity.KEY_FLAG, ContactListActivity.FLAG_NEW_TASK);
        mInflatedPhoneFields = new ArrayList<>();
        mInflatedEmailFields = new ArrayList<>();
        initViews();



    }

    private void initViews(){
        setContentView(R.layout.activity_contact_edit);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mAddPhoneNumber.setOnClickListener(this);
        mAddEmail.setOnClickListener(this);
        if (mFlag == ContactListActivity.FLAG_EDIT_TASK){
            mContact = getIntent().getParcelableExtra(ContactListActivity.KEY_CONTACT);
            populateFields();
            getSupportActionBar().setTitle(R.string.title_edit_contact);
        } else {
            mContact = new Contact();
            addPhoneField(null);
            addEmailField(null);
            getSupportActionBar().setTitle(R.string.title_new_contact);
        }
    }

    private void populateFields(){
        mEditFirstName.setText(mContact.getFirstName());
        mEditLastName.setText(mContact.getLastName());
        if (mContact.getPhoneNumbers().isEmpty()) addPhoneField(null);
        else {
            for (PhoneNumber number : mContact.getPhoneNumbers()){
                addPhoneField(number);
            }
        }
        if (mContact.getEmails().isEmpty()) addEmailField(null);
        else {
            for (EmailAddress email : mContact.getEmails()){
                addEmailField(email);
            }
        }
    }


    /**
     * Method inflates Email Address input field with e-mail for editing or empty
     * if e-mail is null
     * @param emailAddress {@link EmailAddress}
     */
    void addEmailField(@Nullable EmailAddress emailAddress){
        final View inflatedView = LayoutInflater.from(this)
                .inflate(R.layout.item_email, mEmailContainer, false);
        final TextInputEditText etEmail = (TextInputEditText) inflatedView.findViewById(R.id.etEmail);
        ImageView imgClear = (ImageView) inflatedView.findViewById(R.id.imgClear);
        Spinner emailTypeSpinner = (Spinner) inflatedView.findViewById(R.id.spinnerEmail);
        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(this, R.array.email_types_array,
                android.R.layout.simple_spinner_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        emailTypeSpinner.setAdapter(arrayAdapter);
        mInflatedEmailFields.add(inflatedView);
        final View.OnClickListener removeListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mInflatedEmailFields.size()<=1){
                    etEmail.setText("");
                } else {
                    if (!TextUtils.isEmpty(etEmail.getText().toString())){
                        etEmail.setText("");
                    } else {
                        ((LinearLayout) inflatedView.getParent()).removeView(inflatedView);
                        mInflatedEmailFields.remove(inflatedView);
                    }
                }
            }
        };
        imgClear.setOnClickListener(removeListener);
        if (emailAddress != null){
            etEmail.setText(emailAddress.getEmail());
            emailTypeSpinner.setSelection(arrayAdapter.getPosition(emailAddress.getType()));
        }
        mEmailContainer.addView(inflatedView);
    }

    /**
     * Method inflates Phone Number input field with phone number for editing or empty
     * if number is null
     * @param number {@link PhoneNumber}
     */
    void addPhoneField(@Nullable PhoneNumber number){
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
                    if (!TextUtils.isEmpty(etPhone.getText().toString())){
                        etPhone.setText("");
                    } else {
                        ((LinearLayout) inflatedView.getParent()).removeView(inflatedView);
                        mInflatedPhoneFields.remove(inflatedView);
                    }
                }
            }
        };
        imgClear.setOnClickListener(removeListener);
        if (number != null){
            etPhone.setText(number.getNumber());
            phoneTypeSpinner.setSelection(arrayAdapter.getPosition(number.getType()));
        }
        mPhoneNumberContainer.addView(inflatedView);
    }

    private void saveContact(){
        String firstName = mEditFirstName.getText().toString();
        String lastName = mEditLastName.getText().toString();

        ContactsDao dao = new ContactsDao(this, mAccount);
        mContact.setFirstName(firstName);
        mContact.setLastName(lastName);
        mContact.setPhoneNumbers(getPhoneNumbers());
        mContact.setEmails(getEmails());
        if (mFlag == ContactListActivity.FLAG_NEW_TASK){
            dao.insertContact(mContact);
        } else if (mFlag == ContactListActivity.FLAG_EDIT_TASK){
            dao.updateContact(mContact);
        }
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
            if (!TextUtils.isEmpty(phoneNumber)) {
                phoneNumbers.add(new PhoneNumber(phoneNumber, type, 0));
            }
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
            if (!TextUtils.isEmpty(email)){
                emails.add(new EmailAddress(email, type, 0));
            }
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
            Toast.makeText(this, "Contact saved!", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
            return true;
        } else if (id == android.R.id.home){
            super.onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.addEmail:
                addEmailField(null);
                break;
            case R.id.addPhoneNumber:
                addPhoneField(null);
                break;
        }
    }
}

package ua.vladprischepa.contactbooktesttask.ui;

import android.app.LoaderManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ua.vladprischepa.contactbooktesttask.R;
import ua.vladprischepa.contactbooktesttask.database.ContactsDao;
import ua.vladprischepa.contactbooktesttask.utils.ContactsLoader;
import ua.vladprischepa.contactbooktesttask.model.Contact;
import ua.vladprischepa.contactbooktesttask.adapter.ContactListAdapter;


@SuppressWarnings("unused")
public class ContactListActivity extends AppCompatActivity
        implements GoogleApiClient.OnConnectionFailedListener,
        LoaderManager.LoaderCallbacks<List<Contact>>, ContactListAdapter.OnItemClickListener {

    /**
     * Request code for {@link com.google.android.gms.auth.api.signin.GoogleSignInApi}
     */
    private static final int SIGN_IN_REQUEST_CODE = 20;

    /**
     * Request codes for {@link ContactEditActivity}
     */
    public static final int FLAG_EDIT_TASK = 100;
    public static final int FLAG_NEW_TASK = 101;

    /**
     * Keys for intent arguments arguments
     */
    public static final String KEY_FLAG = "flag";
    public static final String KEY_CONTACT = "contact";
    public static final String KEY_GOOGLE_ACCOUNT = "account";

    private GoogleSignInAccount mAccount;

    private GoogleApiClient mGoogleApiClient;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.fabAddContact)
    FloatingActionButton mFabAddContact;
    @BindView(R.id.layoutSignIn)
    LinearLayout mSignInLayout;
    @BindView(R.id.btnSignIn)
    SignInButton mSignInButton;
    @BindView(R.id.recyclerContacts)
    RecyclerView mRecyclerContacts;
    @BindView(R.id.progressBar)
    ProgressBar mProgressBar;
    private MenuItem mSignOutMenuItem;
    private MenuItem mSortOrderMenuItem;
    private ContactListAdapter mAdapter;
    private ActionMode mActionMode;

    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.menu_action_mode, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            mFabAddContact.setVisibility(View.GONE);
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()){
                case R.id.delete:
                    for (Contact contact : mAdapter.getSelectedItems()){
                        ContactsDao dao = new ContactsDao(getApplicationContext(), mAccount.getEmail());
                        dao.deleteContact(contact.getId());
                    }
                    mAdapter.clearSelection();
                    getLoaderManager().getLoader(0).forceLoad();
                    mode.finish();
            }
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
            mFabAddContact.setVisibility(View.VISIBLE);
            mAdapter.clearSelection();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initGoogleApiClient();
        initViews();
    }

    /**
     * Initialize views
     */
    private void initViews(){
        setContentView(R.layout.activity_contact_list);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        mSignInButton.setSize(SignInButton.SIZE_STANDARD);
        mAdapter = new ContactListAdapter(this, this);
        mRecyclerContacts.setAdapter(mAdapter);
        mRecyclerContacts.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerContacts.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL));
    }

    /**
     * Initializing GoogleApiClient for accessing to Google Account
     */
    private void initGoogleApiClient(){
        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(
                GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, signInOptions)
                .build();
    }

    @OnClick(R.id.btnSignIn)
    void performSignIn(){
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, SIGN_IN_REQUEST_CODE);
    }

    @OnClick(R.id.fabAddContact)
    void addNewContact(){
        Intent editIntent = new Intent(
                ContactListActivity.this, ContactEditActivity.class);
        editIntent.putExtra(KEY_FLAG, FLAG_NEW_TASK);
        editIntent.putExtra(KEY_GOOGLE_ACCOUNT, mAccount.getEmail());
        startActivityForResult(editIntent, FLAG_NEW_TASK);
    }

    private void editContact(@NonNull Contact contact){
        Intent contactEditIntent = new Intent(this, ContactEditActivity.class);
        contactEditIntent.putExtra(KEY_CONTACT, contact);
        contactEditIntent.putExtra(KEY_FLAG, FLAG_EDIT_TASK);
        contactEditIntent.putExtra(KEY_GOOGLE_ACCOUNT, mAccount.getEmail());
        startActivityForResult(contactEditIntent, FLAG_EDIT_TASK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case SIGN_IN_REQUEST_CODE:
                handleSignInResult(Auth.GoogleSignInApi.getSignInResultFromIntent(data));
                break;
        }
        switch (resultCode){
            case RESULT_OK:
                getLoaderManager().getLoader(0).forceLoad();
        }
    }

    private void handleSignInResult(GoogleSignInResult result){
        if (result.isSuccess()){
            mAccount = result.getSignInAccount();
            showSignInLayout(false);
            getLoaderManager().initLoader(0,null, this);
            getLoaderManager().getLoader(0).forceLoad();
            Toast.makeText(this, getString(R.string.welcome) + " " + mAccount.getDisplayName(),
                    Toast.LENGTH_SHORT).show();
        } else {
            showSignInLayout(true);
        }
    }

    private void showSignInLayout(boolean show){
        if (show){
            mSignInLayout.setVisibility(View.VISIBLE);
            mFabAddContact.setVisibility(View.GONE);
            if (mSignOutMenuItem != null) mSignOutMenuItem.setVisible(false);
            if (mSortOrderMenuItem != null) mSortOrderMenuItem.setVisible(false);
            mRecyclerContacts.setVisibility(View.GONE);

        } else {
            mSignInLayout.setVisibility(View.GONE);
            mFabAddContact.setVisibility(View.VISIBLE);
            if (mSignOutMenuItem != null) mSignOutMenuItem.setVisible(true);
            if (mSortOrderMenuItem != null) mSortOrderMenuItem.setVisible(true);
            mRecyclerContacts.setVisibility(View.VISIBLE);
        }
    }

    private void silentSignIn(){
        OptionalPendingResult<GoogleSignInResult> optionalPendingResult
                = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (optionalPendingResult.isDone()){
            handleSignInResult(optionalPendingResult.get());
        } else {
            showProgress(true);
            optionalPendingResult.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    showProgress(false);
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }

    private void signOut(){
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        Toast.makeText(ContactListActivity.this, R.string.sign_out_message,
                                Toast.LENGTH_SHORT).show();
                        showSignInLayout(true);
                    }
                });
    }

    private void showSortOrderDialog(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.sort_dialog_title));
        builder.setSingleChoiceItems(R.array.sorting_array, mAdapter.getSortOrder(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mAdapter.sortContacts(which);
                        dialog.dismiss();
                    }
                }
        );
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_contact_list, menu);
        mSignOutMenuItem = menu.findItem(R.id.sign_out);
        mSortOrderMenuItem = menu.findItem(R.id.sort);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (mSignInLayout.getVisibility() == View.VISIBLE){
            mSignOutMenuItem.setVisible(false);
            mSortOrderMenuItem.setVisible(false);
        } else{
            mSignOutMenuItem.setVisible(true);
            mSortOrderMenuItem.setVisible(true);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.sign_out) {
            signOut();
            return true;
        } else if (id == R.id.sort){
            showSortOrderDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    private void showProgress(boolean show){
        mProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        showSignInLayout(show ? false : true);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, connectionResult.getErrorMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAccount == null){
            silentSignIn();
        }
    }

    @Override
    public Loader<List<Contact>> onCreateLoader(int id, Bundle args) {
        return new ContactsLoader(this, mAccount.getEmail());
    }

    @Override
    public void onLoadFinished(Loader<List<Contact>> loader, List<Contact> data) {
        mAdapter.updateDataSet(data);
    }

    @Override
    public void onLoaderReset(Loader<List<Contact>> loader) {
    }

    @Override
    public void onItemClick(Contact contact, int position) {
        if (mActionMode == null){
            editContact(contact);
        } else {
            mAdapter.toggleSelection(position);
            mActionMode.setTitle(mAdapter.getSelectedItemsCount()
                    + " " + getString(R.string.title_action_mode));
        }
    }

    @Override
    public void onItemLongClick(int position) {
        ContactListActivity activity = ContactListActivity.this;
        if (mActionMode == null){
            mActionMode = activity.startSupportActionMode(mActionModeCallback);
        }
        mAdapter.toggleSelection(position);
        mActionMode.setTitle(mAdapter.getSelectedItemsCount()
                + " " + getString(R.string.title_action_mode));
    }

    @Override
    public void onBackPressed() {
        if (mActionMode != null){
            mActionMode.finish();
        } else {
            super.onBackPressed();
        }

    }
}

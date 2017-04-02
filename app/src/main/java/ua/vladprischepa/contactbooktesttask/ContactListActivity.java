package ua.vladprischepa.contactbooktesttask;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@SuppressWarnings("unused")
public class ContactListActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final int SIGN_IN_REQUEST_CODE = 20;

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
    private MenuItem mSignOutMenuItem;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initGoogleApiClient();
        initViews();
    }

    private void initViews(){
        setContentView(R.layout.activity_contact_list);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        mSignInButton.setSize(SignInButton.SIZE_STANDARD);

    }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SIGN_IN_REQUEST_CODE){
            handleSignInResult(Auth.GoogleSignInApi.getSignInResultFromIntent(data));
        }
    }

    private void handleSignInResult(GoogleSignInResult result){
        if (result.isSuccess()){
            GoogleSignInAccount account = result.getSignInAccount();
            showSignInLayout(false);
            Toast.makeText(this, "Welcome " + account.getDisplayName(),
                    Toast.LENGTH_SHORT).show();
        } else {
            showSignInLayout(true);
            Toast.makeText(this, result.getStatus().getStatusMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void showSignInLayout(boolean show){
        if (show){
            mSignInLayout.setVisibility(View.VISIBLE);
            mFabAddContact.setVisibility(View.GONE);
            if (mSignOutMenuItem != null) mSignOutMenuItem.setVisible(false);

        } else {
            mSignInLayout.setVisibility(View.GONE);
            mFabAddContact.setVisibility(View.VISIBLE);
            if (mSignOutMenuItem != null) mSignOutMenuItem.setVisible(true);
        }
    }

    private void silentSignIn(){
        OptionalPendingResult<GoogleSignInResult> optionalPendingResult
                = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (optionalPendingResult.isDone()){
            handleSignInResult(optionalPendingResult.get());
        } else {
            showProgressDialog(true);
            optionalPendingResult.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    showProgressDialog(false);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_contact_list, menu);
        mSignOutMenuItem = menu.findItem(R.id.sign_out);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (mSignInLayout.getVisibility() == View.VISIBLE){
            mSignOutMenuItem.setVisible(false);
        } else mSignOutMenuItem.setVisible(true);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.sign_out) {
            signOut();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showProgressDialog(boolean show){
        if (show){
            if (mProgressDialog == null){
                mProgressDialog = new ProgressDialog(this);
                mProgressDialog.setMessage(getString(R.string.sign_in_dialog_message));
                mProgressDialog.setIndeterminate(true);
            }
            mProgressDialog.show();
        } else if (mProgressDialog != null && mProgressDialog.isShowing()){
            mProgressDialog.hide();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, connectionResult.getErrorMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        silentSignIn();
    }
}

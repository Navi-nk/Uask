package com.codelords.project.uask;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;

import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChallengeContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.NewPasswordContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GetDetailsHandler;
import com.codelords.project.uask.helper.CognitoHelper;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cz.msebera.android.httpclient.Header;



public class LoginActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener{
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;
    //FB Login parameters
    private LoginButton loginButton;
    private CallbackManager callbackManager;

    // User Details
    private String username;
    private String password;

    private AlertDialog userDialog;

    private GoogleApiClient mGoogleApiClient;

    private ProgressDialog progressDialog;

    private static final int RC_SIGN_IN = 9001;

    // Session Manager Class
    SessionManager _session;

    //get all the layout elements by their id
    @InjectView(R.id.input_name) EditText _userName;
    @InjectView(R.id.input_password) EditText _passwordText;
    @InjectView(R.id.btn_login) Button _loginButton;
    @InjectView(R.id.link_signup) TextView _signupLink;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        callbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.activity_login);
        ButterKnife.inject(this);
        loginButton = (LoginButton)findViewById(R.id.login_button);
        _session = new SessionManager(getApplicationContext());
        // Set the dimensions of the sign-in button.
        SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_WIDE);
        TextView textView = (TextView) signInButton.getChildAt(0);
        textView.setText("Continue with Google");

        //This method to implement the sign up link functionality.
        implementSignUpLink();
        //FB sign in
        implementFBSignIn();

        implementGoogleSignIn();

        //Setup AWS Cognito attributes
        CognitoHelper.init(getApplicationContext());

        //Listener for Log in button
        _loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Main logic for login
                performLogin();
            }
        });

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

        findCurrent();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if(progressDialog == null){
            progressDialog = new ProgressDialog(LoginActivity.this,
                    R.style.AppTheme_Dark_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Signing In...");
            progressDialog.show();
        }
    }

    private void implementGoogleSignIn() {
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

    }

    private void implementFBSignIn(){
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("fb",loginResult.getAccessToken().getToken()+' '+loginResult.getAccessToken().getUserId());
                Map<String, String> logins = new HashMap<String, String>();
                logins.put("graph.facebook.com", loginResult.getAccessToken().getToken());
                CognitoHelper.setLogins(logins);

                //_session.createLoginSession(uname,obj.getJSONArray("res").getJSONObject(0).getString("_faculty") ); have to implement this

                Intent intent = new Intent(getApplicationContext(), About.class);
                startActivity(intent);
            }

            @Override
            public void onCancel() {
                Log.d("fb","1");
            }

            @Override
            public void onError(FacebookException e) {
                Log.d("fb","2");
            }
        });
      /*  //Check if user is currently logged in
        if (AccessToken.getCurrentAccessToken() != null && com.facebook.Profile.getCurrentProfile() != null){
            //Logged in so show the login button
            loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("fb","3");
         //           GraphRequest delPermRequest = new GraphRequest(AccessToken.getCurrentAccessToken(),
           //                 "/" + AccessToken.getCurrentAccessToken().getUserId() + "/permissions/", null, HttpMethod.DELETE, new GraphRequest.Callback() {
             //           @Override
               //         public void onCompleted(GraphResponse graphResponse) {
                //            if (graphResponse != null){
                 //               FacebookRequestError error = graphResponse.getError();
                  //          }
                   //     }
                   // });
                    //delPermRequest.executeAsync();
                    LoginManager.getInstance().logOut();

                }
            });
        }*/
    }

    private void implementSignUpLink()
    {
        SpannableString signupText = new SpannableString("Don't Have an Account? Create one");

        ClickableSpan myClickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View v) {
                Log.d("LoginActivity", "clickable Span");
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
            }
        };
        signupText.setSpan(myClickableSpan, 23, 33, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        signupText.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.primaryOrange)), 23, 33, 0);
        _signupLink.setText(signupText);
        _signupLink.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void performLogin()
    {
        Log.d("LoginActivity", "login");
        _loginButton.setEnabled(false);

        username = _userName.getText().toString();
        password = _passwordText.getText().toString();

        if(!validate(username,password))
        {
            _loginButton.setEnabled(true);
            return;
        }

        progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        CognitoHelper.getPool().getUser(username).getSessionInBackground(authenticationHandler);


       /* RequestParams params = new RequestParams();
        params.put("username", uname);
        params.put("password", password);

        AsyncHttpClient client = new AsyncHttpClient();
        //client.get("http://192.168.0.114:8080/UaskServiceProvider/login/dologin", params, new AsyncHttpResponseHandler() {
//        client.get("http://172.27.242.165:8080/UaskServiceProvider/login/dologin", params, new AsyncHttpResponseHandler() {
        client.get("http://731af621.ngrok.io/UaskServiceProvider/login/dologin", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    progressDialog.dismiss();
                    // JSON Object
                    String s = new String(responseBody,"UTF-8");

                    JSONObject obj = new JSONObject(s);
                    // When the JSON response has status boolean value assigned with true
                    if (obj.getBoolean("status")) {
                        //Store user info in session so that user is only required to login if he/she logs out of the app
                        _session.createLoginSession(uname,obj.getJSONArray("res").getJSONObject(0).getString("_faculty") );

                        // Navigate to Home screen
                        Intent intent = new Intent(getApplicationContext(), MainCanvas.class);
                        startActivity(intent);
                        _loginButton.setEnabled(true);
                        finish();
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "User name or Password are wrong. Please try again.", Toast.LENGTH_LONG).show();
                        _loginButton.setEnabled(true);
                    }
                    // Else display error message

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    Toast.makeText(getApplicationContext(), "Error Occured [Server's JSON response might be invalid]!", Toast.LENGTH_LONG).show();
                    e.printStackTrace();

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                progressDialog.dismiss();
                _loginButton.setEnabled(true);
                // When Http response code is '404'
                if (statusCode == 404) {
                    Toast.makeText(getApplicationContext(), "Requested resource not found", Toast.LENGTH_LONG).show();
                }
                // When Http response code is '500'
                else if (statusCode == 500) {
                    Toast.makeText(getApplicationContext(), "Something went wrong. Please try later.", Toast.LENGTH_LONG).show();
                }
                // When Http response code other than 404, 500
                else {
                    Toast.makeText(getApplicationContext(), "Connection Error. Make sure device is connected to Internet.", Toast.LENGTH_LONG).show();
                }
            }

        });*/

    }
    private void findCurrent() {
        CognitoUser user = CognitoHelper.getPool().getCurrentUser();
        username = user.getUserId();
        if(username != null) {
            CognitoHelper.setUser(username);
            _userName.setText(user.getUserId());
            user.getSessionInBackground(authenticationHandler);
        }
    }

    private boolean validate(final String email,final String password) {
        boolean valid = true;

        //if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
        if(email.isEmpty()){
            _userName.setError("enter a valid email address");
            valid = false;
        } else {
            _userName.setError(null);
        }

        if (password.isEmpty() || password.length() < 3 || password.length() > 10) {
            _passwordText.setError("between 3 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }

    public void onLoginSuccess() {
        _loginButton.setEnabled(true);
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("fb",Integer.toString(requestCode));

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }else if(requestCode == REQUEST_SIGNUP){
            String name = data.getStringExtra("name");
            String userPasswd = data.getStringExtra("password");

            progressDialog = new ProgressDialog(LoginActivity.this,
                    R.style.AppTheme_Dark_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Signing In...");
            progressDialog.show();
            if (!name.isEmpty() && !userPasswd.isEmpty()) {
                // We have the user details, so sign in!
                username = name;
                password = userPasswd;
                CognitoHelper.getPool().getUser(username).getSessionInBackground(authenticationHandler);
            }else{
                Toast.makeText(getApplicationContext(), "Something went wrong. Please try later.", Toast.LENGTH_LONG).show();
            }
        }
        else{
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }


    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();

            //Need to check this
            /*GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
            AccountManager am = AccountManager.get(this);
            Account[] accounts = am.getAccountsByType(GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE);
            String token = GoogleAuthUtil.getToken(getApplicationContext(), accounts[0].name,
                    "audience:server:client_id:YOUR_GOOGLE_CLIENT_ID");*/
            Map<String, String> logins = new HashMap<String, String>();
            logins.put("accounts.google.com", acct.getIdToken());
            CognitoHelper.setLogins(logins);
            //this will be changed anyway
            Intent intent = new Intent(getApplicationContext(), About.class);
            startActivity(intent);

        } else {
            // Signed out, show unauthenticated UI.

        }
    }
    private void showDialogMessage(String title, String body) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title).setMessage(body).setNeutralButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    userDialog.dismiss();
                } catch (Exception e) {
                    //
                }
            }
        });
        userDialog = builder.create();
        userDialog.show();
    }

    private void getUserAuthentication(AuthenticationContinuation continuation, String username) {
        if(username != null) {
            this.username = username;
            CognitoHelper.setUser(username);
        }
        if(this.password == null) {
            return;
        }
        AuthenticationDetails authenticationDetails = new AuthenticationDetails(this.username, password, null);
        continuation.setAuthenticationDetails(authenticationDetails);
        continuation.continueTask();
    }

    AuthenticationHandler authenticationHandler = new AuthenticationHandler() {
        @Override
        public void onSuccess(CognitoUserSession cognitoUserSession, CognitoDevice device) {
            Log.e(TAG, "Auth Success");
            _loginButton.setEnabled(true);
            CognitoHelper.setCurrSession(cognitoUserSession);
            CognitoHelper.newDevice(device);
            if(progressDialog != null)
                progressDialog.dismiss();
           // _session.createLoginSession(uname,obj.getJSONArray("res").getJSONObject(0).getString("_faculty") ); have to implement this

            String idToken = cognitoUserSession.getIdToken().getJWTToken();

            Map<String, String> logins = new HashMap<String, String>();
            logins.put(CognitoHelper.getUserPoolUrl(), idToken);
            CognitoHelper.setLogins(logins);

            CognitoHelper.getPool().getUser(username).getDetailsInBackground(getUserDetailsHandler);
        }

        @Override
        public void getAuthenticationDetails(AuthenticationContinuation authenticationContinuation, String username) {
            if(progressDialog != null)
                progressDialog.dismiss();
            Locale.setDefault(Locale.US);
            getUserAuthentication(authenticationContinuation, username);
        }

        @Override
        public void getMFACode(MultiFactorAuthenticationContinuation multiFactorAuthenticationContinuation) {
            progressDialog.dismiss();
            //Not required
        }

        @Override
        public void authenticationChallenge(ChallengeContinuation continuation) {
            progressDialog.dismiss();
            //Not required
        }

        @Override
        public void onFailure(Exception e) {
            progressDialog.dismiss();
            _userName.setError("Sign In Failed");

            _passwordText.setError("Sign In Failed");

            showDialogMessage("Sign-in failed", CognitoHelper.formatException(e));
        }

    };

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    // Implement callback handler for getting details
    GetDetailsHandler getUserDetailsHandler = new GetDetailsHandler() {
        @Override
        public void onSuccess(CognitoUserDetails cognitoUserDetails) {
            // The user detail are in cognitoUserDetails

            CognitoHelper.setUserDetails(cognitoUserDetails);


            _session.createLoginSession(cognitoUserDetails.getAttributes().getAttributes().get(CognitoHelper.getSignUpFields().get("preferred username")), cognitoUserDetails.getAttributes().getAttributes().get(CognitoHelper.getSignUpFields().get("faculty")));

            // Navigate to Home screen
            Intent intent = new Intent(getApplicationContext(), MainCanvas.class);
            startActivity(intent);
            finish();

//            Intent intent = new Intent(getApplicationContext(), About.class);
//            startActivity(intent);
//            finish();
        }

        @Override
        public void onFailure(Exception exception) {
            // Fetch user details failed, check exception for the cause
        }
    };

    //This need to be incorporated for logout..
    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // [START_EXCLUDE]

                        // [END_EXCLUDE]
                    }
                });
    }


}
     
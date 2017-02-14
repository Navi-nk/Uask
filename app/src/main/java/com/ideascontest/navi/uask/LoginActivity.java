/*
Code Written by Navi
Implements Log in functionality of uAsk
7-02-17
* */
package com.ideascontest.navi.uask;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cz.msebera.android.httpclient.Header;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;

    // Session Manager Class
    SessionManager _session;

    //get all the layout elements by their id
    @InjectView(R.id.input_email) EditText _emailText;
    @InjectView(R.id.input_password) EditText _passwordText;
    @InjectView(R.id.btn_login) Button _loginButton;
    @InjectView(R.id.link_signup) TextView _signupLink;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.inject(this);

        _session = new SessionManager(getApplicationContext());
        //This method to implement the sign up link functionality.
        implementSignUpLink();

        //Listener for Log in button
        _loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Main logic for login
                    performLogin();
            }
        });
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

        final String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if(!validate(email,password))
        {
            _loginButton.setEnabled(true);
            return;
        }

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        RequestParams params = new RequestParams();
        params.put("username", email);
        params.put("password", password);

        AsyncHttpClient client = new AsyncHttpClient();
        client.get("http://192.168.0.114:8080/UaskServiceProvider/login/dologin", params, new AsyncHttpResponseHandler() {
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
                        _session.createLoginSession(email);

                        // Navigate to Home screen
                        Intent intent = new Intent(getApplicationContext(), MainCanvas.class);
                        startActivity(intent);
                        _loginButton.setEnabled(true);
                        finish();
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "Email or Password are wrong. Please try again.", Toast.LENGTH_LONG).show();
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

        });

    }

    private boolean validate(String email,String password) {
        boolean valid = true;

        //if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
        if(email.isEmpty()){
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
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
}
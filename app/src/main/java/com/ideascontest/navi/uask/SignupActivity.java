package com.ideascontest.navi.uask;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cz.msebera.android.httpclient.Header;

public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";

    @InjectView(R.id.input_name) EditText _nameText;
    @InjectView(R.id.input_email) EditText _emailText;
    @InjectView(R.id.input_password) EditText _passwordText;
    @InjectView(R.id.input_password_confirm) EditText _pwdCofirmText;
    @InjectView(R.id.faculty_spinner) EditText
    @InjectView(R.id.btn_signup) Button _signupButton;
    @InjectView(R.id.link_login) TextView _loginLink;

   static String[] SPINNERLIST = {"Arts & Social Sciences",
            "Business",
            "Computing",
            "Continuing and Lifelong Education",
            "Dentistry",
            "Design & Environment",
            "Development of Teaching & Learning",
            "Duke-NUS",
            "Engineering",
            "English Language Communication",
            "Integrative Sciences & Engineering",
            "Law",
            "Medicine",
            "Music",
            "Public Health",
            "Public Policy",
            "Science",
            "Systems Science",
            "USP",
            "Yale-NUS"
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.inject(this);

        implementLoginUpLink();

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, SPINNERLIST);
        MaterialBetterSpinner materialDesignSpinner = (MaterialBetterSpinner)
                findViewById(R.id.android_material_design_spinner);
        materialDesignSpinner.setAdapter(arrayAdapter);

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void implementLoginUpLink()
    {
        SpannableString signupText = new SpannableString("Don't Have an Account? Create one");
        ClickableSpan myClickableSpan = new ClickableSpan()
        {
            @Override
            public void onClick(View v) {
                Log.d("LoginActivity","clickable Span");
                finish();
            }
        };
        signupText.setSpan(myClickableSpan, 18,23, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        signupText.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.primaryOrange)),18,23,0);
        _loginLink.setText(signupText);
        _loginLink.setMovementMethod(LinkMovementMethod.getInstance());
    }
}

    private void performSignup()
    {
        Log.d("SignupActivity", "sign");
        _signupButton.setEnabled(false);

        String name = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if(!validate(name,email,password))
        {
            _signupButton.setEnabled(true);
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
                        //Toast.makeText(getApplicationContext(), "You are successfully logged in!", Toast.LENGTH_LONG).show();
                        // Navigate to Home screen
                        Intent intent = new Intent(getApplicationContext(), MainCanvas.class);
                        startActivityForResult(intent, REQUEST_SIGNUP);
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
                    Toast.makeText(getApplicationContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                }
                // When Http response code other than 404, 500
                else {
                    Toast.makeText(getApplicationContext(), "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet or remote server is not up and running]", Toast.LENGTH_LONG).show();
                }
            }

        });
    }

    private boolean validate(String name, String email,String password) {
        boolean valid = true;

        if(name.isEmpty()) {
            _nameText.setError("Enter user profile name");
            valid = false;
        }
        else{
            _nameText.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
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

package com.ideascontest.navi.uask;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cz.msebera.android.httpclient.Header;

public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";
    String _facultyText;

    // Session Manager Class
    SessionManager _session;

    @InjectView(R.id.input_name)
    EditText _nameText;
    @InjectView(R.id.input_email)
    EditText _emailText;
    @InjectView(R.id.input_password)
    EditText _passwordText;
    @InjectView(R.id.input_password_confirm)
    EditText _pwdConfirmText;
    @InjectView(R.id.faculty_spinner)
    MaterialBetterSpinner _facultyDropdown;
    @InjectView(R.id.btn_signup)
    Button _signupButton;
    @InjectView(R.id.link_login)
    TextView _loginLink;

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
            "Systems Science - ISS",
            "USP",
            "Yale-NUS"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.inject(this);

        _session = new SessionManager(getApplicationContext());

        implementLogInLink();

        populateFacultySpinner();

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //logic for signup
                performSignup();
            }
        });

    }

    private void implementLogInLink() {
        SpannableString signupText = new SpannableString("Already a member? Login");
        ClickableSpan myClickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View v) {
                Log.d("SignupActivity", "clickable Span");
                finish();
            }
        };
        signupText.setSpan(myClickableSpan, 18, 23, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        signupText.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.primaryOrange)), 18, 23, 0);
        _loginLink.setText(signupText);
        _loginLink.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void populateFacultySpinner() {
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, SPINNERLIST);
        final MaterialBetterSpinner materialDesignSpinner = (MaterialBetterSpinner)
                findViewById(R.id.faculty_spinner);
        materialDesignSpinner.setAdapter(arrayAdapter);
        materialDesignSpinner.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    //Not required now
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    //Not required
            }

            @Override
            public void afterTextChanged(Editable editable) {
                _facultyText = materialDesignSpinner.getText().toString();
            }
        });
    }

    private void performSignup() {
        Log.d("SignupActivity", "signup");
        _signupButton.setEnabled(false);

        final String name = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();
        String confirmPassword = _pwdConfirmText.getText().toString();

        if (!validate(name, email, password, confirmPassword, _facultyText)) {
            _signupButton.setEnabled(true);
            return;
        }

        final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Processing...");
        progressDialog.show();

        RequestParams params = new RequestParams();
        params.put("username", name);
        params.put("email", email);
        params.put("password", password);
        params.put("faculty", _facultyText);

        AsyncHttpClient client = new AsyncHttpClient();
        //client.get("http://192.168.0.114:8080/UaskServiceProvider/signup/doregister", params, new AsyncHttpResponseHandler() {
        client.get("http://1a60a9a0.ngrok.io/UaskServiceProvider/signup/doregister", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    progressDialog.dismiss();
                    // JSON Object
                    String s = new String(responseBody, "UTF-8");

                    JSONObject obj = new JSONObject(s);
                    // When the JSON response has status boolean value assigned with true
                    if (obj.getBoolean("status")) {

                        _session.createLoginSession(name,"ISS");
                        // Navigate to Home screen
                        Intent intent = new Intent(getApplicationContext(), MainCanvas.class);
                        startActivity(intent);
                        _signupButton.setEnabled(true);
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "User already registered. Please continue to login.", Toast.LENGTH_LONG).show();
                        _signupButton.setEnabled(true);
                    }
                    // Else display error message

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    Toast.makeText(getApplicationContext(), "Something went wrong. Please try later.", Toast.LENGTH_LONG).show();
                    e.printStackTrace();

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                progressDialog.dismiss();
                _signupButton.setEnabled(true);
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

    private boolean validate(String name, String email, String password, String confirmpassword, String faculty) {
        boolean valid = true;

        if (name.isEmpty()) {
            _nameText.setError("Please enter user profile name");
            valid = false;
        } else {
            _nameText.setError(null);
        }
      //  ^[A-Za-z].*?@gmail\\.com$
        Pattern p_1 = Pattern.compile("^[A-Za-z0-9+_-]+@nus\\.edu\\.sg$");
        Pattern p_2 = Pattern.compile("^[A-Za-z0-9+_-]+@u\\.nus\\.edu$");
        Matcher match_1 = p_1.matcher(email);
        Matcher match_2 = p_2.matcher(email);
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() || (!match_1.matches() && !match_2.matches())) {
            _emailText.setError("Please enter a valid email address");
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

        if (confirmpassword.isEmpty() || !confirmpassword.equals(password)) {
            _pwdConfirmText.setError("Please enter password same as entered above");
            valid = false;
        } else {
            _pwdConfirmText.setError(null);
        }

        if ((faculty == null) || faculty.isEmpty()) {
            _facultyDropdown.setError("Please select your faculty");
            valid = false;
        } else {
            _facultyDropdown.setError(null);
        }

        return valid;
    }
}
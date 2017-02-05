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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";

    @InjectView(R.id.input_name) EditText _nameText;
    @InjectView(R.id.input_email) EditText _emailText;
    @InjectView(R.id.input_password) EditText _passwordText;
    @InjectView(R.id.btn_signup) Button _signupButton;
    @InjectView(R.id.link_login) TextView _loginLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.inject(this);

        SpannableString signupText = new SpannableString("Already a member? Login");
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

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}

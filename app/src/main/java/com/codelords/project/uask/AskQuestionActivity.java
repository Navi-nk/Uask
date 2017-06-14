package com.codelords.project.uask;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.codelords.project.uask.helper.ApiGatewayHelper;
import com.codelords.uask.apiclientsdk.UAskClient;
import com.codelords.uask.apiclientsdk.model.QueryPostStatus;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cz.msebera.android.httpclient.Header;

public class AskQuestionActivity extends AppCompatActivity {
    private static final String TAG = "AskQuestion";
    String _categoryText;
    String _questionText;
    private static UAskClient apiClient;

    // Session Manager Class
    SessionManager _session;
    ProgressDialog progressDialog;

    @InjectView(R.id.askquestion)
    EditText _quesText;
    @InjectView(R.id.cat_spinner)
    MaterialBetterSpinner _categoryDropdown;
    @InjectView(R.id.checkBox)
    CheckBox privateFlag;
    @InjectView(R.id.tb_submit_ques)
    TextView _submitButton;

    static String[] CATLIST = {"Getting Around",
            "Food & Beverages",
            "Faculties/Departments",
            "Sports & Recreation",
            "Residences",
            "General"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask_question);
        ButterKnife.inject(this);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.tb_postques);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Intent i = getIntent();
        int catDropDownPosition = i.getIntExtra("position",-1);
        Log.d("Position in Q",Integer.toString(catDropDownPosition));
        //get session object
        _session = new SessionManager(getApplicationContext());
        //populate category dropdown
        populateCategorySpinner(catDropDownPosition);

        //implement listener for question submit
        _submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //save question
                submitQuestion();
            }
        });
    }

    private void populateCategorySpinner(int dropDownPosition) {
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, CATLIST);
        final MaterialBetterSpinner materialDesignSpinner = (MaterialBetterSpinner)
                findViewById(R.id.cat_spinner);
        materialDesignSpinner.setAdapter(arrayAdapter);

        if(dropDownPosition >= 0 && dropDownPosition < 6) {
            materialDesignSpinner.setText(CATLIST[dropDownPosition]);
            _categoryText = CATLIST[dropDownPosition];
        }

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
                _categoryText = materialDesignSpinner.getText().toString();
            }
        });
    }

    private void submitQuestion() {
        Log.d("AskQuestion", "submitquestion");
        _submitButton.setEnabled(false);

        //get question asked
        _questionText = _quesText.getText().toString();
        String userName = _session.getUserDetails().get("name");

        if (!validate(userName, _questionText, _categoryText)) {
            _submitButton.setEnabled(true);
            return;
        }

        progressDialog = new ProgressDialog(AskQuestionActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Submitting...");
        progressDialog.show();

        String flag = (privateFlag.isChecked())? "true" :"false" ;
        Log.d("flag",flag);

        String[] params = {_categoryText, userName, _questionText, flag};
        new postQuestionTask().execute(params);
        /*RequestParams params = new RequestParams();
        params.put("question", _questionText);
        params.put("category", _categoryText);
        params.put("flag", flag);
        params.put("userId",userName);*/

      /*  AsyncHttpClient client = new AsyncHttpClient();
       // client.get("http://192.168.0.114:8080/UaskServiceProvider/qfeed/askques", params, new AsyncHttpResponseHandler() {
        client.get("http://731af621.ngrok.io/UaskServiceProvider/qfeed/askques", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    progressDialog.dismiss();
                    // JSON Object
                    String s = new String(responseBody, "UTF-8");

                    JSONObject obj = new JSONObject(s);
                    // When the JSON response has status boolean value assigned with true
                    if (obj.getBoolean("status")) {
                        Intent i = new Intent();
                        setResult(RESULT_OK, i);
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "Something went wrong. Please try later.", Toast.LENGTH_LONG).show();
                        _submitButton.setEnabled(true);
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
                _submitButton.setEnabled(true);
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

    public class postQuestionTask extends AsyncTask<String[], Void, QueryPostStatus> {

        // COMPLETED (26) Override onPreExecute to set the loading indicator to visible
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected QueryPostStatus doInBackground(String[]... params) {
            //URL searchUrl = params[0];
            QueryPostStatus QuestionAnswerSearchResults = null;
            apiClient = ApiGatewayHelper.getApiClientFactory().build(UAskClient.class);
            try {
                QuestionAnswerSearchResults = apiClient.postquestionGet(params[0][0],params[0][1],params[0][2],params[0][3]);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return QuestionAnswerSearchResults;
        }

        @Override
        protected void onPostExecute(QueryPostStatus postQuestionStatus) {
            // Log.d("Check result",QuestionAnswerSearchResults);
            // COMPLETED (27) As soon as the loading is complete, hide the loading indicator
            if (postQuestionStatus != null && !postQuestionStatus.equals("")) {
                progressDialog.dismiss();
                if (Boolean.valueOf(postQuestionStatus.getStatus())) {
                    Intent i = new Intent();
                    setResult(RESULT_OK, i);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Something went wrong. Please try later.", Toast.LENGTH_LONG).show();
                    _submitButton.setEnabled(true);
                }
            }

        }
    }

    private boolean validate(String name, String question, String category) {
        boolean valid = true;

        if (name.isEmpty()) {
            valid = false;
        }

        if (question.isEmpty()) {
            _quesText.setError("Please enter your question");
            valid = false;
        } else {
            _quesText.setError(null);
        }

        if ((category == null) || category.isEmpty()) {
            _categoryDropdown.setError("Please select a category");
            valid = false;
        } else {
            _categoryDropdown.setError(null);
        }

        return valid;
    }

}

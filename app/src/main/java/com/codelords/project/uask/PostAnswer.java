package com.codelords.project.uask;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.codelords.project.uask.helper.ApiGatewayHelper;
import com.codelords.uask.apiclientsdk.UAskClient;
import com.codelords.uask.apiclientsdk.model.QueryPostStatus;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

public class PostAnswer extends AppCompatActivity {

    private TextView questionText,authorText,timeStampText,saveActionText;
    private EditText answer;
   // private URL SearchUrl;
    String parentIdentifier;
    private static UAskClient apiClient;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_answer);
        Intent intent = getIntent();
        String id = intent.getStringExtra("id");
        String question = intent.getStringExtra("question");
        String author = intent.getStringExtra("author");
        String timeStamp = intent.getStringExtra("timestamp");
        parentIdentifier = intent.getStringExtra("numanswers");
        questionText = (TextView)findViewById(R.id.textQuestion);
        questionText.setText(question);
        questionText.setTag(id);
        authorText = (TextView)findViewById(R.id.textAuthor);
        authorText.setText(author);
        timeStampText = (TextView)findViewById(R.id.textTimeStamp);
        timeStampText.setText(timeStamp);
        saveActionText = (TextView) findViewById(R.id.tb_save);
        answer = (EditText)findViewById(R.id.answer);

        SessionManager _session;
        _session = new SessionManager(getApplicationContext());
        // Make API call and display question

        // get user data from session
        HashMap<String, String> user = _session.getUserDetails();

        Toolbar mToolbar = (Toolbar) findViewById(R.id.tb_postans);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        // name
        final String user_id = user.get(SessionManager.KEY_NAME);
        saveActionText.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String question_id = questionText.getTag().toString();
                String answerText = answer.getText().toString();
                if(answerText.isEmpty())
                    answer.setError("Answer cannot be empty");
                else {
                    answer.setError(null);
                    progressDialog = new ProgressDialog(PostAnswer.this,
                            R.style.AppTheme_Dark_Dialog);
                    progressDialog.setIndeterminate(true);
                    progressDialog.setMessage("Submitting...");
                    progressDialog.show();
                    //SearchUrl = NetworkUtils.buildUrlToPostAnswer(NetworkUtils.POST_ANSWER, NetworkUtils.PARAM_USER_ID, user_id, NetworkUtils.PARAM_QUESTION_ID, question_id, NetworkUtils.ANSWER, answerText);
                    new PostAnswerTask().execute(user_id,question_id,answerText);
                }
            }
        });

    }

    public class PostAnswerTask extends AsyncTask<String, Void, QueryPostStatus> {

        // COMPLETED (26) Override onPreExecute to set the loading indicator to visible
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected QueryPostStatus doInBackground(String... strings) {
            QueryPostStatus answerPostStatus = null;
            apiClient = ApiGatewayHelper.getApiClientFactory().build(UAskClient.class);
            try {
                answerPostStatus = apiClient.answerquestionGet(strings[0] ,strings[1],strings[2]);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return answerPostStatus;
        }

        @Override
        protected void onPostExecute(QueryPostStatus answerPostStatus) {
           // Log.d("Check result",QuestionAnswerSearchResults);
            try {

                // JSONObject jsonObj = new JSONObject(QuestionAnswerSearchResults);
                //String success = jsonObj.getString("status").toString();
                if (answerPostStatus != null && !answerPostStatus.equals("")) {
                    if (Boolean.valueOf(answerPostStatus.getStatus())) {
                        progressDialog.dismiss();
                        if (parentIdentifier == null || parentIdentifier.isEmpty()) {
                            Log.d("PostAnswer", "from answerlist");
                            Intent i = new Intent();
                            setResult(RESULT_OK, i);
                            finish();
                        } else {
                            Log.d("PostAnswer", "from questionlist");
                            Intent i = new Intent(getApplicationContext(), MainCanvas.class);
                            startActivity(i);
                            finish();
                        }
                    }
                }
                } catch(Exception e) {
                Toast.makeText(PostAnswer.this, e.toString(), Toast.LENGTH_LONG).show();
            }
                // COMPLETED (27) As soon as the loading is complete, hide the loading indicator
        }

    }
}

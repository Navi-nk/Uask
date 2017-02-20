package com.ideascontest.navi.uask;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PostAnswer extends AppCompatActivity {

    private TextView questionText,authorText,timeStampText,saveActionText;
    private EditText answer;
    private Button submitanswer;
    private URL SearchUrl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_answer);
        Intent intent = getIntent();
        String id = intent.getStringExtra("id");
        String question = intent.getStringExtra("question");
        String author = intent.getStringExtra("author");
        String timeStamp = intent.getStringExtra("timestamp");
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
                    SearchUrl = NetworkUtils.buildUrlToPostAnswer(NetworkUtils.POST_ANSWER, NetworkUtils.PARAM_USER_ID, user_id, NetworkUtils.PARAM_QUESTION_ID, question_id, NetworkUtils.ANSWER, answerText);
                    new PostAnswerTask().execute(SearchUrl);
                }
            }
        });

    }

    public class PostAnswerTask extends AsyncTask<URL, Void, String> {

        // COMPLETED (26) Override onPreExecute to set the loading indicator to visible
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(URL... params) {
            URL searchUrl = params[0];
            String QuestionAnswerSearchResults = null;
            try {
                QuestionAnswerSearchResults = NetworkUtils.getResponseFromHttpUrl(searchUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return QuestionAnswerSearchResults;
        }

        @Override
        protected void onPostExecute(String QuestionAnswerSearchResults) {
            Log.d("Check result",QuestionAnswerSearchResults);
            try {

                JSONObject jsonObj = new JSONObject(QuestionAnswerSearchResults);
                String success = jsonObj.getString("status").toString();
                if(success.equalsIgnoreCase("true")){
                    Intent i = new Intent(getApplicationContext(),MainCanvas.class);
                    startActivity(i);
                    finish();
                }

            } catch (JSONException e) {
                Toast.makeText(PostAnswer.this, e.toString(), Toast.LENGTH_LONG).show();
            }
            // COMPLETED (27) As soon as the loading is complete, hide the loading indicator

        }

    }


}

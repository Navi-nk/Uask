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
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class AnswerActivity extends AppCompatActivity {

    private RecyclerView mainAnswerList;
    private MainAnswerAdapter mAnswerAdapter;
    private TextView questionText,authorText,timeStampText,submitAns;
    String id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer);
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        final String question = intent.getStringExtra("question");
        final String author = intent.getStringExtra("author");
        final String timeStamp = intent.getStringExtra("timestamp");
        final String noOfAnswers = intent.getStringExtra("numanswers");
        questionText = (TextView)findViewById(R.id.textQuestion);
        questionText.setText(question);
        authorText = (TextView)findViewById(R.id.textAuthor);
        authorText.setText(author);
        timeStampText = (TextView)findViewById(R.id.textTimeStamp);
        timeStampText.setText(timeStamp);

        submitAns = (TextView)findViewById(R.id.tb_answer);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.tb_viewqa);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        if(!noOfAnswers.equalsIgnoreCase("0 Answers")) {
            URL SearchUrl = NetworkUtils.buildUrl(NetworkUtils.GET_ALL_ANSWERS, NetworkUtils.PARAM_QUESTION, id);
            new AnswerQueryTask().execute(SearchUrl);
        }
        else
        {
            List<Answer> noData=new ArrayList<>();

                    Answer answerData = new Answer();
                    answerData.answerText="No Answers Available";
                    answerData.author="";
                    answerData.timeStamp="";
                    noData.add(answerData);

                mainAnswerList = (RecyclerView) findViewById(R.id.question_top_answer_recylerview);
                LinearLayoutManager layoutManager = new LinearLayoutManager(AnswerActivity.this);
                mainAnswerList.setLayoutManager(layoutManager);
                mainAnswerList.setHasFixedSize(true);
                mAnswerAdapter = new MainAnswerAdapter(noData);
                mainAnswerList.setAdapter(mAnswerAdapter);
        }

        submitAns.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PostAnswer.class);
                intent.putExtra("id",id);
                intent.putExtra("question",question);
                intent.putExtra("author",author);
                intent.putExtra("timestamp",timeStamp);
                startActivityForResult(intent,0);
                //finish();
            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("here ",Integer.toString(requestCode)+" "+Integer.toString(resultCode));
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                URL SearchUrl = NetworkUtils.buildUrl(NetworkUtils.GET_ALL_ANSWERS, NetworkUtils.PARAM_QUESTION, id);
                new AnswerQueryTask().execute(SearchUrl);
            }
        }
    }

    public class AnswerQueryTask extends AsyncTask<URL, Void, String> {

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
            // COMPLETED (27) As soon as the loading is complete, hide the loading indicator
            if (QuestionAnswerSearchResults != null && !QuestionAnswerSearchResults.equals("")) {
                // COMPLETED (17) Call showJsonDataView if we have valid, non-null results
                //this method will be running on UI thread

                List<Answer> data=new ArrayList<>();

                try {

                    JSONArray jArray = new JSONArray(QuestionAnswerSearchResults);

                    // Extract data from json and store into ArrayList as class objects
                    for(int i=0;i<jArray.length();i++){
                        JSONObject json_data = jArray.getJSONObject(i);
                        Answer answerData = new Answer();
                        answerData.answerText= json_data.getString("_Text");
                        answerData.author=json_data.getString("_Answered_UserID");
                        answerData.timeStamp=json_data.getString("_Datetime");
                        data.add(answerData);
                    }


                    mainAnswerList = (RecyclerView) findViewById(R.id.question_top_answer_recylerview);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(AnswerActivity.this);
                    mainAnswerList.setLayoutManager(layoutManager);
                    mainAnswerList.setHasFixedSize(true);
                    mAnswerAdapter = new MainAnswerAdapter(data);
                    mainAnswerList.setAdapter(mAnswerAdapter);

                    // Setup and Handover data to recyclerview

                } catch (JSONException e) {
                    Toast.makeText(AnswerActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                }

            }
        }

    }
}

package com.ideascontest.navi.uask;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
    private TextView questionText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer);
        Intent intent = getIntent();
        String id = intent.getStringExtra("id");
        String question = intent.getStringExtra("question");
        questionText = (TextView)findViewById(R.id.textQuestion);
        questionText.setText(question);


        URL SearchUrl = NetworkUtils.buildUrl(NetworkUtils.GET_ALL_ANSWERS,NetworkUtils.PARAM_QUESTION,id);
        new AnswerQueryTask().execute(SearchUrl);
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

package com.codelords.project.uask;

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

import com.codelords.project.uask.helper.ApiGatewayHelper;
import com.codelords.project.uask.helper.CognitoHelper;
import com.codelords.uask.apiclientsdk.UAskClient;
import com.codelords.uask.apiclientsdk.model.AnswerFeedModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import com.amazonaws.mobileconnectors.amazonmobileanalytics.*;

public class AnswerActivity extends AppCompatActivity {

    private RecyclerView mainAnswerList;
    private MainAnswerAdapter mAnswerAdapter;
    private TextView questionText,authorText,timeStampText,submitAns,categoryText;
    private static UAskClient apiClient;
    private static MobileAnalyticsManager analytics;
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
        final String category = intent.getStringExtra("category");
        questionText = (TextView)findViewById(R.id.textQuestion);
        questionText.setText(question);
        authorText = (TextView)findViewById(R.id.textAuthor);
        authorText.setText(author);
        timeStampText = (TextView)findViewById(R.id.textTimeStamp);
        timeStampText.setText(timeStamp);
        categoryText = (TextView)findViewById(R.id.textCategory);
        categoryText.setText(category);

        submitAns = (TextView)findViewById(R.id.tb_answer);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.tb_viewqa);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        try {
            analytics = MobileAnalyticsManager.getOrCreateInstance(
                    this.getApplicationContext(),
                    "8d5e6256080e4acea50fcf9a805f0822", //Amazon Mobile Analytics App ID
                    CognitoHelper.getIdentityPoolId()//Amazon Cognito Identity Pool ID
            );
        } catch(InitializationException ex) {
            Log.e(this.getClass().getName(), "Failed to initialize Amazon Mobile Analytics", ex);
        }

        AnalyticsEvent questionViewEvent = analytics.getEventClient().createEvent("ViewQuestion")
                .withAttribute("questionId", id);
        //Record the  Question View  event
        analytics.getEventClient().recordEvent(questionViewEvent);

        if(!noOfAnswers.equalsIgnoreCase("0 Answers")) {
           // URL SearchUrl = NetworkUtils.buildUrl(NetworkUtils.GET_ALL_ANSWERS, NetworkUtils.PARAM_QUESTION, id);
            new AnswerQueryTask().execute();
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
                //URL SearchUrl = NetworkUtils.buildUrl(NetworkUtils.GET_ALL_ANSWERS, NetworkUtils.PARAM_QUESTION, id);
                new AnswerQueryTask().execute();
            }
        }
    }

    public class AnswerQueryTask extends AsyncTask<Void, Void, AnswerFeedModel> {

        // COMPLETED (26) Override onPreExecute to set the loading indicator to visible
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected AnswerFeedModel doInBackground(Void... params) {
            //URL searchUrl = params[0];
            AnswerFeedModel QuestionAnswerSearchResults = null;
            apiClient = ApiGatewayHelper.getApiClientFactory().build(UAskClient.class);
            try {
                QuestionAnswerSearchResults = apiClient.getanswersGet(id);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return QuestionAnswerSearchResults;
        }

        @Override
        protected void onPostExecute(AnswerFeedModel QuestionAnswerSearchResults) {
 //           Log.d("Check result",QuestionAnswerSearchResults);
            // COMPLETED (27) As soon as the loading is complete, hide the loading indicator
            if (QuestionAnswerSearchResults != null && !QuestionAnswerSearchResults.equals("")) {
                // COMPLETED (17) Call showJsonDataView if we have valid, non-null results
                //this method will be running on UI thread

                List<Answer> data=new ArrayList<>();

                try {

                    //JSONArray jArray = new JSONArray(QuestionAnswerSearchResults);

                    // Extract data from json and store into ArrayList as class objects
                    for(int i=0;i<QuestionAnswerSearchResults.size();i++){
                      //  JSONObject json_data = jArray.getJSONObject(i);
                        Answer answerData = new Answer();
                        answerData.answerText= QuestionAnswerSearchResults.get(i).getText();
                        answerData.author=QuestionAnswerSearchResults.get(i).getAnsweredUserID();
                        answerData.timeStamp=QuestionAnswerSearchResults.get(i).getDatetime();
                        data.add(answerData);
                    }


                    mainAnswerList = (RecyclerView) findViewById(R.id.question_top_answer_recylerview);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(AnswerActivity.this);
                    mainAnswerList.setLayoutManager(layoutManager);
                    mainAnswerList.setHasFixedSize(true);
                    mAnswerAdapter = new MainAnswerAdapter(data);
                    mainAnswerList.setAdapter(mAnswerAdapter);

                    // Setup and Handover data to recyclerview

                } catch (Exception e) {
                    Toast.makeText(AnswerActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                }

            }
        }

    }
}

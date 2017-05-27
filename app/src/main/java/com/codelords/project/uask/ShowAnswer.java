package com.codelords.project.uask;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class ShowAnswer extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_answer);
        Intent i = getIntent();
        String answertext = i.getStringExtra("answertext");
        String author = i.getStringExtra("author");
        String timeStamp = i.getStringExtra("timestamp");
        TextView answer = (TextView)findViewById(R.id.showanswer);
        TextView auth = (TextView)findViewById(R.id.txtAuthor);
        TextView timeStmp = (TextView)findViewById(R.id.txtTimeStamp);
        answer.setText(answertext);
        auth.setText(author);
        timeStmp.setText(timeStamp);
    }
}

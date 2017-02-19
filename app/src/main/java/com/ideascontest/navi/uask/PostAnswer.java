package com.ideascontest.navi.uask;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class PostAnswer extends AppCompatActivity {

    private TextView questionText,authorText,timeStampText;
    private Button submitanswer;
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
        submitanswer = (Button)findViewById(R.id.submitanswer);
        submitanswer.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {


            }
        });



    }

    
}

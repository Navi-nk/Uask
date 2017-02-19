package com.ideascontest.navi.uask;

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
        TextView answer = (TextView)findViewById(R.id.showanswer);
        answer.setText(answertext);
    }
}

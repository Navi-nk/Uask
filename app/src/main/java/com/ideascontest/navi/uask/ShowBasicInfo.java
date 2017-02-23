package com.ideascontest.navi.uask;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Text;

public class ShowBasicInfo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i = getIntent();
        int category = i.getIntExtra("category",-1);
        if(category == 0)
            setContentView(R.layout.question_list_item_one);
        else if(category == 1)
            setContentView(R.layout.question_list_item_two);
        else if(category == 2)
            setContentView(R.layout.question_list_item_five);
        else if(category == 3)
            setContentView(R.layout.question_list_item_three);
        else if(category == 4)
            setContentView(R.layout.question_list_item_four);
        else
            setContentView(R.layout.question_list_item_one);

        TextView th = (TextView) findViewById(R.id.heading);
        th.setVisibility(View.GONE);

    }
}

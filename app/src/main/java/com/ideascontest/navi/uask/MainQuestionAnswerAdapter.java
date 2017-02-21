package com.ideascontest.navi.uask;

/**
 * Created by ethi on 18/02/17.
 */

/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.ColorUtils;
import android.support.v7.widget.ButtonBarLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Collections;
import java.util.List;

import static android.R.attr.author;
import static android.R.attr.id;
import static android.R.attr.textSize;


public class MainQuestionAnswerAdapter extends RecyclerView.Adapter<MainQuestionAnswerAdapter.QuestionTopAnswerHolder> {

    private static final String TAG = MainQuestionAnswerAdapter.class.getSimpleName();
    List<Question> data= Collections.emptyList();
    Question current;

    public MainQuestionAnswerAdapter(List<Question> data) {
        this.data=data;
    }

    /**
     *
     * This gets called when each new ViewHolder is created. This happens when the RecyclerView
     * is laid out. Enough ViewHolders will be created to fill the screen and allow for scrolling.
     *
     * @param viewGroup The ViewGroup that these ViewHolders are contained within.
     * @param viewType  If your RecyclerView has more than one type of item (which ours doesn't) you
     *                  can use this viewType integer to provide a different layout. See
     *                  {@link android.support.v7.widget.RecyclerView.Adapter#getItemViewType(int)}
     *                  for more details.
     * @return A new NumberViewHolder that holds the View for each list item
     */
    @Override
    public QuestionTopAnswerHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.question_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        QuestionTopAnswerHolder viewHolder = new QuestionTopAnswerHolder(view);


        return viewHolder;
    }

    /**
     * OnBindViewHolder is called by the RecyclerView to display the data at the specified
     * position. In this method, we update the contents of the ViewHolder to display the correct
     * indices in the list for this particular position, using the "position" argument that is conveniently
     * passed into us.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(QuestionTopAnswerHolder holder, int position) {
        Log.d(TAG, "#" + position);
        // Get current position of item in recyclerview to bind data and assign values from list
        QuestionTopAnswerHolder questionTopAnswerHolder= (QuestionTopAnswerHolder) holder;
        Question current=data.get(position);
        questionTopAnswerHolder.textQuestion.setText(current.questionText);

        if(current.topAnswer.equals("0"))
            questionTopAnswerHolder.textTopAnswer.setText("");
        else
            questionTopAnswerHolder.textTopAnswer.setText(current.topAnswer);

        questionTopAnswerHolder.textAnswerCount.setText(String.valueOf(current.noOfAnswers + " Answers"));
        questionTopAnswerHolder.textTimeStamp.setText(current.timeStamp);
        questionTopAnswerHolder.textAuthor.setText(current.author);
        questionTopAnswerHolder.textQuestion.setTag(String.valueOf(current.id));
    }

    /**
     * This method simply returns the number of items to display. It is used behind the scenes
     * to help layout our Views and for animations.
     *
     * @return The number of items available
     */
    @Override
    public int getItemCount() {
        return data.size();
    }

    // COMPLETED (5) Implement OnClickListener in the NumberViewHolder class
    /**
     * Cache of the children views for a list item.
     */
    class QuestionTopAnswerHolder extends RecyclerView.ViewHolder
             {

        // Will display the position in the list, ie 0 through getItemCount() - 1
        TextView textQuestion,textTopAnswer,textAnswerCount,textAuthor,textTimeStamp,textId;
        Button postAnswer;
        // Will display which ViewHolder is displaying this data

        /**
         * Constructor for our ViewHolder. Within this constructor, we get a reference to our
         * TextViews and set an onClickListener to listen for clicks. Those will be handled in the
         * onClick method below.
         * @param itemView The View that you inflated in
         *                 {@link MainQuestionAnswerAdapter#onCreateViewHolder(ViewGroup, int)}
         */
        public QuestionTopAnswerHolder(View itemView) {
            super(itemView);

            textQuestion= (TextView) itemView.findViewById(R.id.textQuestion);
            textTopAnswer= (TextView) itemView.findViewById(R.id.textTopAnswer);
            textAnswerCount = (TextView) itemView.findViewById(R.id.textAnswerCount);
            textAuthor = (TextView) itemView.findViewById(R.id.textAuthor);
            textTimeStamp = (TextView) itemView.findViewById(R.id.textTimeStamp);
            textId = (TextView) itemView.findViewById(R.id.textId);
            postAnswer =(Button) itemView.findViewById(R.id.postanswer);

            textQuestion.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    setTextForIntentAndCall(v,AnswerActivity.class);
                }
            });

            postAnswer.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    setTextForIntentAndCall(v,PostAnswer.class);
                }
            });

            textTopAnswer.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    setTextForIntentAndCall(v,AnswerActivity.class);
                }
            });
            textAnswerCount.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    setTextForIntentAndCall(v,AnswerActivity.class);
                }
            });

        }
                 public void setTextForIntentAndCall(View v,Class s) {

                     Context context = v.getContext();
                     String questionText = textQuestion.getText().toString();
                     String id = textQuestion.getTag().toString();
                     String authorText = textAuthor.getText().toString();
                     String timeStamp = textTimeStamp.getText().toString();
                     Intent intent = new Intent(context, s);
                     intent.putExtra("id",id);
                     intent.putExtra("question",questionText);
                     intent.putExtra("author",authorText);
                     intent.putExtra("timestamp",timeStamp);
                     intent.putExtra("numanswers",textAnswerCount.getText());
                     context.startActivity(intent);
                 }

    }
}


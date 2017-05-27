package com.codelords.project.uask;

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
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


import android.view.View.OnClickListener;

import java.util.Collections;
import java.util.List;


public class MainQuestionAnswerAdapter extends RecyclerView.Adapter<MainQuestionAnswerAdapter.QuestionTopAnswerHolder> {

    private static final String TAG = MainQuestionAnswerAdapter.class.getSimpleName();
    List<Question> data= Collections.emptyList();
    int _category;
    Question current;
    private static final int STATIC_CARD = 0;
    private static final int DYNAMIC_CARD = 1;

    public MainQuestionAnswerAdapter(List<Question> data,int category) {
        this.data=data;
        _category = category;
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
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        int layoutIdForListItem;
        View view;
        QuestionTopAnswerHolder viewHolder;

        if(viewType == STATIC_CARD)
        {
            Log.d("onCreateAdapter","static");

            layoutIdForListItem = getStaticLayouts(_category);
            view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
            viewHolder = new QuestionTopAnswerHolder(view,viewType);
        }
        else {
            Log.d("onCreateAdapter","dynamic");
            layoutIdForListItem = R.layout.question_list_item;
            view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
            viewHolder = new QuestionTopAnswerHolder(view,viewType);
        }
        return viewHolder;
    }

    private int getStaticLayouts(int category) {
        int layout;
        switch(category){
            case 0:
                layout = R.layout.question_list_item_one;
                break;
            case 1:
                layout = R.layout.question_list_item_two;
            break;
            case 2:
                layout = R.layout.question_list_item_five;
                break;
            case 3:
                layout = R.layout.question_list_item_three;
                break;
            case 4:
                layout = R.layout.question_list_item_four;
                break;
            default:
                layout = R.layout.question_list_item_one;
        }
        return layout;
    }

    @Override
    public int getItemViewType(int position)
    {
        if(position == 0 && (_category >= 0 && _category <9))
            return STATIC_CARD;
        else
            return DYNAMIC_CARD;
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
        QuestionTopAnswerHolder questionTopAnswerHolder = (QuestionTopAnswerHolder) holder;
        final Context context = questionTopAnswerHolder.v.getContext();
        Log.d(TAG, "Category" + _category);
            switch (_category) {
                case 0:
                case 1:
                case 2:
                case 3:
                case 4:
                {
                    if(position == 0) {
                      /*  String infoText = (String) questionTopAnswerHolder.basicInfoText.getText();
                        int count = infoText.split("\n").length;
                        int upperLimit = (count > 5) ? MainAnswerAdapter.ordinalIndexOf(infoText, "\n", 5) : 140;
                        if (infoText.length() > 140 || count > 5) {
                            infoText = infoText.substring(0, upperLimit) + "... " + "view more";

                            SpannableString sText = new SpannableString(infoText);
                            ClickableSpan myClickableSpan = new ClickableSpan() {
                                @Override
                                public void onClick(View v) {
                                    Log.d("MainCanvas Category", "clickable Span");
                                    //finish();
                                }
                            };
                            int spanLowLimit = upperLimit + 4;
                            int spanHighLimit = upperLimit + 13;
                            sText.setSpan(myClickableSpan, spanLowLimit, spanHighLimit, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            sText.setSpan(new RelativeSizeSpan(0.75f), spanLowLimit, spanHighLimit, 0);
                            sText.setSpan(new ForegroundColorSpan( questionTopAnswerHolder.v.getResources().getColor(R.color.primaryOrange)), spanLowLimit, spanHighLimit, 0);
                            questionTopAnswerHolder.basicInfoText.setText(sText);
                            questionTopAnswerHolder.basicInfoText.setMovementMethod(LinkMovementMethod.getInstance());
                        }*/
                        String infoText = (String) questionTopAnswerHolder.basicInfoText.getText();
                        int upperLimit =  150;
                        if (infoText.length() > 150 ) {
                            infoText = infoText.substring(0, upperLimit) + "... " + "view more";

                            SpannableString sText = new SpannableString(infoText);
                            ClickableSpan myClickableSpan = new ClickableSpan() {
                                @Override
                                public void onClick(View v) {
                                    Log.d("Mainadapter Category", "clickable Span");
                                    Intent intent = new Intent(context, ShowBasicInfo.class);
                                    intent.putExtra("category",_category);
                                    context.startActivity(intent);//finish();
                                }
                            };
                            int spanLowLimit = upperLimit + 4;
                            int spanHighLimit = upperLimit + 13;
                            sText.setSpan(myClickableSpan, spanLowLimit, spanHighLimit, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            sText.setSpan(new RelativeSizeSpan(0.75f), spanLowLimit, spanHighLimit, 0);
                            sText.setSpan(new ForegroundColorSpan(questionTopAnswerHolder.v.getResources().getColor(R.color.primaryOrange)), spanLowLimit, spanHighLimit, 0);
                            questionTopAnswerHolder.basicInfoText.setText(sText);
                            questionTopAnswerHolder.basicInfoText.setMovementMethod(LinkMovementMethod.getInstance());
                        }

                    }
                    else
                    {
                        populateDynamicUiElements(questionTopAnswerHolder,(position-1));
                    }
                }
                break;
                case 5:
                {
                    if(position == 0) {

                        questionTopAnswerHolder.basicInfoText.setText("List of all non-categorical questions");
                        questionTopAnswerHolder.basicInfoText.setGravity(Gravity.CENTER | Gravity.BOTTOM);
                        questionTopAnswerHolder.headingText.setVisibility(View.INVISIBLE);
                    }
                    else{
                        populateDynamicUiElements(questionTopAnswerHolder,(position-1));
                    }
                }
                    break;
                case 6:
                {
                    if(position == 0) {
                        questionTopAnswerHolder.basicInfoText.setText("List of all questions asked by you.");
                        questionTopAnswerHolder.basicInfoText.setGravity(Gravity.CENTER | Gravity.BOTTOM);
                        questionTopAnswerHolder.headingText.setVisibility(View.INVISIBLE);
                    }
                    else{
                        populateDynamicUiElements(questionTopAnswerHolder,(position-1));
                    }
                }
                    break;
                case 7:
                {
                    if(position == 0) {

                        questionTopAnswerHolder.basicInfoText.setText("List of all questions answered by you.");
                        questionTopAnswerHolder.basicInfoText.setGravity(Gravity.CENTER | Gravity.BOTTOM);
                        questionTopAnswerHolder.headingText.setVisibility(View.INVISIBLE);
                    }
                    else{
                        populateDynamicUiElements(questionTopAnswerHolder,(position-1));
                    }
                }
                break;
                case 8:
                {
                    if(position == 0) {

                        questionTopAnswerHolder.basicInfoText.setText("All the private questions asked by your faculty students. Visible only to fellow faculty students");
                        questionTopAnswerHolder.basicInfoText.setGravity(Gravity.CENTER | Gravity.BOTTOM);
                        questionTopAnswerHolder.headingText.setVisibility(View.INVISIBLE);
                    }
                    else{
                        populateDynamicUiElements(questionTopAnswerHolder,(position-1));
                    }
                }
                break;
                default: {
                    populateDynamicUiElements(questionTopAnswerHolder,position);
                }
                break;

            }

    }

    public void populateDynamicUiElements(QuestionTopAnswerHolder holder,int position )
    {
        Question current = data.get(position);
        holder.textQuestion.setText(current.questionText);

        if (current.topAnswer.equals("0"))
            holder.textTopAnswer.setText("");
        else
            holder.textTopAnswer.setText(current.topAnswer);

        holder.textAnswerCount.setText(String.valueOf(current.noOfAnswers + " Answers"));
        holder.textTimeStamp.setText(current.timeStamp);
        holder.textAuthor.setText(current.author);
        holder.textQuestion.setTag(String.valueOf(current.id));
        holder.textCategory.setText(current.category);
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
        TextView textQuestion,textTopAnswer,textAnswerCount,textAuthor,textTimeStamp,textId,basicInfoText,headingText,textCategory;
        Button postAnswer;
                 View v;
        // Will display which ViewHolder is displaying this data

        /**
         * Constructor for our ViewHolder. Within this constructor, we get a reference to our
         * TextViews and set an onClickListener to listen for clicks. Those will be handled in the
         * onClick method below.
         * @param itemView The View that you inflated in
         *                 {@link MainQuestionAnswerAdapter#onCreateViewHolder(ViewGroup, int)}
         */
        public QuestionTopAnswerHolder(View itemView,int viewType) {
            super(itemView);
            v = itemView;
            if(viewType == STATIC_CARD)
            {
                basicInfoText = (TextView) itemView.findViewById(R.id.basicInfo);
                headingText = (TextView) itemView.findViewById(R.id.heading);
            }
            else {

                textQuestion = (TextView) itemView.findViewById(R.id.textQuestion);
                textTopAnswer = (TextView) itemView.findViewById(R.id.textTopAnswer);
                textAnswerCount = (TextView) itemView.findViewById(R.id.textAnswerCount);
                textAuthor = (TextView) itemView.findViewById(R.id.textAuthor);
                textTimeStamp = (TextView) itemView.findViewById(R.id.textTimeStamp);
                textId = (TextView) itemView.findViewById(R.id.textId);
                postAnswer = (Button) itemView.findViewById(R.id.postanswer);
                textCategory = (TextView) itemView.findViewById(R.id.textCategory);

                textQuestion.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setTextForIntentAndCall(v, AnswerActivity.class);
                    }
                });

                postAnswer.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setTextForIntentAndCall(v, PostAnswer.class);
                    }
                });

                textTopAnswer.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setTextForIntentAndCall(v, AnswerActivity.class);
                    }
                });
                textAnswerCount.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setTextForIntentAndCall(v, AnswerActivity.class);
                    }
                });
            }
        }
                 public void setTextForIntentAndCall(View v,Class s) {

                     Context context = v.getContext();
                     String questionText = textQuestion.getText().toString();
                     String id = textQuestion.getTag().toString();
                     String authorText = textAuthor.getText().toString();
                     String timeStamp = textTimeStamp.getText().toString();
                     String category = textCategory.getText().toString();
                     Intent intent = new Intent(context, s);
                     intent.putExtra("id",id);
                     intent.putExtra("question",questionText);
                     intent.putExtra("author",authorText);
                     intent.putExtra("timestamp",timeStamp);
                     intent.putExtra("category",category);
                     intent.putExtra("numanswers",textAnswerCount.getText());
                     context.startActivity(intent);
                 }

    }
}


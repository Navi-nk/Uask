package com.codelords.project.uask;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

/**
 * Created by ethi on 19/02/17.
 */

public class MainAnswerAdapter extends RecyclerView.Adapter<MainAnswerAdapter.AnswerHolder>{
    private static final String TAG = MainAnswerAdapter.class.getSimpleName();
    List<Answer> data= Collections.emptyList();
    Answer current;

    public MainAnswerAdapter(List<Answer> data) {
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
    public MainAnswerAdapter.AnswerHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.answer_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        MainAnswerAdapter.AnswerHolder viewHolder = new MainAnswerAdapter.AnswerHolder(view);


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
    public void onBindViewHolder(MainAnswerAdapter.AnswerHolder holder, int position) {
        Log.d(TAG, "#" + position);
        // Get current position of item in recyclerview to bind data and assign values from list
        Answer current = data.get(position);
        holder.textAnswer.setText(current.answerText);
        holder.textTimeStamp.setText(current.timeStamp);
        holder.textAuthor.setText(current.author);

        final String ansText = (String) holder.textAnswer.getText();
        int count = ansText.split("\n").length;
        int upperLimit = (count > 5) ?ordinalIndexOf(ansText,"\n",5):140;
        //Log.d("MAAdapter string", ansText+" "+Integer.toString(count));
        if (ansText.length()>140 || count > 5) {
            final String authorText = holder.textAuthor.getText().toString();
            final String timeStamp = holder.textTimeStamp.getText().toString();
            Log.d("MainAnswerAdapter", "clickable Span");
            String spantext = ansText.substring(0, upperLimit) + "... " + "view more";

            SpannableString sText = new SpannableString(spantext);
            ClickableSpan myClickableSpan = new ClickableSpan() {
                @Override
                public void onClick(View v) {
                    Log.d("MainAnswerAdapter", "clickable Span1");
                    Context context = v.getContext();
                    Intent i = new Intent(context, ShowAnswer.class);
                    i.putExtra("answertext", ansText);
                    i.putExtra("author",authorText);
                    i.putExtra("timestamp",timeStamp);
                    context.startActivity(i);
                }
            };
            int spanLowLimit = upperLimit + 4;
            int spanHighLimit = upperLimit + 13;
            sText.setSpan(myClickableSpan, spanLowLimit, spanHighLimit, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            sText.setSpan(new RelativeSizeSpan(0.75f),spanLowLimit, spanHighLimit, 0);
            sText.setSpan(new ForegroundColorSpan(holder.v.getResources().getColor(R.color.primaryOrange)), spanLowLimit, spanHighLimit, 0);
            holder.textAnswer.setText(sText);
            holder.textAnswer.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }

    public static int ordinalIndexOf(String str, String substr, int n) {
        int pos = str.indexOf(substr);
        while (--n > 0 && pos != -1)
            pos = str.indexOf(substr, pos + 1);
        return pos;
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
    class AnswerHolder extends RecyclerView.ViewHolder
    {

        // Will display the position in the list, ie 0 through getItemCount() - 1
        TextView textAnswer,textAuthor,textTimeStamp;
        View v;
        // Will display which ViewHolder is displaying this data

        /**
         * Constructor for our ViewHolder. Within this constructor, we get a reference to our
         * TextViews and set an onClickListener to listen for clicks. Those will be handled in the
         * onClick method below.
         * @param itemView The View that you inflated in
         *                 {@link MainQuestionAnswerAdapter#onCreateViewHolder(ViewGroup, int)}
         */
        public AnswerHolder(View itemView) {
            super(itemView);
            v=itemView;
            textAnswer= (TextView) itemView.findViewById(R.id.textAnswer);
            textAuthor = (TextView) itemView.findViewById(R.id.textAuthor);
            textTimeStamp = (TextView) itemView.findViewById(R.id.textTimeStamp);
        }
    }
}

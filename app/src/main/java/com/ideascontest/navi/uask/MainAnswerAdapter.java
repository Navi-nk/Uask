package com.ideascontest.navi.uask;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import static android.R.attr.id;
import static com.ideascontest.navi.uask.R.id.textAnswerCount;
import static com.ideascontest.navi.uask.R.id.textAuthor;
import static com.ideascontest.navi.uask.R.id.textQuestion;
import static com.ideascontest.navi.uask.R.id.textTimeStamp;
import static com.ideascontest.navi.uask.R.id.textTopAnswer;

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

            textAnswer= (TextView) itemView.findViewById(R.id.textAnswer);
            textAuthor = (TextView) itemView.findViewById(R.id.textAuthor);
            textTimeStamp = (TextView) itemView.findViewById(R.id.textTimeStamp);

        }

    }
}

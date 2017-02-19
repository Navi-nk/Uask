package com.ideascontest.navi.uask;

import android.app.Application;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.R.attr.id;
import static com.ideascontest.navi.uask.NetworkUtils.PARAM_QUESTION;
import static com.ideascontest.navi.uask.R.string.question;

public class MainCanvas extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final int REQUEST_SIGNUP = 0;
    private static final String TAG = "Main Canvas";
    // Session Manager Class
    SessionManager _session;

    private RecyclerView mainQuestionAnswerList;
    private static final int NUM_LIST_ITEMS = 100;
    private MainQuestionAnswerAdapter mQuestionAnswerAdapter;
    private TextView mErrorMessageDisplay;
    private FrameLayout mQuestionTopAnswerContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_canvas);
        // Session class instance
        _session = new SessionManager(getApplicationContext());
        //Check if user is still loggedin if not redirect to login activity
        if(!_session.isLoggedIn())
        {
            Intent i = new Intent(getApplicationContext(), LoginActivity.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            // Staring Login Activity
            startActivity(i);
            finish();
        }


        // Make API call and display question


        // get user data from session
        HashMap<String, String> user = _session.getUserDetails();

        // name
        String userName = user.get(SessionManager.KEY_NAME);
        final String facultyName =  user.get(SessionManager.KEY_FAC);

        //get reference to navigation view
        final NavigationView navView = (NavigationView) findViewById(R.id.nav_view);
        //get reference to header view embedded in navigation view
        View header = navView.getHeaderView(0);
        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);
        mQuestionTopAnswerContent = (FrameLayout) findViewById(R.id.question_top_answer_content);
        //Set user name in header
        TextView name=(TextView)header.findViewById(R.id.tv1);
        name.setText(userName);
        //Set faculty name in header
        TextView faculty=(TextView)header.findViewById(R.id.tv2);
        faculty.setText(facultyName);

        //Implement listener to detect to up-down arrow buttonto switch between menus
        ToggleButton mAccountToggle = (ToggleButton) header.findViewById(R.id.account_view_icon_button);
        mAccountToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d(TAG,"Toggle");
                navView.getMenu().clear();
                if(isChecked) {
                    navView.inflateMenu(R.menu.user_profile_menu);
                    MenuItem mitem =  navView.getMenu().findItem(R.id.nav_facprivate);
                   mitem.setTitle("Faculty Exclusive");

                }else
                    navView.inflateMenu(R.menu.activity_main_canvas_drawer);
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        {
            public void onDrawerClosed(View view)
            {
                super.onDrawerClosed(view);
                navView.getMenu().clear();
                navView.inflateMenu(R.menu.activity_main_canvas_drawer);
            }

            public void onDrawerOpened(View drawerView)
            {
                super.onDrawerOpened(drawerView);
            }
        };
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        URL SearchUrl = NetworkUtils.buildUrl(NetworkUtils.GET_ALL_QUESTIONS,NetworkUtils.PARAM_QUESTION,"");
        new QuestionAnswerQueryTask().execute(SearchUrl);
    }


    public class QuestionAnswerQueryTask extends AsyncTask<URL, Void, String> {

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
            // COMPLETED (27) As soon as the loading is complete, hide the loading indicator
            if (QuestionAnswerSearchResults != null && !QuestionAnswerSearchResults.equals("")) {
                // COMPLETED (17) Call showJsonDataView if we have valid, non-null results
                showJsonDataView();
                //this method will be running on UI thread

                List<Question> data=new ArrayList<>();

                try {

                    JSONArray jArray = new JSONArray(QuestionAnswerSearchResults);

                    // Extract data from json and store into ArrayList as class objects
                    for(int i=0;i<jArray.length();i++){
                        JSONObject json_data = jArray.getJSONObject(i);
                        Question questionData = new Question();
                        questionData.questionText= json_data.getString("_Text");
                        questionData.noOfAnswers = json_data.getInt("_Number_Answers");
                        questionData.topAnswer= json_data.getString("_Answer");
                        questionData.author=json_data.getString("_Used_Id");
                        questionData.timeStamp=json_data.getString("_Datetime");
                        questionData.id=json_data.getString("_Id");
                        data.add(questionData);
                    }


                    mainQuestionAnswerList = (RecyclerView) findViewById(R.id.question_top_answer_recylerview);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(MainCanvas.this);
                    mainQuestionAnswerList.setLayoutManager(layoutManager);
                    mainQuestionAnswerList.setHasFixedSize(true);
                    mQuestionAnswerAdapter = new MainQuestionAnswerAdapter(data);
                    mainQuestionAnswerList.setAdapter(mQuestionAnswerAdapter);

                    // Setup and Handover data to recyclerview

                } catch (JSONException e) {
                    Toast.makeText(MainCanvas.this, e.toString(), Toast.LENGTH_LONG).show();
                }

            } else {
                // COMPLETED (16) Call showErrorMessage if the result is null in onPostExecute
                showErrorMessage();
            }
        }

        private void showJsonDataView() {
            // First, make sure the error is invisible
            mErrorMessageDisplay.setVisibility(View.INVISIBLE);
            // Then, make sure the JSON data is visible
            mQuestionTopAnswerContent.setVisibility(View.VISIBLE);
        }

        // COMPLETED (15) Create a method called showErrorMessage to show the error and hide the data
        /**
         * This method will make the error message visible and hide the JSON
         * View.
         * <p>
         * Since it is okay to redundantly set the visibility of a View, we don't
         * need to check whether each view is currently visible or invisible.
         */
        private void showErrorMessage() {
            // First, hide the currently visible data
            mQuestionTopAnswerContent.setVisibility(View.INVISIBLE);
            // Then, show the error
            mErrorMessageDisplay.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public void navigateToAnswer(View view) {
        String id = (String) view.getTag();

        TextView question = (TextView) view;
        String questionText = question.getText().toString();
//        TextView author = (TextView)view.findViewById(R.id.textAuthor);
//        String authorText = author.getText().toString();
//        Log.d("Checkauthor",authorText);
        Log.d("CheckID",id);
        Intent intent = new Intent(getApplicationContext(), AnswerActivity.class);
        intent.putExtra("id",id);
        intent.putExtra("question",questionText);
        startActivity(intent);
    }

    public void navigateToPostAnswer(View view) {
        Intent intent = new Intent(getApplicationContext(), PostAnswer.class);
        startActivity(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_canvas, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_transport) {

        } else if (id == R.id.nav_food) {

        } else if (id == R.id.nav_maps) {
            Intent i = new Intent(getApplicationContext(), MapActivity.class);
            startActivity(i);
        }else if(id == R.id.nav_logout){
            //Clear session data
            _session.logoutUser();
            // After logout redirect user to Loing Activity
            Intent i = new Intent(getApplicationContext(), LoginActivity.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            // Staring Login Activity
            startActivity(i);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

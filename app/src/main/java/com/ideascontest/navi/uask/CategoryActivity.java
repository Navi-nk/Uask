package com.ideascontest.navi.uask;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
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

import static android.R.attr.category;
import static com.ideascontest.navi.uask.R.id.toolbar;

public class CategoryActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    SessionManager _session;
    private MainQuestionAnswerAdapter mCategoryQuestionAnswerAdapter;
    private RecyclerView mainCategoryQuestionAnswerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        //session
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

        //navigation bar
        final NavigationView navView = (NavigationView) findViewById(R.id.nav_view);
        //get reference to header view embedded in navigation view
        View header = navView.getHeaderView(0);
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
        Intent intent = getIntent();
        String category = intent.getStringExtra("category").toString();
        Log.d("categorycheck",category);
        URL SearchUrl = NetworkUtils.buildUrl(NetworkUtils.GET_ALL_QUESTION_FOR_CAT,NetworkUtils.PARAM_CATEGORY,category);
        Log.d("url",SearchUrl.toString());

        new CategoryQuestionAnswerQueryTask().execute(SearchUrl);
    }

    public class CategoryQuestionAnswerQueryTask extends AsyncTask<URL, Void, String> {

        // COMPLETED (26) Override onPreExecute to set the loading indicator to visible
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(URL... params) {
            URL searchUrl = params[0];
            String CategoryQuestionAnswerSearchResults = null;
            try {
                CategoryQuestionAnswerSearchResults = NetworkUtils.getResponseFromHttpUrl(searchUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return CategoryQuestionAnswerSearchResults;
        }

        @Override
        protected void onPostExecute(String CategoryQuestionAnswerSearchResults) {
            // COMPLETED (27) As soon as the loading is complete, hide the loading indicator
            if (CategoryQuestionAnswerSearchResults != null && !CategoryQuestionAnswerSearchResults.equals("")) {
                // COMPLETED (17) Call showJsonDataView if we have valid, non-null results
                //this method will be running on UI thread

                List<Question> data=new ArrayList<>();

                try {

                    JSONArray jArray = new JSONArray(CategoryQuestionAnswerSearchResults);

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


                    mainCategoryQuestionAnswerList = (RecyclerView) findViewById(R.id.question_top_answer_recylerview);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(CategoryActivity.this);
                    mainCategoryQuestionAnswerList.setLayoutManager(layoutManager);
                    mainCategoryQuestionAnswerList.setHasFixedSize(true);
                    mCategoryQuestionAnswerAdapter = new MainQuestionAnswerAdapter(data);
                    mainCategoryQuestionAnswerList.setAdapter(mCategoryQuestionAnswerAdapter);

                    // Setup and Handover data to recyclerview

                } catch (JSONException e) {
                    Toast.makeText(CategoryActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                }

            }
        }

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_transport) {
            Intent i = new Intent(getApplicationContext(),CategoryActivity.class);
            i.putExtra("category","temp");
            startActivity(i);

        }
        else if (id == R.id.nav_food) {
            Intent i = new Intent(getApplicationContext(),CategoryActivity.class);
            i.putExtra("category","temp_1");
            startActivity(i);
        }
        else if (id == R.id.nav_sport) {
            Intent i = new Intent(getApplicationContext(),CategoryActivity.class);
            i.putExtra("category","temp");
            startActivity(i);
        }

        else if (id == R.id.nav_general) {
            Intent i = new Intent(getApplicationContext(),MainCanvas.class);
            startActivity(i);
        }
        else if (id == R.id.nav_maps) {
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

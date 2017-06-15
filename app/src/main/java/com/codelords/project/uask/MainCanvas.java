package com.codelords.project.uask;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
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


//Amazon mobile analytics
import com.amazonaws.mobileconnectors.amazonmobileanalytics.*;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.codelords.project.uask.helper.ApiGatewayHelper;
import com.codelords.project.uask.helper.CognitoHelper;
import com.codelords.uask.apiclientsdk.UAskClient;
import com.codelords.uask.apiclientsdk.model.QuestionFeedModel;
import com.facebook.login.LoginManager;
import com.google.gson.Gson;

public class MainCanvas extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final int REQUEST_SIGNUP = 0;
    private static final String TAG = "Main Canvas";
    private static MobileAnalyticsManager analytics;
    private static UAskClient apiClient;
    int _categorySelected;
    // Session Manager Class
    SessionManager _session;

    static String[] CAT_LIST = {"Getting Around",
            "Food & Beverages",
            "Faculties/Departments",
            "Sports & Recreation",
            "Residences",
            "General"
    };
    private RecyclerView mainQuestionAnswerList;
    private MainQuestionAnswerAdapter mQuestionAnswerAdapter;
    private TextView mErrorMessageDisplay;
    private FrameLayout mQuestionTopAnswerContent;
    //private  URL SearchUrl;
    private Integer apiId = 0;
    private String apiParam;

    String userName;
    String facultyName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Session class instance
        _session = new SessionManager(getApplicationContext());
        //Check if user is still loggedin if not redirect to login activity

       /*if(!_session.isLoggedIn())
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
        else {
           CognitoHelper.init(this);
           new RefreshTask().execute();
        }*/

        CognitoHelper.init(this);
        ApiGatewayHelper.init(CognitoHelper.getCredentialsProvider());

//        initialize sesssion for AWS
        try {
            analytics = MobileAnalyticsManager.getOrCreateInstance(
                    this.getApplicationContext(),
                    "8d5e6256080e4acea50fcf9a805f0822", //Amazon Mobile Analytics App ID
                    CognitoHelper.getIdentityPoolId()//Amazon Cognito Identity Pool ID
            );
        } catch(InitializationException ex) {
            Log.e(this.getClass().getName(), "Failed to initialize Amazon Mobile Analytics", ex);
        }
        Intent i = getIntent();
        String feedType = i.getStringExtra("feedType");
        final int menuItemIdx = i.getIntExtra("itemposition",-1);
        if (feedType == null)
        {
            Log.d("MainCanvas","first page");
            setContentView(R.layout.activity_main_canvas);
            //SearchUrl = NetworkUtils.buildUrl(NetworkUtils.GET_ALL_QUESTIONS,NetworkUtils.PARAM_QUESTION,"");
            apiId=1;
            apiParam=null;
            _categorySelected = -1;
        }
        else if(feedType.equalsIgnoreCase("search"))  {
            setContentView(R.layout.activity_main_canvas);
            //SearchUrl = NetworkUtils.buildUrl(NetworkUtils.SEARCH_QUESTION,NetworkUtils.PARAM_SEARCH_STRING,i.getStringExtra("search_string").toString());
            _categorySelected = -1;
            apiParam=i.getStringExtra("search_string").toString();
            apiId=6;
        }
        else {
            Log.d("MainCanvas","Category");
            setContentView(R.layout.activity_category);

            if (feedType.equalsIgnoreCase("category")) {
                apiParam = i.getStringExtra("category").toString();
                Log.d("category",apiParam);
                //SearchUrl = NetworkUtils.buildUrl(NetworkUtils.GET_ALL_QUESTION_FOR_CAT, NetworkUtils.PARAM_CATEGORY, category);
                apiId=2;
                int index = -1;
                for (int cnt=0;cnt<CAT_LIST.length;cnt++) {
                    if (CAT_LIST[cnt].equals(apiParam)) {
                        index = cnt;
                        break;
                    }
                }
                _categorySelected = index;
                Log.d("Index-Category",Integer.toString(_categorySelected));
                //Log.d("url",SearchUrl.toString());
            } else if (feedType.equalsIgnoreCase("userQuestions")) {
                apiParam = i.getStringExtra("user").toString();
                //SearchUrl = NetworkUtils.buildUrl(NetworkUtils.GET_ALL_QUESTION_FROM_USER, NetworkUtils.PARAM_USERID, user);
                apiId=3;
                _categorySelected = 6;
            } else if (feedType.equalsIgnoreCase("userAnswers")) {
                apiParam = i.getStringExtra("user").toString();
                //SearchUrl = NetworkUtils.buildUrl(NetworkUtils.GET_ALL_QUESTION_ANS_BY_USER, NetworkUtils.PARAM_USERID, user);
                apiId=4;
                _categorySelected = 7;
            } else if (feedType.equalsIgnoreCase("privateQues")) {
                apiParam = i.getStringExtra("userfaculty").toString();
                //SearchUrl = NetworkUtils.buildUrl(NetworkUtils.GET_ALL_PQUESTION_BY_FACUSER, NetworkUtils.PARAM_FACULTY, userFaculty);
                apiId=5;
                _categorySelected = 8;
            }

        }

        // get user data from session
        HashMap<String, String> user = _session.getUserDetails();

        // name
        userName = user.get(SessionManager.KEY_NAME);
        facultyName =  user.get(SessionManager.KEY_FAC);

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

        new QuestionAnswerQueryTask().execute();
        //Ask a question
        FloatingActionButton qfab = (FloatingActionButton) findViewById(R.id.fab);
        qfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), AskQuestionActivity.class);
                i.putExtra("position",menuItemIdx);
                startActivityForResult(i,0);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("here in maincanvas",Integer.toString(requestCode)+" "+Integer.toString(resultCode));
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                new QuestionAnswerQueryTask().execute();
            }
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        if(analytics != null) {
            analytics.getSessionClient().pauseSession();
            analytics.getEventClient().submitEvents();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(analytics != null) {
            analytics.getSessionClient().resumeSession();
        }
    }


    public class QuestionAnswerQueryTask extends AsyncTask<Void, Void, QuestionFeedModel> {

        // COMPLETED (26) Override onPreExecute to set the loading indicator to visible
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected QuestionFeedModel doInBackground(Void... params) {
            QuestionFeedModel response = null;
            try {
                apiClient = ApiGatewayHelper.getApiClientFactory().build(UAskClient.class);
                //Gson gson = new Gson();
                switch (apiId){
                    case 1:
                        response = apiClient.getfeedGet();
                        break;
                    case 2:
                        response = apiClient.getcategoryfeedGet(apiParam);
                        break;
                    case 3:
                        response = apiClient.getuserquestionsGet(apiParam);
                        break;
                    case 4:
                        response = apiClient.getuseranswersGet(apiParam);
                        break;
                    case 5:
                        response = apiClient.getprivatefeedGet(apiParam);
                        break;
                    case 6:
                        response = apiClient.searchquestionGet(apiParam);
                        break;
                    default:
                        response = apiClient.getfeedGet();
                        break;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(QuestionFeedModel response) {
            // COMPLETED (27) As soon as the loading is complete, hide the loading indicator
            if (response != null && !response.equals("")) {
                // COMPLETED (17) Call showJsonDataView if we have valid, non-null results
                //this method will be running on UI thread

                List<Question> data=new ArrayList<>();

                try {

                   // JSONArray jArray = new JSONArray(response);

                    // Extract data from json and store into ArrayList as class objects
                    for(int i=0;i<response.size();i++){
                        //JSONObject json_data = response.get(i);
                        Question questionData = new Question();
                        questionData.questionText= response.get(i).getText();
                        questionData.noOfAnswers = response.get(i).getNumberAnswers();
                        questionData.topAnswer= response.get(i).getAnswer();
                        questionData.author=response.get(i).getUsedId();
                        questionData.timeStamp=response.get(i).getDatetime();
                        questionData.category=response.get(i).getCategory();
                        questionData.id=response.get(i).getId();
                        data.add(questionData);
                    }
                    if(_categorySelected != -1)
                    {
                        data.add(new Question());
                    }

                    mainQuestionAnswerList = (RecyclerView) findViewById(R.id.question_top_answer_recylerview);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(MainCanvas.this);
                    mainQuestionAnswerList.setLayoutManager(layoutManager);
                    mainQuestionAnswerList.setHasFixedSize(true);
                    mQuestionAnswerAdapter = new MainQuestionAnswerAdapter(data,_categorySelected);
                    mainQuestionAnswerList.setAdapter(mQuestionAnswerAdapter);

                    // Setup and Handover data to recyclerview

                } catch (Exception e) {
                    Toast.makeText(MainCanvas.this, e.toString(), Toast.LENGTH_LONG).show();
                }

            }
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

    public void navigateToPostAnswer(View view) {
        Intent intent = new Intent(getApplicationContext(), PostAnswer.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_canvas_menu, menu);
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
//        searchView.setSearchableInfo(
//                searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){
            @Override
            public boolean onQueryTextChange(String newText) {
                                return false;
            }


            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent i = new Intent(getApplicationContext(),MainCanvas.class);
                i.putExtra("feedType","search");
                i.putExtra("search_string",query);
                startActivity(i);
                finish();

                return false;
            }
        });

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

        if (id == R.id.nav_mainfeed) {
            Intent i = new Intent(getApplicationContext(),MainCanvas.class);
            startActivity(i);
            finish();

        }
        else if (id == R.id.nav_transport) {
            Intent i = new Intent(getApplicationContext(),MainCanvas.class);
            i.putExtra("feedType","category");
            i.putExtra("category",CAT_LIST[0]);
            i.putExtra("itemposition",0);
            startActivity(i);
            finish();

        }
        else if (id == R.id.nav_food) {
            Intent i = new Intent(getApplicationContext(),MainCanvas.class);
            i.putExtra("feedType","category");
            i.putExtra("category",CAT_LIST[1]);
            i.putExtra("itemposition",1);
            startActivity(i);
            finish();
        }
        else if (id == R.id.nav_fac) {
           // SearchUrl = NetworkUtils.buildUrl(NetworkUtils.GET_ALL_QUESTION_FOR_CAT, NetworkUtils.PARAM_CATEGORY, "temp");
           // new QuestionAnswerQueryTask().execute(SearchUrl);

            Intent i = new Intent(getApplicationContext(),MainCanvas.class);
            i.putExtra("feedType","category");
            i.putExtra("category",CAT_LIST[2]);
            i.putExtra("itemposition",2);
            startActivity(i);
            finish();
        }
        else if (id == R.id.nav_sport) {
     //       SearchUrl = NetworkUtils.buildUrl(NetworkUtils.GET_ALL_QUESTION_FOR_CAT, NetworkUtils.PARAM_CATEGORY, "temp");
     //       new QuestionAnswerQueryTask().execute(SearchUrl);

            Intent i = new Intent(getApplicationContext(),MainCanvas.class);
            i.putExtra("feedType","category");
            i.putExtra("category",CAT_LIST[3]);
            i.putExtra("itemposition",3);
            startActivity(i);
            finish();
        }
        else if (id == R.id.nav_accom) {
      //      SearchUrl = NetworkUtils.buildUrl(NetworkUtils.GET_ALL_QUESTION_FOR_CAT, NetworkUtils.PARAM_CATEGORY, "temp");
       //     new QuestionAnswerQueryTask().execute(SearchUrl);

            Intent i = new Intent(getApplicationContext(),MainCanvas.class);
            i.putExtra("feedType","category");
            i.putExtra("category",CAT_LIST[4]);
            i.putExtra("itemposition",4);
            startActivity(i);
            finish();
        }

        else if (id == R.id.nav_general) {
            Intent i = new Intent(getApplicationContext(),MainCanvas.class);
            i.putExtra("feedType","category");
            i.putExtra("category",CAT_LIST[5]);
            i.putExtra("itemposition",5);
            startActivity(i);
            finish();
        }
        else if(id == R.id.nav_question){
            Intent i = new Intent(getApplicationContext(),MainCanvas.class);
            i.putExtra("feedType","userQuestions");
            i.putExtra("user",userName);
            startActivity(i);
            finish();
        }
        else if(id == R.id.nav_answers){
            Intent i = new Intent(getApplicationContext(),MainCanvas.class);
            i.putExtra("feedType","userAnswers");
            i.putExtra("user",userName);
            startActivity(i);
            finish();
        }
        else if(id == R.id.nav_facprivate){
            Intent i = new Intent(getApplicationContext(),MainCanvas.class);
            i.putExtra("feedType","privateQues");
            i.putExtra("userfaculty",facultyName);
            startActivity(i);
            finish();
        }
        else if (id == R.id.chatbot) {
            Intent i = new Intent(getApplicationContext(), TextActivity.class);
            startActivity(i);
        }
        else if (id == R.id.nav_maps) {
            Intent i = new Intent(getApplicationContext(), MapActivity.class);
            startActivity(i);
        }else if (id == R.id.nav_about) {
            Intent i = new Intent(getApplicationContext(), About.class);
            startActivity(i);
        }
        else if(id == R.id.nav_logout){
            //Clear session data
            _session.logoutUser();
            if(CognitoHelper.getPool().getCurrentUser() != null)
                CognitoHelper.getPool().getCurrentUser().signOut();

                LoginManager.getInstance().logOut();

            // After logout redirect user to Loing Activity
            Intent i = new Intent(getApplicationContext(), LoginActivity.class);
            i.putExtra("logout","google");
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

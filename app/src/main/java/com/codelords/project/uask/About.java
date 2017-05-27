package com.codelords.project.uask;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;

import com.facebook.login.LoginManager;

public class About extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
        /*    GraphRequest delPermRequest = new GraphRequest(AccessToken.getCurrentAccessToken(),
                    "/" + AccessToken.getCurrentAccessToken().getUserId() + "/permissions/", null, HttpMethod.DELETE, new GraphRequest.Callback() {
                @Override
                public void onCompleted(GraphResponse graphResponse) {
                    if (graphResponse != null){
                        FacebookRequestError error = graphResponse.getError();
                        if (error == null)
                            Log.d("Fb","logout");

                    }
                }
            });
            delPermRequest.executeAsync();*/
            LoginManager.getInstance().logOut();
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}

package com.codelords.project.uask;

import android.os.AsyncTask;

import com.codelords.project.uask.helper.CognitoHelper;



public class RefreshTask extends AsyncTask<Void, Void, Boolean> {

    @Override
    protected Boolean doInBackground(Void... voids) {
        CognitoHelper.getCredentialsProvider().refresh();
        return true;
    }
}

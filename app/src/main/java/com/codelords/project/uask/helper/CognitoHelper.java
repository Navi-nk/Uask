package com.codelords.project.uask.helper;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import com.amazonaws.regions.Regions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Navi on 27/5/2017.
 */

public class CognitoHelper {

    private static CognitoHelper cognitoHelper;
    private static CognitoUserPool userPool;
    private static Map<String, String> signUpFields;
    private static String user;

    private static final String userPoolId = "replace_this_with_your_cognito_pool_id";
    private static final String clientId = "replace_this_with_app_client_id";
    private static final String clientSecret = "replace_this_with_the_app_client_secret";


    private static final Regions cognitoRegion = Regions.DEFAULT_REGION;

    private static CognitoUserSession currSession;
    private static CognitoUserDetails userDetails;

    private static CognitoCachingCredentialsProvider credentialsProvider;

    private static CognitoDevice newDevice;

    private static Set<String> currUserAttributes;

    private static String userPoolUrl="cognito-idp."+cognitoRegion.getName()+".amazonaws.com/"+userPoolId;

    public static void init(Context context) {
        setData();
        if (cognitoHelper != null && userPool != null) {
            return;
        }

        if (cognitoHelper == null) {
            cognitoHelper = new CognitoHelper();
        }

        if (userPool == null) {

            // Create a user pool with default ClientConfiguration
            userPool = new CognitoUserPool(context, userPoolId, clientId, clientSecret, cognitoRegion);
        }

        if(credentialsProvider == null) {
            credentialsProvider = new CognitoCachingCredentialsProvider(
                    context, // Context
                    userPoolId, // Identity Pool ID
                    cognitoRegion // Region
            );
        }

        newDevice = null;
        currUserAttributes = new HashSet<String>();

    }

    public static CognitoUserPool getPool() {
        return userPool;
    }


    public static  CognitoUserSession getCurrSession() {
        return currSession;
    }

    public static void setUserDetails(CognitoUserDetails details) {
        userDetails = details;
    }

    public static String getUserPoolUrl() {
        return userPoolUrl;
    }

    public static  CognitoUserDetails getUserDetails() {
        return userDetails;
    }

    public static String getCurrUser() {
        return user;
    }

    public static void setUser(String newUser) {
        user = newUser;
    }

    public static void addCurrUserattribute(String attribute) {
        currUserAttributes.add(attribute);
    }

    public static void setCurrSession(CognitoUserSession session) {
        currSession = session;
    }

    public static void newDevice(CognitoDevice device) {
        newDevice = device;
    }

    public static CognitoDevice getNewDevice() {
        return newDevice;
    }

    public static Map<String, String> getSignUpFields() {
        return signUpFields;
    }

    private static void setData() {
        signUpFields = new HashMap<String, String>();
        signUpFields.put("email","email");
        signUpFields.put("preferred username","preferred_username");
    }
    public static String formatException(Exception exception) {
        String formattedString = "Internal Error";
        Log.e("App Error",exception.toString());
        Log.getStackTraceString(exception);

        String temp = exception.getMessage();

        if(temp != null && temp.length() > 0) {
            formattedString = temp.split("\\(")[0];
            if(temp != null && temp.length() > 0) {
                return formattedString;
            }
        }

        return  formattedString;
    }

    public static void setLogins(Map<String,String> logins) {
        CognitoHelper.credentialsProvider.setLogins(logins);
    }
}

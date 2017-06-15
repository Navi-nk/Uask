package com.codelords.project.uask.helper;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import com.amazonaws.regions.Regions;
import com.amazonaws.util.StringInputStream;

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

    private static final String identityPoolId = "us-east-1:ef4509a3-af9a-40e9-8eff-915dba4cc0e4";
    private static final String userPoolId = "us-east-1_JBDCXi8Kh";
    private static final String clientId = "2cjum8451c8tvfp3mlgi9ndhsf";
    private static final String clientSecret = "1e25fnqghmeckf3p7hsn9r8hspjr92e8q8ljkoinkft7s3tvbmn6";


    private static final Regions cognitoRegion = Regions.US_EAST_1;

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
                    identityPoolId, // Identity Pool ID
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


    public static String getIdentityPoolId() {
        return identityPoolId;
    }

    private static void setData() {
        signUpFields = new HashMap<String, String>();
        signUpFields.put("email","email");
        signUpFields.put("preferred username","preferred_username");
        signUpFields.put("faculty","custom:faculty");
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

    public static void clearToken() {
        CognitoHelper.credentialsProvider.clear();
    }


    public static CognitoCachingCredentialsProvider getCredentialsProvider(){
        return credentialsProvider;
    }

}

package com.codelords.project.uask.helper;

import android.content.Context;
import android.graphics.Color;

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
    private static String user;

    private static final String userPoolId = "replace_this_with_your_cognito_pool_id";
    private static final String clientId = "replace_this_with_app_client_id";
    private static final String clientSecret = "replace_this_with_the_app_client_secret";

    private static final Regions cognitoRegion = Regions.DEFAULT_REGION;

    private static CognitoUserSession currSession;
    private static CognitoUserDetails userDetails;

    private static Set<String> currUserAttributes;


    public static void init(Context context) {

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

}

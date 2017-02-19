package com.ideascontest.navi.uask;
/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.net.Uri;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;


import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

/**
 * These utilities will be used to communicate with the network.
 */
public class NetworkUtils {

    final static String BASE_URL =
            "http://a8c45348.ngrok.io/UaskServiceProvider";

    final static String GET_ALL_QUESTIONS = BASE_URL+"/qfeed/getfeed";
    final static String GET_ALL_ANSWERS = BASE_URL+"/qfeed/getans";
    final static String GET_ALL_QUESTION_FOR_CAT = BASE_URL+"/qfeed/getcatfeed";
    final static String POST_ANSWER = BASE_URL+"/qfeed/ansques";

    final static String PARAM_QUESTION = "question";
    final static String PARAM_CATEGORY="category";
    final  static String PARAM_QUESTION_ID = "questionId";
    final static String PARAM_USER_ID = "userId";
    final static  String ANSWER ="answer";

    /*
     * The sort field. One of stars, forks, or updated.
     * Default: results are sorted by best match if no field is specified.
     */
    /**
     * Builds the URL used to query GitHub.
     *
     * @return The URL to use to query the GitHub.
     */
    public static URL buildUrl(String BaseURL,String param,String paramvalue) {
        Uri builtUri = Uri.parse(BaseURL).buildUpon()
                .appendQueryParameter(param, paramvalue)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    public static URL buildUrlToPostAnswer(String BaseURL,String user_id,String user_value,String question_id,String question_value,String answer,String answer_value) {
        Uri builtUri = Uri.parse(BaseURL).buildUpon()
                .appendQueryParameter(user_id, user_value)
                .appendQueryParameter(question_id, question_value)
                .appendQueryParameter(answer, answer_value)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    public static String putResponseFromHttpUrl(URL url) throws IOException {
        String response="";
        try {
            Log.d("url check",url.toString());
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            OutputStream os = urlConnection.getOutputStream();
        BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(os, "UTF-8"));
        writer.write(urlConnection.toString());

        writer.flush();
        writer.close();
        os.close();
        int responseCode=urlConnection.getResponseCode();
        Log.d("responsecode",String.valueOf(responseCode));

    } catch (Exception e) {
        e.printStackTrace();
    }

    return response;


}
}

package com.codelords.project.uask.helper;

import android.content.Context;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.apigateway.ApiClientFactory;
import com.codelords.uask.apiclientsdk.UAskClient;

import java.util.HashSet;

/**
 * Created by Navi-PC on 14/6/2017.
 */

public class ApiGatewayHelper {

    private static ApiGatewayHelper apiGatewayHelper;
    private static ApiClientFactory apiClientFactory;
    //private static UAskClient apiClient;

    public static void init(CognitoCachingCredentialsProvider credentialsProvider) {
        if (apiGatewayHelper != null) {
            return;
        }

        if (apiGatewayHelper == null) {
            apiGatewayHelper = new ApiGatewayHelper();
        }

            apiClientFactory = new ApiClientFactory()
                    .credentialsProvider(credentialsProvider);

        //apiClient = apiClientFactory.build(UAskClient.class);

        }
    public static ApiClientFactory getApiClientFactory() {
        return apiClientFactory;
    }
/*    public static UAskClient getApiClient() {
        return apiClient;
    }*/


}

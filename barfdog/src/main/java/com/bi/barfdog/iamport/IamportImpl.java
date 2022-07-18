package com.bi.barfdog.iamport;

import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.response.AccessToken;
import com.siot.IamportRestClient.response.IamportResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class IamportImpl implements Iamport{

    private IamportClient client;

    public IamportImpl() {
        this.client = new IamportClient(Iamport_API.API_KEY,Iamport_API.API_SECRET);
    }

    private IamportClient getNaverTestClient() {
        String test_api_key = "5978210787555892";
        String test_api_secret = "9e75ulp4f9Wwj0i8MSHlKFA9PCTcuMYE15Kvr9AHixeCxwKkpsFa7fkWSd9m0711dLxEV7leEAQc6Bxv";

        return new IamportClient(test_api_key, test_api_secret);
    }

    private IamportClient getBillingTestClient() {
        String test_api_key = "7544324362787472";
        String test_api_secret = "9frnPjLAQe3evvAaJl3xLOODfO3yBk7LAy9pRV0H93VEzwPjRSQDHFhWtku5EBRea1E1WEJ6IEKhbAA3";

        return new IamportClient(test_api_key, test_api_secret);
    }


    @Override
    public void getToken() {
        try {
            IamportResponse<AccessToken> auth_response = client.getAuth();

            AccessToken response = auth_response.getResponse();
            String token = response.getToken();
            System.out.println("token = " + token);
        } catch (IamportResponseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

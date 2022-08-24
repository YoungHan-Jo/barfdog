package com.bi.barfdog.goodsFlow;

import com.bi.barfdog.directsend.DirectSend;
import com.bi.barfdog.directsend.DirectSendResponseDto;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.fasterxml.jackson.core.JsonParser.Feature.*;
import static com.fasterxml.jackson.databind.DeserializationConfig.*;

public class GoodsFlowUtils {

//    public static void getTraceResults() throws IOException {
//
//        String baseUrl = GoodsFlow.TEST_URL_API;
//        String apiKey = GoodsFlow.TEST_API_KEY;
//
//        String url = baseUrl + "/traceresults";		// URL
//
//    }


    public static TraceResultResponseDto getTraceResults() {

        String baseUrl = GoodsFlow.TEST_URL_API;
        String apiKey = GoodsFlow.TEST_API_KEY;

        String url = baseUrl + "/orders/traceresults";		// URL

        Map<String, String> requestHeaders = new HashMap<>();
        requestHeaders.put("goodsFLOW-Api-Key", apiKey);
        String responseBody = get(url, requestHeaders);

        System.out.println(responseBody);

        TraceResultResponseDto responseDto = null;

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            responseDto = objectMapper.readValue(responseBody, TraceResultResponseDto.class);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }


        return responseDto;
    }

    private static String get(String apiUrl, Map<String, String> requestHeaders) {
        HttpURLConnection con = connect(apiUrl);
        try {
            con.setRequestMethod("GET");
            for (Map.Entry<String, String> header : requestHeaders.entrySet()) {
                con.setRequestProperty(header.getKey(), header.getValue());
            }

            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) { // 정상 호출
                return readBody(con.getInputStream());
            } else { // 에러 발생
                return readBody(con.getErrorStream());
            }
        } catch (IOException e) {
            throw new RuntimeException("API 요청과 응답 실패", e);
        } finally {
            con.disconnect();
        }
    }


    private static HttpURLConnection connect(String apiUrl) {
        try {
            URL url = new URL(apiUrl);
            return (HttpURLConnection) url.openConnection();
        } catch (MalformedURLException e) {
            throw new RuntimeException("API URL이 잘못되었습니다. : " + apiUrl, e);
        } catch (IOException e) {
            throw new RuntimeException("연결이 실패했습니다. : " + apiUrl, e);
        }
    }


    private static String readBody(InputStream body) {
        InputStreamReader streamReader = new InputStreamReader(body);


        try (BufferedReader lineReader = new BufferedReader(streamReader)) {
            StringBuilder responseBody = new StringBuilder();

            String line;
            while ((line = lineReader.readLine()) != null) {
                responseBody.append(line);
            }

            return responseBody.toString();
        } catch (IOException e) {
            throw new RuntimeException("API 응답을 읽는데 실패했습니다.", e);
        }
    }

    public static void checkTraceResults(List<CheckTraceResultRequestDto> requestDtoList) throws IOException{
        String baseUrl = GoodsFlow.TEST_URL_API;
        String apiKey = GoodsFlow.TEST_API_KEY;

        String data = "";
        String items = "";

        for (CheckTraceResultRequestDto requestDto : requestDtoList) {
            String uniqueCd = requestDto.getUniqueCd();
            String seq = requestDto.getSeq();

            items += ",{\"uniqueCd\": \"" + uniqueCd + "\", " +
                    "\"seq\":\"" + seq + "\"}";
        }

        items = items.substring(1);

        data = "\"items\":["+items+"]";             //필수입력

        data = "{\"data\":{"+data+"}}";      //JSON 데이터
        System.out.println("data = " + data);

        String url = baseUrl + "/orders/check-traceresults";

        URL obj;
        obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestProperty("Cache-Control", "no-cache");
        con.setRequestProperty("Content-Type", "application/json;charset=utf-8");
        con.setRequestProperty("Accept", "application/json");
        con.setRequestProperty("goodsFLOW-Api-Key", apiKey);

        System.setProperty("jsse.enableSNIExtension", "false");
        con.setDoOutput(true);
        OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream(), "UTF-8");
        wr.write(data);
        System.out.println("api 전송 시각 : " + LocalDateTime.now());
        wr.flush();
        wr.close();

        int responseCode = con.getResponseCode();
        System.out.println(responseCode);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }

        in.close();

        System.out.println("결과 : " + response.toString());

    }



}

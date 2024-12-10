package com.team01.billage.map.service;

import com.team01.billage.exception.CustomException;
import com.team01.billage.exception.ErrorCode;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class AddressService {

    private static final String API_URL = "https://naveropenapi.apigw.ntruss.com/map-reversegeocode/v2/gc";


    public String getAddressFromCoordinates(double latitude, double longitude) {
        String clientId = System.getenv("NAVER_CLIENT_ID");
        String clientSecret = System.getenv("NAVER_CLIENT_SECRET");
        String url = API_URL + "?coords=" + longitude + "," + latitude + "&output=json";

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-NCP-APIGW-API-KEY-ID", clientId);
        headers.set("X-NCP-APIGW-API-KEY", clientSecret);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        // JSON 파싱 후 세부 주소 추출
        JSONObject jsonResponse = new JSONObject(response.getBody());
        JSONArray results = jsonResponse.getJSONArray("results");

        if (results.length() > 0) {
            JSONObject region = results.getJSONObject(0).getJSONObject("region");
            String area1 = region.getJSONObject("area1").getString("name"); // 시/도
            String area2 = region.getJSONObject("area2").getString("name"); // 시/군/구
            String area3 = region.getJSONObject("area3").getString("name"); // 읍/면/동
            String area4 = region.getJSONObject("area4").optString("name", ""); // 리/번지 (있을 경우)

            // 세부 주소 조합
            return area1 + " " + area2 + " " + area3 + (area4.isEmpty() ? "" : " " + area4);
        } else {
            throw new CustomException(ErrorCode.ADDRESS_NOT_FOUND);
        }
    }
}

package com.thoth.client;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import java.net.http.HttpResponse;
import java.util.Map;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

public class GatlingTest {

    private static final String token = "Bearer eyJraWQiOiJhZ3JuTStOTzh5ME1idStXZzFudXQraDRpOWNwM1k3ODQ0UElqVEY5MUxFPSIsImFsZyI6IlJTMjU2In0.eyJzdWIiOiIwMmFkNDg0MC02N2E3LTRlNjItOTNkMi0xYjZhNjk4ZWYxNmIiLCJjb2duaXRvOmdyb3VwcyI6WyJldS13ZXN0LTFfVmZrendjUnFKX0d1YWxhQUQtdGhvdGgiXSwiaXNzIjoiaHR0cHM6XC9cL2NvZ25pdG8taWRwLmV1LXdlc3QtMS5hbWF6b25hd3MuY29tXC9ldS13ZXN0LTFfVmZrendjUnFKIiwidmVyc2lvbiI6MiwiY2xpZW50X2lkIjoiM2ZnNmQwdmhxYmFnMzZhZDIxdmdlOHZtaW0iLCJ0b2tlbl91c2UiOiJhY2Nlc3MiLCJzY29wZSI6ImF3cy5jb2duaXRvLnNpZ25pbi51c2VyLmFkbWluIG9wZW5pZCBwcm9maWxlIGVtYWlsIiwiYXV0aF90aW1lIjoxNzA5MzY0MjcyLCJleHAiOjE3MDkzNjc4NzIsImlhdCI6MTcwOTM2NDI3MiwianRpIjoiMGQ3ZjBlMDAtMzVlZi00MTEwLTkyM2ItN2Y5MjNjOTlmNmU1IiwidXNlcm5hbWUiOiJndWFsYWFkLXRob3RoX2dnYWxhenpvQGd1YWxhY2xvc3VyZXMuY29tIn0.Z4dpFdjoLXFIxHDk2U_8KrV1GVAmIOVYE55QeTn_FLJ1GnGLQEQutoOevMXAM0P8d5QSbMZ93GFlSjDfryuLedU0hRIS5wDqDlQsoZ9s9Ch9pxWLNk4F3q9cfQlzFr6ykyk11oETXOwX8sEweX5jfgKjuEKck25TPd1Opt3ZfBgGCelmFq1BxmPSBheiyMLqm0dIJ9V5dsD-vXIJhEKByygtUfmgJ_3FV9iXZxgF9Nh_5vXsdwLRjWdWnuxgAL29_zswyBBp1Vam77Q1kQ7yva3-XCGWbwgYibui3cpdoAJswfPYxlG6Upxp6fq1-nH8YGjmuis3Lis11kmIAAFV8g";
    private static void send() {
        try {
            var t = new RestTemplate();
            var headers = new HttpHeaders();

            headers.put("Content-Type", List.of("application/json"));
            headers.put("Authorization", List.of(token));
            var e = new HttpEntity<>(
                    Map.of("copies", 1,
                            "printService", "PDF Scribe",
                            "clientIdentifier", "788966541250",
                            "parameters", Map.of(
                                    "iris_box_number", "396",
                                    "iris_id_production_order", "4828",
                                    "sap_order_number", ""
                            )), headers);

            var resp = t.exchange(
                    "https://thoth1-be.api.gualaclosures.com/renderer/rndr_e20122b5-f5f8-4fb7-845d-e8f20c5db577/print",
                    HttpMethod.POST,
                    e,
                    HttpResponse.class);
            System.out.println(resp.getStatusCode());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        var requests = 1;
        var p = Executors.newFixedThreadPool(32);
        var s = new Semaphore(0);
        for (int i = 0; i < requests; i++) {
            p.execute(() -> {
                /*
                try {
                    Thread.sleep(new Random().nextInt(5 * 1000, 30*1000));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }*/
                send();
                s.release();
            });
        }
        for (int i = 0; i < requests; i++) {
            s.acquire();
            System.out.println(i);
        }
        System.exit(0);
    }
}

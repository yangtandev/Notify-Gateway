package com.gini.notifyGateway.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gini.notifyGateway.dao.SgsmsRepository;
import com.gini.notifyGateway.exception.RestExceptionHandler;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import net.minidev.json.JSONObject;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping
public class SgsmsController extends RestExceptionHandler {
    public static final Logger logger = LoggerFactory.getLogger(SgsmsController.class);
    @Autowired
    private SgsmsRepository sgsmsRepository;
    @Value("${spring.sms.url}")
    private String SET_SMS_URL;
    @Value("${spring.apikey}")
    private String API_KEY;
    @Value("${spring.hid}")
    private String HID;
    @Value("${spring.apid}")
    private String APID;
    @Value("${spring.sms.source}")
    private String SOURCE;

    @PostMapping("/sendSMS")
    public ResponseEntity sendMail(
        @RequestBody Map<String, String> sendSMSMap
    ) throws Exception {
        Map<String, String> result = new LinkedHashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String sendType = sendSMSMap.get("sendType");
        String content = sendSMSMap.get("content");
        String schtm = sendSMSMap.get("schtm");
        String sendtm = LocalDateTime.now().format(formatter);
        String source = SOURCE;
        List<String> userList = new ArrayList<>();
        JSONArray jsonArray = new JSONArray(sendSMSMap.get("userList"));
        if (jsonArray != null) {
            int len = jsonArray.length();

            for (int i = 0; i < len; i++) {
                userList.add(jsonArray.get(i).toString());
            }
        }
        if (userList.size() > 0) {
            try {
                TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }

                        public void checkClientTrusted(X509Certificate[] certs, String authType) {
                        }

                        public void checkServerTrusted(X509Certificate[] certs, String authType) {
                        }
                    }
                };
                SSLContext sslcontext = SSLContext.getInstance("SSL");
                sslcontext.init(null, trustAllCerts, new java.security.SecureRandom());
                HttpsURLConnection.setDefaultSSLSocketFactory(sslcontext.getSocketFactory());
                SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext);
                CloseableHttpClient httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
                Unirest.setHttpClient(httpclient);
                String url = SET_SMS_URL;
                String apiKey = API_KEY;
                String hid = HID;
                String apid = APID;

                for (String rsvID : userList) {
                    JSONObject object = new JSONObject();
                    object.put("hid", hid);
                    object.put("apid", apid);
                    object.put("sendType", sendType);
                    object.put("sendtm", sendtm);
                    object.put("content", content);
                    object.put("rsvID", rsvID);
                    object.put("schtm", schtm);
                    object.put("source", source);
                    HttpResponse<String> response = Unirest.post(url)
                        .header("Content-Type", "application/json; charset=utf-8")
                        .header("KeyId", apiKey)
                        .body(object.toString())
                        .asString();
                    if (response.getStatus() == 200) {
                        Map<String, String> responseRecord = new LinkedHashMap<>();
                        ObjectMapper objectMapper = new ObjectMapper();
                        Map<String, String> resultMap = objectMapper.readValue(response.getBody(), new TypeReference<Map<String, String>>() {
                        });
                        String id = rsvID;
                        String success = resultMap.get("success");
                        String msg = resultMap.get("msg");
                        LocalDateTime time = LocalDateTime.parse(sendtm, formatter);
                        // API 若呼叫成功，則存入資料庫，並印出回傳值。
//                      sgsmsRepository.save(new Sgsms(id, content, success, msg, time));
                        responseRecord.put("id", id);
                        responseRecord.put("success", success);
                        responseRecord.put("msg", msg);
                        responseRecord.put("time", String.valueOf(time));
                        logger.info("SMS API Response {}", responseRecord);
                    }
                }

                // 若執行成功，則回傳 success。
                result.put("httpStatusCode", "200");
                result.put("status", "success");
                return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(result);
            } catch (Exception e) {
                result.put("httpStatusCode", "500");
                result.put("status", "fail");
                result.put("message", String.valueOf(e));
                return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(result);
            }
        } else {
            // 若無法取得任何 user 資訊，則回傳 fail。
            result.put("httpStatusCode", "500");
            result.put("status", "fail");
            result.put("message", "unable to get user list");
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(result);
        }
    }
}

package com.gini.notifyGateway.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gini.notifyGateway.exception.RestExceptionHandler;
import com.gini.notifyGateway.model.CalendarRequest;
import com.gini.notifyGateway.utils.CalendarService;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import net.minidev.json.JSONObject;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;
import java.util.*;

@RestController
@RequestMapping
public class MailController extends RestExceptionHandler {
    public static final Logger logger = LoggerFactory.getLogger(MailController.class);
    @Value("${spring.mail.url}")
    private String GET_MAIL_URL;
    @Value("${spring.apikey}")
    private String API_KEY;
    @Value("${spring.hid}")
    private String HID;
    @Value("${spring.apid}")
    private String APID;
    @Value("${spring.mail.host}")
    private String HOST;
    @Value("${spring.mail.port}")
    private int PORT;
    @Value("${spring.mail.fromEmail}")
    private String FROM_EMAIL;
    @Value("${spring.mail.organizer}")
    private String ORGANIZER;
    @Value("${spring.mail.addEvent}")
    private String ADD_EVENT;
    @Value("${spring.mail.updateEvent}")
    private String UPDATE_EVENT;
    @Value("${spring.mail.cancelEvent}")
    private String CANCEL_EVENT;
//    @Value("${spring.mail.username}")
//    private String USERNAME;
//    @Value("${spring.mail.password}")
//    private String PASSWORD;


    public List<Map<String,String>> getMailList(List<String> userList) {
        List<Map<String, String>> mailList = new ArrayList<>();
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
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
            String url = GET_MAIL_URL;
//            String url = "http://localhost:8080/sgs/notifyGateway/getEREmp";
//            String apiKey = API_KEY;
            String apiKey = "7d08ee30-acf1-4a6b-99d6-7596c7a45fb6";
            String hid = HID;
            String apid = APID;
            JSONObject object = new JSONObject();
            object.put("hid", hid);
            object.put("apid", apid);
            HttpResponse<String> response = Unirest.post(url)
                .header("Content-Type", "application/json; charset=utf-8")
                .header("KeyId", apiKey)
                .body(object.toString())
                .asString();
            if (response.getStatus() == 200) {
                ObjectMapper objectMapper = new ObjectMapper();
                Map<String, Object> result = objectMapper.readValue(response.getBody(), new TypeReference<Map<String, Object>>() {
                });
                if (result.get("success").equals("Y")) {
                    List<Map<String, String>> resultList = (List<Map<String, String>>) result.get("resultList");
                    if (resultList.size() > 0) {
                        for (Map<String, String> resultMap : resultList) {
                            Boolean available = userList.contains(resultMap.get("USERID").trim());
                            if (available) {
                                String userid = resultMap.get("USERID").trim();
                                String email = resultMap.get("EMAIL").trim();
                                Map<String, String> mailMap = new HashMap<>();
                                mailMap.put(userid, email);
                                mailList.add(mailMap);
                            }
                        }
                        return mailList;
                    }
                }
            }
        } catch (Exception e) {
            logger.error("UnknownHostException", e);
        }
        return mailList;
    }

    @PostMapping("/sendMail")
    public ResponseEntity sendMail(@RequestBody Map<String, String> sendMailMap) throws Exception {
        Map<String, String> result = new LinkedHashMap<>();
        List<String> userList = new ArrayList<>();
        JSONArray jsonArray = new JSONArray(sendMailMap.get("userList"));
        if (jsonArray != null) {
            int len = jsonArray.length();
            for (int i=0;i<len;i++){
                userList.add(jsonArray.get(i).toString());
            }
        }
        String content = "";
        String method = "";
        switch(sendMailMap.get("method").toUpperCase()){
            case "ADD" :
                content = ADD_EVENT;
                method = "REQUEST";
                break;
            case "UPDATE" :
                content = UPDATE_EVENT;
                method = "REQUEST";
                break;
            case "CANCEL" :
                content = CANCEL_EVENT;
                method = "CANCEL";
                break;
        }
        List<Map<String,String>> mailList = getMailList(userList);
        if (mailList.size() > 0) {
            JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
            mailSender.setHost(HOST);
            mailSender.setPort(PORT);
//            mailSender.setUsername(USERNAME);
//            mailSender.setPassword(PASSWORD);
            mailSender.setProtocol("smtp");
            CalendarService calendarService = new CalendarService(mailSender);
            try {
//                for (Map<String, String> mailMap : mailList) {
//                    String userid = "";
//                    String mail = "";
//                    for ( Map.Entry<String, String> entry : mailMap.entrySet()) {
//                        userid = entry.getKey();
//                        mail = entry.getValue();
//                    }
//                    String body = userid + " 您好, " + content;
//                    calendarService.sendCalendarInvite(
//                        FROM_EMAIL,
//                        new CalendarRequest.Builder()
//                            .withStartTime(sendMailMap.get("startTime"))
//                            .withEndTime(sendMailMap.get("endTime"))
//                            .withToEmail(mail)
//                            .withUid(sendMailMap.get("uid"))
//                            .withLocation(sendMailMap.get("location"))
//                            .withSubject(sendMailMap.get("subject"))
//                            .withBody(body)
//                            .withMethod(method)
//                            .build(),
//                        ORGANIZER
//                    );
//                }
                String body = "Yang 您好, " + content;
                calendarService.sendCalendarInvite(
                    FROM_EMAIL,
                    new CalendarRequest.Builder()
                        .withStartTime(sendMailMap.get("startTime"))
                        .withEndTime(sendMailMap.get("endTime"))
                        .withToEmail("jack5542856@gmail.com")
                        .withUid(sendMailMap.get("uid"))
                        .withLocation(sendMailMap.get("location"))
                        .withSubject(sendMailMap.get("subject"))
                        .withBody(body)
                        .withMethod(method)
                        .build(),
                    ORGANIZER
                );
                // 若執行成功，則回傳 success。
                result.put("httpStatusCode", "200");
                result.put("status", "success");
                result.put("massage", "sent email list: " + mailList);
                logger.info("Mail API Response {}", result);
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
            // 若反查不到任何 mail 資訊，則回傳 fail。
            result.put("httpStatusCode", "500");
            result.put("status", "fail");
            result.put("message", "unable to get mail list");
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(result);
        }
    }

//    @PostMapping("/getEREmp")
//    public ResponseEntity getEREmp() {
//        Map<String, Object> response = new LinkedHashMap<>();
//        List<Map<String, String>> resultList = new ArrayList<>();
//        Map<String, String> result = new LinkedHashMap<>();
//        result.put("GSM", "78340");
//        result.put("OU_NAME", "急診部");
//        result.put("WKNAME", "主任");
//        result.put("PSWKTYP", "Z");
//        result.put("USERID", "Q996");
//        result.put("USERNAME", "黃豐締");
//        result.put("COSTID", "480");
//        result.put("EMAIL", "yang.tan@gini.tw");
//        result.put("EMPNAME", "黃豐締");
//
//        Map<String, String> result2 = new LinkedHashMap<>();
//        result2.put("GSM", "78342");
//        result2.put("OU_NAME", "急診部");
//        result2.put("WKNAME", "契約行政助理");
//        result2.put("PSWKTYP", "A");
//        result2.put("USERID", "T058");
//        result2.put("USERNAME", "蔡水春");
//        result2.put("COSTID", "480");
//        result2.put("EMAIL", "jack5542856@gmail.com");
//        result2.put("EMPNAME", "蔡水春");
//
//        resultList.add(result);
//        resultList.add(result2);
//
//        response.put("success", "Y");
//        response.put("msg", "查詢成功");
//        response.put("resultList", resultList);
//        return ResponseEntity
//            .status(HttpStatus.OK)
//            .body(response);
//    }
}

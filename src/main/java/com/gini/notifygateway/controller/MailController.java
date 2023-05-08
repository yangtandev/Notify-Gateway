package com.gini.notifygateway.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gini.notifygateway.exception.RestExceptionHandler;
import com.gini.notifygateway.model.CalendarRequest;
import com.gini.notifygateway.service.CalendarService;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import net.minidev.json.JSONObject;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
@RestController
@RequestMapping
public class MailController extends RestExceptionHandler {
    public static final Logger logger = LoggerFactory.getLogger(MailController.class);
    public static List<Map<String, String>> mailList = new ArrayList<>();
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
    @Value("${spring.mail.protocol}")
    private String PROTOCOL;
    @Value("${spring.mail.organizer}")
    private String ORGANIZER;
    @Value("${spring.mail.costids}")
    private String[] costids;
    @Value("${spring.mail.addEvent}")
    private String ADD_EVENT;
    @Value("${spring.mail.updateEvent}")
    private String UPDATE_EVENT;
    @Value("${spring.mail.cancelEvent}")
    private String CANCEL_EVENT;
    // 如為需通過 Auth 驗證才能寄信的信箱，則需提供帳號、密碼。
//    @Value("${spring.mail.username}")
//    private String USERNAME;
//    @Value("${spring.mail.password}")
//    private String PASSWORD;
    // 測試收發用信箱。
    @Value("${spring.mail.testMail1}")
    private String TESTMAIL1;
    @Value("${spring.mail.testMail2}")
    private String TESTMAIL2;

    // 每小時自動更新 mailList。
    @Scheduled(cron = "0 0 * * * ?", zone = "Asia/Taipei")
    public void getMailList() {
        mailList.clear();
        try {
            SSLContext sslContext = new SSLContextBuilder()
                .loadTrustMaterial(null, (certificate, authType) -> true).build();
            CloseableHttpClient httpclient = HttpClients.custom().setSSLContext(sslContext)
                .setSSLHostnameVerifier(new NoopHostnameVerifier())
                .build();
            Unirest.setHttpClient(httpclient);
            String url = GET_MAIL_URL;
//            String apiKey = API_KEY;
//            String url = "http://localhost:8080/sgs/notifyGateway/getEREmp";
            String apiKey = "7d08ee30-acf1-4a6b-99d6-7596c7a45fb6";
            String hid = HID;
            String apid = APID;
            for (String costid : costids) {
                JSONObject object = new JSONObject();
                object.put("hid", hid);
                object.put("apid", apid);
                object.put("costid", costid);
                HttpResponse<String> response = Unirest.post(url)
                    .header("Content-Type", "application/json; charset=utf-8")
                    .header("KeyId", apiKey)
                    .body(object.toString())
                    .asString();
                if (response.getStatus() == 200) {
                    logger.info("certificate status OK!");
                    ObjectMapper objectMapper = new ObjectMapper();
                    Map<String, Object> result = objectMapper.readValue(response.getBody(), new TypeReference<Map<String, Object>>() {
                    });
                    logger.info("result {}", result);
                    if (result.get("success").equals("Y")) {
                        logger.info("certificate result OK!");
                        List<Map<String, String>> resultList = (List<Map<String, String>>) result.get("resultList");
                        logger.info("resultList {}", resultList);
                        if (resultList.size() > 0) {
                            for (Map<String, String> resultMap : resultList) {
                                String userid = resultMap.get("USERID").trim();
                                String email = resultMap.get("EMAIL").trim();
                                if (!(userid.equals("") || email.equals(""))) {
                                    Map<String, String> mailMap = new HashMap<>();
                                    mailMap.put(userid, email);
                                    mailList.add(mailMap);
                                }
                            }
                        }
                    }
                }
            }
            logger.info("updateTime {}", LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
            logger.info("mailList {}", mailList);
        } catch (Exception e) {
            logger.error("UnknownHostException", e);
        }
    }

    @PostMapping("/sendMail")
    public ResponseEntity sendMail(@RequestBody Map<String, Object> sendMailMap) throws Exception {
        Map<String, String> result = new LinkedHashMap<>();
        List<String> userList = new ArrayList<>();
        JSONArray jsonArray = new JSONArray(sendMailMap.get("userList").toString());
        if (jsonArray != null) {
            int len = jsonArray.length();
            for (int i = 0; i < len; i++) {
                userList.add(jsonArray.get(i).toString());
            }
        }

        String content = "";
        String method = "";

        switch (sendMailMap.get("method").toString().toUpperCase(Locale.ENGLISH)) {
            case "ADD":
                content = ADD_EVENT;
                method = "REQUEST";
                break;
            case "UPDATE":
                content = UPDATE_EVENT;
                method = "REQUEST";
                break;
            case "CANCEL":
                content = CANCEL_EVENT;
                method = "CANCEL";
                break;
        }

        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(HOST);
        mailSender.setPort(PORT);
//        mailSender.setUsername(USERNAME);
//        mailSender.setPassword(PASSWORD);
        mailSender.setProtocol(PROTOCOL);
        CalendarService calendarService = new CalendarService(mailSender);

        // 信箱測試，無論是否反查到 mailList，皆寄出信。
        for (String mail : new String[]{TESTMAIL1, TESTMAIL2}) {
            calendarService.sendCalendarInvite(
                FROM_EMAIL,
                new CalendarRequest.Builder()
                    .withStartTime(sendMailMap.get("startTime").toString())
                    .withEndTime(sendMailMap.get("endTime").toString())
                    .withToEmail(mail)
                    .withUid(sendMailMap.get("uid").toString())
                    .withLocation(sendMailMap.get("location").toString())
                    .withSubject(sendMailMap.get("subject").toString())
                    .withBody("您好, " + content)
                    .withMethod(method)
                    .build(),
                ORGANIZER
            );
        }

        // 如果 mailList 無值，則重新獲取 mail 資訊。
        if (mailList.size() == 0) {
            getMailList();
        }
        if (mailList.size() > 0) {
            List<Map<String, String>> sentMailList = new ArrayList<>();
            try {
                for (Map<String, String> mailMap : mailList) {
                    String userid = "";
                    String mail = "";

                    for (Map.Entry<String, String> entry : mailMap.entrySet()) {
                        userid = entry.getKey();
                        mail = entry.getValue();
                    }

                    if (userList.contains(userid)) {
                        sentMailList.add(mailMap);
//                        calendarService.sendCalendarInvite(
//                            FROM_EMAIL,
//                            new CalendarRequest.Builder()
//                                .withStartTime(sendMailMap.get("startTime").toString())
//                                .withEndTime(sendMailMap.get("endTime").toString())
//                                .withToEmail(mail)
//                                .withUid(sendMailMap.get("uid").toString())
//                                .withLocation(sendMailMap.get("location").toString())
//                                .withSubject(sendMailMap.get("subject").toString())
//                                .withBody(userid + " 您好, " + content)
//                                .withMethod(method)
//                                .build(),
//                            ORGANIZER
//                        );
                    }
                }

                // 若執行成功，則回傳 success。
                result.put("httpStatusCode", "200");
                result.put("status", "success");
                result.put("massage", "sent email list: " + sentMailList);
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

    // 模擬院方"查詢人員名單"API，替換以下資料中 EMAIL 欄位的測試信箱，即可執行收發。
//    @PostMapping("/getEREmp")
//    public ResponseEntity getEREmp(@RequestBody Map<String, String> EREmpMap) {
//        String costid = EREmpMap.get("costid");
//        Map<String, Object> response = new LinkedHashMap<>();
//        List<Map<String, String>> resultList = new ArrayList<>();
//        Map<String, String> result = new LinkedHashMap<>();
//        switch (costid) {
//            case "301":
//                result.put("USERID", "P075");
//                result.put("EMAIL", TESTMAIL1);
//                break;
//            case "302":
//                result.put("USERID", "P432");
//                result.put("EMAIL", TESTMAIL1);
//                break;
//            case "303":
//                result.put("USERID", "P038");
//                result.put("EMAIL", TESTMAIL1);
//                break;
//            case "304":
//                result.put("USERID", "P295");
//                result.put("EMAIL", TESTMAIL1);
//                break;
//            case "306":
//                result.put("USERID", "P207");
//                result.put("EMAIL", TESTMAIL1);
//                break;
//            case "307":
//                result.put("USERID", "P459");
//                result.put("EMAIL", TESTMAIL1);
//                break;
//            case "308":
//                result.put("USERID", "P424");
//                result.put("EMAIL", TESTMAIL1);
//                break;
////            case "309":
////                break;
////            case "310":
////                break;
////            case "311":
////                break;
//            case "480":
//                result.put("USERID", "P279");
//                result.put("EMAIL", "");
//                break;
//            case "481":
//                result.put("USERID", "P455");
//                result.put("EMAIL", "");
//                break;
////            case "482":
////                break;
//            case "7E0":
//                result.put("USERID", "P045");
//                result.put("EMAIL", "");
//                break;
//            default:
//                return ResponseEntity
//                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(response);
//        }
//
//        resultList.add(result);
//
//        response.put("success", "Y");
//        response.put("msg", "查詢成功");
//        response.put("resultList", resultList);
//        return ResponseEntity
//            .status(HttpStatus.OK)
//            .body(response);
//    }
}

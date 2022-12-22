package com.gini.notifyGateway.model;

import javax.persistence.*;
import java.time.LocalDateTime;

// 設定訊息發送 API 回傳值紀錄
@Entity
@IdClass(SgsmsId.class)
public class Sgsms {
    // 使用者卡號或電話
    @Id
    @Column(columnDefinition = "CHAR(10) NOT NULL WITH DEFAULT")
    private String id;
    
    // 醫院代碼
    @Id
    @Column(columnDefinition = "CHAR(003) NOT NULL CHECK (HID NOT IN ('   '))")
    private String hid = "2A0";
    
    // 簡訊內容
    @Column(columnDefinition = "CHAR(210) NOT NULL WITH DEFAULT")
    private String content;

    // API 呼叫狀態
    @Column(columnDefinition = "CHAR(1) NOT NULL WITH DEFAULT")
    private String success;

    // API 回傳訊息
    @Column(columnDefinition = "CHAR(040) NOT NULL WITH DEFAULT")
    private String msg;

    // API 呼叫時間
    @Id
    @Column(columnDefinition = "TIMESTAMP NOT NULL WITH DEFAULT")
    private LocalDateTime time;

    public Sgsms() {
    }

    public Sgsms(
        String id,
        String content,
        String success,
        String msg,
        LocalDateTime time
    ) {
        this.id = id;
        this.content = content;
        this.success = success;
        this.msg = msg;
        this.time = time;
    }

    // ************************************************************************
    // Getters and setters
    // ************************************************************************

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHid() {
        return hid;
    }

    public void setHid(String hid) {
        this.hid = hid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }
}
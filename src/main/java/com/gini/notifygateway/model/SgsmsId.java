package com.gini.notifygateway.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;


public class SgsmsId implements Serializable {
    private String id;
    private String hid;
    private LocalDateTime time;

    public SgsmsId() {
    }

    public SgsmsId(String id, String hid, LocalDateTime time) {
        this.id = id;
        this.hid = hid;
        this.time = time;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SgsmsId sgsmsId = (SgsmsId) o;
        return id.equals(sgsmsId.id) &&
            hid.equals(sgsmsId.hid) &&
            time.equals(sgsmsId.time);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, hid, time);
    }
}

package com.so.luotk.models.output;

import lombok.Data;

@Data
public class Result {
    private String user_id;
    private String otp;
    //for json parsing
   // private String days_time;


    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }
}

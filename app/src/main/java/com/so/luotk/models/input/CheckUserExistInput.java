package com.so.luotk.models.input;

import lombok.Data;

@Data
public class CheckUserExistInput {
    private String phone_number;
    private String orgCode;

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }
}

package com.so.luotk.models.output;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class DurationType {
    @SerializedName("1")
    @Expose
    private String _1;
    @SerializedName("2")
    @Expose
    private String _2;
    @SerializedName("3")
    @Expose
    private String _3;

    public String get_1() {
        return _1;
    }

    public void set_1(String _1) {
        this._1 = _1;
    }

    public String get_2() {
        return _2;
    }

    public void set_2(String _2) {
        this._2 = _2;
    }

    public String get_3() {
        return _3;
    }

    public void set_3(String _3) {
        this._3 = _3;
    }
}

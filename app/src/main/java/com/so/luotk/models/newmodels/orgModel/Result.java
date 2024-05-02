
package com.so.luotk.models.newmodels.orgModel;


import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.BindingAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.so.luotk.R;

public class Result extends BaseObservable {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("website")
    @Expose
    private String website;
    @SerializedName("owner")
    @Expose
    private Object owner;
    @SerializedName("start_date")
    @Expose
    private String startDate;
    @SerializedName("end_date")
    @Expose
    private String endDate;
    @SerializedName("planId")
    @Expose
    private Object planId;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("orgCode")
    @Expose
    private String orgCode;
    @SerializedName("logo")
    @Expose
    private String logo;
    @SerializedName("isActive")
    @Expose
    private Integer isActive;
    @SerializedName("latitude")
    @Expose
    private Object latitude;
    @SerializedName("longitude")
    @Expose
    private Object longitude;
    @SerializedName("comment")
    @Expose
    private String comment;
    @SerializedName("city")
    @Expose
    private Object city;
    @SerializedName("state")
    @Expose
    private Object state;
    @SerializedName("pincode")
    @Expose
    private Object pincode;
    @SerializedName("contactName")
    @Expose
    private String contactName;
    @SerializedName("contactEmail")
    @Expose
    private String contactEmail;
    @SerializedName("fk_userId")
    @Expose
    private Integer fkUserId;
    @SerializedName("facebook")
    @Expose
    private String facebook;
    @SerializedName("instagram")
    @Expose
    private String instagram;
    @SerializedName("fcm_key")
    @Expose
    private String fcmKey;
    @SerializedName("live_option")
    @Expose
    private Integer liveOption;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("deleted_at")
    @Expose
    private Object deletedAt;
    @SerializedName("zoomUserId")
    @Expose
    private String zoomUserId;
    @SerializedName("zoomSdkSecret")
    @Expose
    private String zoomSdkSecret;
    @SerializedName("zoomSdkKey")
    @Expose
    private String zoomSdkKey;
    @SerializedName("zoomAppSecret")
    @Expose
    private String zoomAppSecret;
    @SerializedName("zoomAppKey")
    @Expose
    private String zoomAppKey;
    @SerializedName("israzorpayactivated")
    @Expose
    private Integer israzorpayactivated;
    @SerializedName("razorpaytoken")
    @Expose
    private Object razorpaytoken;
    @SerializedName("feeStucturephoto")
    @Expose
    private String feeStucturephoto;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Bindable
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public Object getOwner() {
        return owner;
    }

    public void setOwner(Object owner) {
        this.owner = owner;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public Object getPlanId() {
        return planId;
    }

    public void setPlanId(Object planId) {
        this.planId = planId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    @Bindable
    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public Integer getIsActive() {
        return isActive;
    }

    public void setIsActive(Integer isActive) {
        this.isActive = isActive;
    }

    public Object getLatitude() {
        return latitude;
    }

    public void setLatitude(Object latitude) {
        this.latitude = latitude;
    }

    public Object getLongitude() {
        return longitude;
    }

    public void setLongitude(Object longitude) {
        this.longitude = longitude;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Object getCity() {
        return city;
    }

    public void setCity(Object city) {
        this.city = city;
    }

    public Object getState() {
        return state;
    }

    public void setState(Object state) {
        this.state = state;
    }

    public Object getPincode() {
        return pincode;
    }

    public void setPincode(Object pincode) {
        this.pincode = pincode;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public Integer getFkUserId() {
        return fkUserId;
    }

    public void setFkUserId(Integer fkUserId) {
        this.fkUserId = fkUserId;
    }

    public String getFacebook() {
        return facebook;
    }

    public void setFacebook(String facebook) {
        this.facebook = facebook;
    }

    public String getInstagram() {
        return instagram;
    }

    public void setInstagram(String instagram) {
        this.instagram = instagram;
    }

    public String getFcmKey() {
        return fcmKey;
    }

    public void setFcmKey(String fcmKey) {
        this.fcmKey = fcmKey;
    }

    public Integer getLiveOption() {
        return liveOption;
    }

    public void setLiveOption(Integer liveOption) {
        this.liveOption = liveOption;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Object getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Object deletedAt) {
        this.deletedAt = deletedAt;
    }

    public String getZoomUserId() {
        return zoomUserId;
    }

    public void setZoomUserId(String zoomUserId) {
        this.zoomUserId = zoomUserId;
    }

    public String getZoomSdkSecret() {
        return zoomSdkSecret;
    }

    public void setZoomSdkSecret(String zoomSdkSecret) {
        this.zoomSdkSecret = zoomSdkSecret;
    }

    public String getZoomSdkKey() {
        return zoomSdkKey;
    }

    public void setZoomSdkKey(String zoomSdkKey) {
        this.zoomSdkKey = zoomSdkKey;
    }

    public String getZoomAppSecret() {
        return zoomAppSecret;
    }

    public void setZoomAppSecret(String zoomAppSecret) {
        this.zoomAppSecret = zoomAppSecret;
    }

    public String getZoomAppKey() {
        return zoomAppKey;
    }

    public void setZoomAppKey(String zoomAppKey) {
        this.zoomAppKey = zoomAppKey;
    }

    public Integer getIsrazorpayactivated() {
        return israzorpayactivated;
    }

    public void setIsrazorpayactivated(Integer israzorpayactivated) {
        this.israzorpayactivated = israzorpayactivated;
    }

    public Object getRazorpaytoken() {
        return razorpaytoken;
    }

    public void setRazorpaytoken(Object razorpaytoken) {
        this.razorpaytoken = razorpaytoken;
    }

    public String getFeeStucturephoto() {
        return feeStucturephoto;
    }

    public void setFeeStucturephoto(String feeStucturephoto) {
        this.feeStucturephoto = feeStucturephoto;
    }

    @BindingAdapter("orgLogo")
    public static void orgImage(ImageView imageView, String url) {
        Glide.with(imageView.getContext()).load(url)
                .error(imageView.getContext().getResources().getDrawable(R.mipmap.ic_launcher))
                .centerInside()
                .into(imageView);
    }

    @BindingAdapter("hideText")
    public static void hideText(View view, boolean flag) {
        view.setVisibility(flag ? View.VISIBLE : View.GONE);
    }

    @BindingAdapter("hideItem")
    public static void hideItem(View view, boolean flag) {
        if (flag) {
            view.setVisibility(View.VISIBLE);
            view.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        } else {
            view.setVisibility(View.GONE);
            view.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
        }
    }

    @BindingAdapter("invisibleView")
    public static void inView(View view, Boolean hide) {
        if (hide)
            view.setVisibility(View.VISIBLE);
        else
            view.setVisibility(View.INVISIBLE);
    }


    @BindingAdapter("webView")
    public static void webView(WebView view, String data) {
        if (!TextUtils.isEmpty(data)) {
            String color = Integer.toHexString(ContextCompat.getColor(view.getContext(), R.color.black));
            String htmlData;
            Log.e("TAG", "webView: "+color );
            if (color.equalsIgnoreCase("DFFFFFFF"))
                htmlData = "<font color='white'>" + data + "</font>";
            else
                htmlData = "<font color='black'>" + data + "</font>";
            view.loadData(htmlData, "text/html", "UTF-8");
            view.setBackgroundColor(Color.TRANSPARENT);
            view.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);
        }
    }
}

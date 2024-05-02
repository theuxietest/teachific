package com.so.luotk.models.input;

import lombok.Data;

@Data
public class  LoginSignupInput {
   private String phone_number;
   private String  device_id;
   private String device_type;
   private String name;
   private String orgCode;
   private String email;

   public String getPhone_number() {
      return phone_number;
   }

   public void setPhone_number(String phone_number) {
      this.phone_number = phone_number;
   }

   public String getDevice_id() {
      return device_id;
   }

   public void setDevice_id(String device_id) {
      this.device_id = device_id;
   }

   public String getDevice_type() {
      return device_type;
   }

   public void setDevice_type(String device_type) {
      this.device_type = device_type;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getOrgCode() {
      return orgCode;
   }

   public void setOrgCode(String orgCode) {
      this.orgCode = orgCode;
   }

   public String getEmail() {
      return email;
   }

   public void setEmail(String email) {
      this.email = email;
   }
}

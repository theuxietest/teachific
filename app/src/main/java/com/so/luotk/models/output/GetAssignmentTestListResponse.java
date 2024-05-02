package com.so.luotk.models.output;

import lombok.Data;

@Data
public class GetAssignmentTestListResponse {
  //  private ArrayList<GetAssignmentTestListResult> result;
    private GetAssignmentTestListResult result;
    private String success;
    private String status;

  public GetAssignmentTestListResult getResult() {
    return result;
  }

  public void setResult(GetAssignmentTestListResult result) {
    this.result = result;
  }

  public String getSuccess() {
    return success;
  }

  public void setSuccess(String success) {
    this.success = success;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }
}

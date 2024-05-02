package com.so.luotk.models.output;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import lombok.Data;

@Data
public class StudentDetails {


        @SerializedName("current_page")
        @Expose
        public Integer currentPage;
        @SerializedName("data")
        @Expose
        public List<StudentDetailsData> data = null;
        @SerializedName("first_page_url")
        @Expose
        public String firstPageUrl;
        @SerializedName("from")
        @Expose
        public Integer from;
        @SerializedName("last_page")
        @Expose
        public Integer lastPage;
        @SerializedName("last_page_url")
        @Expose
        public String lastPageUrl;
        @SerializedName("next_page_url")
        @Expose
        public Object nextPageUrl;
        @SerializedName("path")
        @Expose
        public String path;
        @SerializedName("per_page")
        @Expose
        public String perPage;
        @SerializedName("prev_page_url")
        @Expose
        public Object prevPageUrl;
        @SerializedName("to")
        @Expose
        public Integer to;
        @SerializedName("total")
        @Expose
        public Integer total;


        public Integer getCurrentPage() {
                return currentPage;
        }

        public void setCurrentPage(Integer currentPage) {
                this.currentPage = currentPage;
        }

        public List<StudentDetailsData> getData() {
                return data;
        }

        public void setData(List<StudentDetailsData> data) {
                this.data = data;
        }

        public String getFirstPageUrl() {
                return firstPageUrl;
        }

        public void setFirstPageUrl(String firstPageUrl) {
                this.firstPageUrl = firstPageUrl;
        }

        public Integer getFrom() {
                return from;
        }

        public void setFrom(Integer from) {
                this.from = from;
        }

        public Integer getLastPage() {
                return lastPage;
        }

        public void setLastPage(Integer lastPage) {
                this.lastPage = lastPage;
        }

        public String getLastPageUrl() {
                return lastPageUrl;
        }

        public void setLastPageUrl(String lastPageUrl) {
                this.lastPageUrl = lastPageUrl;
        }

        public Object getNextPageUrl() {
                return nextPageUrl;
        }

        public void setNextPageUrl(Object nextPageUrl) {
                this.nextPageUrl = nextPageUrl;
        }

        public String getPath() {
                return path;
        }

        public void setPath(String path) {
                this.path = path;
        }

        public String getPerPage() {
                return perPage;
        }

        public void setPerPage(String perPage) {
                this.perPage = perPage;
        }

        public Object getPrevPageUrl() {
                return prevPageUrl;
        }

        public void setPrevPageUrl(Object prevPageUrl) {
                this.prevPageUrl = prevPageUrl;
        }

        public Integer getTo() {
                return to;
        }

        public void setTo(Integer to) {
                this.to = to;
        }

        public Integer getTotal() {
                return total;
        }

        public void setTotal(Integer total) {
                this.total = total;
        }
}

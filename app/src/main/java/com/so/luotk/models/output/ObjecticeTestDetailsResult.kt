package com.so.luotk.models.output

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


data class ObjecticeTestDetailsResult(
        @SerializedName("id")
        @Expose
        var id: Int? = null,

        @SerializedName("topic")
        @Expose
        var topic: String? = null,

        @SerializedName("test_type")
        @Expose
        var testType: Int? = null,

        @SerializedName("fk_bulkTestId")
        @Expose
        var fkBulkTestId: Int? = null,

        @SerializedName("submitDate")
        @Expose
        var submitDate: String? = null,

        @SerializedName("submitTime")
        @Expose
        var submitTime: String? = null,

        @SerializedName("startDate")
        @Expose
        var startDate: String? = null,

        @SerializedName("startTime")
        @Expose
        var startTime: String? = null,

        @SerializedName("attachment")
        @Expose
        var attachment: Any? = null,

        @SerializedName("answer")
        @Expose
        var answer: Any? = null,

        @SerializedName("notes")
        @Expose
        var notes: Any? = null,

        @SerializedName("isSms")
        @Expose
        var isSms: Int? = null,

        @SerializedName("isReminder")
        @Expose
        var isReminder: Int? = null,

        @SerializedName("show_test_result")
        @Expose
        var showTestResult: Int? = null,

        @SerializedName("when_to_display_result")
        @Expose
        var whenToDisplayResult: String? = null,

        @SerializedName("test_result_display_date")
        @Expose
        var testResultDisplayDate: String? = null,

        @SerializedName("test_result_display_time")
        @Expose
        var testResultDisplayTime: String? = null,

        @SerializedName("fk_batchId")
        @Expose
        var fkBatchId: Int? = null,

        @SerializedName("fk_courseId")
        @Expose
        var fkCourseId: Int? = null,

        @SerializedName("fk_createdBy")
        @Expose
        var fkCreatedBy: Int? = null,

        @SerializedName("created_at")
        @Expose
        var createdAt: String? = null,

        @SerializedName("updated_at")
        @Expose
        var updatedAt: String? = null,

        @SerializedName("deleted_at")
        @Expose
        var deletedAt: Any? = null,

        @SerializedName("duration")
        @Expose
        var duration: String = " ",

        @SerializedName("totalMarks")
        @Expose
        var totalMarks: Double? = null,

        @SerializedName("totalQuestion")
        @Expose
        var totalQuestion: Int? = null,

        @SerializedName("is_locked")
        @Expose
        var isLocked: Int? = null,

        @SerializedName("sections")
        @Expose
        var sections: List<TestSection>? = null,

        @SerializedName("testStatus")
        @Expose
        var testStatus: Int? = null
)
package datatypes;

import com.fasterxml.jackson.databind.JsonNode;
import play.libs.Json;

import java.util.HashMap;
import java.util.Map;

public enum Error {
    // Security Errors
    NOT_ENOUGH_PARAMETERS(10001, "參數不足"),
    PROCESS_CODE_ERROR(10002, "無效ProcessCode"),
    USER_NOT_FOUND(10003, "使用者不存在"),

    TYPE_ERROR(10004, "型態參數錯誤"),
    STATUS_ERROR(10005, "狀態參數錯誤"),
    HEADER_ERROR(10006, "Header參數錯誤"),
    EXHIBITION_NOT_FOUND(10007, "展會不存在"),
    ACTIVITY_NOT_FOUND(10008, "活動不存在"),
    MEMBER_NOT_FOUND(10009, "會員不存在"),
    COMPANY_NOT_FOUND(10010, "機構不存在"),
    COMPANY_BAN_FORMAT_ERROR(10011, "統一編號格式錯誤"),
    LOG_TYPE_NOT_FOUND(10012, "Log類型不存在"),

    API_VERIFY_ERROR(50001, "API_KEY驗證失敗"),
    VERIFY_ERROR(50002, "檢驗碼驗證失敗"),
    RECORD_NOT_FOUND(50003, "紀錄不存在"),
    INVALID_PARAMETER(50004, "參數錯誤"),

    SYSTEM_EXCEPTION(90000, "系統執行錯誤"),
    ;

    private final int code;
    private final String description;

    Error(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public int getCode() {
        return code;
    }

    @Override
    public String toString() {
        return String.format("Error %d => %s", code, description);
    }

    public Map<String, Object> toErrorMap() {
        return toErrorMap(null);
    }

    public Map<String, Object> toErrorMap(String detail) {
        Map<String, Object> result = new HashMap<String, Object>();
        Map<String, String> error = new HashMap<String, String>();
        result.put("error", error);
        error.put("code", String.valueOf(this.getCode()));
        error.put("message", this.getDescription());
        if (detail != null) {
            error.put("message", detail);
        }
        return result;
    }

    public JsonNode toJson() {
        return Json.toJson(toErrorMap());

    }
}
package com.nuce.group3.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.List;

public class ExceptionMessage {
    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "dd-MM-yyyy hh:mm:ss"
    )
    private LocalDateTime timestamp;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String errorKey;
    private Integer status;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<String> params;

    public ExceptionMessage(List<String> params, String message) {
        this.params = params;
        this.message = message;
    }

    public LocalDateTime getTimestamp() {
        return this.timestamp;
    }

    public String getErrorKey() {
        return this.errorKey;
    }

    public Integer getStatus() {
        return this.status;
    }

    public String getMessage() {
        return this.message;
    }

    public List<String> getParams() {
        return params;
    }

    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "dd-MM-yyyy hh:mm:ss"
    )
    public void setTimestamp(final LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public void setErrorKey(final String errorKey) {
        this.errorKey = errorKey;
    }

    public void setStatus(final Integer status) {
        this.status = status;
    }

    public void setMessage(final String message) {
        this.message = message;
    }

    public void setParams(List<String> params) {
        this.params = params;
    }

    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof ExceptionMessage)) {
            return false;
        } else {
            ExceptionMessage other = (ExceptionMessage)o;
            if (!other.canEqual(this)) {
                return false;
            } else {
                label71: {
                    Object this$status = this.getStatus();
                    Object other$status = other.getStatus();
                    if (this$status == null) {
                        if (other$status == null) {
                            break label71;
                        }
                    } else if (this$status.equals(other$status)) {
                        break label71;
                    }

                    return false;
                }

                Object this$timestamp = this.getTimestamp();
                Object other$timestamp = other.getTimestamp();
                if (this$timestamp == null) {
                    if (other$timestamp != null) {
                        return false;
                    }
                } else if (!this$timestamp.equals(other$timestamp)) {
                    return false;
                }

                label57: {
                    Object this$errorKey = this.getErrorKey();
                    Object other$errorKey = other.getErrorKey();
                    if (this$errorKey == null) {
                        if (other$errorKey == null) {
                            break label57;
                        }
                    } else if (this$errorKey.equals(other$errorKey)) {
                        break label57;
                    }

                    return false;
                }

                Object this$message = this.getMessage();
                Object other$message = other.getMessage();
                if (this$message == null) {
                    if (other$message != null) {
                        return false;
                    }
                } else if (!this$message.equals(other$message)) {
                    return false;
                }

                Object this$params = this.getParams();
                Object other$params = other.getParams();
                if (this$params == null) {
                    if (other$params == null) {
                        return true;
                    }
                } else if (this$params.equals(other$params)) {
                    return true;
                }

                return false;
            }
        }
    }

    protected boolean canEqual(final Object other) {
        return other instanceof ExceptionMessage;
    }

    public int hashCode() {
        boolean PRIME = true;
        int result = 1;
        Object $status = this.getStatus();
        result = result * 59 + ($status == null ? 43 : $status.hashCode());
        Object $timestamp = this.getTimestamp();
        result = result * 59 + ($timestamp == null ? 43 : $timestamp.hashCode());
        Object $errorKey = this.getErrorKey();
        result = result * 59 + ($errorKey == null ? 43 : $errorKey.hashCode());
        Object $message = this.getMessage();
        result = result * 59 + ($message == null ? 43 : $message.hashCode());
        Object $params = this.getParams();
        result = result * 59 + ($params == null ? 43 : $params.hashCode());
        return result;
    }

    public String toString() {
        LocalDateTime var10000 = this.getTimestamp();
        return "ExceptionMessage(timestamp=" + var10000 + ", errorKey=" + this.getErrorKey() + ", status=" + this.getStatus() + ", message=" + this.getMessage() + ", params=" + this.getParams() + ")";
    }

    public ExceptionMessage() {
    }

    public ExceptionMessage(final LocalDateTime timestamp, final String errorKey, final Integer status, final String message, final List<String> params) {
        this.timestamp = timestamp;
        this.errorKey = errorKey;
        this.status = status;
        this.message = message;
        this.params = params;
    }
}


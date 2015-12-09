package com.gu.baselibrary.uploadfile;

/**
 * 上传文件model
 */
public class UploadModel {
    private String result;
    private int code;
    private boolean isError;

    public UploadModel(String result, int code, boolean isError) {
        this.result = result;
        this.code = code;
        this.isError = isError;
    }

    public boolean isError() {
        return isError;
    }

    public void setIsError(boolean isError) {
        this.isError = isError;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

}

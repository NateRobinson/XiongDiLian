package com.gu.baselibrary.uploadfile;

/**
 * UploadListener 文件上传监听回调接口
 */
public interface UploadListener {
    void uploadSuccess(String result, int code);

    void uploadError(String error, int code);
}

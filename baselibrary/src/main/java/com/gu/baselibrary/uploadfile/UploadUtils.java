package com.gu.baselibrary.uploadfile;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

/**
 * 上传文件工具类
 */
public class UploadUtils {

    private UploadListener listener;
    private int code;

    public UploadUtils(UploadListener listener, int code) {
        this.listener = listener;
        this.code = code;
    }

    // 上传参数和文件
    public void uploadFile(
            final Map<String, String> paramters,
            final Map<String, File> filesMap) {
        if (filesMap == null || filesMap.keySet() == null
                || filesMap.keySet().size() == 0) {
            if (listener != null) {
                listener.uploadError("文件为空", code);
            }
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    httpClient.getParams().setParameter(
                            CoreProtocolPNames.PROTOCOL_VERSION,
                            HttpVersion.HTTP_1_1);
                    /* 建立HTTPPost对象 */
                    HttpPost httpRequest = new HttpPost(UploadConfig.UPLOAD_URL);
                    httpRequest.setHeader("Accept",
                            "application/json, text/javascript, */*; q=0.01");
                    httpRequest.setHeader("Connection", "keep-alive");
                    String strResult = "doPostError";
                    // 文件传输
                    MultipartEntity mEntity = new MultipartEntity();
                    // 添加上传参数
                    if (paramters != null && paramters.entrySet() != null
                            && paramters.entrySet().size() != 0) {
                        for (Map.Entry<String, String> entry : paramters
                                .entrySet()) {
                            ContentBody strBody = new StringBody(entry
                                    .getValue());
                            mEntity.addPart(entry.getKey(), strBody);
                        }
                    }
                    // 添加上传的文件
                    Iterator<String> iterator = filesMap.keySet().iterator();
                    while (iterator.hasNext()) {
                        String fileName = iterator.next();
                        ContentBody cBody = new FileBody(filesMap.get(fileName));
                        mEntity.addPart(fileName, cBody);
                    }
                    httpRequest.setEntity(mEntity);
                    /* 发送请求并等待响应 */
                    HttpResponse httpResponse = httpClient.execute(httpRequest);
                    /* 若状态码为200 ok */
                    if (httpResponse.getStatusLine().getStatusCode() == 200) {
                        /* 读返回数据 */
                        strResult = EntityUtils.toString(httpResponse
                                .getEntity());
                        if (listener != null) {
                            listener.uploadSuccess(strResult, code);
                        }
                    } else {
                        strResult = "Error Response:"
                                + httpResponse.getStatusLine().toString();
                        if (listener != null) {
                            listener.uploadError(strResult, code);
                        }
                    }
                    // 关闭连接
                    httpClient.getConnectionManager().shutdown();
                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                    if (listener != null) {
                        listener.uploadError(e.toString(), code);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                    if (listener != null) {
                        listener.uploadError(e.toString(), code);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    if (listener != null) {
                        listener.uploadError(e.toString(), code);
                    }
                }
            }
        }).start();

    }
}

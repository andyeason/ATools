package com.andyeason.atools.oss;

import android.content.Context;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
import com.alibaba.sdk.android.oss.common.auth.OSSPlainTextAKSKCredentialProvider;
import com.alibaba.sdk.android.oss.model.OSSRequest;
import com.alibaba.sdk.android.oss.model.OSSResult;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;


/**
 * @author Andy
 * 阿里云OSS上传文件
 * 2020-02-07
 */
public class MyOSSUtils {

    private static MyOSSUtils instance;
    private OSS oss;

    public static MyOSSUtils getInstance() {
        if (instance == null) {
            return new MyOSSUtils();
        }
        return instance;
    }

    private void getOSs(Context context) {
        OSSPlainTextAKSKCredentialProvider credentialProvider = new OSSPlainTextAKSKCredentialProvider(OssConfig.ACCESS_ID, OssConfig.ACCESS_KEY);
        ClientConfiguration conf = new ClientConfiguration();
        conf.setConnectionTimeout(15 * 1000);// 连接超时，默认15秒
        conf.setSocketTimeout(15 * 1000);// socket超时，默认15秒
        conf.setMaxConcurrentRequest(5);// 最大并发请求数，默认5个
        conf.setMaxErrorRetry(2);// 失败后最大重试次数，默认2次
        oss = new OSSClient(context, OssConfig.END_POINT, credentialProvider);

    }

    /**
     * 上传自定义文件
     *
     * @param context
     * @param ossUpCallback 上传回调
     * @param fileName      上传路径/文件名
     * @param loaclFilePath 本地文件路径
     */
    public void uploadFile(Context context, final String fileName, String loaclFilePath, final MyOSSUtils.OssUpCallback ossUpCallback) {
        getOSs(context);
        PutObjectRequest putObjectRequest = new PutObjectRequest(OssConfig.BUCKET, fileName, loaclFilePath);
        putObjectRequest.setProgressCallback(new OSSProgressCallback() {
            @Override
            public void onProgress(Object o, long currentSize, long totalSize) {
                ossUpCallback.inProgress(currentSize, totalSize);
            }
        });
        oss.asyncPutObject(putObjectRequest, new OSSCompletedCallback() {
            @Override
            public void onSuccess(OSSRequest ossRequest, OSSResult ossResult) {
                ossUpCallback.UploadSuccess(OssConfig.ReturnAllPath ? oss.presignPublicObjectURL(OssConfig.BUCKET, fileName) : fileName);
            }

            @Override
            public void onFailure(OSSRequest ossRequest, ClientException e, ServiceException e1) {
                ossUpCallback.UploadFail();
            }
        });
    }

    /**
     * 设置回调方法
     */
    public interface OssUpCallback {
        void UploadSuccess(String ossUrl);

        void UploadFail();

        void inProgress(long curProgress, long totalSize);
    }

}
package com.mpaas.demo.share;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;

public class ImageDownloader {
//    public Bitmap getPic(String url) {
//        //获取okHttp对象get请求
//        try {
//            OkHttpClient client = new OkHttpClient();
//            //获取请求对象
//            Request request = new Request.Builder().url(url).build();
//            //获取响应体
//            ResponseBody body = client.newCall(request).execute().body();
//            //获取流
//            InputStream in = body.byteStream();
//
//            return bitmap;
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
    public static byte[] inputStreamToByte(InputStream is) {
        try {
            ByteArrayOutputStream bytestream = new ByteArrayOutputStream();
            int ch;
            while ((ch = is.read()) != -1) {
                bytestream.write(ch);
            }
            byte imgdata[] = bytestream.toByteArray();
            bytestream.close();
            return imgdata;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}

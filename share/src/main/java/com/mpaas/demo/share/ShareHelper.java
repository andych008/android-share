package com.mpaas.demo.share;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.alipay.mobile.antui.basic.AUToast;
import com.alipay.mobile.common.share.ShareContent;
import com.alipay.mobile.common.share.constant.ShareType;
import com.alipay.mobile.framework.service.ShareService;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class ShareHelper {
    public static ShareContent createUrlShareContent(String title, String desc, String url, String icon) {
        ShareContent content = new ShareContent();
        content.setContentType("url");
        content.setContent(desc);
        content.setTitle(title);
        content.setImgUrl(icon);
        content.setIconUrl(icon);//支付宝的小图
        // 设置分享图片地址，微信分享请保证图片小于32KB
//        content.setImgUrl("https://gw.alipayobjects.com/zos/rmsportal/WqYuuhbhRSCdtsyNOKPv.png");
        // 此image url为一张超过32KB的大图，用于测试微信分享超过32KB时，默认icon的功能
//        content.setImgUrl("http://seopic.699pic.com/photo/00026/7248.jpg_wh1200.jpg");
        content.setUrl(url);

        return content;
    }

    public static ShareContent createTextShareContent(String desc) {
        ShareContent content = new ShareContent();
        content.setContentType("text");
        content.setContent(desc);

        return content;
    }

    public static ShareContent createImageShareContent(InputStream imageStream) {
        ShareContent content = new ShareContent();
        content.setContentType("image");
        content.setContent(" ");

        byte[] bytes = inputStreamToByte(imageStream);
        content.setImage(bytes);

        return content;
    }

    public static ShareContent createImageShareContent(String imageUrl) {
        ShareContent content = new ShareContent();
        content.setContentType("image");
        content.setContent(" ");

        content.setImgUrl(imageUrl);

        return content;
    }


    public static void share(final ShareService service, final ShareContent content, final int shareType, final String biz) {
        Application application = service.getMicroApplicationContext().getApplicationContext();
        switch (shareType) {
            case ShareType.SHARE_TYPE_WEIXIN:
            case ShareType.SHARE_TYPE_WEIXIN_TIMELINE:
                if (isTextContent(content)) {
                    content.setUrl(null);
                    content.setImage(null);
                    content.setImgUrl(null);
                } else if (isImageContent(content)) {
                    content.setUrl(null);
                }
                break;
            case ShareType.SHARE_TYPE_WEIBO:
                if (isTextContent(content)) {
                    content.setUrl(" ");
                    content.setImage(null);
                    content.setImgUrl(null);
                } else if (isImageContent(content)) {
                    content.setUrl(" ");
                }
                break;
            case ShareType.SHARE_TYPE_QQ:
                if (isTextContent(content)) {

                    shareTextToQQ(application, content);
//                    Activity activity = service.getMicroApplicationContext().getTopActivity().get();
//                    if (activity!=null) {
//                        shareToQQ(activity, content);
//                    }
//                    else {
//                        AUToast.makeToast(application, com.alipay.mobile.antui.R.drawable.loading_error_icon,
//                                "不支持分享文字到QQ", Toast.LENGTH_SHORT).show();
//                    }
                    return;
                }
                break;
            case ShareType.SHARE_TYPE_QZONE:
                //com.alipay.android.shareassist.api.QZoneShare
                //com.alipay.android.shareassist.ShareAssistApp
                if (isTextContent(content)) {
                    content.setTitle("null");
                    content.setUrl(null);
                    content.setImage(null);
                    content.setImgUrl(null);
                } else if (isImageContent(content)) {
                    content.setTitle("null");
                    content.setUrl(null);
//                    content.getImgUrl();

                }
//                if (isTextContent(content)) {
//                    AUToast.makeToast(application, com.alipay.mobile.antui.R.drawable.loading_error_icon,
//                            "不支持分享文字到QQ", Toast.LENGTH_SHORT).show();
//                    return;
//                } else if (isImageContent(content)) {
//
//                    AUToast.makeToast(application, com.alipay.mobile.antui.R.drawable.loading_error_icon,
//                            "不支持分享文字到QQ", Toast.LENGTH_SHORT).show();
//                    return;
//                }
                break;
        }

        service.silentShare(content, shareType, biz);
    }


    private static void a(ShareContent mShareContent) {
        if (mShareContent.getUrl() == null || mShareContent.getUrl().length() <= 0) {
            mShareContent.setUrl("https://d.alipay.com/share/index.htm");
        }
        if (mShareContent.getTitle() == null || mShareContent.getTitle().length() <= 0) {
            mShareContent.setTitle("分享一下");
        }
    }

    private static void shareTextToQQ(Context context, final ShareContent content){
        Intent intent = new Intent(Intent.ACTION_SEND);

        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
        intent.putExtra(Intent.EXTRA_TEXT, content.getContent());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setComponent(new ComponentName("com.tencent.mobileqq", "com.tencent.mobileqq.activity.JumpActivity"));
        context.startActivity(intent);


        if (topActivity != null) {
            Intent intent3 = new Intent(topActivity, QQCallbackActivity.class);
            intent3.putExtra("shareType", mShareType);
            intent3.putExtra("biz", biz);
            intent3.putExtra("ShareContent", mShareContent);
            topActivity.startActivity(intent3);


        }
//    /**分享到QQ好友*/
//    public void shareToQQ(Activity activity, String url, String shareTitle, String description, IUiListener uiListener){
//
//        Bundle qqParams = new Bundle();
//        qqParams.putInt("req_type", QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
//        qqParams.putString(QQShare.SHARE_TO_QQ_TITLE, shareTitle);
//        qqParams.putString(QQShare.SHARE_TO_QQ_SUMMARY,  description);
//        qqParams.putString(QQShare.SHARE_TO_QQ_TARGET_URL,  url);
//        //qqParams.putString(QQShare.SHARE_TO_QQ_APP_NAME,  "APP名称");
//        tencent.shareToQQ(activity, qqParams, uiListener);
//
//    }

    private static boolean isTextContent(final ShareContent content) {
        return content.getContentType().contentEquals("text");
    }

    private static boolean isImageContent(final ShareContent content) {
        return content.getContentType().contentEquals("image");
    }

    private static byte[] inputStreamToByte(InputStream is) {
        try {
            ByteArrayOutputStream bytestream = new ByteArrayOutputStream();
            int ch;
            while ((ch = is.read()) != -1) {
                bytestream.write(ch);
            }
            byte bytes[] = bytestream.toByteArray();
            bytestream.close();
            return bytes;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}

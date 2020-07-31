package com.mpaas.demo.share;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.alipay.android.shareassist.utils.CallBackUtils;
import com.alipay.mobile.antui.basic.AUToast;
import com.alipay.mobile.common.share.ShareContent;
import com.alipay.mobile.common.share.ShareException;
import com.alipay.mobile.common.share.constant.ShareType;
import com.alipay.mobile.framework.service.ShareService;
import com.alipay.mobile.framework.service.common.ImageLoaderListener;
import com.alipay.mobile.framework.service.common.ImageLoaderService;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class ShareHelper {

    public static final String TAG = ShareHelper.class.getSimpleName();

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


    public static void share(final ShareService service, final ShareContent content, int shareType, final String biz) {
        if (isImageContent(content)) {

            final CountDownLatch countDownLatch = new CountDownLatch(1);

            ((ImageLoaderService) service.getMicroApplicationContext().findServiceByInterface(ImageLoaderService.class.getName())).startLoad("BANK_ICON", (String) null, content.getImgUrl(), new ImageLoaderListener() {
                public final void onPostLoad(String arg0, Bitmap bitmap) {
                    if (bitmap != null) {
                        int options = 100;
                        int rowBytes = bitmap.getRowBytes() * bitmap.getHeight();
                        int size = rowBytes;
                        if (size > 2*1024*1024) {
                            options = 65;
                        } else if (size > 1024*1024) {
                            options = 75;
                        }
                        ByteArrayOutputStream os = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, options, os);
                        byte[] b2 = os.toByteArray();

                        if (b2.length < 3*1024*1024) {
                            content.setImage(os.toByteArray());
                            content.setImgUrl(null);
                        }
                    }
                    countDownLatch.countDown();
                }

                public final void onFailed(String arg0, int arg1, String arg2) {

                    countDownLatch.countDown();
                }

                public final void onCancelled(String arg0) {
                    countDownLatch.countDown();
                }

                public final void onPreLoad(String arg0) {
                }

                public final void onProgressUpdate(String arg0, double arg1) {
                }
            }, -1, -1);

            try {
                countDownLatch.await(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

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

//                    CallBackUtils.a(ShareType.SHARE_TYPE_QQ);
                    return;
                }
                break;
            case ShareType.SHARE_TYPE_QZONE:
                //com.alipay.android.shareassist.api.QZoneShare
                //com.alipay.android.shareassist.ShareAssistApp
                if (isTextContent(content)) {
                    Log.e(TAG, "不支持分享--文字--到QZone");
                    ShareService.ShareActionListener listener = service.getShareActionListener();
                    if (listener  != null) {
                        listener.onException(shareType, new ShareException("不支持分享文字到QQ空间"));
                    }

                    return;
                } else if (isImageContent(content)) {
                    shareType = ShareType.SHARE_TYPE_QQ;//转成分享到QQ，然后选择“空间”
                }

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

    //https://www.jianshu.com/p/9522e24713e1
    private static void shareTextToQQ(Context context, final ShareContent content) {
        Intent intent = new Intent(Intent.ACTION_SEND);

        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
        intent.putExtra(Intent.EXTRA_TEXT, content.getContent());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setComponent(new ComponentName("com.tencent.mobileqq", "com.tencent.mobileqq.activity.JumpActivity"));
        context.startActivity(intent);

    }

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

package com.mpaas.demo.share;

import android.content.Context;
import android.os.Bundle;

import com.alipay.android.shareassist.api.QZoneShare;
import com.alipay.mobile.common.logging.api.LoggerFactory;
import com.alipay.mobile.common.share.ShareContent;
import com.tencent.connect.share.QzoneShare;
import com.tencent.tauth.Tencent;

import java.util.ArrayList;

public class QZoneShareHelper extends QZoneShare {
    private Context c;
    private QzoneShare d;

//    public final void shareText(Context context, ShareContent shareContent) {
//        try {
//            this.c = context;
//            b = Tencent.a(a, this.c);
//            this.d = new QzoneShare(b.a());
//            Bundle params;
//            (params = new Bundle()).putInt("req_type", 0);
//            params.putString("title", shareContent.getTitle());
//            params.putString("summary", shareContent.getContent());
//            LoggerFactory.getTraceLogger().info("share", "6   " + shareContent.getUrl() + "   " + shareContent.getImgUrl() + "   " + shareContent.getLocalImageUrl());
//            if (shareContent.getUrl() != null) {
//                params.putString("targetUrl", shareContent.getUrl());
//            }
//
//            ArrayList imageUrls;
//            if (shareContent.getImgUrl() != null && !shareContent.getImgUrl().isEmpty()) {
//                (imageUrls = new ArrayList()).add(shareContent.getImgUrl());
//                params.putStringArrayList("imageUrl", imageUrls);
//            } else {
//                (imageUrls = new ArrayList()).add("https://pic.alipayobjects.com/i/mobileapp/png/201410/3dIQjERc5F.png");
//                params.putStringArrayList("imageUrl", imageUrls);
//            }
//
//            super.a(params);
//        } catch (Throwable var5) {
//        }
//    }

}

package com.mpaas.demo.share;

import android.os.Bundle;
import android.support.v7.widget.MenuPopupWindow;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.alipay.android.shareassist.constants.ShareExtraInfoConstant;
import com.alipay.mobile.antui.basic.AUButton;
import com.alipay.mobile.antui.basic.AUTitleBar;
import com.alipay.mobile.antui.dialog.AUListDialog;
import com.alipay.mobile.antui.dialog.AUProgressDialog;
import com.alipay.mobile.common.share.ShareContent;
import com.alipay.mobile.common.share.constant.ShareType;
import com.alipay.mobile.framework.app.ui.BaseActivity;
import com.alipay.mobile.framework.service.ShareService;
import com.alipay.mobile.h5container.api.H5Bundle;
import com.alipay.mobile.h5container.api.H5Param;
import com.alipay.mobile.h5container.service.H5Service;
import com.mpaas.framework.adapter.api.MPFramework;
import com.mpaas.demo.sharedemo.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class ShareActivity extends BaseActivity {

    public static final String TAG = ShareActivity.class.getSimpleName();
    private int type=0;//0 url, 1 text, 2 image
    private AUButton mGetInstructionsBtn;
    private AUListDialog mShareDialog;

    private final ArrayList<String> mData = new ArrayList<>();

    private H5Service mH5Service;
    private ShareService service;

    private ShareListener mShareListener;

    private byte[] mWechatDefaultIconBytes;

    private AUTitleBar mTitle;
    private AUProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        initData();
        initService();
        initListener();
        initWechatDefaultIcon();
        findView();
        initView();
        progressDialog = new AUProgressDialog(this);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        mData.clear();
        mData.add(getString(R.string.alipay));
        mData.add(getString(R.string.wechat));
        mData.add(getString(R.string.wechat_timeline));
        mData.add(getString(R.string.weibo));
        mData.add(getString(R.string.qq));
        mData.add(getString(R.string.qzone));
        mData.add(getString(R.string.dingding));
        mData.add(getString(R.string.sms));
    }

    /**
     * 初始化service
     */
    private void initService() {
        mH5Service = MPFramework.getExternalService(H5Service.class.getName());
        service = MPFramework.getExternalService(ShareService.class.getName());
    }

    /**
     * 初始化分享回调
     */
    private void initListener() {
        mShareListener = new ShareListener(this);
    }

    private void initWechatDefaultIcon() {
        ByteArrayOutputStream outputStream = null;
        try {
            InputStream inputStream = getResources().getAssets().open("share/appicon.png");
            outputStream = new ByteArrayOutputStream();
            byte[] bytes = new byte[1024];
            while (inputStream.read(bytes) != -1) {
                outputStream.write(bytes, 0, bytes.length);
            }
            outputStream.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mWechatDefaultIconBytes = outputStream.toByteArray();
    }

    private void findView() {
        mTitle = ((AUTitleBar) findViewById(R.id.title_atb));
        mGetInstructionsBtn = (AUButton) findViewById(R.id.share_get_instructions_btn);
        mShareDialog = new AUListDialog(this, mData);
    }

    private void initView() {
        mTitle.setTitleText(getString(R.string.share), (int)getResources().getDimension(R.dimen.text_size)
                , getResources().getColor(R.color.title_color));
        mTitle.getRightButtonIconView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                MenuPopupWindow popupWindow = new MenuPopupWindow(ShareActivity.this);
//                popupWindow.showAsDropDown(mTitle.getRightButtonIconView(), 0, 0);
            }
        });
        findViewById(R.id.share_url_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                type=0;
                mShareDialog.show();
            }
        });
        findViewById(R.id.share_text_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                type=1;
                mShareDialog.show();
            }
        });
        findViewById(R.id.share_image_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                type=2;
                mShareDialog.show();
            }
        });
        findViewById(R.id.share_big_image_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                type=3;
                mShareDialog.show();
            }
        });
        mGetInstructionsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 启动H5容器，打开mPaaS社交分享文档网页
                Bundle param = new Bundle();
                param.putString(H5Param.LONG_URL, "https://www.cloud.alipay.com/docs/2/49577");
                H5Bundle bundle = new H5Bundle();
                bundle.setParams(param);
                mH5Service.startPage(MPFramework.getMicroApplicationContext().findTopRunningApp(), bundle);
            }
        });
        mShareDialog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final String shareMsg = mData.get(i);

                progressDialog.setProgressVisiable(true);
                progressDialog.setMessage("等待中...");
                progressDialog.show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        if (getString(R.string.alipay).equals(shareMsg)) {
                            shareToAlipay();
                            return;
                        }
                        if (getString(R.string.wechat).equals(shareMsg)) {
                            shareToWechat();
                            return;
                        }
                        if (getString(R.string.wechat_timeline).equals(shareMsg)) {
                            shareToWechatTimeline();
                            return;
                        }
                        if (getString(R.string.weibo).equals(shareMsg)) {
                            shareToWeibo();
                            return;
                        }
                        if (getString(R.string.qq).equals(shareMsg)) {
                            shareToQQ();
                            return;
                        }
                        if (getString(R.string.qzone).equals(shareMsg)) {
                            shareToQZone();
                            return;
                        }
                        if (getString(R.string.dingding).equals(shareMsg)) {
                            shareToDingDing();
                            return;
                        }
                        if (getString(R.string.sms).equals(shareMsg)) {
                            shareToSms();
                            return;
                        }
                    }
                }).start();

            }
        });
    }

    /**
     * 分享到支付宝
     */
    private void shareToAlipay() {
        service.initAlipayContact("2016111102737103");
        ShareContent content = createShareContent();
        service.setShareActionListener(mShareListener);
        ShareHelper.share(service, content, ShareType.SHARE_TYPE_ALIPAY, "test");
    }

    /**
     * 分享到微信好友
     */
    private void shareToWechat() {
        service.initWeixin("wxa077a4686304b04a", "e9c754349381d16cd88a1df19592cc23");
        ShareContent content = createShareContent();
        setWechatDefaultIcon(content);
        service.setShareActionListener(mShareListener);
        ShareHelper.share(service, content, ShareType.SHARE_TYPE_WEIXIN, "test");
    }

    /**
     * 分享到微信朋友圈
     */
    private void shareToWechatTimeline() {
        service.initWeixin("wxa077a4686304b04a", "e9c754349381d16cd88a1df19592cc23");
        ShareContent content = createShareContent();
        setWechatDefaultIcon(content);
        service.setShareActionListener(mShareListener);
        ShareHelper.share(service, content, ShareType.SHARE_TYPE_WEIXIN_TIMELINE, "test");
    }

    /**
     * 分享到微博
     */
    private void shareToWeibo() {
        service.initWeiBo("1095133729", "eba90f2ef316f8106fd8b6507b44fcb5", "http://alipay.com");
        ShareContent content = createShareContent();
        service.setShareActionListener(mShareListener);
        ShareHelper.share(service, content, ShareType.SHARE_TYPE_WEIBO, "test");
    }

    /**
     * 分享到QQ
     */
    private void shareToQQ() {
        service.initQQ("1104122330");
        ShareContent content = createShareContent();
        service.setShareActionListener(mShareListener);
        ShareHelper.share(service, content, ShareType.SHARE_TYPE_QQ, "test");
    }

    /**
     * 分享到QZone
     */
    private void shareToQZone() {
        service.initQZone("1104122330");
        ShareContent content = createShareContent();
        service.setShareActionListener(mShareListener);
        ShareHelper.share(service, content, ShareType.SHARE_TYPE_QZONE, "test");
    }

    /**
     * 分享到钉钉
     */
    private void shareToDingDing() {
        service.initDingDing("dingoa7rxo7sxowhwpg5ke");
        ShareContent content = createShareContent();
        service.setShareActionListener(mShareListener);
        ShareHelper.share(service, content, ShareType.SHARE_TYPE_DINGDING, "test");
    }


    /**
     * 分享到短信
     */
    private void shareToSms() {
        ShareContent content = createShareContent();
        service.setShareActionListener(mShareListener);
        ShareHelper.share(service, content, ShareType.SHARE_TYPE_SMS, "test");
    }

    /**
     * 生成分享内容
     *
     * @return 分享内容
     */
    private ShareContent createShareContent() {
        Log.i(TAG, "createShareContent() called type ="+type);
        ShareContent content = null;
        if (type==0) {//url

            content = ShareHelper.createUrlShareContent("mPaaS share title", "mPaaS share content",
                    "https://www.baidu.com",
                    "https://gw.alipayobjects.com/zos/rmsportal/WqYuuhbhRSCdtsyNOKPv.png");

        } else if (type==1) {//text
            content = ShareHelper.createTextShareContent("hello-text");

        }  else if (type==2) {//image

//            content = ShareHelper.createImageShareContent("https://gw.alipayobjects.com/zos/rmsportal/WqYuuhbhRSCdtsyNOKPv.png");//成功  小图
            content = ShareHelper.createImageShareContent("https://desk-fd.zol-img.com.cn/t_s1920x1200c5/g5/M00/02/09/ChMkJ1bKzpKIW2RfAAY9MGwSXMYAALJMQAgrXAABj1I606.jpg");//失败 超过32k 以默认图代替
//            content = ShareHelper.createImageShareContent("https://desk-fd.zol-img.com.cn/t_s960x600c5/g6/M00/04/08/ChMkKV8iKBiIZt0oACm2vPiaZH8AAAR-wPFpGkAKbbU155.jpg");//失败 超过32k 以默认图代替

//            content = new ShareContent();
//            content.setContentType("image");
//            File file = new File("/data/data/com.mpaas.demo/files/123.png");
//            if (!file.exists()) {
//                Log.e(TAG, "文件不存在");
//                return null;
//            }
//            content.setLocalImageUrl("/data/data/com.mpaas.demo/files/123.png");
//            content.setLocalImageUrl(getFileStreamPath("123.png").getAbsolutePath());

        }else {
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            try {
                InputStream inputStream = getResources().getAssets().open("share/big_pic.jpg");//成功  传大图
                content = ShareHelper.createImageShareContent(inputStream);
                } catch (IOException e) {
                e.printStackTrace();
            }

//            content.setLocalImageUrl("file:///android_asset/share/big_pic.jpg");//失败
//            content.setLocalImageUrl("file:///android_asset/share/appicon.png");//失败

            // TODO: 2020/7/31 localImage  sdcard
        }

        progressDialog.dismiss();
        return content;
    }

    private void setWechatDefaultIcon(ShareContent content) {
        HashMap<String, Object> extraInfo = content.getExtraInfo();
        if (null == extraInfo) {
            extraInfo = new HashMap<>();
        }
        if (null == extraInfo.get(ShareExtraInfoConstant.DEFAULT_ICON)
                || !(extraInfo.get(ShareExtraInfoConstant.DEFAULT_ICON) instanceof byte[])) {
            extraInfo.put(ShareExtraInfoConstant.DEFAULT_ICON, mWechatDefaultIconBytes);
        }
        content.setExtraInfo(extraInfo);
    }

}

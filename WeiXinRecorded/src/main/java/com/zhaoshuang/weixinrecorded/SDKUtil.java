package com.zhaoshuang.weixinrecorded;

import android.content.Context;

import com.zero.smallvideorecord.jniinterface.FFmpegBridge;
import com.yixia.camera.VCamera;

import java.io.File;

/**
 * Created by zhaoshuang on 17/2/8.
 */

public class SDKUtil {

    public static String VIDEO_PATH =  "/sdcard/";

    public static void initSDK(Context context,String path) {
        VIDEO_PATH=path;
        File file = new File(VIDEO_PATH);
        if (!file.exists()) {
            boolean mkdirs = file.mkdirs();
        }

        //设置视频缓存路径
        VCamera.setVideoCachePath(VIDEO_PATH);

        // log输出,ffmpeg输出到logcat
        VCamera.setDebugMode(true);

        // 初始化拍摄SDK，必须
        VCamera.initialize(context);
        FFmpegBridge.initJXFFmpeg(true,String.format("%s/ffmpeg.log",VIDEO_PATH));
    }
}

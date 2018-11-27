package com.zero.smallvideorecord;

import java.io.Serializable;

/**
 * @author zero
 * @date 2017/5/18
 * https://github.com/zerochl/FFMPEG-AAC-264-Android-32-64
 * zerochl0912@gmail.com
 */

public class VideoInfo implements Serializable {

    private boolean succeed;

    private String videoPath;

    private String picPath;

    public VideoInfo(boolean succeed, String videoPath, String picPath) {
        this.succeed = succeed;
        this.videoPath = videoPath;
        this.picPath = picPath;
    }

    public VideoInfo() {
    }

    public boolean isSucceed() {
        return succeed;
    }

    public void setSucceed(boolean succeed) {
        this.succeed = succeed;
    }

    public String getVideoPath() {
        return videoPath;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    public String getPicPath() {
        return picPath;
    }

    public void setPicPath(String picPath) {
        this.picPath = picPath;
    }

    @Override
    public String toString() {
        return "VideoInfo{" +
                "succeed=" + succeed +
                ", videoPath='" + videoPath + '\'' +
                ", picPath='" + picPath + '\'' +
                '}';
    }
}

package com.zhaoshuang.weixinrecordeddemo;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yixia.camera.MediaRecorderBase;
import com.zero.smallvideorecord.VideoInfo;
import com.zhaoshuang.weixinrecorded.MyVideoView;
import com.zhaoshuang.weixinrecorded.RecordedActivity;

import java.io.File;
import java.text.DecimalFormat;


public class MainActivity extends AppCompatActivity {

    private MyVideoView vv_play;
    private ImageView iv_photo;
    private View rl_show;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        vv_play = findViewById(R.id.vv_play);
        iv_photo = findViewById(R.id.iv_photo);
        rl_show = findViewById(R.id.rl_show);
        textView = findViewById(R.id.textView);
    }

    public void recordVideo(View view) {
        if (view.getId() == R.id.btn_local_compression) {
            Intent intent = new Intent(this, LocalVideoCompressActivity.class);
            startActivityForResult(intent, 1);
        } else {
            Intent intent = new Intent(this, RecordedActivity.class);
            startActivityForResult(intent, 0);
        }


    }

    public String formatFileSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        String wrongSize = "0B";
        if (fileS == 0L) {
            return wrongSize;
        } else {
            fileSizeString = fileS < (long) 1024 ? df.format((double) fileS) + "B" : (fileS < (long) 1048576 ? df.format((double) fileS / (double) 1024) + "KB" : (fileS < (long) 1073741824 ? df.format((double) fileS / (double) 1048576) + "MB" : df.format((double) fileS / (double) 1073741824) + "GB"));
            return fileSizeString;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        textView.setText("");
        StringBuffer buffer = new StringBuffer();
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data.hasExtra("videoInfo")) {
            VideoInfo videoInfo = (VideoInfo) data.getSerializableExtra("videoInfo");
            Bitmap bitmap = BitmapFactory.decodeFile(videoInfo.getPicPath());

            buffer.append("视频地址：");
            buffer.append(videoInfo.getVideoPath());
            buffer.append("\n");
            buffer.append("视频大小：");
            buffer.append(formatFileSize(new File(videoInfo.getVideoPath()).length()));
            buffer.append("\n");
            buffer.append("缩略图地址：");
            buffer.append(videoInfo.getPicPath());
            buffer.append("\n");
            buffer.append("缩略图大小：");
            buffer.append(formatFileSize(new File(videoInfo.getPicPath()).length()));
            buffer.append("\n");
            buffer.append("缩略图尺寸：");
            buffer.append(bitmap.getWidth() + "x" + bitmap.getHeight());
            buffer.append("\n");

            vv_play.setVideoPath(videoInfo.getVideoPath());
            vv_play.setOnPreparedListener(mp -> {
                vv_play.setLooping(true);
                vv_play.start();

                float widthF = vv_play.getVideoWidth() * 1f / MediaRecorderBase.VIDEO_HEIGHT;
                float heightF = vv_play.getVideoHeight() * 1f / MediaRecorderBase.VIDEO_WIDTH;
                ViewGroup.LayoutParams layoutParams = vv_play.getLayoutParams();
                layoutParams.width = (int) (rl_show.getWidth() * widthF);
                layoutParams.height = (int) (rl_show.getHeight() * heightF);
                vv_play.setLayoutParams(layoutParams);

                iv_photo.setImageBitmap(bitmap);

                FrameLayout.LayoutParams layoutParams1 = new FrameLayout.LayoutParams(300, 300);
                layoutParams1.gravity = Gravity.TOP|Gravity.RIGHT;
                iv_photo.setLayoutParams(layoutParams1);
            });
        } else if (resultCode == RESULT_OK && data.hasExtra("imagePath")) {
            String imagePath = data.getStringExtra("imagePath");
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            buffer.append("图片地址：");
            buffer.append(imagePath);
            buffer.append("\n");
            buffer.append("图片大小：");
            buffer.append(formatFileSize(new File(imagePath).length()));
            buffer.append("\n");
            buffer.append("缩图片尺寸：");
            buffer.append(bitmap.getWidth() + "x" + bitmap.getHeight());
            buffer.append("\n");
            FrameLayout.LayoutParams layoutParams1 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            layoutParams1.gravity = Gravity.CENTER;
            iv_photo.setLayoutParams(layoutParams1);
            iv_photo.setImageBitmap(bitmap);


        }
        textView.setText(buffer);
    }
}

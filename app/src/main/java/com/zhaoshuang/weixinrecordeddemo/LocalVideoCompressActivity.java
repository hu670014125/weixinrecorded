package com.zhaoshuang.weixinrecordeddemo;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.yixia.camera.util.StringUtils;
import com.zero.smallvideorecord.AutoVBRMode;
import com.zero.smallvideorecord.BaseMediaBitrateConfig;
import com.zero.smallvideorecord.CBRMode;
import com.zero.smallvideorecord.LocalMediaCompress;
import com.zero.smallvideorecord.LocalMediaConfig;
import com.zero.smallvideorecord.VideoInfo;
import com.zero.smallvideorecord.VBRMode;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class LocalVideoCompressActivity extends AppCompatActivity {
    private final int PERMISSION_REQUEST_CODE = 0x001;
    private ScrollView sv;
    private Button bt_start;
    private TextView tv_size;
    private Button bt_choose;
    private EditText et_width;
    private EditText et_height;
    private EditText et_maxtime;
    private Spinner spinner_record;
    private EditText et_maxframerate;
    private final int CHOOSE_CODE = 0x000520;
    private RadioGroup rg_aspiration;
    private ProgressDialog mProgressDialog;
    private LinearLayout ll_only_compress;
    private RadioGroup rg_only_compress_mode;
    private LinearLayout ll_only_compress_crf;
    private EditText et_only_compress_crfSize;
    private LinearLayout ll_only_compress_bitrate;
    private EditText et_only_compress_maxbitrate;
    private TextView tv_only_compress_maxbitrate;
    private EditText et_only_compress_bitrate;
    private Spinner spinner_only_compress;
    private EditText et_only_framerate;
    private EditText et_bitrate;
    private EditText et_only_scale;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_video_compress);
        initView();
        initEvent();
    }
    private void initEvent() {

        rg_only_compress_mode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_auto:
                        ll_only_compress_crf.setVisibility(View.VISIBLE);
                        ll_only_compress_bitrate.setVisibility(View.GONE);
                        break;
                    case R.id.rb_vbr:
                        ll_only_compress_crf.setVisibility(View.GONE);
                        ll_only_compress_bitrate.setVisibility(View.VISIBLE);
                        tv_only_compress_maxbitrate.setVisibility(View.VISIBLE);
                        et_only_compress_maxbitrate.setVisibility(View.VISIBLE);
                        break;
                    case R.id.rb_cbr:
                        ll_only_compress_crf.setVisibility(View.GONE);
                        ll_only_compress_bitrate.setVisibility(View.VISIBLE);
                        tv_only_compress_maxbitrate.setVisibility(View.GONE);
                        et_only_compress_maxbitrate.setVisibility(View.GONE);
                        break;
                        default:
                }
            }
        });
    }

    private void initView() {
        bt_choose = (Button) findViewById(R.id.bt_choose);
        ll_only_compress = (LinearLayout) findViewById(R.id.ll_only_compress);
        et_only_framerate = (EditText) findViewById(R.id.et_only_framerate);
        et_only_scale = (EditText) findViewById(R.id.et_only_scale);
        rg_only_compress_mode = (RadioGroup) findViewById(R.id.rg_mode);
        ll_only_compress_crf = (LinearLayout) findViewById(R.id.ll_crf);
        et_only_compress_crfSize = (EditText) findViewById(R.id.et_crfSize);
        ll_only_compress_bitrate = (LinearLayout)findViewById(R.id.ll_bitrate);
        et_only_compress_maxbitrate = (EditText) findViewById(R.id.et_maxbitrate);
        tv_only_compress_maxbitrate = (TextView) findViewById(R.id.tv_maxbitrate);
        et_only_compress_bitrate = (EditText) findViewById(R.id.et_bitrate);
        spinner_only_compress = (Spinner) findViewById(R.id.spinner_only_compress);
    }
    /**
     * 选择本地视频，为了方便我采取了系统的API，所以也许在一些定制机上会取不到视频地址，
     * 所以选择手机里视频的代码根据自己业务写为妙。
     *
     * @param v
     */
    public void choose(View v) {
        Intent it = new Intent(Intent.ACTION_GET_CONTENT, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        it.setDataAndType(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, "video/*");
        startActivityForResult(it, CHOOSE_CODE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CHOOSE_CODE) {
            //
            if (resultCode == RESULT_OK && data != null && data.getData() != null) {
                Uri uri = data.getData();
//
                String[] proj = {MediaStore.Video.Media.DATA, MediaStore.Video.Media.MIME_TYPE};
                Cursor cursor = getContentResolver().query(uri, proj, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    int _data_num = cursor.getColumnIndex(MediaStore.Video.Media.DATA);
                    int mime_type_num = cursor.getColumnIndex(MediaStore.Video.Media.MIME_TYPE);

                    String _data = cursor.getString(_data_num);
                    String mime_type = cursor.getString(mime_type_num);

                    if (!TextUtils.isEmpty(mime_type) && mime_type.contains("video") && !TextUtils.isEmpty(_data)) {
                        BaseMediaBitrateConfig compressMode = null;

                        int compressModeCheckedId = rg_only_compress_mode.getCheckedRadioButtonId();

                        if (compressModeCheckedId == R.id.rb_cbr) {
                            String bitrate = et_only_compress_bitrate.getText().toString();
                            if (checkStrEmpty(bitrate, "请输入压缩额定码率")) {
                                return;
                            }
                            compressMode = new CBRMode(166, Integer.valueOf(bitrate));
                        } else if (compressModeCheckedId == R.id.rb_auto) {
                            String crfSize = et_only_compress_crfSize.getText().toString();
                            if (TextUtils.isEmpty(crfSize)) {
                                compressMode = new AutoVBRMode();
                            } else {
                                compressMode = new AutoVBRMode(Integer.valueOf(crfSize));
                            }
                        } else if (compressModeCheckedId == R.id.rb_vbr) {
                            String maxBitrate = et_only_compress_maxbitrate.getText().toString();
                            String bitrate = et_only_compress_bitrate.getText().toString();

                            if (checkStrEmpty(maxBitrate, "请输入压缩最大码率") || checkStrEmpty(bitrate, "请输入压缩额定码率")) {
                                return;
                            }
                            compressMode = new VBRMode(Integer.valueOf(maxBitrate), Integer.valueOf(bitrate));
                        } else {
                            compressMode = new AutoVBRMode();
                        }

                        if (!spinner_only_compress.getSelectedItem().toString().equals("none")) {
                            compressMode.setVelocity(spinner_only_compress.getSelectedItem().toString());
                        }

                        this.videoCompress(_data,compressMode);


                    } else {
                        Toast.makeText(this, "选择的不是视频或者地址错误,也可能是这种方式定制神机取不到！", Toast.LENGTH_SHORT);
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private boolean checkStrEmpty(String str, String display) {
        if (TextUtils.isEmpty(str)) {
            Toast.makeText(this, display, Toast.LENGTH_SHORT);
            return true;
        }
        return false;
    }

    /**
     * 视频压缩
     *
     * @param filePath
     */
    @SuppressLint("CheckResult")
    private void videoCompress(String filePath, BaseMediaBitrateConfig compressMode) {
        String sRate = et_only_framerate.getText().toString();
        String scale = et_only_scale.getText().toString();
        int iRate = 0;
        float fScale = 0;
        if (!TextUtils.isEmpty(sRate)) {
            iRate = Integer.valueOf(sRate);
        }
        if (!TextUtils.isEmpty(scale)) {
            fScale = Float.valueOf(scale);
        }

        int finalIRate = iRate;
        float finalFScale = fScale;
        Observable.create((ObservableOnSubscribe<VideoInfo>) emitter -> {
            LocalMediaConfig.Buidler buidler = new LocalMediaConfig.Buidler();
            final LocalMediaConfig config = buidler
                    .setVideoPath(filePath)
                    .captureThumbnailsTime(1)
                    .doH264Compress(compressMode)
                    .setFramerate(finalIRate)
                    .setScale(finalFScale)
                    .build();

            VideoInfo onlyCompressOverBean = new LocalMediaCompress(config).startCompress();
            emitter.onNext(onlyCompressOverBean);

        }).subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposable -> {
                    showProgress("", "压缩中...", -1);
                }).subscribe(videoInfo -> {
                    Intent intent=new Intent();
                    intent.putExtra("videoInfo",videoInfo);
            setResult(RESULT_OK,intent);
            finish();
            hideProgress();
        });
    }

    private void hideProgress() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }
    private void showProgress(String title, String message, int theme) {
        if (mProgressDialog == null) {
            if (theme > 0) {
                mProgressDialog = new ProgressDialog(this, theme);
            } else {
                mProgressDialog = new ProgressDialog(this);
            }
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mProgressDialog.setCanceledOnTouchOutside(false);// 不能取消
            mProgressDialog.setCancelable(false);
            mProgressDialog.setIndeterminate(true);// 设置进度条是否不明确
        }

        if (!StringUtils.isEmpty(title)) {
            mProgressDialog.setTitle(title);
        }
        mProgressDialog.setMessage(message);
        mProgressDialog.show();
    }

}

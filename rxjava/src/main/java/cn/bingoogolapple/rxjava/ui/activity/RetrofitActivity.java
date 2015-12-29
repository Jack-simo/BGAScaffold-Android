package cn.bingoogolapple.rxjava.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.view.View;

import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.File;
import java.io.IOException;

import cn.bingoogolapple.basenote.activity.TitlebarActivity;
import cn.bingoogolapple.basenote.util.Logger;
import cn.bingoogolapple.basenote.util.PermissionUtil;
import cn.bingoogolapple.basenote.util.ToastUtil;
import cn.bingoogolapple.rxjava.R;
import cn.bingoogolapple.rxjava.engine.LocalServerEngine;
import cn.bingoogolapple.rxjava.model.JsonResp;
import cn.bingoogolapple.rxjava.model.Person;
import cn.bingoogolapple.rxjava.util.GlobalHeaderInterceptor;
import cn.bingoogolapple.rxjava.util.LoggingInterceptor;
import cn.bingoogolapple.rxjava.util.ProgressRequestBody;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class RetrofitActivity extends TitlebarActivity {
    private static final int REQUEST_CODE_CHOOSE_PHOTO = 1;
    private LocalServerEngine mLocalServerEngine;

    private enum UpdateAvatarMethod {
        Part, Body
    }

    private UpdateAvatarMethod mUpdateAvatarMethod = UpdateAvatarMethod.Part;

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_retrofit);
    }

    @Override
    protected void setListener() {
    }

    private static final Interceptor REWRITE_CACHE_CONTROL_INTERCEPTOR = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Response originalResponse = chain.proceed(chain.request());
            return originalResponse.newBuilder()
                    .header("Cache-Control", "max-age=60")
                    .build();
        }
    };

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        setTitle("Retrofit学习笔记");

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.199.190:8080/netnote/retrofit/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        retrofit.client().interceptors().add(new LoggingInterceptor());
//        retrofit.client().interceptors().add(new GzipRequestInterceptor());
        retrofit.client().interceptors().add(new GlobalHeaderInterceptor());
        retrofit.client().interceptors().add(REWRITE_CACHE_CONTROL_INTERCEPTOR);


        mLocalServerEngine = retrofit.create(LocalServerEngine.class);
    }

    @Override
    public void onClick(View v) {
    }

    private Observer<JsonResp> mMsgObserver = new Observer<JsonResp>() {
        @Override
        public void onCompleted() {
            dismissLoadingDialog();
        }

        @Override
        public void onError(Throwable e) {
            dismissLoadingDialog();
            ToastUtil.show("数据加载失败");
        }

        @Override
        public void onNext(JsonResp jsonResp) {
            ToastUtil.show(jsonResp.msg);
        }
    };

    public void loginGet(View v) {
        mLocalServerEngine.loginGet("hello", "world")
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(() -> showLoadingDialog(R.string.loading))
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mMsgObserver);
    }

    public void loginPost(View v) {
        mLocalServerEngine.loginPost("hello", "world")
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(() -> showLoadingDialog(R.string.loading))
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mMsgObserver);
    }

    public void staticHeaders1(View v) {
        mLocalServerEngine.staticHeaders1()
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(() -> showLoadingDialog(R.string.loading))
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mMsgObserver);
    }

    public void staticHeaders2(View v) {
        mLocalServerEngine.staticHeaders2()
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(() -> showLoadingDialog(R.string.loading))
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mMsgObserver);
    }

    public void dynamicHeader1(View v) {
        mLocalServerEngine.dynamicHeader1("headerParam1Value")
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(() -> showLoadingDialog(R.string.loading))
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mMsgObserver);
    }

    public void dynamicHeader2(View v) {
        mLocalServerEngine.dynamicHeader2("headerParam1Value", "headerParam2Value")
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(() -> showLoadingDialog(R.string.loading))
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mMsgObserver);
    }

    public void createPerson(View v) {
        mLocalServerEngine.createPerson(new Person())
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(() -> showLoadingDialog(R.string.loading))
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mMsgObserver);
    }

    public void updateAvatarByPart(View v) {
        mUpdateAvatarMethod = UpdateAvatarMethod.Part;
        choosePhotoWrapper();
    }

    public void updateAvatarByBody(View v) {
        mUpdateAvatarMethod = UpdateAvatarMethod.Body;
        choosePhotoWrapper();
    }

    private void choosePhotoWrapper() {
        PermissionUtil.request(this, REQUEST_CODE_CHOOSE_PHOTO, new PermissionUtil.Delegate() {
            @Override
            public void onPermissionGranted() {
                choosePhoto();
            }

            @Override
            public void onPermissionDenied() {
                ToastUtil.show("您拒绝了RxJava Demo访问SD卡");
            }
        }, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    private void choosePhoto() {
        startActivityForResult(new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI), REQUEST_CODE_CHOOSE_PHOTO);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_CHOOSE_PHOTO:
                PermissionUtil.result(permissions, grantResults, new PermissionUtil.Delegate() {
                    @Override
                    public void onPermissionGranted() {
                        choosePhoto();
                    }

                    @Override
                    public void onPermissionDenied() {
                        ToastUtil.show("您拒绝了RxJava Demo访问SD卡");
                    }
                });
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && null != data && requestCode == REQUEST_CODE_CHOOSE_PHOTO) {
            String picturePath;
            try {
                Uri selectedImage = data.getData();
                String[] filePathColumns = {MediaStore.Images.Media.DATA};
                Cursor c = getContentResolver().query(selectedImage, filePathColumns, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePathColumns[0]);
                picturePath = c.getString(columnIndex);
                c.close();
            } catch (Exception e) {
                picturePath = data.getData().getPath();
            }
            File imageFile = new File(picturePath);
            if (imageFile.exists()) {
                if (mUpdateAvatarMethod == UpdateAvatarMethod.Part) {
                    updateAvatarByPart(imageFile);
                } else {
                    updateAvatarByBody(imageFile);
                }
            }
        }
    }

    private void updateAvatarByBody(File imageFile) {
        RequestBody avatar1 = new ProgressRequestBody(imageFile, new ProgressRequestBody.UploadCallbacks() {
            @Override
            public void onProgressUpdate(int percentage) {
                Logger.i(TAG, "当前进度:" + percentage);
            }

            @Override
            public void onError() {
            }

            @Override
            public void onFinish() {
            }
        });

        RequestBody avatar2 = RequestBody.create(MediaType.parse("image/*"), imageFile);

        RequestBody body = new MultipartBuilder().type(MultipartBuilder.FORM)
                .addFormDataPart("desc", "通过Body方式上传")
                .addFormDataPart("avatar", imageFile.getName(), avatar1)
                .build();

        mLocalServerEngine.updateAvatarByBody(body)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(() -> showLoadingDialog(R.string.loading))
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mMsgObserver);
    }

    private void updateAvatarByPart(File imageFile) {
        // 直接传String类型时，会多两个双引号
//        mLocalServerEngine.updateAvatarByPart(RequestBody.create(MultipartBuilder.FORM, imageFile), "通过Part方式上传")
//                .subscribeOn(Schedulers.io())
//                .doOnSubscribe(() -> showLoadingDialog(R.string.loading))
//                .subscribeOn(AndroidSchedulers.mainThread())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(mMsgObserver);

        mLocalServerEngine.updateAvatarByPart(RequestBody.create(MultipartBuilder.FORM, imageFile), RequestBody.create(MultipartBuilder.FORM, "通过Part方式上传"))
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(() -> showLoadingDialog(R.string.loading))
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mMsgObserver);
    }
}
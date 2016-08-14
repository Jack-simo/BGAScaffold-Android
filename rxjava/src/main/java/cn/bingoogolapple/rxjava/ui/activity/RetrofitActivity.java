package cn.bingoogolapple.rxjava.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.view.View;

import com.trello.rxlifecycle.ActivityEvent;

import java.io.File;
import java.io.IOException;

import cn.bingoogolapple.basenote.activity.TitlebarActivity;
import cn.bingoogolapple.basenote.engine.ApiParams;
import cn.bingoogolapple.basenote.util.Logger;
import cn.bingoogolapple.basenote.util.Md5Util;
import cn.bingoogolapple.basenote.util.PermissionUtil;
import cn.bingoogolapple.basenote.util.RxUtil;
import cn.bingoogolapple.basenote.util.StorageUtil;
import cn.bingoogolapple.basenote.util.ToastUtil;
import cn.bingoogolapple.rxjava.R;
import cn.bingoogolapple.rxjava.engine.LocalServerEngine;
import cn.bingoogolapple.rxjava.engine.RemoteServerEngine;
import cn.bingoogolapple.rxjava.model.JsonResp;
import cn.bingoogolapple.rxjava.model.Person;
import cn.bingoogolapple.rxjava.model.RefreshModel;
import cn.bingoogolapple.rxjava.util.GlobalHeaderInterceptor;
import cn.bingoogolapple.rxjava.util.LoggingInterceptor;
import cn.bingoogolapple.rxjava.util.ProgressRequestBody;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class RetrofitActivity extends TitlebarActivity {
    private static final int REQUEST_CODE_CHOOSE_PHOTO = 1;
    private LocalServerEngine mLocalServerEngine;
    private RemoteServerEngine mRemoteServerEngine;

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
        public Response intercept(Interceptor.Chain chain) throws IOException {
            Response originalResponse = chain.proceed(chain.request());
            return originalResponse.newBuilder()
                    .header("Cache-Control", "max-age=60")
                    .build();
        }
    };

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        setTitle("Retrofit学习笔记");

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
//        logging.setLevel(HttpLoggingInterceptor.Level.BODY);


        OkHttpClient okHttpClient = new OkHttpClient
                .Builder()
                .addInterceptor(new LoggingInterceptor())
//                            .addInterceptor(new GzipRequestInterceptor())
                .addInterceptor(new GlobalHeaderInterceptor())
//                            .addInterceptor(REWRITE_CACHE_CONTROL_INTERCEPTOR)
                .addInterceptor(logging)
                .build();

        mLocalServerEngine = new Retrofit.Builder()
                .baseUrl("http://192.168.199.190:8080/netnote/retrofit/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build()
                .create(LocalServerEngine.class);


        mRemoteServerEngine = new Retrofit.Builder()
                .baseUrl("http://7xk9dj.com1.z0.glb.clouddn.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build()
                .create(RemoteServerEngine.class);
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
//        mLocalServerEngine.loginGet("hello", "world")
//                .subscribeOn(Schedulers.io())
//                .doOnSubscribe(() -> showLoadingDialog(R.string.loading))
//                .subscribeOn(AndroidSchedulers.mainThread())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(mMsgObserver);

        showLoadingDialog(R.string.loading);
        mLocalServerEngine.loginGet("hello", "world")
                .compose(RxUtil.applySchedulers())
                .subscribe(mMsgObserver);
    }

    public void loginGetMap(View v) {
        mLocalServerEngine.loginGetMap(new ApiParams("username", "hello").with("password", "world"))
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

    public void loginPostMap(View v) {
        mLocalServerEngine.loginPostMap(new ApiParams("username", "hello").with("password", "world"))
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

        RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
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

        mLocalServerEngine.updateAvatarByPart(RequestBody.create(MultipartBody.FORM, imageFile), RequestBody.create(MultipartBody.FORM, "通过Part方式上传"))
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(() -> showLoadingDialog(R.string.loading))
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mMsgObserver);
    }

    public void dynamicUrl(View v) {
        mLocalServerEngine.dynamicUrl("http://7xk9dj.com1.z0.glb.clouddn.com/refreshlayout/api/defaultdata.json")
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(() -> showLoadingDialog(R.string.loading))
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.io())
                .flatMap(refreshModels -> Observable.from(refreshModels))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<RefreshModel>() {
                    @Override
                    public void onCompleted() {
                        dismissLoadingDialog();
                        ToastUtil.show("数据加载成功");
                    }

                    @Override
                    public void onError(Throwable e) {
                        dismissLoadingDialog();
                        ToastUtil.show("数据加载失败");
                    }

                    @Override
                    public void onNext(RefreshModel refreshModel) {
                        Logger.i(TAG, refreshModel.title);
                    }
                });
    }

    public void download(View v) {
        mRemoteServerEngine.download()
                .compose(bindUntilEvent(ActivityEvent.DESTROY))
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(() -> showLoadingDialog(R.string.loading))
                .subscribeOn(AndroidSchedulers.mainThread())
                .map(new Func1<ResponseBody, File>() {
                    @Override
                    public File call(ResponseBody responseBody) {
                        File file = new File(StorageUtil.getFileDir(), Md5Util.md5("medianote/oppo.mp4") + ".mp4");
                        StorageUtil.writeFile(file, responseBody.byteStream());
                        return file;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<File>() {
                    @Override
                    public void call(File file) {
                        dismissLoadingDialog();
                        ToastUtil.show("下载成功");
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        dismissLoadingDialog();
                        ToastUtil.show("下载失败" + throwable.getMessage());
                    }
                });
    }
}
package cn.bingoogolapple.bottomnavigation.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;

import cn.bingoogolapple.basenote.activity.TitlebarActivity;
import cn.bingoogolapple.bottomnavigation.R;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/9/7 下午1:21
 * 描述:
 */
public class SchemeActivity extends TitlebarActivity {
    private TextView mResultTv;

    @Override
    protected boolean isSupportSwipeBack() {
        return true;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_scheme);
        mResultTv = getViewById(R.id.tv_scheme_result);
    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        setTitle("Scheme");

        Intent intent = getIntent();
        Uri uri = intent.getData();
        StringBuilder sb = new StringBuilder();
        if (uri != null) {
            sb.append("dataString:" + intent.getDataString()).append("\n");
            sb.append("scheme:" + intent.getScheme()).append("\n");
            sb.append("host: " + uri.getHost()).append("\n");
            sb.append("port: " + uri.getPort()).append("\n");
            sb.append("path: " + uri.getPath()).append("\n");
            sb.append("pathSegments: " + uri.getPathSegments().toString()).append("\n");
            sb.append("queryString: " + uri.getQuery()).append("\n");
            sb.append("param1: " + uri.getQueryParameter("param1")).append("\n");
            sb.append("param2: " + uri.getQueryParameter("param2")).append("\n");
            sb.append("param3: " + uri.getQueryParameter("param3"));
        }

        mResultTv.setText(sb.toString());
    }
}

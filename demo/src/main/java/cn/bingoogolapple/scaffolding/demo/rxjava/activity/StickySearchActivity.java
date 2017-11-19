package cn.bingoogolapple.scaffolding.demo.rxjava.activity;

import android.os.Bundle;
import android.support.v4.util.LongSparseArray;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;

import com.orhanobut.logger.Logger;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.bingoogolapple.baseadapter.BGADivider;
import cn.bingoogolapple.baseadapter.BGARecyclerViewAdapter;
import cn.bingoogolapple.baseadapter.BGAViewHolderHelper;
import cn.bingoogolapple.scaffolding.demo.R;
import cn.bingoogolapple.scaffolding.demo.rxjava.api.Engine;
import cn.bingoogolapple.scaffolding.demo.rxjava.entity.Blog;
import cn.bingoogolapple.scaffolding.demo.rxjava.entity.Category;
import cn.bingoogolapple.scaffolding.util.NetResult;
import cn.bingoogolapple.scaffolding.util.StringUtil;
import cn.bingoogolapple.scaffolding.util.ToastUtil;
import cn.bingoogolapple.scaffolding.view.MvcActivity;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.subjects.PublishSubject;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/11/5 下午5:53
 * 描述:
 */
public class StickySearchActivity extends MvcActivity {
    private SearchView mKeywordSv;
    private PublishSubject<String> mKeywordPs;

    private RecyclerView mBlogRv;
    private LinearLayoutManager mLayoutManager;
    private BlogAdapter mBlogAdapter;

    private Engine.BlogApi mBlogApi;
    private LongSparseArray<String> mCategoryArray = new LongSparseArray<>();
    private List<Category> mCategoryList = new ArrayList<>();

    @Override
    protected int getRootLayoutResID() {
        return R.layout.activity_sticky_search;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        mKeywordSv = findViewById(R.id.sv_sticky_search_keyword);
        mBlogRv = findViewById(R.id.rv_sticky_search_blog);
    }

    @Override
    protected void setListener() {
        mKeywordSv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String keyword) {
                if (mKeywordPs == null) {
                    initSearch();
                }
                mKeywordPs.onNext(keyword);
                return true;
            }
        });
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        mBlogApi = Engine.getRxJavaApi();
        initStickyList();
    }

    // 初始化吸顶效果列表
    private void initStickyList() {
        mLayoutManager = new LinearLayoutManager(this);
        mBlogRv.setLayoutManager(mLayoutManager);
        mBlogAdapter = new BlogAdapter(mBlogRv);
        mBlogRv.addItemDecoration(BGADivider.newBitmapDivider()
                .setStartSkipCount(0)
                .setDelegate(new BGADivider.StickyDelegate() {
                    @Override
                    protected boolean isCategoryFistItem(int position) {
                        return mBlogAdapter.isCategoryFistItem(position);
                    }

                    @Override
                    protected String getCategoryName(int position) {
                        return mCategoryArray.get(mBlogAdapter.getItem(position).getCategoryId(), "默认分类");
                    }

                    @Override
                    protected int getFirstVisibleItemPosition() {
                        return mLayoutManager.findFirstVisibleItemPosition();
                    }
                }));
        mBlogRv.setAdapter(mBlogAdapter);
    }

    // 初始化搜索
    private void initSearch() {
        /**
         * CombineLatest 和 zip 类似，都是组合两个 Observable 的数据为新的 Observable
         * zip 当原始 Observable 中每一个都发射了一条数据时才发射数据
         * CombineLatest 当原始 Observable 中任何一个发射了一条数据时发射数据
         */
        Observable.combineLatest(getCategoryListObservable(), getBlogListObservable(), (blogCategoryList, blogList) -> {
            convertToCategoryArray(blogCategoryList);
            return blogList;
        }).observeOn(AndroidSchedulers.mainThread())
                .compose(bindToLifecycle())
                .retryWhen(new Function<Observable<Throwable>, ObservableSource<?>>() {
                    private int mRetryCount;

                    @Override
                    public ObservableSource<?> apply(Observable<Throwable> throwableObservable) throws Exception {
                        return throwableObservable.flatMap(throwable -> {
                            if (throwable instanceof ConnectException) {
                                mRetryCount++;
                                if (mRetryCount > 3) {
                                    Logger.d("错误超过3次");
                                    return Observable.error(throwable);
                                } else {
                                    Logger.d("错误" + mRetryCount + "次");
                                    return Observable.timer(mRetryCount * 1000, TimeUnit.MILLISECONDS);
                                }
                            } else {
                                Logger.d("未知异常");
                                return Observable.error(throwable);
                            }
                        });
                    }
                }) // 处理 onError 时重订阅，避免发生一次错误后就再也搜索不到结果。Observer 的 onError 将不会被回调
                .filter(result -> StringUtil.isNotEmpty(mKeywordSv.getQuery())) // 避免返回结果时，如果当前搜索框关键字为空则忽略此次搜索结果
                .subscribe(result -> {
                    mBlogAdapter.setData(result);
                }, throwable -> {
                    mKeywordPs = null;
                    Logger.d("错误：" + throwable.getMessage());
                    ToastUtil.show("错误：" + throwable.getMessage());
                });
    }

    // 获取发送博客列表的被观察者
    private Observable<List<Blog>> getBlogListObservable() {
        mKeywordPs = PublishSubject.create();
        return mKeywordPs.debounce(400, TimeUnit.MILLISECONDS) // debounce 默认是在 computation 线程的。发送一个延时消息给下游，如果在这段延时时间内没有收到新的请求，那么下游就会收到该消息；而如果在这段延时时间内收到来新的请求，那么就会取消之前的消息，并重新发送一个新的延时消息
                .observeOn(AndroidSchedulers.mainThread()) // 这里手动将后续操作符切换到主线程，否则 filter 也是在 computation 线程的
                .filter(keyword -> { // 只有返回 true 时，才会将事件发送给下游，否则就丢弃该事件
                    if (StringUtil.isNotEmpty(keyword)) {
                        return true;
                    } else {
                        mBlogAdapter.clear();
                        return false;
                    }
                }).switchMap(keyword -> mBlogApi.findBlogList(keyword)
                        .flatMap(new Function<NetResult<List<Blog>>, ObservableSource<List<Blog>>>() {
                            @Override
                            public ObservableSource<List<Blog>> apply(NetResult<List<Blog>> netResult) throws Exception {
                                return Observable.just(netResult.data);
                            }
                        }));
    }

    // 获取发送分类列表的被观察者
    private Observable<List<Category>> getCategoryListObservable() {
        // concat 操作符是接收若干个 Observables，发射数据是有序的，不会交叉。concat + takeUntil 实现二级缓存
        return Observable.concat(
                Observable.just(mCategoryList),
                mBlogApi.getCategoryList().flatMap(netResult -> Observable.just(netResult.data))
        ).takeUntil(categoryList -> categoryList != null && mCategoryList.size() > 0)
                .doOnNext(categoryList -> convertToCategoryArray(categoryList));
    }

    // 将分类集合转成 id 和名称映射的 LongSparseArray
    private void convertToCategoryArray(List<Category> categoryList) {
        // 只有当 mCategoryList 为空时才转换
        if (mCategoryList.size() == 0) {
            mCategoryArray.clear();
            for (Category category : categoryList) {
                mCategoryArray.put(category.getId(), category.getName());
            }
            mCategoryList = categoryList;
        }
    }

    private class BlogAdapter extends BGARecyclerViewAdapter<Blog> {
        public BlogAdapter(RecyclerView recyclerView) {
            super(recyclerView, R.layout.item_blog);
        }

        @Override
        protected void fillData(BGAViewHolderHelper helper, int position, Blog blog) {
            helper.setText(R.id.tv_item_blog_title, blog.getTitle());
            helper.setText(R.id.tv_item_blog_content, blog.getContent());
        }

        // 是否为该分类下的第一个条目
        public boolean isCategoryFistItem(int position) {
            // 第一条数据是该分类下的第一个条目
            if (position == 0) {
                return true;
            }

            long currentCategoryId = getItem(position).getCategoryId();
            long preCategoryId = getItem(position - 1).getCategoryId();
            // 当前条目的分类 id 和上一个条目的分类 id 不相等时，当前条目为该分类下的第一个条目
            return currentCategoryId != preCategoryId;
        }
    }
}
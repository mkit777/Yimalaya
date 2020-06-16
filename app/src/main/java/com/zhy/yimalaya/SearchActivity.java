package com.zhy.yimalaya;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.word.HotWord;
import com.ximalaya.ting.android.opensdk.model.word.QueryResult;
import com.zhy.yimalaya.adapters.AlbumListAdapter;
import com.zhy.yimalaya.adapters.SuggestWordsAdapter;
import com.zhy.yimalaya.base.BaseApplication;
import com.zhy.yimalaya.interfaces.ISearchCallBack;
import com.zhy.yimalaya.interfaces.ISearchPresenter;
import com.zhy.yimalaya.presenters.AlbumDetailPresenter;
import com.zhy.yimalaya.presenters.SearchPresenter;
import com.zhy.yimalaya.views.FlowView;
import com.zhy.yimalaya.views.UiLoader;

import java.util.List;
import java.util.stream.Collectors;

public class SearchActivity extends AppCompatActivity implements ISearchCallBack, UiLoader.OnRefreshListener {

    private ImageView mBackBtn;
    private EditText mSearchInput;
    private TextView mSearchBtn;
    private FrameLayout mSearchResultPlaceholder;
    private FlowView mHotWordsContainer;
    private ISearchPresenter mSearchPresenter;
    private UiLoader mUiLoader;
    private AlbumListAdapter mAlbumAdapter;
    private InputMethodManager mManager;
    private ImageView mClearInputBtn;
    private RecyclerView mSuggestWordsContainer;
    private SuggestWordsAdapter mSuggestWordsAdapter;
    private RecyclerView mSearResultContainer;
    private TwinklingRefreshLayout mRefreshLayout;
    private View mSearchResultView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initView();
        setupEvent();
        initPresenter();
    }


    private void initView() {
        mBackBtn = findViewById(R.id.back_btn);
        mSearchInput = findViewById(R.id.search_input);
        mSearchBtn = findViewById(R.id.search_btn);

        mSearchResultPlaceholder = findViewById(R.id.search_result_placeholder);
        mUiLoader = new UiLoader(this) {
            @Override
            protected View getSuccessView(LayoutInflater inflater, ViewGroup parent) {
                return createSuccessView();
            }
        };
        mUiLoader.setOnRefreshClickListener(this);
        mSearchResultPlaceholder.addView(mUiLoader);
        mSearchResultPlaceholder.setVisibility(View.GONE);

        mHotWordsContainer = findViewById(R.id.hot_words_container);

        mManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mSearchInput.setFocusable(true);
        BaseApplication.handler.postDelayed(() -> {
            mManager.showSoftInput(mSearchInput, InputMethodManager.SHOW_IMPLICIT);
        }, 500);


        mClearInputBtn = findViewById(R.id.clear_input_btn);
        mClearInputBtn.setVisibility(View.GONE);


        mSuggestWordsContainer = findViewById(R.id.suggestWords);
        mSuggestWordsContainer.setLayoutManager(new LinearLayoutManager(this));
        mSuggestWordsAdapter = new SuggestWordsAdapter();
        mSuggestWordsContainer.setAdapter(mSuggestWordsAdapter);


        mRefreshLayout = (TwinklingRefreshLayout) LayoutInflater.from(this).inflate(R.layout.view_search_result, null);
        mSearResultContainer = mRefreshLayout.findViewById(R.id.search_result_view);
        mSearResultContainer.setLayoutManager(new LinearLayoutManager(this));
        mAlbumAdapter = new AlbumListAdapter();
        mSearResultContainer.setAdapter(mAlbumAdapter);
    }

    private View createSuccessView() {
        return mRefreshLayout;
    }

    private void setupEvent() {
        mBackBtn.setOnClickListener(v -> finish());

        mHotWordsContainer.setOnItemClickListener(view -> {
            String hotWord = view.getText().toString();
            mSearchInput.setText(hotWord);
            mSearchInput.setSelection(hotWord.length());
            doSearch();
        });


        mSearchBtn.setOnClickListener(v -> doSearch());

        mClearInputBtn.setOnClickListener(v -> {
            mSearchInput.setText("");
        });

        mSearchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    showHotWordsContainer();
                } else {
                    getSuggestWords(s.toString());
                    showSuggestWordsContainer();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mSuggestWordsAdapter.setItemClickListener(keyword -> {
            mSearchInput.setText(keyword);
            mSearchInput.setSelection(keyword.length());
            doSearch();
        });


        mRefreshLayout.setEnableRefresh(false);
        mRefreshLayout.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onRefresh(TwinklingRefreshLayout refreshLayout) {
                doSearch();
            }

            @Override
            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
                doLoadMore();
            }
        });

        mAlbumAdapter.setOnItemClickListener((position, album) -> {
            Intent intent = new Intent(SearchActivity.this, DetailActivity.class);
            intent.putExtra("album", album);
            startActivity(intent);
        });
    }

    private void doLoadMore() {
        mSearchPresenter.loadMore();
    }

    private void getSuggestWords(String keywords) {
        mSearchPresenter.getSuggestWords(keywords);

    }

    private void doSearch() {
        if (mSearchInput.getText().length() == 0) {
            Toast.makeText(this, "搜索内容不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        showSearchResultContainer();
        mSearchPresenter.search(mSearchInput.getText().toString());
        mUiLoader.updateState(UiLoader.State.LOADING);
    }

    private void initPresenter() {
        // 注册回调
        mSearchPresenter = SearchPresenter.getInstance();
        mSearchPresenter.registerViewCallback(this);
        // 获取数据
        mSearchPresenter.getHotWord();
    }


    private void showHotWordsContainer() {
        mSearchResultPlaceholder.setVisibility(View.GONE);
        mSuggestWordsContainer.setVisibility(View.GONE);
        mHotWordsContainer.setVisibility(View.VISIBLE);
        mClearInputBtn.setVisibility(View.GONE);
    }

    private void showSuggestWordsContainer() {
        mSearchResultPlaceholder.setVisibility(View.GONE);
        mSuggestWordsContainer.setVisibility(View.VISIBLE);
        mHotWordsContainer.setVisibility(View.GONE);
        mClearInputBtn.setVisibility(View.VISIBLE);
    }

    private void showSearchResultContainer() {
        mSearchResultPlaceholder.setVisibility(View.VISIBLE);
        mSuggestWordsContainer.setVisibility(View.GONE);
        mHotWordsContainer.setVisibility(View.GONE);
        mClearInputBtn.setVisibility(View.VISIBLE);
    }



    //-------------- SearchPresenter 回调 -------------------------------
    @Override
    public void onSearchResult(List<Album> albums) {
        // 取消刷新
        mRefreshLayout.finishRefreshing();

        mUiLoader.updateState(UiLoader.State.SUCCESS);
        // 隐藏键盘
        mManager.hideSoftInputFromWindow(mSearchInput.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

        mAlbumAdapter.setData(albums);
        mAlbumAdapter.notifyDataSetChanged();
        mSearResultContainer.scrollToPosition(0);
        showSearchResultContainer();
    }

    @Override
    public void OnSearchError() {
        mUiLoader.updateState(UiLoader.State.NETWORK_ERROR);
    }

    @Override
    public void onLoadMoreResult(List<Album> albums, boolean isOk) {
        // 取消加载更多
        mRefreshLayout.finishLoadmore();
        if (isOk) {
            // 更新数据
            mAlbumAdapter.addData(albums);
            mAlbumAdapter.notifyDataSetChanged();
        } else {
            Toast.makeText(this, "没有更多数据了~", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLoadSuggestWordResult(List<QueryResult> keywords) {
        mSuggestWordsAdapter.setData(keywords);
    }

    @Override
    public void onLoadHotWords(List<HotWord> hotWords) {
        List<String> collect = hotWords.stream()
                .map(HotWord::getSearchword)
                .collect(Collectors.toList());
        mHotWordsContainer.setData(collect);
    }

    @Override
    public void onRefresh() {
        mSearchPresenter.research();
    }
}

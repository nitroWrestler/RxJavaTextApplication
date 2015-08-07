package com.example.admin.rxjavatestapplication.design;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.appunite.rx.android.MoreViewObservables;
import com.appunite.rx.functions.BothParams;
import com.example.admin.rxjavatestapplication.BaseFragment;
import com.example.admin.rxjavatestapplication.DetailsActivity;
import com.example.admin.rxjavatestapplication.MainApplication;
import com.example.admin.rxjavatestapplication.MyListViewAdapter;
import com.example.admin.rxjavatestapplication.RetrofitPresenter;
import com.example.admin.rxjavatestapplication.helpers.LoadMoreHelper;
import com.google.common.collect.ImmutableList;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import dagger.Module;
import rx.functions.Action1;

public class TabFragment extends BaseFragment {

    @Inject
    MyListViewAdapter myListViewAdapter;

    public static final String ARG_PAGE = "arg_page";

    private RetrofitPresenter presenter;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;

    public static TabFragment newInstance(int pageNumber) {
        TabFragment myFragment = new TabFragment();
        Bundle arguments = new Bundle();
        arguments.putInt(ARG_PAGE, pageNumber + 1);
        myFragment.setArguments(arguments);
        return myFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        presenter = MainApplication
                .fromApplication(getActivity().getApplication())
                .objectGraph()
                .plus(new TabFragmentModule())
                .get(RetrofitPresenter.class);

        MainApplication
                .fromApplication(getActivity().getApplication())
                .objectGraph()
                .plus(new TabFragmentModule())
                .inject(this);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        int pageNumber = arguments.getInt(ARG_PAGE);
        recyclerView = new RecyclerView(getActivity());
        recyclerView.setAdapter(myListViewAdapter);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        return recyclerView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        presenter.listObservable()
//                .compose(lifecycleMainObservable.<ImmutableList<RetrofitPresenter.AdapterItem>>bindLifecycle())
                .subscribe(myListViewAdapter);

        presenter.openDetailsObservable()
//                .compose(lifecycleMainObservable.<BothParams<RetrofitPresenter.AdapterItem, ImageView>>bindLifecycle())
                .subscribe(startDetailsActivityAction(getActivity()));

        MoreViewObservables.scroll(recyclerView)
                .filter(LoadMoreHelper.mapToNeedLoadMore(linearLayoutManager, myListViewAdapter))
//                .compose(lifecycleMainObservable.bindLifecycle())
                .subscribe(presenter.loadMoreObserver());
    }

    @Nonnull
    private static Action1<BothParams<RetrofitPresenter.AdapterItem, ImageView>> startDetailsActivityAction(final Activity activity) {
        return new Action1<BothParams<RetrofitPresenter.AdapterItem, ImageView>>() {
            @Override
            public void call(BothParams<RetrofitPresenter.AdapterItem, ImageView> bothParams) {
                //noinspection unchecked
                ActivityOptionsCompat options =  ActivityOptionsCompat.makeSceneTransitionAnimation(activity, bothParams.param2(), "profile");
                ActivityCompat.startActivity(activity,
                        DetailsActivity.getIntent(activity, bothParams.param1().getId(), bothParams.param1().getOffset()),
                        options.toBundle());

            }
        };
    }

    @Module (
            injects = {
                    TabFragment.class,
                    RetrofitPresenter.class
            },
            addsTo = MainApplication.Module.class
    )
    class TabFragmentModule {
    }
}

package com.example.admin.rxjavatestapplication.design;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.admin.rxjavatestapplication.MainApplication;
import com.example.admin.rxjavatestapplication.MyListViewAdapter;
import com.example.admin.rxjavatestapplication.RetrofitPresenter;

import javax.inject.Inject;

import dagger.Module;

public class TabFragment extends Fragment {

    @Inject
    MyListViewAdapter myListViewAdapter;

    private RetrofitPresenter presenter;

    public static final String ARG_PAGE = "arg_page";

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

        presenter.listObservable()
//                .compose(lifecycleMainObservable.<ImmutableList<RetrofitPresenter.AdapterItem>>bindLifecycle())
                .subscribe(myListViewAdapter);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        int pageNumber = arguments.getInt(ARG_PAGE);
        RecyclerView recyclerView = new RecyclerView(getActivity());
        recyclerView.setAdapter(myListViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return recyclerView;
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

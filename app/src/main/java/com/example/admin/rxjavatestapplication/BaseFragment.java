package com.example.admin.rxjavatestapplication;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

//import com.appunite.onemedical.dagger.BaseActivityComponent;
//import com.appunite.onemedical.dagger.FragmentModule;
import com.appunite.rx.android.LifecycleMainObservable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import butterknife.ButterKnife;
import rx.android.lifecycle.LifecycleEvent;
import rx.subjects.BehaviorSubject;

public abstract class BaseFragment extends Fragment {

    @Nonnull
    private final BehaviorSubject<LifecycleEvent> lifecycleSubject = BehaviorSubject.create();
    protected final LifecycleMainObservable lifecycleMainObservable = new LifecycleMainObservable(
            new LifecycleMainObservable.LifecycleProviderFragment(lifecycleSubject, this));

//    protected abstract void injectComponent(@Nonnull BaseActivityComponent baseActivityComponent,
//                                            @Nonnull FragmentModule fragmentModule,
//                                            @Nullable Bundle savedInstanceState);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        final BaseActivityComponent activityComponent = ((BaseActivity) getActivity())
//                .getActivityComponent();
//        injectComponent(activityComponent, new FragmentModule(this), savedInstanceState);
//        lifecycleSubject.onNext(LifecycleEvent.CREATE);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
        lifecycleSubject.onNext(LifecycleEvent.CREATE_VIEW);
    }

    @Override
    public void onStart() {
        super.onStart();
        lifecycleSubject.onNext(LifecycleEvent.START);
    }

    @Override
    public void onResume() {
        super.onResume();
        lifecycleSubject.onNext(LifecycleEvent.RESUME);
    }

    @Override
    public void onPause() {
        lifecycleSubject.onNext(LifecycleEvent.PAUSE);
        super.onPause();
    }

    @Override
    public void onStop() {
        lifecycleSubject.onNext(LifecycleEvent.STOP);
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        lifecycleSubject.onNext(LifecycleEvent.DESTROY_VIEW);
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        lifecycleSubject.onNext(LifecycleEvent.DESTROY);
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        lifecycleSubject.onNext(LifecycleEvent.DETACH);
        super.onDetach();
    }
}
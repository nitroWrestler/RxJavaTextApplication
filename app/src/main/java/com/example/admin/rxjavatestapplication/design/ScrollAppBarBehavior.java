package com.example.admin.rxjavatestapplication.design;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;

public class ScrollAppBarBehavior extends AppBarLayout.Behavior {

    public ScrollAppBarBehavior(Context context, AttributeSet attrs) {
        super();
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, AppBarLayout child,
                                       View directTargetChild, View target,
                                       int nestedScrollAxes) {
        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL ||
                super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target, nestedScrollAxes);
    }

    @Override
    public void onNestedScroll(CoordinatorLayout coordinatorLayout, AppBarLayout child,
                               View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed,
                dyUnconsumed);

        if (dyConsumed > 0 && child.getVisibility() == View.VISIBLE) {
//            child.setVisibility(View.GONE);
            child.setVisibility(View.INVISIBLE);
        } else if (dyConsumed < 0 && child.getVisibility() != View.VISIBLE) {
            child.setVisibility(View.VISIBLE);
        }
    }

//    public void show() {
//        if(this.getVisibility() != 0) {
//            this.setVisibility(0);
//            if(ViewCompat.isLaidOut(this)) {
//                this.mImpl.show();
//            }
//
//        }
//    }
//
//    public void hide(AppBarLayout child) {
//        if(child.getVisibility() == View.VISIBLE) {
//            if(ViewCompat.isLaidOut(child) && !child.isInEditMode()) {
//                child.setVisibility(View.GONE);
//            } else {
//                child.setVisibility(View.GONE);
//            }

//        }
//    }
}

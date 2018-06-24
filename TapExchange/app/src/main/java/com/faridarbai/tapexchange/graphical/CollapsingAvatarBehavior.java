package com.faridarbai.tapexchange.graphical;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.faridarbai.tapexchange.R;

public class CollapsingAvatarBehavior extends CoordinatorLayout.Behavior<View> {
	private static final String TAG = "CollapsingAvatarBehavio";

    private final static int X = 0;
    private final static int Y = 1;
    private final static int WIDTH = 2;
    private final static int HEIGHT = 3;

    private int mTargetId;

    private int[] mView;

    private int[] mTarget;

    public CollapsingAvatarBehavior() {
    }

    public CollapsingAvatarBehavior(Context context, AttributeSet attrs) {

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CollapsingAvatarBehavior);
            mTargetId = a.getResourceId(R.styleable.CollapsingAvatarBehavior_collapsedTarget, 0);
            a.recycle();
        }

        if (mTargetId == 0) {
            throw new IllegalStateException("collapsedTarget attribute not specified on view for behavior");
        }
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
        return dependency instanceof AppBarLayout;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {

        setup(parent, child);

        AppBarLayout appBarLayout = (AppBarLayout) dependency;

        int range = appBarLayout.getTotalScrollRange();
        
        if(range==0){
        	range=1;
		  }
        float factor = -appBarLayout.getY() / range;
        
        float factor_slow = (float)Math.pow(factor,19);
        float factor_med = (float)Math.pow(factor,3);
        
        int left = mView[X] + (int) (factor_slow * (mTarget[X] - mView[X]));
        int top = mView[Y] + (int) (factor * (mTarget[Y] - mView[Y]));
        int width = mView[WIDTH] + (int) (factor_med * (mTarget[WIDTH] - mView[WIDTH]));
        int height = mView[HEIGHT] + (int) (factor_med * (mTarget[HEIGHT] - mView[HEIGHT]));

        CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) child.getLayoutParams();
        lp.width = width;
        lp.height = height;
        child.setLayoutParams(lp);
        child.setX(left);
        child.setY(top);

        return true;
    }

    private void setup(CoordinatorLayout parent, View child) {
    		if (mView != null) return;

        mView = new int[4];
        mTarget = new int[4];

        mView[X] = (int) child.getX();
        mView[Y] = (int) child.getY();
        mView[WIDTH] = child.getWidth();
        mView[HEIGHT] = child.getHeight();

        View target = parent.findViewById(R.id.avatar_collapsed_target);
        
        if (target == null) {
            throw new IllegalStateException("target view not found");
        }
        
        mTarget[WIDTH] += target.getWidth();
        mTarget[HEIGHT] += target.getHeight();

        View view = target;
        while (view != parent) {
            mTarget[X] += (int) view.getX();
            mTarget[Y] += (int) view.getY();
            view = (View) view.getParent();
        }
	
        
        String log_str = String.format("[x,y]:[%d,%d]\n[w,h]:[%d,%d]\n\n[x,y]:[%d,%d]",
				  mView[X], mView[Y], mView[WIDTH], mView[HEIGHT], mTarget[X], mTarget[Y]);
				  
		 Log.d(TAG, "setup: " + log_str);

    }
}
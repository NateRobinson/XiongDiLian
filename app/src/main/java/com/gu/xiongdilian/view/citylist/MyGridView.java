package com.gu.xiongdilian.view.citylist;

/**
 * Created by Administrator on 2015/6/29.
 */

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

public class MyGridView extends GridView {
    public MyGridView(Context paramContext) {
        super(paramContext);
    }

    public MyGridView(Context paramContext, AttributeSet paramAttributeSet) {
        super(paramContext, paramAttributeSet);
    }

    public MyGridView(Context paramContext, AttributeSet paramAttributeSet, int paramInt) {
        super(paramContext, paramAttributeSet, paramInt);
    }

    public void onMeasure(int paramInt1, int paramInt2) {
        //这个也就是父组件，能够给出的最大的空间，当前组件的长或宽最大只能为这么大，当然也可以比这个小
        super.onMeasure(paramInt1, MeasureSpec.makeMeasureSpec(536870911, MeasureSpec.AT_MOST));

    }
}

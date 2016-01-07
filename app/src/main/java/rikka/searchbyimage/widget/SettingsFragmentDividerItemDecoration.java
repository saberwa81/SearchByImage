package rikka.searchbyimage.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import rikka.searchbyimage.R;

/**
 * Created by Rikka on 2016/1/4.
 */
public class SettingsFragmentDividerItemDecoration extends RecyclerView.ItemDecoration {
    private Drawable mDivider;


    public SettingsFragmentDividerItemDecoration(Context context) {
        mDivider = context.getResources().getDrawable(R.drawable.line_divider);
    }


    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();


        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);

            boolean canDraw = ((i < childCount - 1)
                    && parent.getChildAt(i + 1).findViewById(android.R.id.summary) != null
                    && child.findViewById(android.R.id.summary) != null);

            if (!canDraw) {
                continue;
            }

            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

            int top = child.getBottom() + params.bottomMargin;
            int bottom = top + mDivider.getIntrinsicHeight();

            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }
}
package com.ultimate.music.downloader.downloader;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class FlowLayout extends ViewGroup {
    private List<List<View>> mAllViews;
    private List<Integer> mLineHeight;

    public FlowLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mAllViews = new ArrayList();
        this.mLineHeight = new ArrayList();
    }

    public FlowLayout(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public FlowLayout(Context context) {
        this(context, null);
    }


    public void onMeasure(int i, int i2) {
        int i3;
        int size = MeasureSpec.getSize(i);
        int mode = MeasureSpec.getMode(i);
        int size2 = MeasureSpec.getSize(i2);
        int mode2 = MeasureSpec.getMode(i2);
        int childCount = getChildCount();
        int i4 = 0;
        int i5 = 0;
        int i6 = 0;
        int i7 = 0;
        int i8 = 0;
        while (i4 < childCount) {
            View childAt = getChildAt(i4);
            measureChild(childAt, i, i2);
            MarginLayoutParams marginLayoutParams = (MarginLayoutParams) childAt.getLayoutParams();
            int i9 = size2;
            int measuredWidth = childAt.getMeasuredWidth() + marginLayoutParams.leftMargin + marginLayoutParams.rightMargin;
            int measuredHeight = childAt.getMeasuredHeight() + marginLayoutParams.topMargin + marginLayoutParams.bottomMargin;
            int i10 = i5 + measuredWidth;
            if (i10 > (size - getPaddingLeft()) - getPaddingRight()) {
                i6 = Math.max(i6, i5);
                i8 += i7;
                i7 = measuredHeight;
                i5 = measuredWidth;
            } else {
                i7 = Math.max(i7, measuredHeight);
                i5 = i10;
            }
            if (i4 == childCount - 1) {
                i8 += i7;
                i6 = Math.max(i5, i6);
            }
            i4++;
            size2 = i9;
        }
        int i11 = size2;
        if (mode != 1073741824) {
            size = getPaddingRight() + i6 + getPaddingLeft();
        }
        if (mode2 == 1073741824) {
            i3 = i11;
        } else {
            i3 = i8 + getPaddingTop() + getPaddingBottom();
        }
        setMeasuredDimension(size, i3);
    }

    public LayoutParams generateLayoutParams(AttributeSet attributeSet) {
        return new MarginLayoutParams(getContext(), attributeSet);
    }

    /* access modifiers changed from: protected */
    @SuppressLint("WrongConstant")
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        this.mAllViews.clear();
        this.mLineHeight.clear();
        int width = getWidth();
        ArrayList arrayList = new ArrayList();
        int childCount = getChildCount();
        ArrayList arrayList2 = arrayList;
        int i5 = 0;
        int i6 = 0;
        for (int i7 = 0; i7 < childCount; i7++) {
            View childAt = getChildAt(i7);
            MarginLayoutParams marginLayoutParams = (MarginLayoutParams) childAt.getLayoutParams();
            int measuredWidth = childAt.getMeasuredWidth();
            int measuredHeight = childAt.getMeasuredHeight();
            if (measuredWidth + i6 + marginLayoutParams.leftMargin + marginLayoutParams.rightMargin > (width - getPaddingLeft()) - getPaddingRight()) {
                this.mLineHeight.add(Integer.valueOf(i5));
                this.mAllViews.add(arrayList2);
                i5 = marginLayoutParams.topMargin + measuredHeight + marginLayoutParams.bottomMargin;
                arrayList2 = new ArrayList();
                i6 = 0;
            }
            i6 += measuredWidth + marginLayoutParams.leftMargin + marginLayoutParams.rightMargin;
            i5 = Math.max(i5, measuredHeight + marginLayoutParams.topMargin + marginLayoutParams.bottomMargin);
            arrayList2.add(childAt);
        }
        this.mLineHeight.add(Integer.valueOf(i5));
        this.mAllViews.add(arrayList2);
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int size = this.mAllViews.size();
        int i8 = paddingTop;
        int i9 = paddingLeft;
        for (int i10 = 0; i10 < size; i10++) {
            List list = (List) this.mAllViews.get(i10);
            int intValue = ((Integer) this.mLineHeight.get(i10)).intValue();
            int i11 = i9;
            for (int i12 = 0; i12 < list.size(); i12++) {
                View view = (View) list.get(i12);
                if (view.getVisibility() != 8) {
                    MarginLayoutParams marginLayoutParams2 = (MarginLayoutParams) view.getLayoutParams();
                    int i13 = marginLayoutParams2.leftMargin + i11;
                    int i14 = marginLayoutParams2.topMargin + i8;
                    view.layout(i13, i14, view.getMeasuredWidth() + i13, view.getMeasuredHeight() + i14);
                    i11 += view.getMeasuredWidth() + marginLayoutParams2.leftMargin + marginLayoutParams2.rightMargin;
                }
            }
            i9 = getPaddingLeft();
            i8 += intValue;
        }
    }

    public void addView(String artist_name) {
    }
}

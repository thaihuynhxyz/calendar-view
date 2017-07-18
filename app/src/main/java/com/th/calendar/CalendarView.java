package com.th.calendar;

import android.content.Context;
import android.os.Build;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import java.util.Date;
import java.util.List;

import static com.th.calendar.CalendarAdapter.TYPE_DAY;
import static com.th.calendar.CalendarAdapter.TYPE_TITLE;

public class CalendarView extends ConstraintLayout {

    private CalendarAdapter mAdapter;

    public CalendarView(Context context) {
        super(context);
        init(context, null);
    }

    public CalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    void init(final Context context, AttributeSet attrs) {
        View root = inflate(context, R.layout.calendar_view, this);

        final TextView mMonthView = root.findViewById(R.id.month);
        final RecyclerView recyclerView = root.findViewById(R.id.days);
        mAdapter = new CalendarAdapter();
        GridLayoutManager layoutManager = new GridLayoutManager(context, 13, LinearLayoutManager.HORIZONTAL, true);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (mAdapter.getItemViewType(position)) {
                    case TYPE_TITLE:
                        return 1;
                    case TYPE_DAY:
                        return 2;
                    default:
                        return -1;
                }
            }
        });
        recyclerView.setLayoutManager(layoutManager);

        final CalendarSnapHelper snapHelper = new CalendarSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);
        recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mAdapter.setDimensions(recyclerView.getMeasuredWidth() / 7);
                recyclerView.setAdapter(mAdapter);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    recyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            }
        });
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            boolean mScrolled = true;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && mScrolled) {
                    mScrolled = false;
                    mMonthView.setText(String.format("%tB", mAdapter.getFirstDayOfMonth(snapHelper.getSnapPosition())));
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dx != 0 || dy != 0) mScrolled = true;
            }
        });
    }

    public void setData(List<Date> data) {
        mAdapter.setData(data);
    }

    public void notifyDataSetChanged() {
        mAdapter.notifyDataSetChanged();
    }
}

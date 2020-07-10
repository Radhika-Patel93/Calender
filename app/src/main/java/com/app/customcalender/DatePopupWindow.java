package com.app.customcalender;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DatePopupWindow extends PopupWindow {

    private View rootView;
    private TextView tvOk;
    private RecyclerView rv;
    private TextView tvStartDate;
    //    private TextView tvStartWeek;
    private TextView tvEndDate;
    //    private TextView tvEndWeek;
//    private TextView tvTime;
    private LinearLayout llEnd;
    private TextView tvHintText;
    //    private TextView btnClose;
//    private TextView btnClear;
    private boolean dayFalg;
    private Activity activity;
    private Date mSetDate;
    private String currentDate;
    private String startDesc;
    private String endDesc;
    private int startGroupPosition = -1;
    private int endGroupPosition = -1;
    private int startChildPosition = -1;
    private int endChildPosition = -1;
    private int c_stratChildPosition = -1;//Sub-index in the list that day
    private DateAdapter mDateAdapter;
    private List<DateInfo> mList;
    private DateOnClickListener mOnClickListener = null;


    private DatePopupWindow(Builder builder) {

        this.activity = builder.context;
        this.currentDate = builder.date;
        this.startDesc = builder.startDesc;
        this.endDesc = builder.endDesc;
        this.dayFalg = builder.dayFalg;
        this.startGroupPosition = builder.startGroupPosition;
        this.startChildPosition = builder.startChildPosition;
        this.endGroupPosition = builder.endGroupPosition;
        this.endChildPosition = builder.endChildPosition;
        this.mOnClickListener = builder.mOnClickListener;

        LayoutInflater inflater = (LayoutInflater) this.activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        rootView = inflater.inflate(R.layout.popupwindow_hotel_date, null);
        this.setContentView(rootView);
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setFocusable(true);
        this.setBackgroundDrawable(new BitmapDrawable());
        this.setOnDismissListener(new ShareDismissListener());
        backgroundAlpha(activity, 0.5f);

        initView();
        setInitSelect();
        create(builder.parentView);
    }

    private void backgroundAlpha(Activity context, float bgAlpha) {
        WindowManager.LayoutParams lp = context.getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        context.getWindow().setAttributes(lp);
    }

    private void initView() {
        tvOk = (TextView) rootView.findViewById(R.id.tv_ok);
//        btnClose = (TextView) rootView.findViewById(R.id.btn_close);
//        btnClear = (TextView) rootView.findViewById(R.id.btn_clear);
        tvStartDate = (TextView) rootView.findViewById(R.id.tv_startDate);
//        tvStartWeek = (TextView) rootView.findViewById(R.id.tv_startWeek);
        tvEndDate = (TextView) rootView.findViewById(R.id.tv_endDate);
//        tvEndWeek = (TextView) rootView.findViewById(R.id.tv_endWeek);
//        tvTime = (TextView) rootView.findViewById(R.id.tv_time);
        llEnd = (LinearLayout) rootView.findViewById(R.id.ll_end);
        tvHintText = (TextView) rootView.findViewById(R.id.tv_hintText);
        rv = (RecyclerView) rootView.findViewById(R.id.rv);
        TextView tvStartDateDesc = (TextView) rootView.findViewById(R.id.tv_startDateDesc);
        TextView tvEndDateDesc = (TextView) rootView.findViewById(R.id.tv_endDateDesc);
        tvStartDateDesc.setText(startDesc);
        tvEndDateDesc.setText(endDesc);
        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnClickListener != null) {
                    String startDate = mList.get(startGroupPosition)
                            .getList()
                            .get(startChildPosition)
                            .getDate();
                    String endDate = mList.get(endGroupPosition)
                            .getList()
                            .get(endChildPosition)
                            .getDate();
                    mOnClickListener.getDate(startDate, endDate, startGroupPosition, startChildPosition, endGroupPosition, endChildPosition);
                }
                DatePopupWindow.this.dismiss();
            }
        });
//        btnClose.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                DatePopupWindow.this.dismiss();
//            }
//        });
//        btnClear.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //Reset operation
//                initView();//Reset current view
//                setDefaultSelect();//Check initial state value
//            }
//        });

        LinearLayoutManager manager = new LinearLayoutManager(activity);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        rv.setLayoutManager(manager);
        mList = new ArrayList<>();
        mDateAdapter = new DateAdapter(mList);
        rv.setAdapter(mDateAdapter);
        rv.setItemViewCacheSize(200);
        rv.setHasFixedSize(true);
        rv.setNestedScrollingEnabled(false);
        initData();
    }

    @SuppressLint("SimpleDateFormat")
    private void initData() {
        SimpleDateFormat ymd_sdf = new SimpleDateFormat("yyyy-MM-dd");//Current date to date
        try {
            if (currentDate == null) {
                new Throwable("please set one start time");
                return;
            }
            mSetDate = ymd_sdf.parse(currentDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //Initialization date
        Calendar c = Calendar.getInstance();
        c.setTime(mSetDate);
        int firstM = c.get(Calendar.MONTH) + 1;//Get the month, the month starts from 0
        int days = c.get(Calendar.DATE);//date
        int week = c.get(Calendar.DAY_OF_WEEK);//which day
        //Get the current maximum number of days in this month
        int maxDys = c.getActualMaximum(Calendar.DAY_OF_MONTH);
        DateInfo info = new DateInfo();
        List<DayInfo> dayList = new ArrayList<>();
        Month month1 = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            month1 = Month.of(c.get(Calendar.MONTH) + 1);
            Locale locale = Locale.getDefault();
            Log.d("Date:", month1.getDisplayName(TextStyle.FULL, locale));
            info.setDate(month1.getDisplayName(TextStyle.FULL, locale) + " " + c.get(Calendar.YEAR));
        }
// else {
//            info.setDate(c.get(Calendar.YEAR) + "Year" + firstM + "month");
////        }

        //When less than the current date, it is not optional, setEnable(false)
        //The first day of the current month is the day of the week
        int w = CalendarUtil.getWeekNoFormat(c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH) + 1) + "-01") - 1;
        //According to the first day of the month, the day of the week, fill in the blank date of the previous month
        for (int t = 0; t < w; t++) {
            DayInfo dayInfo = new DayInfo();
            dayInfo.setName("");
            dayInfo.setEnable(false);
            dayInfo.setDate("");
            dayList.add(dayInfo);
        }
        //Calculate the number of days in the current month
        for (int i = 1; i <= maxDys; i++) {
            DayInfo dayInfo = new DayInfo();
            dayInfo.setName(i + "");
            dayInfo.setDate(c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH) + 1) + "-" + i);
            int c_year = Integer.parseInt(currentDate.split("-")[0]);
            int c_month = Integer.parseInt(currentDate.split("-")[1]);
            int c_day = Integer.parseInt(currentDate.split("-")[2]);
            if (c_year == c.get(Calendar.YEAR) && c_month == (c.get(Calendar.MONTH) + 1) && c_day == i) {
                c_stratChildPosition = dayList.size();
            }
            if (i < days) {
                dayInfo.setEnable(false);
            } else {
                dayInfo.setEnable(true);
            }
            dayList.add(dayInfo);
        }
        info.setList(dayList);
        mList.add(info);
        //Get data for the next 7 months
        for (int i = 1; i < 5; i++) {
            //Current month cycle plus 1
            c.add(Calendar.MONTH, 01);
            DateInfo nextInfo = new DateInfo();
            List<DayInfo> nextdayList = new ArrayList<>();
            int maxDays = c.getActualMaximum(Calendar.DAY_OF_MONTH);
            Log.d("Date : ", (c.get(Calendar.MONTH) + 1) + "");

            Month month = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                month = Month.of(c.get(Calendar.MONTH) + 1);
                Locale locale = Locale.getDefault();
                Log.d("Date:", month.getDisplayName(TextStyle.FULL, locale));
                nextInfo.setDate(month.getDisplayName(TextStyle.FULL, locale) + " " + c.get(Calendar.YEAR));
            }
// else {
//                nextInfo.setDate(c.get(Calendar.YEAR) + "year" + (c.get(Calendar.MONTH) + 1) + "month");
//            }


            //which day
            int weeks = CalendarUtil.getWeekNoFormat(c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH) + 1) + "-01") - 1;
            //According to the first day of the month, the day of the week, fill in the blank date of the previous month
            for (int t = 0; t < weeks; t++) {
                DayInfo dayInfo = new DayInfo();
                dayInfo.setName("");
                dayInfo.setEnable(false);
                dayInfo.setDate("");
                nextdayList.add(dayInfo);
            }
            //All dates of the month
            for (int j = 0; j < maxDays; j++) {
                DayInfo dayInfo = new DayInfo();
                dayInfo.setName((j + 1) + "");
                dayInfo.setEnable(true);
                dayInfo.setDate(c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH) + 1) + "-" + (j + 1));
                nextdayList.add(dayInfo);
            }
            nextInfo.setList(nextdayList);
            mList.add(nextInfo);
        }
        mDateAdapter.updateData();
    }

    private void setInitSelect() {
        if (0 <= this.startGroupPosition && this.startGroupPosition < mList.size() && 0 <= this.endGroupPosition && this.endGroupPosition < mList.size()) {
            int maxEndChild = mList.get(this.endGroupPosition).getList().size();
            int maxStartChild = mList.get(this.startGroupPosition).getList().size();
            if (0 <= this.startChildPosition && this.startChildPosition < maxStartChild && 0 <= this.endChildPosition && this.endChildPosition < maxEndChild) {
                setInit();
            } else {
                setDefaultSelect();//Set the date of today and tomorrow according to mDate
            }
        } else {
            setDefaultSelect();//Set the date of today and tomorrow according to mDate
        }
    }

    private void setInit() {
        mList.get(this.startGroupPosition).getList().get(this.startChildPosition).setStatus(1);
        mList.get(this.endGroupPosition).getList().get(this.endChildPosition).setStatus(2);
        mDateAdapter.notifyDataSetChanged();
        getoffsetDate(mList.get(startGroupPosition).getList().get(startChildPosition).getDate(),
                mList.get(endGroupPosition).getList().get(endChildPosition).getDate(), true);
        rv.scrollToPosition(this.startGroupPosition);
    }

    //Set the calendar to indicate the status of the current date
    @SuppressLint("SimpleDateFormat")
    private void setDefaultSelect() {
        if (c_stratChildPosition == -1) return;
        String date = mList.get(0).getList().get(c_stratChildPosition).getDate();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date curDate = null;
        try {
            curDate = sdf.parse(FormatDate(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (curDate == null) return;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(curDate);
        calendar.add(Calendar.DATE, 1);

        int year = Integer.parseInt(date.split("-")[0]);
        int month = Integer.parseInt(date.split("-")[1]);
        if (year == calendar.get(Calendar.YEAR) && month == calendar.get(Calendar.MONTH) + 1
                && c_stratChildPosition < mList.get(0).getList().size() - 1) {
            this.startGroupPosition = 0;
            this.startChildPosition = c_stratChildPosition;
            this.endGroupPosition = 0;
            this.endChildPosition = c_stratChildPosition + 1;
            setInit();
        } else {
            for (int i = 0; i < mList.get(1).getList().size(); i++) {
                if (!TextUtils.isEmpty(mList.get(1).getList().get(i).getDate())) {
                    this.startGroupPosition = 0;
                    this.startChildPosition = c_stratChildPosition;
                    this.endGroupPosition = 1;
                    this.endChildPosition = i;
                    setInit();
                    break;
                }
            }
        }
    }

    /**
     * Set the start and end time checkmarks, or set the unchecked
     *
     * @param startDate
     * @param endDate
     * @param status    Checked set to true setting does not select false
     */
    @SuppressLint("SetTextI18n")
    private void getoffsetDate(String startDate, String endDate, boolean status) {

        //Update start and end date information and status
        Calendar sCalendar = CalendarUtil.toDate(startDate);
        Calendar eCalendar = CalendarUtil.toDate(endDate);

        Month sMonth = null;
        Month eMonth = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            sMonth = Month.of(sCalendar.get(Calendar.MONTH) + 1);
            eMonth = Month.of(eCalendar.get(Calendar.MONTH) + 1);
            Locale locale = Locale.getDefault();
            tvStartDate.setText(sMonth.getDisplayName(TextStyle.SHORT, locale) + " " + sCalendar.get(Calendar.DAY_OF_MONTH));
            tvEndDate.setText(eMonth.getDisplayName(TextStyle.SHORT, locale) + " " + eCalendar.get(Calendar.DAY_OF_MONTH));
        } else {
//            tvStartDate.setText((sCalendar.get(Calendar.MONTH) + 1) + "month" + sCalendar.get(Calendar.DAY_OF_MONTH) + "day");
//            tvEndDate.setText((eCalendar.get(Calendar.MONTH) + 1) + "month" + eCalendar.get(Calendar.DAY_OF_MONTH) + "day");
        }
//        tvStartWeek.setText("week" + CalendarUtil.getWeekByFormat(startDate));
//        tvEndWeek.setText("week" + CalendarUtil.getWeekByFormat(endDate));
        int daysOffset = Integer.parseInt(CalendarUtil.getTwoDay(endDate, startDate));
        if (daysOffset < 0) return;
//        if (dayFalg) {
//            tvTime.setText("Total " + (daysOffset + 1) + " day");
//        } else {
//            tvTime.setText("Total " + daysOffset + " day");
//        }
        //Change end date and completion button status
        llEnd.setVisibility(View.VISIBLE);
        tvHintText.setVisibility(View.GONE);
        tvOk.setText("Apply Dates");
        tvOk.setEnabled(true);
        tvOk.setBackgroundResource(R.drawable.img_btn_bg_y);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        DayInfo info = mList.get(startGroupPosition).getList().get(startChildPosition);
        try {
            c.setTime(sdf.parse(info.getDate()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //Circulate according to the difference of two time periods
        for (int i = 0; i < daysOffset; i++) {
            //Next day (target day)
            c.add(Calendar.DATE, 1);
            //Change the date of the day (target day)
            String d = c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH) + 1) + "-" + c.get(Calendar.DATE);
            // Loop group list
            for (int j = 0; j < mList.size(); j++) {
                //Get a random one of the month dayInfo
                DayInfo dayInfo = mList.get(j).getList().get(mList.get(j).getList().size() - 1);
                boolean isCheck = false;
                //Determine if the day is the same month as the target day
                if (!TextUtils.isEmpty(dayInfo.getDate()) && Integer.valueOf(dayInfo.getDate().split("-")[0]) == (c.get(Calendar.YEAR))
                        && Integer.valueOf(dayInfo.getDate().split("-")[1]) == ((c.get(Calendar.MONTH) + 1))) {
                    //Is the same month, then there are more days in the cycle
                    for (int t = 0; t < mList.get(j).getList().size(); t++) {
                        //Find the date of the month as the target date, exist, set the selection tag
                        if (mList.get(j).getList().get(t).getDate().equals(d)) {
                            mList.get(j).getList().get(t).setSelect(status);
                            isCheck = true;
                            break;
                        }
                    }
                }
                if (isCheck) {
                    mDateAdapter.notifyItemChanged(j);
                    break;
                }
            }
        }
    }

    private String FormatDate(String date) {
        if (TextUtils.isEmpty(date)) return "";
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(date.split("-")[0]);
        stringBuffer.append("-");
        stringBuffer.append(date.split("-")[1].length() < 2 ? "0" + date.split("-")[1] : date.split("-")[1]);
        stringBuffer.append("-");
        stringBuffer.append(date.split("-")[2].length() < 2 ? "0" + date.split("-")[2] : date.split("-")[2]);
        return stringBuffer.toString();
    }

    private void create(View view) {
        this.showAtLocation(view, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    /**
     * return startDate, endDate (format: 2012-12-10)
     * Return to start time and end time after selection is completed
     * return startGroupPosition, startChildPosition, endGroupPosition, endChildPosition
     * Returns the status flag of the selected time interval. After receiving, the listener directly sends in and out in the setInitSelect() method in the builder (can be used to record the last selected state, and the user resumes the last interval selection when clicking again)
     */
    public interface DateOnClickListener {
        void getDate(String startDate, String endDate, int startGroupPosition, int startChildPosition, int endGroupPosition, int endChildPosition);
    }

    public static class Builder {
        private String date;
        private Activity context;
        private View parentView;
        private String startDesc;
        private String endDesc;
        private boolean dayFalg = true;
        private int startGroupPosition = -1;
        private int endGroupPosition = -1;
        private int startChildPosition = -1;
        private int endChildPosition = -1;
        private DateOnClickListener mOnClickListener = null;

        @SuppressLint("SimpleDateFormat")
        public Builder(Activity context, Date date, View parentView) {
            this.date = new SimpleDateFormat("yyyy-MM-dd").format(date);
            this.context = context;
            this.parentView = parentView;
            this.startDesc = "Start";
            this.endDesc = "End";
            this.dayFalg = true;
        }

        public DatePopupWindow builder() {
            return new DatePopupWindow(this);
        }

        public Builder setInitSelect(int startGroup, int startChild, int endGroup, int endChild) {
            this.startGroupPosition = startGroup;
            this.startChildPosition = startChild;
            this.endGroupPosition = endGroup;
            this.endChildPosition = endChild;
            return this;
        }

        public Builder setInitDay(boolean dayFalg) {
            this.dayFalg = dayFalg;
            if (dayFalg) {
                this.startDesc = "Start";
                this.endDesc = "End";
            } else {
                this.startDesc = "Check-in ";
                this.endDesc = "Check-out ";
            }
            return this;
        }

        public Builder setDateOnClickListener(DateOnClickListener mlListener) {
            mOnClickListener = mlListener;
            return this;
        }
    }

    private class DateAdapter extends BaseQuickAdapter<DateInfo, BaseViewHolder> {

        DateAdapter(@Nullable List<DateInfo> data) {
            super(R.layout.adapter_hotel_select_date, data);
        }

        @Override
        public void onBindViewHolder(BaseViewHolder holder, int positions) {
            super.onBindViewHolder(holder, positions);
            TextView tv = holder.getView(R.id.tv_date);
            tv.setText(mList.get(positions).getDate());
        }

        @Override
        protected void convert(final BaseViewHolder helper, final DateInfo item) {
            RecyclerView rv = helper.getView(R.id.rv_date);

            GridLayoutManager manager = new GridLayoutManager(activity, 7);
            rv.setLayoutManager(manager);
            final TempAdapter groupAdapter = new TempAdapter(item.getList());
            rv.setAdapter(groupAdapter);
            rv.setItemViewCacheSize(200);
            rv.setHasFixedSize(true);
            rv.setNestedScrollingEnabled(false);
            groupAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    if (!item.getList().get(position).isEnable()) return;
                    if (TextUtils.isEmpty(item.getList().get(position).getName())) return;
                    if (TextUtils.isEmpty(item.getList().get(position).getDate())) return;
                    int status = item.getList().get(position).getStatus();
                    if (status == 0 &&
                            startGroupPosition == -1 &&
                            startChildPosition == -1 &&
                            item.getList().get(position).isEnable()) {
                        //Start
                        item.getList().get(position).setStatus(1);
                        adapter.notifyItemChanged(position);
                        startGroupPosition = helper.getAdapterPosition();
                        startChildPosition = position;
                        tvStartDate.setText(CalendarUtil.FormatDateMD(item.getList().get(position).getDate()));
//                        tvStartWeek.setText("week" + CalendarUtil.getWeekByFormat(item.getList().get(position).getDate()));
//                        tvTime.setText("please choose" + endDesc + "date");

                        tvOk.setEnabled(false);
                        tvOk.setText("please choose " + endDesc + " date");
                        tvOk.setBackgroundResource(R.drawable.img_btn_bg_n);
                        llEnd.setVisibility(View.GONE);
                        tvHintText.setText(endDesc + "date");
                        tvHintText.setVisibility(View.VISIBLE);
                        return;
                    }
                    //End
                    if (status == 0 && endGroupPosition == -1 && endChildPosition == -1) {
                        int offset = Integer.parseInt(CalendarUtil.getTwoDay(item.getList().get(position).getDate()
                                , mList.get(startGroupPosition).getList().get(startChildPosition).getDate()));
                        // Determine whether the departure date is smaller than the check-in time, is to reset the check-in time.
                        if (offset < 0) {
                            //Refresh previous start date
                            mList.get(startGroupPosition).getList().get(startChildPosition).setStatus(0);
                            mDateAdapter.notifyItemChanged(startGroupPosition);
                            //Set a new entry start date
                            item.getList().get(position).setStatus(1);
                            startGroupPosition = helper.getAdapterPosition();
                            startChildPosition = position;
                            String mStartTime = CalendarUtil.FormatDateMD(item.getList().get(position).getDate());
                            tvStartDate.setText(mStartTime);

//                            tvStartWeek.setText("week" + CalendarUtil.getWeekByFormat(item.getList().get(position).getDate()));
                            adapter.notifyItemChanged(position);
//                            tvTime.setText("please choose" + endDesc + "date");
                            tvOk.setText("please choose " + endDesc + " date");//?
                            tvOk.setEnabled(false);
                            tvOk.setBackgroundResource(R.drawable.img_btn_bg_n);
                            llEnd.setVisibility(View.GONE);
                            tvHintText.setText(endDesc + "date");
                            tvHintText.setVisibility(View.VISIBLE);
                            return;
                        }
                        //End
                        item.getList().get(position).setStatus(2);
                        adapter.notifyItemChanged(position);
                        endGroupPosition = helper.getAdapterPosition();
                        endChildPosition = position;
                        getoffsetDate(mList.get(startGroupPosition).getList().get(startChildPosition).getDate(),
                                mList.get(endGroupPosition).getList().get(endChildPosition).getDate(), true);

                        return;
                    }
                    //Reset start and end time, set start time
                    if (status == 0 && endGroupPosition != -1 && endChildPosition != -1 && startChildPosition != -1 && startGroupPosition != -1) {
                        //Reset start and end
                        mList.get(startGroupPosition).getList().get(startChildPosition).setStatus(0);
                        mList.get(endGroupPosition).getList().get(endChildPosition).setStatus(0);
                        mDateAdapter.notifyItemChanged(startGroupPosition);
                        mDateAdapter.notifyItemChanged(endGroupPosition);
                        //Reset the status of the selection area
                        getoffsetDate(mList.get(startGroupPosition).getList().get(startChildPosition).getDate(),
                                mList.get(endGroupPosition).getList().get(endChildPosition).getDate(), false);
                        //Start setting
                        item.getList().get(position).setStatus(1);
                        adapter.notifyItemChanged(position);
                        String mStartTime = CalendarUtil.FormatDateMD(item.getList().get(position).getDate());
                        tvStartDate.setText(mStartTime);
//                        tvStartWeek.setText("week" + CalendarUtil.getWeekByFormat(item.getList().get(position).getDate()));

                        startGroupPosition = helper.getAdapterPosition();
                        startChildPosition = position;
                        endGroupPosition = -1;
                        endChildPosition = -1;
//                        tvTime.setText("please choose" + endDesc + "date");
                        tvOk.setText("please choose " + endDesc + " date");
                        tvOk.setEnabled(false);
                        tvOk.setBackgroundResource(R.drawable.img_btn_bg_n);
                        llEnd.setVisibility(View.GONE);
                        tvHintText.setText(endDesc + "date");
                        tvHintText.setVisibility(View.VISIBLE);
                        return;
                    }
                }
            });
        }

        public void updateData() {
            notifyDataSetChanged();
        }
    }

    private class TempAdapter extends BaseQuickAdapter<DayInfo, BaseViewHolder> {
        TempAdapter(@Nullable List<DayInfo> data) {
            super(R.layout.adapter_hotel_select_date_child, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, DayInfo item) {
            String name = item.getName();
            boolean isSelect = item.isSelect();
            boolean isEnable = item.isEnable();
            int status = item.getStatus();
            helper.setText(R.id.tv_date, name);
            //default
            if (status == 0) {
                if (isSelect) {
                    //Selected
                    helper.getView(R.id.tv_date).setVisibility(View.VISIBLE);
                    helper.getView(R.id.tv_status).setVisibility(View.GONE);
                    helper.getView(R.id.tv_dateDel).setVisibility(View.GONE);
                    ((TextView) helper.getView(R.id.tv_date)).setTextColor(activity.getResources().getColor(R.color.white));
                    (helper.getView(R.id.ll_bg)).setBackgroundColor(activity.getResources().getColor(R.color.title_bg2));
                } else {
                    //Unchecked
                    helper.getView(R.id.tv_date).setVisibility(View.VISIBLE);
                    helper.getView(R.id.tv_status).setVisibility(View.GONE);
                    helper.getView(R.id.tv_dateDel).setVisibility(View.GONE);
                    ((TextView) helper.getView(R.id.tv_date)).setTextColor(activity.getResources().getColor(R.color.black));
                    (helper.getView(R.id.ll_bg)).setBackgroundColor(activity.getResources().getColor(R.color.white));
                }
            } else if (status == 1) {
                //Start
                helper.getView(R.id.tv_date).setVisibility(View.VISIBLE);
                helper.setText(R.id.tv_status, startDesc);
                helper.getView(R.id.tv_status).setVisibility(View.VISIBLE);
                helper.getView(R.id.tv_dateDel).setVisibility(View.GONE);
                ((TextView) helper.getView(R.id.tv_status)).setTextColor(activity.getResources().getColor(R.color.white));
                ((TextView) helper.getView(R.id.tv_date)).setTextColor(activity.getResources().getColor(R.color.white));
                (helper.getView(R.id.ll_bg)).setBackgroundColor(activity.getResources().getColor(R.color.title_bg));
            } else if (status == 2) {
                //End
                helper.getView(R.id.tv_date).setVisibility(View.VISIBLE);
                helper.setText(R.id.tv_status, endDesc);
                helper.getView(R.id.tv_status).setVisibility(View.VISIBLE);
                helper.getView(R.id.tv_dateDel).setVisibility(View.GONE);
                ((TextView) helper.getView(R.id.tv_status)).setTextColor(activity.getResources().getColor(R.color.white));
                ((TextView) helper.getView(R.id.tv_date)).setTextColor(activity.getResources().getColor(R.color.white));
                (helper.getView(R.id.ll_bg)).setBackgroundColor(activity.getResources().getColor(R.color.title_bg));
            }
            //Set the style before the current date, not selected, and the status is 0
            if (!isSelect && status == 0) {
                if (!isEnable) {
                    //invalid
                    TextView textView = helper.getView(R.id.tv_dateDel);
                    if (TextUtils.isEmpty(name)) {
                        textView.setVisibility(View.GONE);
                    } else {
                        textView.setText(name);
                        textView.setVisibility(View.VISIBLE);
                    }
                    textView.setTextColor(activity.getResources().getColor(R.color.text_enable));
                    helper.getView(R.id.tv_date).setVisibility(View.GONE);
                    helper.getView(R.id.tv_status).setVisibility(View.GONE);
                } else {
                    helper.getView(R.id.tv_date).setVisibility(View.VISIBLE);
                    helper.getView(R.id.tv_status).setVisibility(View.GONE);
                    helper.getView(R.id.tv_dateDel).setVisibility(View.GONE);
                    TextView textView = helper.getView(R.id.tv_date);
                    textView.setTextColor(activity.getResources().getColor(R.color.black));
                }
            }
        }
    }

    private class ShareDismissListener implements OnDismissListener {
        @Override
        public void onDismiss() {
            backgroundAlpha(activity, 1f);
        }
    }
}

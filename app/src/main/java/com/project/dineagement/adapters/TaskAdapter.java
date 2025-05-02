package com.project.dineagement.adapters;

import static com.project.dineagement.Utilities.display2Format;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.project.dineagement.R;
import com.project.dineagement.objects.Task;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class TaskAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Task> tasks;
    private boolean isManager;
    private LayoutInflater inflater;

    private final int urgent = 1;
    private final int notUrgent = 0;

    private final int show = View.VISIBLE, hide = View.INVISIBLE;

    private Calendar calNow;

    public TaskAdapter(Context context, ArrayList<Task> tasks) {
        this.context = context;
        this.tasks = tasks;
        isManager = false;
        inflater = LayoutInflater.from(context);
        calNow = Calendar.getInstance();
    }



    @Override
    public int getCount() {
        return tasks.size();
    }

    @Override
    public Object getItem(int pos) {
        return tasks.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return pos;
    }

    @Override
    public View getView(int pos, View view, ViewGroup parent) {
        view = inflater.inflate(R.layout.task_layout, parent, false);
        TextView name = view.findViewById(R.id.viewTaskName);
        TextView due = view.findViewById(R.id.viewDueDate);
        TextView importance = view.findViewById(R.id.viewImportance);
        TextView forUser = view.findViewById(R.id.viewFor);
        forUser.setVisibility(hide);
        name.setText(tasks.get(pos).getTaskName());
        due.setText(tasks.get(pos).getDateDue());
        if (tasks.get(pos).getPriority() == 10 || tasks.get(pos).getPriority() == 11) importance.setText("Yes");
        else importance.setText("No");
        if (isManager) {
            forUser.setVisibility(show);
            forUser.setText(tasks.get(pos).getForUser());
        }
        if (isUrgent(tasks.get(pos).getDateDue(), calNow)) {
            setUrgency(name, urgent);
            setUrgency(due, urgent);
            setUrgency(importance, urgent);
            if (forUser.getVisibility() == show)
                setUrgency(forUser, urgent);
        } else {
            setUrgency(name, notUrgent);
            setUrgency(due, notUrgent);
            setUrgency(importance, notUrgent);
            if (forUser.getVisibility() == show)
                setUrgency(forUser, notUrgent);
        }
        return view;
    }

    public static boolean isUrgent(String endDate, Calendar startDate) {
        long start = startDate.getTimeInMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Date date = null;
        try {
            date = sdf.parse(display2Format(endDate));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        Calendar calStart = Calendar.getInstance();
        calStart.setTime(date);
        long end = calStart.getTimeInMillis();
        long days = TimeUnit.MILLISECONDS.toDays(end - start);
        return days <= 3;
    }

    public void setUrgency(TextView textView, int urgent) {
        switch (urgent) {
            case 0: {
                textView.setTextColor(Color.BLACK);
                textView.setTypeface(null, Typeface.NORMAL);
                break;
            }
            case 1: {
                textView.setTextColor(Color.RED);
                textView.setTypeface(null, Typeface.BOLD);
                break;
            }
        }
    }

    public void setManager(boolean manager) {
        isManager = manager;
    }
}

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

import androidx.annotation.NonNull;

import com.project.dineagement.R;
import com.project.dineagement.objects.Task;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Custom adapter for handling task management and display.
 * @author Shaked Awad
 * @version 1.0
 * @since 4/20/2025
 */
public class TaskAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Task> tasks;
    private boolean isManager;
    private LayoutInflater inflater;

    private final boolean urgent = true, notUrgent = false;

    private final int show = View.VISIBLE, hide = View.INVISIBLE;

    private Calendar calNow;

    /**
     * Instantiates a new TaskAdapter.
     * @param context The context
     * @param tasks The Task array list
     * @param isManager Whether the user is a manager
     */
    public TaskAdapter(Context context, ArrayList<Task> tasks, boolean isManager) {
        this.context = context;
        this.tasks = tasks;
        this.isManager = isManager;
        inflater = LayoutInflater.from(context);
        calNow = Calendar.getInstance();
    }

    /**
     * Returns the length of an array list of Tasks.
     * @return count of items in array list
     */
    @Override
    public int getCount() {
        return tasks.size();
    }

    /**
     * Returns the value of the Task at a given position.
     * @param pos The position of the Task
     * @return The Task object at {@code pos}
     */
    @Override
    public Task getItem(int pos) {
        return tasks.get(pos);
    }

    /**
     * Returns the row ID associated with the specified position in the list.
     * @param pos The position of the item
     * @return The ID of the item at the specified position
     */
    @Override
    public long getItemId(int pos) {
        return pos;
    }

    /**
     * Defines the cell in which a Task is displayed.
     * @param pos The position of the Task
     * @param view The View
     * @param parent The parent that {@code view} will eventually be attached to
     * @return {@code view}
     */
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

    /**
     * Check if a task's due date is 3 days away or less from the current date.
     * @param endDate The task's due date
     * @param startDate The current date
     * @return {@code true} if the task is urgent, {@code false} otherwise
     */
    public static boolean isUrgent(String endDate, @NonNull Calendar startDate) {
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

    /**
     * Marks the TextView's task in bold red if it's urgent.
     * @param textView The TextView of the task
     * @param isUrgent {@code true} if the task is urgent, {@code false} otherwise
     */
    public void setUrgency(TextView textView, boolean isUrgent) {
        if (isUrgent) {
            textView.setTextColor(Color.RED);
            textView.setTypeface(null, Typeface.BOLD);
        } else {
            textView.setTextColor(Color.BLACK);
            textView.setTypeface(null, Typeface.NORMAL);
        }
    }
}

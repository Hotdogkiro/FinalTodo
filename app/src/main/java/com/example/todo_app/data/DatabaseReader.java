package com.example.todo_app.data;

import android.content.Context;

import com.example.todo_app.model.Status;
import com.orm.SugarContext;
import com.orm.SugarRecord;
import com.example.todo_app.model.Task;

import java.util.Collections;
import java.util.List;

public class DatabaseReader {
    Context context;
    public DatabaseReader(Context context){
        this.context = context;
        SugarContext.init(context);
    }

    public List<Task> readDatabase() {
        return Task.listAll(Task.class);
    }

    public List<Task> readTasksWithStatus(Status status) {
        return Task.findWithQuery(Task.class, "SELECT * FROM TASK WHERE STATUS = \"" + status.toString() + "\"");
    }
}

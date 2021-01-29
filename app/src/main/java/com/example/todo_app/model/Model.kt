package com.example.todo_app.model

import android.graphics.Color
import android.graphics.drawable.Icon
import androidx.core.graphics.toColor
import com.example.todo.R
import com.orm.SugarRecord
import org.w3c.dom.Entity
import java.time.LocalDate
import java.util.*

enum class Importance(val color: Color){
    LOW(Color.GREEN.toColor()),
    MEDIUM(Color.YELLOW.toColor()),
    HIGH(Color.RED.toColor())
}

enum class Status{
    TODO,
    INPROGRESS,
    DONE
}
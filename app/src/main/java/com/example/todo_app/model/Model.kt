package com.example.todo_app.model

import android.graphics.Color
import android.graphics.drawable.Icon
import androidx.core.graphics.toColor
import com.example.todo.R
import org.w3c.dom.Entity
import java.time.LocalDate
import java.util.*

data class Category(val name : String, val icon: Icon)

data class Task(val title: String, val importance: Importance = Importance.MEDIUM, val description: String = "", val status : Status = Status.TODO) : RecyclerViewItem(){

}
open class RecyclerViewItem(){
}


class Placeholder() : RecyclerViewItem() {
}

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
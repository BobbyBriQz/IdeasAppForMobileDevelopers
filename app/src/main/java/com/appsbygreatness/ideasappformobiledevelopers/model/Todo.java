package com.appsbygreatness.ideasappformobiledevelopers.model;

public class Todo {

    private String taskTodo;
    private boolean isCompleted;

    public Todo(String taskTodo, boolean isCompleted){

        this.taskTodo = taskTodo;
        this.isCompleted = isCompleted;
    }

    public String getTaskTodo() {

        return taskTodo;
    }

    public void setTaskTodo(String taskTodo) {
        this.taskTodo = taskTodo;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }
}

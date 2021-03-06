package com.appsbygreatness.ideasappformobiledevelopers.adapters;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.appsbygreatness.ideasappformobiledevelopers.R;
import com.appsbygreatness.ideasappformobiledevelopers.model.Todo;

import java.util.List;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.ViewHolder> {

    private List<Todo> todos;
    private Context context;

    private OnTodoDeleteClickListener onTodoDeleteClickListener;
    private OnTodoCompleteClickListener onTodoCompleteClickListener;

    public TodoAdapter(Context context, OnTodoDeleteClickListener onTodoDeleteClickListener, OnTodoCompleteClickListener onTodoCompleteClickListener) {
        this.todos = todos;
        this.context = context;
        this.onTodoDeleteClickListener = onTodoDeleteClickListener;
        this.onTodoCompleteClickListener = onTodoCompleteClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.todo_card_layout, viewGroup, false);

        return new ViewHolder(view, onTodoCompleteClickListener, onTodoDeleteClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        Todo todo = todos.get(i);

        viewHolder.todoTV.setText(todo.getTaskTodo());

        if(todo.isCompleted()){

            viewHolder.isCompleteButton.setImageResource(R.drawable.ic_check_box);
            viewHolder.todoTV.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        }else{
            viewHolder.isCompleteButton.setImageResource(R.drawable.ic_check_box_outline);
            viewHolder.todoTV.setPaintFlags(viewHolder.todoTV.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
        }

    }

    public void setTodos(List<Todo> todos) {
        this.todos = todos;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if(todos == null) return 0;

        return todos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageButton isCompleteButton;
        TextView todoTV;
        ImageButton todoDeleteButton;
        OnTodoCompleteClickListener onTodoCompleteClickListener;
        OnTodoDeleteClickListener onTodoDeleteClickListener;


        public ViewHolder(@NonNull View itemView, final OnTodoCompleteClickListener onTodoCompleteClickListener,
                          final OnTodoDeleteClickListener onTodoDeleteClickListener) {
            super(itemView);

            isCompleteButton = itemView.findViewById(R.id.isCompletedButton);
            todoTV = itemView.findViewById(R.id.todoTV);
            todoDeleteButton = itemView.findViewById(R.id.deleteTodoButton);
            this.onTodoCompleteClickListener = onTodoCompleteClickListener;
            this.onTodoDeleteClickListener = onTodoDeleteClickListener;

            isCompleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onTodoCompleteClickListener.onCompleteClick(getAdapterPosition());
                }
            });

            todoDeleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onTodoDeleteClickListener.onDeleteClick(getAdapterPosition());
                }
            });



        }
    }

    public interface OnTodoCompleteClickListener{
        void onCompleteClick(int position);
    }

    public interface OnTodoDeleteClickListener{
        void onDeleteClick(int position);
    }
}

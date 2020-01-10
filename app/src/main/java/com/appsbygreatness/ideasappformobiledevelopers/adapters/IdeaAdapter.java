package com.appsbygreatness.ideasappformobiledevelopers.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.appsbygreatness.ideasappformobiledevelopers.model.Idea;
import com.appsbygreatness.ideasappformobiledevelopers.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class IdeaAdapter extends RecyclerView.Adapter<IdeaAdapter.ViewHolder>{

    private Context context;
    private List<Idea> ideas;
    private List<Idea> filterList;

    private OnIdeaClickListener onIdeaClickListener;
    private OnLongIdeaClickListener onLongIdeaClickListener;
    private CustomFilter filter;

    public IdeaAdapter(Context context, List<Idea> ideas, OnIdeaClickListener onIdeaClickListener, OnLongIdeaClickListener onLongIdeaClickListener) {
        this.context = context;
        this.ideas = ideas;
        this.filterList = ideas;
        this.onIdeaClickListener = onIdeaClickListener;
        this.onLongIdeaClickListener = onLongIdeaClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_layout, viewGroup, false);

        ViewHolder holder = new ViewHolder(view, onIdeaClickListener, onLongIdeaClickListener);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {


        viewHolder.ideaName.setText(ideas.get(i).getName());
        viewHolder.appIdea.setText(ideas.get(i).getIdea());
        viewHolder.ideaTimeStamp.setText(ideas.get(i).getTimestamp());


        if (ideas.get(i).getImageNames().size() >= 1) {

            File f = new File((ideas.get(i).getFullpath()), (ideas.get(i).getImageNames().get(0)));

            Bitmap bitmap = null;
            try {
                bitmap = BitmapFactory.decodeStream(new FileInputStream(f));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            viewHolder.ideaPic.setImageBitmap(bitmap);
        }else{

            viewHolder.ideaPic.setBackgroundResource(R.drawable.ic_launcher_background);
            viewHolder.ideaPic.setImageResource(R.drawable.ic_launcher_foreground);
        }


    }

    @Override
    public int getItemCount() {
        return ideas.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

       ImageView ideaPic;
       TextView ideaName;
       TextView appIdea;
       TextView ideaTimeStamp;
       OnIdeaClickListener onIdeaClickListener;
       OnLongIdeaClickListener onLongIdeaClickListener;


       public ViewHolder(View itemView, OnIdeaClickListener onIdeaClickListener, OnLongIdeaClickListener onLongIdeaClickListener){
           super(itemView);

           ideaPic = itemView.findViewById(R.id.ideaPic);
           ideaName = itemView.findViewById(R.id.ideaNameTV);
           appIdea = itemView.findViewById(R.id.appIdeaTV);
           ideaTimeStamp = itemView.findViewById(R.id.ideaTimestampTV);
           this.onIdeaClickListener = onIdeaClickListener;
           this.onLongIdeaClickListener = onLongIdeaClickListener;

           itemView.setOnClickListener(this);
           itemView.setOnLongClickListener(this);

       }

        @Override
        public void onClick(View view) {
            onIdeaClickListener.onIdeaClick(getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View view) {

            onLongIdeaClickListener.onLongIdeaClick(getAdapterPosition());
            return false;
        }
    }

   public interface OnIdeaClickListener{

         void onIdeaClick(int position);
   }

   public interface OnLongIdeaClickListener{

        void onLongIdeaClick(int position);
   }

    class CustomFilter extends Filter {


        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {

            FilterResults filterResults = new FilterResults();

            if (charSequence != null && charSequence.length() > 0){

                charSequence = charSequence.toString().toUpperCase();

                ArrayList<Idea> filter = new ArrayList<>();

                for(int i = 0; i < filterList.size(); i++){

                    if (filterList.get(i).getName().toUpperCase().contains(charSequence)){
                        filter.add(filterList.get(i));
                    }
                }

                filterResults.count = filter.size();
                filterResults.values = filter;

            } else{

                filterResults.count = filterList.size();
                filterResults.values = filterList;
            }
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {

            ideas = (ArrayList<Idea>) filterResults.values;
            notifyDataSetChanged();
        }



    }


    public Filter getFilter(){

        if (filter == null){

            filter = new CustomFilter();

        }
        return filter;
    }
}

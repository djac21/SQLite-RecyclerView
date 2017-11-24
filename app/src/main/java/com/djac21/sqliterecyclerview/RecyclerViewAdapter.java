package com.djac21.sqliterecyclerview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {
    private LayoutInflater inflater;
    private List<DataModel> data;
    private ClickListener clickListener;
    private LongClickListener longClickListener;

    public RecyclerViewAdapter(Context context, List<DataModel> data) {
        inflater = LayoutInflater.from(context);
        this.data = data;
    }
    
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.card_view, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        DataModel current = data.get(position);
        holder.title.setText(current.getTitle());
        holder.text.setText(current.getText());
    }

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public void setLongClickListener(LongClickListener longClickListener) {
        this.longClickListener = longClickListener;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        TextView title, text;

        public MyViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            text = itemView.findViewById(R.id.text);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (clickListener != null) {
               clickListener.itemClicked(v, getAdapterPosition());
            }
        }

        @Override
        public boolean onLongClick(View v){
            if (longClickListener != null) {
                longClickListener.itemLongClicked(v, getAdapterPosition());
            }
            return true;
        }
    }
    
    public interface ClickListener {
        void itemClicked(View view, int position);
    }

    public interface LongClickListener {
        void itemLongClicked(View view, int position);
    }
}

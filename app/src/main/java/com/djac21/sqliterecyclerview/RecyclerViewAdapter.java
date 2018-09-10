package com.djac21.sqliterecyclerview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {
    private LayoutInflater inflater;
    private List<DataModel> data;
    private List<DataModel> filteredData;
    private ClickListener clickListener;
    private LongClickListener longClickListener;

    public RecyclerViewAdapter(Context context, List<DataModel> data) {
        inflater = LayoutInflater.from(context);
        this.data = data;
        this.filteredData = data;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.card_view, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        DataModel current = filteredData.get(position);
        holder.title.setText(current.getTitle());
        holder.text.setText(current.getText());
        holder.timeStamp.setText(current.getDate());
    }

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public void setLongClickListener(LongClickListener longClickListener) {
        this.longClickListener = longClickListener;
    }

    @Override
    public int getItemCount() {
        return filteredData.size();
    }

    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    filteredData = data;
                } else {
                    List<DataModel> filteredList = new ArrayList<>();
                    for (DataModel model : data)
                        if (model.getTitle().toLowerCase().contains(charString.toLowerCase()))
                            filteredList.add(model);

                    filteredData = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredData;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredData = (ArrayList<DataModel>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        TextView title, text, timeStamp;

        public MyViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            text = itemView.findViewById(R.id.text);
            timeStamp = itemView.findViewById(R.id.timeStamp);

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
        public boolean onLongClick(View v) {
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

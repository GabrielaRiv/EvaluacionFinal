package com.example.gabriela.evaluacionfinal.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.gabriela.evaluacionfinal.R;
import com.example.gabriela.evaluacionfinal.item.Info;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class InfoAdapter extends RecyclerView.Adapter<InfoAdapter.ViewHolder> {

    private final Context context;
    private List<Info> mDataSet;
    //private OnItemClickListener listener;
    private int idLayout;

    public InfoAdapter(Context context, List<Info> mDataSet, int idLayout) {
        this.context = context;
        this.mDataSet = mDataSet;
        this.idLayout = idLayout;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.txvTitle) public TextView title;
        @BindView(R.id.txvDescription) public TextView description;
        @BindView(R.id.txvDate) public TextView date;
        @BindView(R.id.txvLatitude) public TextView latitude;
        @BindView(R.id.txvLongitude) public TextView longitude;
        @BindView(R.id.txvPlaceType) public TextView placeType;
        @BindView(R.id.imvInfo) public ImageView imvInfo;
        public ImageView lnItem;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

        @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            //Inflater layout
            View view = LayoutInflater.from(parent.getContext()).inflate(idLayout, parent, false);
            //Create view holder, view has a constructor in ViewHolder method
            return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InfoAdapter.ViewHolder holder, int position) {
        Info info = mDataSet.get(position);
        holder.title.setText(info.title);
        holder.description.setText(info.description);
        holder.date.setText(info.date);
        holder.latitude.setText(String.valueOf(info.latitude));
        holder.longitude.setText(String.valueOf(info.longitude));
        holder.placeType.setText(info.placeType);
        /*int icon = R.drawable.lugares;
        Picasso.with(this.context).load(icon).into(holder.imvInfo);
        holder.imvInfo.setTag(position);
        holder.itemView.setTag(position);*/
    }

    @Override
    public int getItemCount() {
        return this.mDataSet.size();
    }
}

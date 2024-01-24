package com.sivaram.populartvseries.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.sivaram.populartvseries.Common;
import com.sivaram.populartvseries.R;
import com.sivaram.populartvseries.TvSeriesClickListener;
import com.sivaram.populartvseries.model.HomeTvSeries;
import com.squareup.picasso.Picasso;

import java.util.List;

public class SearchTvSeriesAdapter extends RecyclerView.Adapter<SearchTvSeriesAdapter.SearchTvSeriesHolder> {

    private Context context;
    private List<HomeTvSeries> searchList;
    private TvSeriesClickListener tvSeriesClickListener;

    public SearchTvSeriesAdapter(Context context, List<HomeTvSeries> searchList, TvSeriesClickListener tvSeriesClickListener) {
        this.context = context;
        this.searchList = searchList;
        this.tvSeriesClickListener = tvSeriesClickListener;
    }

    @NonNull
    @Override
    public SearchTvSeriesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_result_layout, parent, false);
        return new SearchTvSeriesHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchTvSeriesHolder holder, int position) {

        holder.seriesName.setText(searchList.get(position).getSeriesName());
        holder.publishedDate.setText(searchList.get(position).getPublishedDate());
        if(searchList.get(position).getPosterUrl()==null){
            holder.profileImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.series_icon));
        }
        else{
            Picasso.get().load(searchList.get(position).getPosterUrl()).into(holder.profileImage);
        }

        holder.detailLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvSeriesClickListener.onClick(searchList.get(position).getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return searchList.size();
    }

    public class SearchTvSeriesHolder extends RecyclerView.ViewHolder{

        private TextView seriesName, publishedDate;
        private ImageView profileImage;
        private RelativeLayout detailLayout;
        public SearchTvSeriesHolder(@NonNull View itemView) {
            super(itemView);
            seriesName = itemView.findViewById(R.id.series_name);
            publishedDate = itemView.findViewById(R.id.published_date);
            profileImage = itemView.findViewById(R.id.profile_img);
            detailLayout = itemView.findViewById(R.id.detail_card);
        }
    }
}

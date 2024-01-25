package com.sivaram.populartvseries.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.akiniyalocts.pagingrecycler.PagingAdapter;
import com.sivaram.populartvseries.R;
import com.sivaram.populartvseries.TvSeriesClickListener;
import com.sivaram.populartvseries.model.HomeTvSeries;
import com.squareup.picasso.Picasso;

import java.util.List;

public class HomeTvSeriesAdapter extends PagingAdapter {

    private Context context;
    private List<HomeTvSeries> homeTvSeriesList;
    private TvSeriesClickListener tvSeriesClickListener;

    public HomeTvSeriesAdapter(Context context, List<HomeTvSeries> homeTvSeriesList, TvSeriesClickListener tvSeriesClickListener){
        this.context = context;
        this.homeTvSeriesList = homeTvSeriesList;
        this.tvSeriesClickListener = tvSeriesClickListener;
    }
    @NonNull
    @Override
    public HomeTvSeriesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.tv_series_layout, parent, false);
        return new HomeTvSeriesAdapter.HomeTvSeriesHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        HomeTvSeriesHolder homeTvSeriesHolder = (HomeTvSeriesHolder) holder;
        homeTvSeriesHolder.seriesName.setText(homeTvSeriesList.get(position).getSeriesName());
        homeTvSeriesHolder.date.setText(homeTvSeriesList.get(position).getPublishedDate());
        String voteAvg = Double.toString(homeTvSeriesList.get(position).getVoteAvg());
        homeTvSeriesHolder.voteAverage.setText(voteAvg);
        if(homeTvSeriesList.get(position).getPosterUrl()==null){
            homeTvSeriesHolder.posterImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.series_icon));
        }
        else{
            Picasso.get().load(homeTvSeriesList.get(position).getPosterUrl()).into(homeTvSeriesHolder.posterImage);
        }

        homeTvSeriesHolder.tvSeriesCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvSeriesClickListener.onClick(homeTvSeriesList.get(position).getId());
            }
        });
        int endStatus = (int) homeTvSeriesList.get(position).getVoteAvg()*10;
        final int[] progressStatus = {0};
        Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (progressStatus[0] < endStatus) {
                    progressStatus[0] += 1;

                    handler.post(new Runnable() {
                        public void run() {
                            homeTvSeriesHolder.voteProgressBar.setProgress(progressStatus[0]);
                        }
                    });

                    try {
                        Thread.sleep(25);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();     //For vote average animation

    }

    @Override
    public int getPagingLayout() {
        return R.layout.tv_series_layout;
    }

    @Override
    public int getPagingItemCount() {
        return homeTvSeriesList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    class HomeTvSeriesHolder extends RecyclerView.ViewHolder {

        private ImageView posterImage;
        private TextView voteAverage, seriesName, date;
        private ProgressBar voteProgressBar;
        private CardView tvSeriesCard;

        public HomeTvSeriesHolder(@NonNull View itemView) {
            super(itemView);
            posterImage = itemView.findViewById(R.id.poster_image);
            voteAverage = itemView.findViewById(R.id.vote_avg);
            seriesName = itemView.findViewById(R.id.series_name);
            date = itemView.findViewById(R.id.published_date);
            voteProgressBar = itemView.findViewById(R.id.progress_bar);
            tvSeriesCard = itemView.findViewById(R.id.tv_series_card);
        }
    }
}

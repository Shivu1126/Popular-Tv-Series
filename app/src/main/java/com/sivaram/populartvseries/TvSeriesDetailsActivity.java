package com.sivaram.populartvseries;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.sivaram.populartvseries.adapter.CastAdapter;
import com.sivaram.populartvseries.adapter.GenreAdapter;
import com.sivaram.populartvseries.model.Cast;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class TvSeriesDetailsActivity extends AppCompatActivity {

    private RecyclerView genreRv;
    private RecyclerView castRv;
    private List<String> genreList;
    private List<Cast> castList;
    private GenreAdapter genreAdapter;
    private CastAdapter castAdapter;
    private Context context;
    private CardView durationCard, publishDateCard, lastSeasonCard, overViewCard;
    private ImageView backDropImg, posterImg, networkImg;
    private ProgressBar progressBar;
    private TextView seriesNameTv, publishedDateTv, durationTv, voteCountTv, voteAvgTv;
    private TextView overViewTv, creatorTv, orgTitleTv, statusTv, orgLanguageTv, typeTv, noOfSeasonTv, noOfEpisodeTv;
    private TextView creatorLabelTv, networkLabelTv, orgTitleLabelTv, statusLabelTv;
    private TextView orgLanguageLabelTv, typeLabelTv, noOfSeasonLabelTv, noOfEpisodeLabelTv;

    private TextView lastSeasonTv, yearAndEpisodes, premieredDetail;
    private ImageView lastSeasonPoster;
    private RelativeLayout backLayout, loadMoreLayout;

    private VolleyRequest volleyRequest;
    private VolleyRequest castVolleyRequest;
    private int progressStatus = 0;
    private int tvSeriesId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tv_series_details);
        tvSeriesId = getIntent().getIntExtra("id", 0);
        init();
        backLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        if(Common.getNetWorkStatus((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE)))
            getTvSeriesDetailsData();
        else
        {
            Common.makeToast("Please check the Network", context);
            new Handler(Looper.myLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            }, 2000);

        }

    }
    private void init(){
        context = TvSeriesDetailsActivity.this;
        genreRv = findViewById(R.id.genre_rv);
        castRv = findViewById(R.id.cast_rv);
        durationCard = findViewById(R.id.series_duration_card);
        publishDateCard = findViewById(R.id.date_card);
        lastSeasonCard = findViewById(R.id.last_season_card);
        overViewCard = findViewById(R.id.overview_card);

        progressBar = findViewById(R.id.progress_bar);

        backDropImg = findViewById(R.id.backdrop_pic);
        posterImg = findViewById(R.id.poster_pic_dt);
        networkImg = findViewById(R.id.network_image);

        seriesNameTv = findViewById(R.id.series_name_dt);
        publishedDateTv = findViewById(R.id.published_date);
        durationTv = findViewById(R.id.duration);
        voteCountTv = findViewById(R.id.vote_score);
        voteAvgTv = findViewById(R.id.vote_avg);

        overViewTv = findViewById(R.id.overview_content);
        creatorTv = findViewById(R.id.creator_name);
        orgTitleTv = findViewById(R.id.original_title);
        statusTv = findViewById(R.id.status);
        orgLanguageTv = findViewById(R.id.original_language);
        typeTv = findViewById(R.id.type);
        noOfSeasonTv = findViewById(R.id.no_of_season);
        noOfEpisodeTv = findViewById(R.id.no_of_epi);

        creatorLabelTv = findViewById(R.id.creator_label);
        orgTitleLabelTv = findViewById(R.id.original_title_label);
        statusLabelTv = findViewById(R.id.status_label);
        orgLanguageLabelTv = findViewById(R.id.original_language_label);
        typeLabelTv = findViewById(R.id.type_label);
        noOfSeasonLabelTv = findViewById(R.id.no_of_season_label);
        noOfEpisodeLabelTv = findViewById(R.id.no_of_epi_label);
        networkLabelTv = findViewById(R.id.network_label);

        lastSeasonTv = findViewById(R.id.season_number);
        yearAndEpisodes = findViewById(R.id.year_episodes);
        premieredDetail = findViewById(R.id.premiere_detail);
        lastSeasonPoster = findViewById(R.id.last_poster_pic_dt);

        backLayout = findViewById(R.id.back_layout);
        loadMoreLayout = findViewById(R.id.load_more_layout);

        genreList = new ArrayList<>();
        volleyRequest = new VolleyRequest(context);
        genreAdapter = new GenreAdapter(context, genreList);
        genreRv.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        genreRv.setAdapter(genreAdapter);

        castList = new ArrayList<>();
        castVolleyRequest = new VolleyRequest(context);
        castAdapter = new CastAdapter(context, castList);
        castRv.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        castRv.setAdapter(castAdapter);
    }

    private void getTvSeriesDetailsData(){
        String urlPath = "tv/"+tvSeriesId;
        volleyRequest.getRequest(urlPath, new VolleyCallback() {
            @Override
            public void onSuccess(String response) throws JSONException {
                JSONObject jsonObj = new JSONObject(response);

                if(jsonObj.has("success")){
                    loadMoreLayout.setVisibility(View.VISIBLE);
                    Common.makeToast("Please check the Network", context);
                }
                else{
                    loadMoreLayout.setVisibility(View.GONE);
                    updateAllData(jsonObj);
                    JSONArray genreArray = jsonObj.getJSONArray("genres");
                    updateGenreArray(genreArray);
                    getCast();
                }

            }
            @Override
            public void onError(VolleyError error) throws JSONException {
                Common.makeToast("Please check the Network", context);
                Log.d("volley-error", error.getMessage());
            }
        });
    }

    private void updateAllData(JSONObject jsonObj){
        try {
            if(jsonObj.getString("poster_path").equals("null")){
                posterImg.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.series_icon));
            }
            else{
                Picasso.get().load((context.getString(R.string.image_base_url)+jsonObj.getString("poster_path"))).into(posterImg);
            }

            if(jsonObj.getString("backdrop_path").equals("null")){
                backDropImg.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.series_icon));
            }
            else{
                Picasso.get().load((context.getString(R.string.image_base_url)+jsonObj.getString("backdrop_path"))).into(backDropImg);
            }

            seriesNameTv.setText(jsonObj.getString("name"));


            String date = null;
            if(jsonObj.getString("first_air_date").length()!=0){
                date = Common.changeDateFormat(jsonObj.getString("first_air_date"));
            }
            if(date==null){
                publishDateCard.setVisibility(View.GONE);
            }else{
                publishedDateTv.setText(date);
            }

            if(jsonObj.getJSONArray("episode_run_time").length()!=0){
                durationCard.setVisibility(View.VISIBLE);
                durationTv.setText(jsonObj.getJSONArray("episode_run_time").get(0)+" min");
            }
            else{
                durationCard.setVisibility(View.GONE);
            }



            if(jsonObj.getString("overview").isEmpty()){
                overViewCard.setVisibility(View.GONE);
            }else{
                overViewCard.setVisibility(View.VISIBLE);
                overViewTv.setText(jsonObj.getString("overview"));
            }

            if((jsonObj.getDouble("vote_average")+"").length()!=3)
                voteAvgTv.setText(""+new DecimalFormat("#.#").format(jsonObj.getDouble("vote_average")));
            else{
                voteAvgTv.setText(""+ jsonObj.getDouble("vote_average"));
            }

            voteCountTv.setText("("+jsonObj.getInt("vote_count")+" vote)");


            if (jsonObj.getJSONArray("created_by").length()!=0){
                creatorTv.setVisibility(View.VISIBLE);
                creatorLabelTv.setVisibility(View.VISIBLE);
                creatorTv.setText(jsonObj.getJSONArray("created_by").getJSONObject(0).getString("name"));
            }
            else {
                creatorTv.setVisibility(View.GONE);
                creatorLabelTv.setVisibility(View.GONE);
            }

            if (jsonObj.getJSONArray("networks").length()!=0){
                networkLabelTv.setVisibility(View.VISIBLE);
                networkImg.setVisibility(View.VISIBLE);
                Picasso.get().load((context.getString(R.string.image_base_url)
                        +jsonObj.getJSONArray("networks").getJSONObject(0).getString("logo_path"))).into(networkImg);
            }
            else {
                networkLabelTv.setVisibility(View.GONE);
                networkImg.setVisibility(View.GONE);
            }

            if (jsonObj.getJSONArray("spoken_languages").length()!=0){
                orgLanguageLabelTv.setVisibility(View.VISIBLE);
                orgLanguageTv.setVisibility(View.VISIBLE);
                orgLanguageTv.setText(jsonObj.getJSONArray("spoken_languages").getJSONObject(0).getString("english_name"));
            }
            else {
                orgLanguageLabelTv.setVisibility(View.GONE);
                orgLanguageTv.setVisibility(View.GONE);
            }


            orgTitleTv.setText(jsonObj.getString("original_name"));
            statusTv.setText(jsonObj.getString("status"));
            typeTv.setText(jsonObj.getString("type"));
            noOfSeasonTv.setText(jsonObj.getInt("number_of_episodes")+"");
            noOfEpisodeTv.setText(jsonObj.getInt("number_of_seasons")+"");

            JSONObject lastSeasonObj = jsonObj.getJSONArray("seasons").getJSONObject(jsonObj.getJSONArray("seasons").length()-1);
            lastSeasonTv.setText(lastSeasonObj.getString("name"));


            if(lastSeasonObj.getString("air_date").equals("null")){
                premieredDetail.setVisibility(View.GONE);
                yearAndEpisodes.setVisibility(View.GONE);
                lastSeasonCard.setVisibility(View.GONE);
            }
            else {
                premieredDetail.setVisibility(View.VISIBLE);
                lastSeasonCard.setVisibility(View.VISIBLE);
                premieredDetail.setText(lastSeasonObj.getString("name")+" of "+jsonObj.getString("name")+
                        "premiered on "+Common.changeDateFormat(lastSeasonObj.getString("air_date")));

                yearAndEpisodes.setText(lastSeasonObj.getString("air_date").split("-")[0] + "  |  "
                        +lastSeasonObj.getInt("episode_count")+" Episodes");
            }


            if(lastSeasonObj.getString("poster_path").equals("null")){
                lastSeasonPoster.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.series_icon));
            }else{
                Picasso.get().load(context.getString(R.string.image_base_url)+lastSeasonObj.getString("poster_path")).into(lastSeasonPoster);
            }

            int endStatus = (int) jsonObj.getDouble("vote_average")*10;

            Handler handler = new Handler();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (progressStatus < endStatus) {
                        progressStatus += 1;

                        handler.post(new Runnable() {
                            public void run() {
                                progressBar.setProgress(progressStatus);
                            }
                        });

                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private void updateGenreArray(JSONArray genreArray) {
        try {
            for (int i = 0; i < genreArray.length(); i++) {
                JSONObject genreObj = genreArray.getJSONObject(i);
                genreList.add(genreObj.getString("name"));
            }
            genreAdapter.notifyDataSetChanged();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private void getCast(){
        String urlPath = "tv/"+tvSeriesId+"/aggregate_credits";
        castVolleyRequest.getRequest(urlPath, new VolleyCallback() {
            @Override
            public void onSuccess(String response) throws JSONException {
                JSONObject jsonObject = new JSONObject(response);
                if(jsonObject.has("success")){
                    Common.makeToast("Please Check the Network", context);
                }else{
                    updateCast(jsonObject.getJSONArray("cast"));
                }
            }

            @Override
            public void onError(VolleyError error) throws JSONException {
                Common.makeToast("Please check the Network", context);
                Log.d("volley-error", error.getMessage());
            }
        });
    }
    private void updateCast(JSONArray jsonArray){
        try {
            for (int i = 0; i < jsonArray.length() && i < 10; i++) {
                String nameStr = jsonArray.getJSONObject(i).getString("name");
                String characterNameStr = jsonArray.getJSONObject(i).getJSONArray("roles").getJSONObject(0).getString("character");
                String episodes = "("+jsonArray.getJSONObject(i).getString("total_episode_count")+" episodes)";
                String url = context.getString(R.string.image_base_url) +jsonArray.getJSONObject(i).getString("profile_path");
                if(jsonArray.getJSONObject(i).getString("profile_path").equals("null")){
                    url = null;
                }
                castList.add(new Cast(nameStr, characterNameStr, episodes, url));
            }
            castAdapter.notifyDataSetChanged();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
package com.sivaram.populartvseries;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.akiniyalocts.pagingrecycler.PagingDelegate;
import com.android.volley.VolleyError;
import com.sivaram.populartvseries.adapter.HomeTvSeriesAdapter;
import com.sivaram.populartvseries.model.HomeTvSeries;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements PagingDelegate.OnPageListener {

    private Context context;
    private VolleyRequest volleyRequest;
    private RecyclerView tvSeriesRecyclerView;
    private EditText searchBar;
    private List<HomeTvSeries> homeTvSeriesList;
    private GridLayoutManager gridLayoutManager;
    private ProgressBar progressBar;

    private boolean isLoad;
    private int page = 1;

    private HomeTvSeriesAdapter homeTvSeriesAdapter;

    private final TvSeriesClickListener tvSeriesClickListener = new TvSeriesClickListener() {
        @Override
        public void onClick(int id) {
            Intent intent = new Intent(MainActivity.this, TvSeriesDetailsActivity.class );
            intent.putExtra("id", id);
            startActivity(intent);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        gridLayoutManager = new GridLayoutManager(context, 2);
        tvSeriesRecyclerView.setLayoutManager(gridLayoutManager);
        tvSeriesRecyclerView.addItemDecoration(new DividerItemDecoration(this, gridLayoutManager.getOrientation()));
        homeTvSeriesAdapter = new HomeTvSeriesAdapter(context, homeTvSeriesList, tvSeriesClickListener);
        tvSeriesRecyclerView.setAdapter(homeTvSeriesAdapter);

        PagingDelegate pagingDelegate = new PagingDelegate.Builder(homeTvSeriesAdapter)
                .attachTo(tvSeriesRecyclerView)
                .listenWith(this)
                .build();

        getTvSeriesData();
    }
    private void init(){
        context = MainActivity.this;
        volleyRequest = new VolleyRequest(this);
        tvSeriesRecyclerView = findViewById(R.id.tv_series_rv);
        searchBar = findViewById(R.id.searchText);
        homeTvSeriesList = new ArrayList<>();
        isLoad = true;
        progressBar = findViewById(R.id.load_more);
    }

    private void getTvSeriesData(){
        String urlPath = "discover/tv?page="+page;
        volleyRequest.getRequest(urlPath, new VolleyCallback() {
            @Override
            public void onSuccess(String response) throws JSONException {
                JSONObject jsonObj = new JSONObject(response.toString());

                if(jsonObj.has("results")){
                    Log.d("tvSeriesResponse", jsonObj.toString());
                    fetchTvSeriesData(jsonObj);
                }
                else{
                    Log.d("page-finished", jsonObj.toString());
                }

            }
            @Override
            public void onError(VolleyError error) throws JSONException {
                Common.makeToast("No More Series", context);
            }
        });
    }

    private void fetchTvSeriesData(JSONObject jsonObject){
        try {

            JSONArray jsonArray = jsonObject.getJSONArray("results");
            for (int i=0;i<jsonArray.length();i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                String posterUrl = obj.getString("poster_path").equals("null")?null:
                        (context.getString(R.string.image_base_url)+obj.getString("poster_path"));
                homeTvSeriesList.add(new HomeTvSeries( obj.getInt("id"),
                        posterUrl,
                        obj.getDouble("vote_average"), obj.getString("name"),
                        Common.changeDateFormat(obj.getString("first_air_date"))
                    )
                );

            }
            homeTvSeriesAdapter.notifyDataSetChanged();

        }
        catch (Exception e){
            e.printStackTrace();
            Common.makeToast("Data Error", context);
        }
    }



    @Override
    public void onPage(int i) {
        progressBar.setVisibility(View.VISIBLE);
            new Handler(Looper.myLooper())
                    .postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            page++;
                            getTvSeriesData();
                            progressBar.setVisibility(View.GONE);
                        }
                    }, 2000);

    }

    @Override
    public void onDonePaging() {
        progressBar.setVisibility(View.GONE);
//        makeToast("Data Finished..");
    }
}
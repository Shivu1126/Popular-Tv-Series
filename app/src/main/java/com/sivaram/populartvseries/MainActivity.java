package com.sivaram.populartvseries;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.akiniyalocts.pagingrecycler.PagingDelegate;
import com.android.volley.VolleyError;
import com.sivaram.populartvseries.adapter.HomeTvSeriesAdapter;
import com.sivaram.populartvseries.database.LocalDB;
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
    private ImageView searchIcon;
    private List<HomeTvSeries> homeTvSeriesList;
    private GridLayoutManager gridLayoutManager;
    private ProgressBar progressBar;
    private SwipeRefreshLayout refreshLayout;
    private LocalDB localDB;

    private boolean isLoad;
    private int page = 1;

    private HomeTvSeriesAdapter homeTvSeriesAdapter;

    private final TvSeriesClickListener tvSeriesClickListener = new TvSeriesClickListener() {
        @Override
        public void onClick(int id) {
            Intent intent = new Intent(MainActivity.this, TvSeriesDetailsActivity.class );
            intent.putExtra("id", id);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
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

        searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SearchActivity.class );
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        if(Common.getNetWorkStatus((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE)))
        {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) progressBar.getLayoutParams();
            layoutParams.removeRule(RelativeLayout.CENTER_IN_PARENT);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            progressBar.setLayoutParams(layoutParams);
            progressBar.setVisibility(View.GONE);
            getTvSeriesData();
        }
        else{
            getTvSeriesFromLocalDb();
        }
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Common.makeToast("Refresh", context);
                if(Common.getNetWorkStatus((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE)))
                {
                    page=1;
                    homeTvSeriesList.clear();
                    getTvSeriesData();
                }
                else{
                    getTvSeriesFromLocalDb();
                }
                refreshLayout.setRefreshing(false);
            }
        });
    }
    private void init(){
        context = MainActivity.this;
        volleyRequest = new VolleyRequest(this);
        tvSeriesRecyclerView = findViewById(R.id.tv_series_rv);
        homeTvSeriesList = new ArrayList<>();
        isLoad = true;
        progressBar = findViewById(R.id.load_more);
        searchIcon = findViewById(R.id.search_icon_);
        refreshLayout = findViewById(R.id.refresh);

        localDB = LocalDB.getInstance(context);
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
                String date =
                        obj.getString("first_air_date").length()!=0?
                                Common.changeDateFormat(obj.getString("first_air_date")):null;
                String posterUrl = obj.getString("poster_path").equals("null")?null:
                        (context.getString(R.string.image_base_url)+obj.getString("poster_path"));

                HomeTvSeries homeTvSeries = new HomeTvSeries( obj.getInt("id"),
                        posterUrl,
                        obj.getDouble("vote_average"), obj.getString("name"),
                        date
                );
                homeTvSeriesList.add(homeTvSeries);
                localDB.tvSeriesDao().addTvSeries(homeTvSeries);
            }
            homeTvSeriesAdapter.notifyDataSetChanged();

        }
        catch (Exception e){
            e.printStackTrace();
            Common.makeToast("Data Error", context);
        }
    }

    private void getTvSeriesFromLocalDb(){
        Log.d("connection", "NO");
        homeTvSeriesList.clear();
        homeTvSeriesList.addAll(localDB.tvSeriesDao().getAllSeries());
        Log.d("local_db_list_size", homeTvSeriesList.size()+"");
        if(homeTvSeriesList.isEmpty()){
            Common.makeToast("Please On the Data", context);
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) progressBar.getLayoutParams();
            layoutParams.removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            progressBar.setLayoutParams(layoutParams);
            progressBar.setVisibility(View.VISIBLE);
        }else{
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) progressBar.getLayoutParams();
            layoutParams.removeRule(RelativeLayout.CENTER_IN_PARENT);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            progressBar.setLayoutParams(layoutParams);
            progressBar.setVisibility(View.GONE);
        }
        homeTvSeriesAdapter.notifyDataSetChanged();
    }



    @Override
    public void onPage(int i) {
        progressBar.setVisibility(View.VISIBLE);
            new Handler(Looper.myLooper())
                    .postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if(Common.getNetWorkStatus((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE)))
                            {
                                page++;
                                getTvSeriesData();

                            }else{

                                Common.makeToast("If you want more series, Please On the Network", context);
                            }
                            progressBar.setVisibility(View.GONE);
                        }
                    }, 2000);

    }

    @Override
    public void onDonePaging() {
        progressBar.setVisibility(View.GONE);
    }

}
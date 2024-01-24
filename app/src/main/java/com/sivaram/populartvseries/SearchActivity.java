package com.sivaram.populartvseries;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.android.volley.VolleyError;
import com.sivaram.populartvseries.adapter.SearchTvSeriesAdapter;
import com.sivaram.populartvseries.database.LocalDB;
import com.sivaram.populartvseries.model.HomeTvSeries;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private Context context;
    private EditText searchEt;
    private RecyclerView searchRv;
    private ProgressBar searchLoading;
    private RelativeLayout rootLayout, noSearchFoundLayout;
    private ImageView clearText;
    private VolleyRequest volleyRequest;
    private List<HomeTvSeries> searchList;
    private SearchTvSeriesAdapter searchTvSeriesAdapter;
    private CardView homeCard;
    private LocalDB localDB;
    private TvSeriesClickListener tvSeriesClickListener = new TvSeriesClickListener() {
        @Override
        public void onClick(int id) {
            Intent intent = new Intent(SearchActivity.this, TvSeriesDetailsActivity.class );
            intent.putExtra("id", id);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
    };

    private void init(){
        context = SearchActivity.this;
        searchEt = findViewById(R.id.searchText);
        searchRv = findViewById(R.id.search_result_rv);
        searchLoading = findViewById(R.id.load_more_search);
        noSearchFoundLayout = findViewById(R.id.no_result_layout);
        clearText = findViewById(R.id.clear_text);
        homeCard = findViewById(R.id.home_card);

        rootLayout = findViewById(R.id.root_layout);
        volleyRequest = new VolleyRequest(context);

        searchList = new ArrayList<>();
        localDB = LocalDB.getInstance(context);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        init();

        searchRv.setLayoutManager(new LinearLayoutManager(context));
        searchTvSeriesAdapter = new SearchTvSeriesAdapter(context, searchList, tvSeriesClickListener);
        searchRv.setAdapter(searchTvSeriesAdapter);

        searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                noSearchFoundLayout.setVisibility(View.GONE);
                searchLoading.setVisibility(View.VISIBLE);
                clearText.setVisibility(View.VISIBLE);
                searchRv.smoothScrollToPosition(0);
                if(editable.toString().isEmpty()){
                    noSearchFoundLayout.setVisibility(View.VISIBLE);
                    clearText.setVisibility(View.GONE);
                    searchLoading.setVisibility(View.GONE);
                    searchList.clear();
                    searchTvSeriesAdapter.notifyDataSetChanged();
                }
                else {
                    new Handler(Looper.myLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            String searchStr = editable.toString();

                            if(Common.getNetWorkStatus((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE)))
                            {
                                getSearchData(searchStr);
                            }
                            else{
                                searchFromLocalDb(searchStr);
                            }
                        }
                    }, 3000);
                }
            }
        });
        clearText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchEt.getText().clear();
                noSearchFoundLayout.setVisibility(View.VISIBLE);
                clearText.setVisibility(View.GONE);
                searchLoading.setVisibility(View.GONE);
                searchList.clear();
                searchTvSeriesAdapter.notifyDataSetChanged();
                hideKeyboard();
            }
        });

        homeCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        rootLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                hideKeyboard();
                return false;
            }
        });

    }

    private void getSearchData(String searchStr){

        String urlPath = "search/tv?query="+searchStr;
        volleyRequest.getRequest(urlPath, new VolleyCallback() {

            @Override
            public void onSuccess(String response) throws JSONException {
                JSONObject jsonObject = new JSONObject(response);
                searchLoading.setVisibility(View.GONE);
                if(jsonObject.getJSONArray("results").length()==0){
                    searchRv.setVisibility(View.VISIBLE);
                    noSearchFoundLayout.setVisibility(View.GONE);
                }
                else{
                    searchRv.setVisibility(View.VISIBLE);
                    noSearchFoundLayout.setVisibility(View.GONE);
                    setData(jsonObject.getJSONArray("results"));
                }
            }

            @Override
            public void onError(VolleyError error) throws JSONException {
                searchLoading.setVisibility(View.GONE);
                noSearchFoundLayout.setVisibility(View.VISIBLE);
                Log.d("error-search", error.getMessage());
            }
        });
    }

    private void setData(JSONArray jsonArray){
        try{
            searchList.clear();

            for(int i=0;i<jsonArray.length();i++){
                String date =
                        jsonArray.getJSONObject(i).getString("first_air_date").length()!=0?
                                Common.changeDateFormat(jsonArray.getJSONObject(i).getString("first_air_date")):null;

                String posterUrl = jsonArray.getJSONObject(i).getString("backdrop_path").equals("null")?null:
                        (context.getString(R.string.image_base_url)+jsonArray.getJSONObject(i).getString("backdrop_path"));
                searchList.add(new HomeTvSeries(jsonArray.getJSONObject(i).getInt("id"),
                        posterUrl,
                        jsonArray.getJSONObject(i).getDouble("vote_average"),
                        jsonArray.getJSONObject(i).getString("name"),
                        date));
            }

            searchTvSeriesAdapter.notifyDataSetChanged();
        }
        catch (Exception e){
            Log.d("setData-Search", e.getMessage());
        }
    }

    private void searchFromLocalDb(String searchStr){
        if(searchStr.isEmpty()){
            searchRv.setVisibility(View.GONE);
            noSearchFoundLayout.setVisibility(View.VISIBLE);
            return;
        }
        Log.d("search_local", "called");
        searchStr = "%"+searchStr+"%";
        searchList.clear();
        Log.d("search_list_size", localDB.tvSeriesDao().getSearchedSeries(searchStr).size()+"");
        searchLoading.setVisibility(View.GONE);
        if(localDB.tvSeriesDao().getSearchedSeries(searchStr).isEmpty()){
            searchRv.setVisibility(View.GONE);
            noSearchFoundLayout.setVisibility(View.VISIBLE);
        }
        else {
            searchList.addAll(localDB.tvSeriesDao().getSearchedSeries(searchStr));
            searchRv.setVisibility(View.VISIBLE);
            searchTvSeriesAdapter.notifyDataSetChanged();
        }
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
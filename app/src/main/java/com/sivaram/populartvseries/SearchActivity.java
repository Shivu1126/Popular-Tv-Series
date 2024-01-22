package com.sivaram.populartvseries;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.EditText;

public class SearchActivity extends AppCompatActivity {

    private EditText searchEt;
    private RecyclerView searchRv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        init();
    }
    private void init(){
        searchEt = findViewById(R.id.searchText);
        searchRv = findViewById(R.id.search_result_rv);
    }
}
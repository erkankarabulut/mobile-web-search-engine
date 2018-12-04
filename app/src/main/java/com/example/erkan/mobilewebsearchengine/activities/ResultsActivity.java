package com.example.erkan.mobilewebsearchengine.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.erkan.mobilewebsearchengine.R;
import com.example.erkan.mobilewebsearchengine.action.HITSAlgorithm;
import com.example.erkan.mobilewebsearchengine.beans.Page;
import com.example.erkan.mobilewebsearchengine.repository.BaseRepository;
import com.example.erkan.mobilewebsearchengine.util.MyRecyclerViewAdapter;

import java.util.ArrayList;

public class ResultsActivity extends AppCompatActivity implements MyRecyclerViewAdapter.ItemClickListener {

    RecyclerView recyclerView;
    MyRecyclerViewAdapter adapter;
    BaseRepository baseRepository;

    Button previous;
    Button next;
    TextView queryResultString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        previous            = (Button) findViewById(R.id.previousButton);
        next                = (Button) findViewById(R.id.nextButton);
        queryResultString   = (TextView) findViewById(R.id.queryResultString);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        baseRepository = new BaseRepository();
        ArrayList<String> urlList = getIntent().getStringArrayListExtra("urlList");
        ArrayList<ArrayList<String>> result = baseRepository.getHeaders(urlList);
        final ArrayList<String> newURLList = result.get(0);
        ArrayList<String> headers = result.get(1);

        final Long rootSetTime = getIntent().getLongExtra("rootSetTime", 0);
        final Long baseSetTime = getIntent().getLongExtra("baseSetTime", 0);
        final Long hitsTime = getIntent().getLongExtra("hitsTime", 0);
        final Integer start   = getIntent().getIntExtra("start", 0);
        final Integer length  = getIntent().getIntExtra("length", 10);

        queryResultString.setText("Showing " + length + " results between " + start + " and " + (start + length) + " out of " +
            newURLList.size() + " results.");
        if (start == 0){
            previous.setVisibility(View.GONE);
        }else {
            previous.setVisibility(View.VISIBLE);
        }

        if(headers.size() < start + length){
            next.setVisibility(View.GONE);
        }else {
            next.setVisibility(View.VISIBLE);
        }

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyRecyclerViewAdapter(this, headers.subList(start.intValue(),
                (headers.size() > start+length ? start.intValue() + length.intValue() : headers.size()))
                , newURLList.subList(start.intValue(), (newURLList.size() > start.intValue()+ length.intValue() ? start.intValue()+ length.intValue() : newURLList.size())));
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(ResultsActivity.this, ResultsActivity.class);
                intent.putExtra("rootSetTime", rootSetTime);
                intent.putExtra("baseSetTime", baseSetTime);
                intent.putExtra("hitsTime", hitsTime);
                intent.putExtra("urlList", newURLList);
                intent.putExtra("start", start+10);
                intent.putExtra("length", (newURLList.size() > 10 ? 10 : newURLList.size()));
                startActivity(intent);
            }
        });

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(ResultsActivity.this, ResultsActivity.class);
                intent.putExtra("rootSetTime", rootSetTime);
                intent.putExtra("baseSetTime", baseSetTime);
                intent.putExtra("hitsTime", hitsTime);
                intent.putExtra("urlList", newURLList);
                intent.putExtra("start", start-10);
                intent.putExtra("length", (newURLList.size() > 10 ? 10 : newURLList.size()));
                startActivity(intent);
            }
        });

    }

    @Override
    public void onItemClick(View view, int position) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

}

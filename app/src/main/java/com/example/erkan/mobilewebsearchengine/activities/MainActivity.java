package com.example.erkan.mobilewebsearchengine.activities;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.erkan.mobilewebsearchengine.R;
import com.example.erkan.mobilewebsearchengine.action.HITSAlgorithm;
import com.example.erkan.mobilewebsearchengine.beans.Page;
import com.example.erkan.mobilewebsearchengine.repository.BaseRepository;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public EditText searchInput;

    public Button feelingLucky;
    public Button searchButton;

    public String inputString;

    public ArrayList<Page> rootPageSet;
    public ArrayList<Page> basePageSet;

    public HITSAlgorithm hitsAlgorithm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setActivityBackgroundColor(Color.WHITE);

        searchInput     = (EditText) findViewById(R.id.search_input);
        feelingLucky    = (Button) findViewById(R.id.feeling_lucky);
        searchButton    = (Button) findViewById(R.id.search_button);

        hitsAlgorithm   = new HITSAlgorithm();

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputString = searchInput.getText().toString();
                System.out.println("Searching for: " + inputString);

                try {
                    new Thread(){
                        @Override
                        public void run() {
                            try {
                                String query = "https://www.google.com/search?q=" + inputString + "&num=10";
                                String page = BaseRepository.getSearchContent(query);
                                List<String> links = BaseRepository.parseLinks(page);

                                rootPageSet = new ArrayList<>();
                                for (int i=0; i<links.size(); i++) {
                                    rootPageSet.add(new Page(links.get(i)));
                                }

                                basePageSet = hitsAlgorithm.getBasePageSet(rootPageSet);
                            } catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public void setActivityBackgroundColor(int color) {
        View view = this.getWindow().getDecorView();
        view.setBackgroundColor(color);
    }
}

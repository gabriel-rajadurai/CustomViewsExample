package com.gabriel.tictaccustomview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.gabriel.tictaccustomview.views.TableView;

public class TableActivity extends AppCompatActivity {

    TableView tableView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.table_layout);
        tableView = findViewById(R.id.table);

        tableView.setColumnTitle("ABC","DEF","GHI");

        tableView.insertRow("A","B","C");
        tableView.insertRow("x","y");
        tableView.insertRow("z");
        tableView.insertRow("","","m");
    }
}

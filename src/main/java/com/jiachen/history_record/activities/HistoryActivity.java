package com.jiachen.history_record.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jiachen.history_record.R;
import com.jiachen.history_record.eneities.History;
import com.jiachen.history_record.tools.CusAdapter;
import com.jiachen.history_record.tools.SharedPreferenceUtil;

import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<History> historyList = new ArrayList<>();
    SharedPreferenceUtil sharedPreferenceUtil = new SharedPreferenceUtil();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_records);

        recyclerView = findViewById(R.id.recyclerView);

        initData();
        initRecyclerView();
    }

    private void initRecyclerView() {
        CusAdapter cusAdapter = new CusAdapter(historyList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(cusAdapter);
    }

    private void initData() {
        historyList.add(new History("Building 1", "Building 10", "2020-02-02 18:00", "1h", "100k"));
        historyList.add(new History("Building 12", "Building 62", "2019-02-02 18:00", "2h", "100k"));
        historyList.add(new History("Building 4", "Building 21", "2018-02-02 18:00", "3h", "100k"));
        historyList.add(new History("Building 5", "Building 2", "2017-02-02 18:00", "4h", "100k"));
        historyList.add(new History("Building 2", "Building 52", "2016-02-02 18:00", "5h", "100k"));
        historyList.add(new History("Building 6", "Building 99", "2015-02-02 18:00", "6h", "100k"));
        historyList.add(new History("Building 3", "Building 14", "2014-02-02 18:00", "7h", "100k"));
        historyList.add(new History("Building 8", "Building 56", "2013-02-02 18:00", "8h", "100k"));
        historyList.add(new History("Building 1", "Building 10", "2020-02-02 18:00", "1h", "100k"));
        historyList.add(new History("Building 12", "Building 62", "2019-02-02 18:00", "2h", "100k"));
        historyList.add(new History("Building 4", "Building 21", "2018-02-02 18:00", "3h", "100k"));
        historyList.add(new History("Building 5", "Building 2", "2017-02-02 18:00", "4h", "100k"));
        historyList.add(new History("Building 2", "Building 52", "2016-02-02 18:00", "5h", "100k"));
        historyList.add(new History("Building 6", "Building 99", "2015-02-02 18:00", "6h", "100k"));
        historyList.add(new History("Building 3", "Building 14", "2014-02-02 18:00", "7h", "100k"));
        historyList.add(new History("Building 8", "Building 56", "2013-02-02 18:00", "8h", "100k"));
        historyList.add(new History("Building 1", "Building 10", "2020-02-02 18:00", "1h", "100k"));
        historyList.add(new History("Building 12", "Building 62", "2019-02-02 18:00", "2h", "100k"));
        historyList.add(new History("Building 4", "Building 21", "2018-02-02 18:00", "3h", "100k"));
        historyList.add(new History("Building 5", "Building 2", "2017-02-02 18:00", "4h", "100k"));
        historyList.add(new History("Building 2", "Building 52", "2016-02-02 18:00", "5h", "100k"));
        historyList.add(new History("Building 6", "Building 99", "2015-02-02 18:00", "6h", "100k"));
        historyList.add(new History("Building 3", "Building 14", "2014-02-02 18:00", "7h", "100k"));
        historyList.add(new History("Building 8", "Building 56", "2013-02-02 18:00", "8h", "100k"));
        sharedPreferenceUtil.save("history", "history", SharedPreferenceUtil.Object2String(historyList), HistoryActivity.this);
        //historyList = (ArrayList<History>) SharedPreferenceUtil.String2Object((String) (sharedPreferenceUtil.get("history", "history", HistoryActivity.this)));
    }
}

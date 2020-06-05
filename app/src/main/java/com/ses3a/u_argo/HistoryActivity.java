package com.ses3a.u_argo;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ses3a.u_argo.eneities.History;
import com.ses3a.u_argo.tools.CusAdapter;
import com.ses3a.u_argo.tools.SharedPreferenceUtil;

import java.util.ArrayList;
import java.util.Collections;

public class HistoryActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<History> historyList = new ArrayList<>();
    SharedPreferenceUtil sharedPreferenceUtil = new SharedPreferenceUtil();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        recyclerView = findViewById(R.id.recyclerView);
        String historyString = (String) (sharedPreferenceUtil.get("historys", "historys", HistoryActivity.this));
        if (historyString != null) {
            historyList = (ArrayList<History>) SharedPreferenceUtil.String2Object(historyString);
        }
        initRecyclerView();
    }

    private void initRecyclerView() {
        if (historyList.size() == 0)
            return;
        Collections.reverse(historyList);
        CusAdapter cusAdapter = new CusAdapter(historyList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(cusAdapter);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}

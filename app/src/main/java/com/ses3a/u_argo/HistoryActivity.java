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
        historyList = (ArrayList<History>) SharedPreferenceUtil.String2Object((String) (sharedPreferenceUtil.get("history", "history", HistoryActivity.this)));
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
        System.out.println("STOP!!!!!");
    }
}

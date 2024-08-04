package com.example.callhistory;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;

import java.util.concurrent.atomic.AtomicInteger;

public class Dashboard extends AppCompatActivity {
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        BottomNavigationView bottomNavigationView = findViewById(R.id.navigation);
        viewPager = findViewById(R.id.view_pager);
        tabLayout = findViewById(R.id.tab_layout);
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int menuItemId = item.getItemId();
                if (menuItemId == R.id.home) {
                   startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                } else if (menuItemId == R.id.dashboard) {


                    return true;
                }

                return false;
            }

        });

        setupviewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);


    }

    private void setupviewPager(ViewPager viewPager) {
        ViewpagerAdapter adapter = new ViewpagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new CallLogFragment(), "Call log");
        adapter.addFragment(new Contacts(), "Contacts");
        //adapter.addFragment(new Fragment3(), "");
        viewPager.setAdapter(adapter);

    }


}

package com.example.oaxacaos;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.example.oaxacaos.Models.UserData;

public class MainActivity extends AppCompatActivity {

    private static final String BACK_STACK_ROOT_TAG = "root_fragment";
    FragmentManager fragmentManager = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Prepare menu selection actions
        final BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_map:
                        fragmentManager.popBackStack(BACK_STACK_ROOT_TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        fragmentManager.beginTransaction()
                                .replace(R.id.placeholder_fl, new MapFragment())
                                .addToBackStack(BACK_STACK_ROOT_TAG)
                                .commit();
                        return true;
                    case R.id.navigation_reports:
                        fragmentManager.popBackStack(BACK_STACK_ROOT_TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        fragmentManager.beginTransaction()
                                .replace(R.id.placeholder_fl, new ReportFragment())
                                .addToBackStack(BACK_STACK_ROOT_TAG)
                                .commit();
                        return true;
                }
                return false;
            }
        });
        navigation.setSelectedItemId(R.id.navigation_map);
        navigation.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem menuItem) {
                Fragment currentFragment = fragmentManager.findFragmentById(R.id.placeholder_fl);
                switch (menuItem.getItemId()) {
                    case R.id.navigation_map:
                        if(!(currentFragment instanceof MapFragment)) {
                            fragmentManager.popBackStack(BACK_STACK_ROOT_TAG, 0);
                        }
                        break;
                    case R.id.navigation_reports:
                        if(!(currentFragment instanceof MapFragment)) {
                            fragmentManager.popBackStack(BACK_STACK_ROOT_TAG, 0);
                        }
                        break;
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (fragmentManager.getBackStackEntryCount() == 1) {
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle(getString(R.string.logout_title))
                    .setMessage(getString(R.string.logout_message))
                    .setPositiveButton(getString(R.string.logout_accept), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            UserData.getInstance().cleanInstance();
                            Intent intent = new Intent(getApplicationContext(), LoginOptionsActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton(getString(R.string.logout_decline), null)
                    .show();
        } else {
            super.onBackPressed();
        }
    }
}

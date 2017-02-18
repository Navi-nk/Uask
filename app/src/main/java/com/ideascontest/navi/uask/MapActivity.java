package com.ideascontest.navi.uask;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.design.widget.TabLayout;
import android.widget.Toast;

/**
 * Created by Navi on 17-02-2017.
 */

public class MapActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener {
    private TabLayout tabLayout;
    private Fragment currFr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_layout);

        // initialize tabLayout
        tabLayout = (TabLayout) findViewById(R.id.mapTabs);
        tabLayout.setOnTabSelectedListener(this);

    //    Toast.makeText(this, "Tab Count : " + tabLayout.getTabCount(), Toast.LENGTH_LONG).show();
        getFragmentManager().beginTransaction().replace(R.id.fragmentPlaceHolder, new PageFragmentOne()).commit();
    }


    @Override
    public void onTabSelected(TabLayout.Tab tab) {
      //  Toast.makeText(this, "You select tab : " + tab.getText(), Toast.LENGTH_SHORT).show();
        selectFragment(tab);
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }
    public void selectFragment(TabLayout.Tab tab) {
        switch (tab.getPosition()) {
            case 0:
                currFr = new PageFragmentOne();
                break;
            case 1:
                currFr = new PageFragmentTwo();
                break;
            case 2:
                currFr = new PageFragmentThree();
                break;
            default:
                currFr = new PageFragmentOne();
                break;
        }

        getFragmentManager().beginTransaction().replace(R.id.fragmentPlaceHolder,currFr).commit();

    }

}

package app.mynta.console.android.activities.about;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import app.mynta.console.android.R;

import app.mynta.console.android.activities.about.fragments.ClosedTicketsFragment;
import app.mynta.console.android.activities.about.fragments.OpenedTicketsFragment;
import app.mynta.console.android.utils.Helper;

public class MyTicketsActivity extends AppCompatActivity {

    final Fragment openedTicketsFragment = new OpenedTicketsFragment();
    final Fragment closedTicketsFragment = new ClosedTicketsFragment();

    // active fragment
    Fragment active = openedTicketsFragment;

    // bottom navigation
    BottomNavigationView bottomNavigationView;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // OPTIONS
        Helper.darkMode(this);

        setContentView(R.layout.activity_my_tickets);

        // initialize bottom navigation
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        FragmentManager fragmentManager = getSupportFragmentManager();

        // fragment manager
        fragmentManager.beginTransaction().add(R.id.fragment_container, closedTicketsFragment, "2").hide(closedTicketsFragment).commit();
        fragmentManager.beginTransaction().add(R.id.fragment_container, openedTicketsFragment, "1").commit();

        // bottom navigation on item selected listener
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.opened_tickets:
                    fragmentManager.beginTransaction().hide(active).show(openedTicketsFragment).commit();
                    active = openedTicketsFragment;
                    return true;
                case R.id.closed_tickets:
                    fragmentManager.beginTransaction().hide(active).show(closedTicketsFragment).commit();
                    active = closedTicketsFragment;
                    return true;
            }
            return false;
        });

        // bottom navigation on item reselected
        bottomNavigationView.setOnNavigationItemReselectedListener(MenuItem::getItemId);
    }
}
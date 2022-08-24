package com.openclassrooms.realestatemanager.UI;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;

import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.openclassrooms.realestatemanager.R;
import com.openclassrooms.realestatemanager.UI.fragments.DialogFragment;
import com.openclassrooms.realestatemanager.UI.fragments.ItemDetailFragment;
import com.openclassrooms.realestatemanager.UI.fragments.ItemListFragment;
import com.openclassrooms.realestatemanager.UI.fragments.MapFragment;
import com.openclassrooms.realestatemanager.UI.fragments.SettingsFragment;
import com.openclassrooms.realestatemanager.UI.fragments.SimulatorFragment;
import com.openclassrooms.realestatemanager.databinding.ActivityItemDetailBinding;
import com.openclassrooms.realestatemanager.placeholder.PlaceholderContent;
import com.openclassrooms.realestatemanager.roomdb.AppDatabase;
import com.openclassrooms.realestatemanager.roomdb.ResidenceDAO;

public class ItemDetailHostActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    AppDatabase db;
    ResidenceDAO residenceDAO;

    Fragment currentFragment;

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityItemDetailBinding binding = ActivityItemDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Request for camera runtime permission
        if (ContextCompat.checkSelfPermission(ItemDetailHostActivity.this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ItemDetailHostActivity.this, new String[]{
                    Manifest.permission.CAMERA
            }, 100);
        }

        //Request for storage access
        if (ContextCompat.checkSelfPermission(ItemDetailHostActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ItemDetailHostActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 200);
        }

        db = AppDatabase.getInstance(this);
        residenceDAO = db.residenceDAO();

        this.configureToolbar();
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();

        configureToolbar();
    }

    // ------------------
    //  CONFIGURE DRAWER
    // ------------------

    // 2 - Configure Drawer Layout
    private void configureDrawerLayout(){
        this.drawerLayout = findViewById(R.id.activity_main_nav_drawer);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    // 3 - Configure NavigationView
    private void configureNavigationView(){
        NavigationView navigationView = findViewById(R.id.activity_main_nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        // 5 - Handle back click to close menu
        if (this.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        // 4 - Handle Navigation Item Click
        int id = item.getItemId();

        switch (id){
            case R.id.activity_main_drawer_map :
                transaction.replace(R.id.nav_host_fragment_item_detail, new MapFragment());
                transaction.setReorderingAllowed(true).addToBackStack(null).commit();
                break;
            case R.id.activity_main_drawer_simulator:
                transaction.replace(R.id.nav_host_fragment_item_detail, new SimulatorFragment());
                transaction.setReorderingAllowed(true).addToBackStack(null).commit();
                break;
            case R.id.activity_main_drawer_settings:
                transaction.replace(R.id.nav_host_fragment_item_detail, new SettingsFragment());
                transaction.setReorderingAllowed(true).addToBackStack(null).commit();
                break;
            default:
                break;
        }

        this.drawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }

    // ---------------------
    // CONFIGURE TOOLBAR
    // ---------------------

    NavHostFragment navHostFragment;

    // 1 - Configure Toolbar
    public void configureToolbar() {
        this.toolbar = findViewById(R.id.activity_main_toolbar);
        setSupportActionBar(toolbar);

        navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_item_detail);
        NavController navController = navHostFragment.getNavController();
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.
                Builder(navController.getGraph())
                .build();

        this.configureDrawerLayout();

        this.configureNavigationView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        currentFragment = navHostFragment.getChildFragmentManager().getFragments().get(0);

        if (currentFragment instanceof ItemListFragment) {
            inflater.inflate(R.menu.toolbar_menu_main, menu);
        } else if (currentFragment instanceof ItemDetailFragment){
            inflater.inflate(R.menu.toolbar_menu_details, menu);
        } else {
            inflater.inflate(R.menu.toolbar_menu_add_only, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle item selection
        switch (item.getItemId()) {
            case R.id.add_residence:
                Intent iA = new Intent(ItemDetailHostActivity.this, AddRealEstateActivity.class);
                startActivity(iA);
                return true;
            case R.id.filter_residence:
                DialogFragment dialog = DialogFragment.newInstance("Filter");
                dialog.show(getSupportFragmentManager(), "Filter");
                return true;
            case R.id.delete_residence:
                PlaceholderContent.PlaceholderItem residence = ItemDetailFragment.getCurrentResidence();
                onDeleteResidence(residence);
                return true;
            case R.id.edit_residence:
                setEditResidence();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setEditResidence() {
        EditRealEstateActivity.setResidence(ItemDetailFragment.getCurrentResidence());
        Intent iE = new Intent(ItemDetailHostActivity.this, EditRealEstateActivity.class);
        startActivity(iE);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_item_detail);
        return navController.navigateUp() || super.onSupportNavigateUp();
    }

    public void onDeleteResidence(PlaceholderContent.PlaceholderItem mItem) {
        residenceDAO.deleteResidence(Long.parseLong(mItem.id));
        PlaceholderContent.deleteItem(mItem);

        onListUpdated();
    }

    public void onListUpdated() {
        finish();
        overridePendingTransition(0, 0);
        startActivity(getIntent());
        overridePendingTransition(0, 0);
    }
}
package com.alphamedia.rutilahu;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public class MainActivity extends ActionBarActivity {

    private ListView mListView = null;
    private ArrayAdapter mAdapter = null;
    private SwipeRefreshLayout mSwipeRefreshLayout = null;

    private String[] feedList = null;
    private String feedUrl = Config.BASE_URL + "Getdata/tojson";

    private Menu menu;
    private List<String> items;
    //private boolean is_downloaddata = false;

    private boolean SEARCH_OPENED = false;
    private boolean mSearchOpened = false;
    private String SEARCH_QUERY = "";
    private String mSearchQuery = "";

    MenuItem mSearchAction;
    private EditText mSearchEt;

    private ListView mDrawerList;
    private DrawerLayout mDrawer;
    private CustomActionBarDrawerToggle mDrawerToggle;
    private String[] menuItems;
    boolean isOpened;
    boolean isVisible;
    String mTitle = "Menu";
    String mDrawerTitle = "Rutilahu";

    // db.getItems()

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //setContentView(R.layout.activity_main);
        setContentView(R.layout.activity_main_drawer);
        //mListView = (ListView) findViewById(R.id.listView);
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        /*
        mListView = (ListView) findViewById(R.id.listView);
         */

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        isOpened = mDrawer.isDrawerOpen(GravityCompat.START);
        isVisible = mDrawer.isDrawerVisible(GravityCompat.START);

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawer.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        createDir();

        _initMenu();

        mDrawerToggle = new CustomActionBarDrawerToggle(this, mDrawer);
        mDrawer.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            mSearchOpened = false;
            mSearchQuery = "";
        } else {
            mSearchOpened = savedInstanceState.getBoolean(String.valueOf(SEARCH_OPENED));
            mSearchQuery = savedInstanceState.getString(SEARCH_QUERY);
        }

        if (mSearchOpened) {
            openSearchBar(mSearchQuery);
        }


        startActivity(new Intent(this, DataPenerimaRealmIO.class));

        /*
        new DownloadFilesTask().execute(feedUrl);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new DownloadFilesTask().execute(feedUrl);
            }
        });
        */

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(String.valueOf(SEARCH_OPENED), mSearchOpened);
        outState.putString(SEARCH_QUERY, mSearchQuery);
    }

    private void openSearchBar(String queryText) {
        // Set custom view on action bar.
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(R.layout.search_bar);

        // Search edit text field setup.
        mSearchEt = (EditText) actionBar.getCustomView()
                .findViewById(R.id.etSearch);
        mSearchEt.addTextChangedListener(new SearchWatcher());
        mSearchEt.setText(queryText);
        mSearchEt.requestFocus();
        mSearchOpened = true;
    }

    private void closeSearchBar() {
        // Remove custom view.
        getSupportActionBar().setDisplayShowCustomEnabled(false);
        mSearchOpened = false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        int id = item.getItemId();
        if (id == R.id.action_search) {
            if (mSearchOpened) {
                closeSearchBar();
            } else {
                openSearchBar(mSearchQuery);
            }
            return true;
        }

        if(id == R.id.action_downloads)
        {
            item.setIcon(R.drawable.ic_file_download_black_18dp);
            startActivity(new Intent(this, Download.class));
            return true;
        }

        if(id == R.id.action_settings)
        {
            item.setIcon(R.drawable.ic_settings_black_18dp);
            startActivity(new Intent(this, SettingsaActivity.class));
            return true;
        }

        if(id == R.id.action_upload)
        {
            item.setIcon(R.drawable.ic_settings_black_18dp);
            Intent i = new Intent(MainActivity.this, UploadActivity.class);
            i.putExtra("filePath", Config.DATA_DIR);
            i.putExtra("isImage", false);
            startActivity(i);
            //startActivity(new Intent(this, UploadActivity.class));
            return true;
        }

        if(id == R.id.action_sync)
        {
            item.setIcon(R.drawable.ic_sync_black_18dp);
            startActivity(new Intent(this, DataPenerimaRealmIO.class));
            return true;
        }

        if(id == R.id.action_about)
        {
            item.setIcon(R.drawable.ic_help_black_18dp);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.title_about));
            builder.setMessage(getString(R.string.msg_about));
            builder.setPositiveButton(R.string.button_open_browser, aboutListener);
            builder.setNegativeButton(R.string.button_cancel, null);
            builder.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private final DialogInterface.OnClickListener aboutListener =
            new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.about_url)));
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                    startActivity(intent);
                }
            };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        //mSearchAction = menu.findItem(R.id.action_search);
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        boolean actionsVisibility = !drawerLayout.isDrawerVisible(Gravity.START);
        for(int i=0;i<menu.size();i++){
            menu.getItem(i).setVisible(actionsVisibility);
        }
        //menu.findItem(R.id.action_save).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    // History
    private void loadHistory(String query) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // Cursor
            String[] columns = new String[] { "_id", "text" };
            Object[] temp = new Object[] { 0, "default" };
            MatrixCursor cursor = new MatrixCursor(columns);
            for(int i = 0; i < items.size(); i++) {
                temp[0] = i;
                temp[1] = items.get(i);
                cursor.addRow(temp);
            }
            // SearchView
            SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
            final SearchView search = (SearchView) menu.findItem(R.id.search).getActionView();
            search.setSuggestionsAdapter(new SearchAdapter(this, cursor, items));
        }
    }


    private void updateList() {
        final ArrayAdapter mAdapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, feedList);
        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String itempos = parent.getItemAtPosition(position).toString();
                        Toast.makeText(MainActivity.this, "Data posisi " + itempos + position, Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(MainActivity.this, DetailPenerima.class);
                        i.putExtra("id_penerima", parent.getId());
                        startActivity(i);
                        finish();
                    }
                }
        );

        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    /**
     * Responsible for handling changes in search edit text.
     */
    private class SearchWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence c, int i, int i2, int i3) {
        }

        @Override
        public void onTextChanged(CharSequence c, int i, int i2, int i3) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
            mSearchQuery = mSearchEt.getText().toString();
            //mMoviesFiltered = performSearch(mMovies, mSearchQuery);
            //getListAdapter().update(mMoviesFiltered);
        }

    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(MainActivity.this).setMessage(
                R.string.exit_message).setTitle(
                R.string.exit_title).setCancelable(false)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int whichButton) {
                                setResult(RESULT_CANCELED);
                                Intent i = new Intent();
                                quit(false,i);
                                //MainActivity.this.finish();
                            }
                        }).setNeutralButton(android.R.string.cancel,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int whichButton) {
                    }
                }).show();
    }

    private void quit(boolean success, Intent i) {
        setResult((success) ? -1:0, i);
        this.finish();
    }

    private void _initMenu() {
        NsMenuAdapter mAdapter = new NsMenuAdapter(this);
        mAdapter.addHeader(R.string.ns_menu_main_header);
        menuItems = getResources().getStringArray(
                R.array.ns_menu_items);
        String[] menuItemsIcon = getResources().getStringArray(
                R.array.ns_menu_items_icon);

        int res = 0;
        for (String item : menuItems) {

            int id_title = getResources().getIdentifier(item, "string",
                    this.getPackageName());
            int id_icon = getResources().getIdentifier(menuItemsIcon[res],
                    "drawable", this.getPackageName());

            NsMenuItemModel mItem = new NsMenuItemModel(id_title, id_icon);
            if (res==1) mItem.counter=12; //it is just an example...
            if (res==3) mItem.counter=3; //it is just an example...
            mAdapter.addItem(mItem);
            res++;
        }

        mAdapter.addHeader(R.string.ns_menu_main_header2);

        mDrawerList = (ListView) findViewById(R.id.drawer);
        if (mDrawerList != null)
            mDrawerList.setAdapter(mAdapter);
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
    }

    private class CustomActionBarDrawerToggle extends ActionBarDrawerToggle {

        public CustomActionBarDrawerToggle(Activity mActivity,DrawerLayout mDrawerLayout){
            super(
                    mActivity,
                    mDrawerLayout,
                    R.drawable.ic_drawer,
                    R.string.ns_menu_open,
                    R.string.ns_menu_close);
        }

        @Override
        public void onDrawerClosed(View view) {
            getSupportActionBar().setTitle(getString(R.string.app_name));
            //invalidateOptionsMenu();
            supportInvalidateOptionsMenu();
        }

        @Override
        public void onDrawerOpened(View drawerView) {
            getSupportActionBar().setTitle(getString(R.string.ns_menu_open));
            //invalidateOptionsMenu();
            supportInvalidateOptionsMenu();
        }

        @Override
        public void onDrawerSlide(View view, float v) {
            //invalidateOptionsMenu();
            supportInvalidateOptionsMenu();
        }

        @Override
        public void onDrawerStateChanged(int state) {}

    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            mDrawerList.setItemChecked(position, true);
            String text= "menu click... should be implemented";
            Toast.makeText(MainActivity.this, text , Toast.LENGTH_LONG).show();
            mDrawer.closeDrawer(mDrawerList);
        }

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    private void createDir()
    {
        File fr = new File(Environment.getExternalStorageDirectory() + "/fieldreport/");
        if(!fr.exists()) {
            fr.mkdirs();
            File fotodir = new File(Environment.getExternalStorageDirectory() + "/fieldreport/foto/");
            if (!fotodir.exists())
                fotodir.mkdirs();
            File filedir = new File(Environment.getExternalStorageDirectory() + "/fieldreport/file/");
            if (!filedir.exists())
                filedir.mkdirs();
        } else
            Log.d("Error: ", "dir. already exists");
    }

}


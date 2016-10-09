package com.alphamedia.rutilahu;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alphamedia.rutilahu.api.BitmapTransform;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.exceptions.RealmException;
import io.realm.exceptions.RealmMigrationNeededException;

import static java.lang.Double.parseDouble;


public class DataPenerimaRealmIO extends ActionBarActivity implements LoaderManager.LoaderCallbacks<String> {

    //private GridView mGridView;
    private DataPenerimaAdapter mAdapter;
    private ListView mListView;
    private Realm realm;
    RealmConfiguration realmConfiguration;

    private boolean mSearchOpened = false;
    private String mSearchQuery = "";
    private EditText mSearchEt;

    private MenuItem mSearchAction;

    private Drawable mIconCloseSearch;
    private Drawable mIconOpenSearch;

    RealmResults<Penerima> result;
    RealmChangeListener realmListener;

    private static final int LOAD_NETWORK_A = 1;
    private static final int LOAD_NETWORK_B = 2;
    private static final int LOAD_NETWORK_C = 3;

    AlertDialog.Builder dialog;

    private static final int MAX_WIDTH = 600;
    private static final int MAX_HEIGHT = 400;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_data_penerima_realm_io);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mIconOpenSearch = getResources()
                .getDrawable(R.drawable.ic_search_black_18dp);
        mIconCloseSearch = getResources()
                .getDrawable(R.drawable.ic_clear_black_18dp);

        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(this).build();
        Realm.setDefaultConfiguration(realmConfiguration);

        /*
        try {
            if (realm != null) {
                realm.close();
            }
            RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(this).build();
            Realm.setDefaultConfiguration(realmConfiguration);
        } catch (RealmException e)
        {
            Log.e("Error: ", e.getMessage());
        }
        */

        getLoaderManager().initLoader(LOAD_NETWORK_C, null, this).forceLoad();
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            realm = Realm.getInstance(this);
            realmListener = new RealmChangeListener() {
                // @Override
                public void onChange() {
                    if (mAdapter != null) {
                        mAdapter.notifyDataSetChanged();
                    }
                    Log.i("RealmListener","Data Penerima di database sudah diupdate!");
                }};
            realm.addChangeListener(realmListener);
        } catch (RealmException e) {
            Log.e("Error: ", e.getMessage());
            //realm.close();
            //Realm.deleteRealmFile(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //realm.close();
        //Realm.deleteRealm(realmConfiguration);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        mSearchAction = menu.findItem(R.id.action_search);
        //menu.findItem(R.id.action_save).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_data_penerima_realm_io, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            item.setIcon(R.drawable.ic_settings_black_18dp);
            startActivity(new Intent(this, SettingsaActivity.class));
            return true;
        }

        if (id == R.id.action_search) {
            if (mSearchOpened) {
                closeSearchBar();
            } else {
                openSearchBar(mSearchQuery);
            }
            return true;
        }

        if (id == R.id.action_downloads) {
            Intent download = new Intent(getApplicationContext(),Download.class);
            startActivity(download);
            return true;
        }

        if (id == R.id.action_backup) {
            Intent backup = new Intent(getApplicationContext(),Backup.class);
            startActivity(backup);
            return true;
        }

        if (id == R.id.action_upload) {
            Intent upload = new Intent(getApplicationContext(),UploadActivity.class);
            startActivity(upload);
            return true;
        }

        if (id == R.id.action_refresh) {
            refreshView();
            return true;
        }

        if (id == R.id.map) {
            Intent map = new Intent(getApplicationContext(),MapActivity.class);
            startActivity(map);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void refreshView()
    {
        mAdapter = new DataPenerimaAdapter(this);
        if(result == null)
        {
            RealmResults<Penerima> result;
            try {
                result = (RealmResults<Penerima>) loadPenerima();
                result = realm.where(Penerima.class).findAll();
                result.sort("namalengkap"); // RealmResults.SORT_ORDER_ASCENDING
                mAdapter.setData(result);
                mListView = (ListView) findViewById(R.id.custom_list);
                mListView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
                mListView.invalidate();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else
        {
            result = realm.where(Penerima.class).findAll();
            result.sort("namalengkap"); // RealmResults.SORT_ORDER_ASCENDING
            mAdapter.setData(result);
            mListView = (ListView) findViewById(R.id.custom_list);
            mListView.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
            mListView.invalidate();
        }
        //realm.close();
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
        mSearchAction.setIcon(mIconCloseSearch);
        mSearchOpened = true;
    }

    private void closeSearchBar() {
        getSupportActionBar().setDisplayShowCustomEnabled(false);
        mSearchAction.setIcon(mIconOpenSearch);
        mSearchEt.setText("");
        mSearchOpened = false;
        refreshView();
    }

    /**
     * Responsible for handling changes in search edit text.
     */
    private class SearchWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence c, int i, int i2, int i3) {
            Log.i("Status TextWatcher: ", "beforeTextChanged");
        }

        @Override
        public void onTextChanged(CharSequence c, int i, int i2, int i3) {
            Log.i("Status TextWatcher: ", "onTextChanged");
        }

        @Override
        public void afterTextChanged(Editable editable) {
            mSearchQuery = mSearchEt.getText().toString();
            Log.i("Query Search: ", mSearchQuery);
            result = realm.where(Penerima.class)
                    .contains("namalengkap", mSearchQuery, Case.INSENSITIVE) // , false
                    .or()
                    .contains("jalan_desa", mSearchQuery, Case.INSENSITIVE) // , false
                    .or()
                    .contains("desa", mSearchQuery, Case.INSENSITIVE)
                    .or()
                    .contains("kecamatan", mSearchQuery, Case.INSENSITIVE)
                    .or()
                    .contains("kabupaten", mSearchQuery, Case.INSENSITIVE)
                    .findAll();
            result.sort("namalengkap"); // RealmResults.SORT_ORDER_ASCENDING

            mAdapter = new DataPenerimaAdapter(getApplicationContext());
            mAdapter.setData(result);
            mListView = (ListView) findViewById(R.id.custom_list);
            mListView.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
        }

    }

    public List<Penerima> loadPenerima() throws IOException {
        return realm.allObjects(Penerima.class);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.removeChangeListener(realmListener);
        realm.close();
        //Realm.deleteRealm(realmConfiguration);
    }


    @Override
    public Loader<String> onCreateLoader(int i, Bundle bundle) {
        switch (i) {
            case LOAD_NETWORK_C:
                return new ApiLoaderTask(this);
            default:
                return new ApiLoaderTask(this);
        }
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String string) {
        if(mAdapter == null) {
            List<Penerima> penerima = null;
            try {
                penerima = loadPenerima();
            } catch (IOException e) {
                e.printStackTrace();
            }

            mAdapter = new DataPenerimaAdapter(this);
            mAdapter.setData(penerima);
            mListView = (ListView) findViewById(R.id.custom_list);
            mListView.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
            mListView.invalidate();

            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, final View view,
                                        int position, long id) {

                    TextView nama = (TextView) view.findViewById(R.id.nama);

                    dialog = new AlertDialog.Builder(DataPenerimaRealmIO.this);
                    dialog.setTitle("Pilih "+nama.getText())
                        .setItems(R.array.pilihanmenu, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which){
                                    case 0: viewData(view); break;
                                    case 1: goDetail(view); break;
                                }
                            }
                        });
                    dialog.create();
                    dialog.show();
                }

            });

        }
    }

    private void viewData(View view)
    {
        TextView idpenerima = (TextView) view.findViewById(R.id.idpenerima);

        int size = (int) Math.ceil(Math.sqrt(MAX_WIDTH * MAX_HEIGHT));

        try {
            RealmResults<Penerima> result = (RealmResults<Penerima>) loadPenerima();
            Penerima p = result.where()
                    .equalTo("id_penerima", Integer.parseInt(idpenerima.getText().toString()))
                    .findFirst();

            AlertDialog.Builder builder = new AlertDialog.Builder(DataPenerimaRealmIO.this);
            LayoutInflater inflater = DataPenerimaRealmIO.this.getLayoutInflater();
            View vdetail = inflater.inflate(R.layout.dialog_detail, null);

            String alamat = new StringBuilder()
                    .append(p.getJalan_desa()).append(" ")
                    .append(" Rt. ")
                    .append(p.getRt())
                    .append(" Rw. ")
                    .append(p.getRw()).toString();

            String catat = (p.getIs_catat()) ? "SUDAH DIDATA" : "BELUM DIDATA";

            TextView vidp = (TextView) vdetail.findViewById(R.id.idpenerima);

            vidp.setText(Integer.toString(p.getId_penerima()));
            ImageView img_penerima = (ImageView) vdetail.findViewById(R.id.img_foto_penerima);

            String p_penerima_loc = new StringBuilder().append(Config.FOTO_DIR)
                    .append(Integer.toString(p.getId_penerima()))
                    .append("/").append(p.getImg_foto_penerima()).toString();

            File p_penerima = new File(p_penerima_loc);

            //Bitmap photo_penerima = decodeSampledBitmapFromFile(p_penerima_loc, 100,100);

            Picasso.with(getApplicationContext())
                    .load(p_penerima)
                    .error(R.drawable.ic_person_outline_black_24dp)
                    .placeholder(R.drawable.ic_person_outline_black_24dp)
                    //.transform(new CircleTransform())
                    .transform(new BitmapTransform(MAX_WIDTH, MAX_HEIGHT))
                    .resize(size, size)
                    .centerInside()
                    .into(img_penerima);

            TextView vnama = (TextView) vdetail.findViewById(R.id.nama);
            vnama.setText(p.getNamalengkap());

            TextView vstatus = (TextView) vdetail.findViewById(R.id.status);
            vstatus.setText(catat);
            TextView valamat = (TextView) vdetail.findViewById(R.id.alamat);
            valamat.setText(alamat);
            TextView vdesa = (TextView) vdetail.findViewById(R.id.desa);
            vdesa.setText(p.getDesa());
            TextView vktp = (TextView) vdetail.findViewById(R.id.ktp);
            vktp.setText(p.getKtp().isEmpty() ? "(DATA KTP KOSONG)" : p.getKtp());
            TextView vkk = (TextView) vdetail.findViewById(R.id.kk);
            vkk.setText(p.getKk().isEmpty() ? "(DATA KK KOSONG)" : p.getKk());
            TextView vkec = (TextView) vdetail.findViewById(R.id.kecamatan);
            vkec.setText(p.getKecamatan());
            TextView vkab = (TextView) vdetail.findViewById(R.id.kabupaten);
            vkab.setText(p.getKabupaten());
            TextView fpenerima = (TextView) vdetail.findViewById(R.id.file_foto_penerima);
            fpenerima.setText(p.getImg_foto_penerima());

            TextView vlong = (TextView) vdetail.findViewById(R.id.loclong);
            vlong.setText(p.getLatitude());
            TextView vlat = (TextView) vdetail.findViewById(R.id.loclat);
            vlat.setText(p.getLongitude());
            TextView vketerangan = (TextView) vdetail.findViewById(R.id.keterangan);
            vketerangan.setText(p.getKeterangan());

            TextView fdepan = (TextView) vdetail.findViewById(R.id.file_foto_depan);
            fdepan.setText(p.getImg_tampak_depan_rumah());
            ImageView img_depan = (ImageView) vdetail.findViewById(R.id.img_foto_depan);
            File p_depan = new File(new StringBuilder().append(Config.FOTO_DIR)
                    .append(Integer.toString(p.getId_penerima()))
                    .append("/").append(p.getImg_tampak_depan_rumah()).toString());
            Picasso.with(getApplicationContext())
                    .load(p_depan)
                    .error(R.drawable.ic_photo_black_24dp)
                    .placeholder(R.drawable.ic_person_outline_black_24dp)
                    .transform(new BitmapTransform(MAX_WIDTH, MAX_HEIGHT))
                    .resize(size, size)
                    .centerInside()
                    .into(img_depan);

            TextView fsamping1 = (TextView) vdetail.findViewById(R.id.file_foto_samping1);
            fsamping1.setText(p.getImg_tampak_samping_1());
            ImageView img_samping1 = (ImageView) vdetail.findViewById(R.id.img_foto_samping1);
            File p_samping1 = new File(new StringBuilder().append(Config.FOTO_DIR)
                    .append(Integer.toString(p.getId_penerima()))
                    .append("/").append(p.getImg_tampak_samping_1()).toString());
            Picasso.with(getApplicationContext())
                    .load(p_samping1)
                    .error(R.drawable.ic_photo_black_24dp)
                    .placeholder(R.drawable.ic_photo_black_24dp)
                    .transform(new BitmapTransform(MAX_WIDTH, MAX_HEIGHT))
                    .resize(size, size)
                    .centerInside()
                    .into(img_samping1);

            TextView fsamping2 = (TextView) vdetail.findViewById(R.id.file_foto_samping2);
            fsamping2.setText(p.getImg_tampak_samping_2());
            ImageView img_samping2 = (ImageView) vdetail.findViewById(R.id.img_foto_samping2);
            File p_samping2 = new File(new StringBuilder().append(Config.FOTO_DIR)
                    .append(Integer.toString(p.getId_penerima()))
                    .append("/").append(p.getImg_tampak_samping_2()).toString());
            Picasso.with(getApplicationContext())
                    .load(p_samping2)
                    .error(R.drawable.ic_photo_black_24dp)
                    .placeholder(R.drawable.ic_photo_black_24dp)
                    .transform(new BitmapTransform(MAX_WIDTH, MAX_HEIGHT))
                    .resize(size, size)
                    .centerInside()
                    .into(img_samping2);

            TextView fjamban = (TextView) vdetail.findViewById(R.id.file_foto_jamban);
            fjamban.setText(p.getImg_tampak_jamban());
            ImageView img_jamban = (ImageView) vdetail.findViewById(R.id.img_foto_jamban);
            File p_jamban = new File(new StringBuilder().append(Config.FOTO_DIR)
                    .append(Integer.toString(p.getId_penerima()))
                    .append("/").append(p.getImg_tampak_jamban()).toString());
            Picasso.with(getApplicationContext())
                    .load(p_jamban)
                    .error(R.drawable.ic_photo_black_24dp)
                    .placeholder(R.drawable.ic_photo_black_24dp)
                    .transform(new BitmapTransform(MAX_WIDTH, MAX_HEIGHT))
                    .resize(size, size)
                    .centerInside()
                    .into(img_jamban);

            TextView fdapur = (TextView) vdetail.findViewById(R.id.file_foto_dapur);
            fdapur.setText(p.getImg_tampak_dapur());
            ImageView img_dapur = (ImageView) vdetail.findViewById(R.id.img_foto_dapur);
            File p_dapur = new File(new StringBuilder().append(Config.FOTO_DIR)
                    .append(Integer.toString(p.getId_penerima()))
                    .append("/").append(p.getImg_tampak_dapur()).toString());
            Picasso.with(getApplicationContext())
                    .load(p_dapur)
                    .error(R.drawable.ic_photo_black_24dp)
                    .placeholder(R.drawable.ic_photo_black_24dp)
                    .transform(new BitmapTransform(MAX_WIDTH, MAX_HEIGHT))
                    .resize(size, size)
                    .centerInside()
                    .into(img_dapur);

            TextView fsumberair = (TextView) vdetail.findViewById(R.id.file_foto_sumber_air);
            fsumberair.setText(p.getImg_tampak_sumber_air());
            ImageView img_sa = (ImageView) vdetail.findViewById(R.id.img_foto_sumberair);
            File p_sa = new File(new StringBuilder().append(Config.FOTO_DIR)
                    .append(Integer.toString(p.getId_penerima()))
                    .append("/").append(p.getImg_tampak_sumber_air()).toString());
            Picasso.with(getApplicationContext())
                    .load(p_sa)
                    .error(R.drawable.ic_photo_black_24dp)
                    .placeholder(R.drawable.ic_photo_black_24dp)
                    .transform(new BitmapTransform(MAX_WIDTH, MAX_HEIGHT))
                    .resize(size, size)
                    .centerInside()
                    .into(img_sa);

            TextView fbelakang = (TextView) vdetail.findViewById(R.id.file_foto_belakang);
            fbelakang.setText(p.getImg_tampak_belakang());
            ImageView img_belakang = (ImageView) vdetail.findViewById(R.id.img_foto_belakang);
            File p_belakang = new File(new StringBuilder().append(Config.FOTO_DIR)
                    .append(Integer.toString(p.getId_penerima()))
                    .append("/").append(p.getImg_tampak_belakang()).toString());
            Picasso.with(getApplicationContext())
                    .load(p_belakang)
                    .error(R.drawable.ic_photo_black_24dp)
                    .placeholder(R.drawable.ic_photo_black_24dp)
                    .transform(new BitmapTransform(MAX_WIDTH, MAX_HEIGHT))
                    .resize(size, size)
                    .centerInside()
                    .into(img_belakang);

            // peta google statik
            if(!((p.getLatitude().toString().equals("") && p.getLongitude().toString().equals(""))))
            {
                Double lati = parseDouble((p.getLatitude()));
                Double longi = parseDouble(p.getLongitude());

                TextView gpeta = (TextView) vdetail.findViewById(R.id.peta_google);
                gpeta.setText("Google Map");
                ImageView img_peta = (ImageView) vdetail.findViewById(R.id.img_gmap);

                String locmap = getGoogleMapThumbnail(lati, longi);

                Picasso.with(getApplicationContext())
                        .load(locmap)
                        .error(R.drawable.markers)
                        .placeholder(R.drawable.markers)
                        .into(img_peta);
            }

            builder.setView(vdetail)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });
            builder.create();
            builder.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void goDetail(View view)
    {
        TextView idpenerima = (TextView) view.findViewById(R.id.idpenerima);
        TextView nama = (TextView) view.findViewById(R.id.nama);
        TextView status = (TextView) view.findViewById(R.id.status);
        TextView ktp = (TextView) view.findViewById(R.id.ktp);
        TextView alamat = (TextView) view.findViewById(R.id.alamat);
        TextView kecamatan = (TextView) view.findViewById(R.id.kecamatan);
        TextView kabupaten = (TextView) view.findViewById(R.id.kabupaten);
        Toast.makeText(getApplicationContext(),
                "Nama: " + nama.getText().toString() + " - " +
                        "KTP: " + ktp.getText().toString(),
                Toast.LENGTH_SHORT)
                .show();
        Intent i = new Intent(DataPenerimaRealmIO.this, DetailActivity.class);
        i.putExtra("id_penerima", Integer.parseInt(idpenerima.getText().toString()));
        i.putExtra("nama", nama.getText().toString());
        i.putExtra("ktp", ktp.getText().toString());
        i.putExtra("status", status.getText().toString());
        i.putExtra("alamat", alamat.getText().toString());
        i.putExtra("kecamatan", kecamatan.getText().toString());
        i.putExtra("kabupaten", kabupaten.getText().toString());
        startActivity(i);
    }


    @Override
    public void onLoaderReset(Loader<String> loader) {
        try {
            realm = Realm.getInstance(this);
        }
        catch (IllegalStateException e) {
            Log.e("Error: ", e.getMessage());
        }
    }

    private static class ApiLoaderTask extends AsyncTaskLoader<String> {

        public ApiLoaderTask(Context context) {
            super(context);
        }

        @Override
        public String loadInBackground() {
            Realm realm = null;
            try {
                realm = Realm.getInstance(getContext());
            } catch (RealmMigrationNeededException ex) {
                ex.printStackTrace();
            }
            realm.beginTransaction();
            try {
                List<Penerima> list = realm.allObjects(Penerima.class);
                if(list.size() <= 0) loadJsonFromStream(realm);
            } catch (IOException e) {
                e.printStackTrace();
                realm.cancelTransaction();
                realm.close();
            }
            realm.commitTransaction();
            //realm.close();
            return "";
        }

        @Override
        protected void onStartLoading() {
        }

        @Override
        protected void onStopLoading() {
            cancelLoad();
        }

        @Override
        protected void onReset() {
            super.onReset();
            onStopLoading();
        }

        private void loadJsonFromStream(Realm realm) throws IOException {
            File initialFile = new File(Config.DATA_DIR);
            if(initialFile.exists())
            {
                InputStream stream = new FileInputStream(initialFile);
                try {
                    //realm.createAllFromJson(Penerima.class, stream);
                    realm.createOrUpdateAllFromJson(Penerima.class, stream);
                } catch (IOException e) {
                    Log.e("Error: ", e.getMessage() + " - getStackTrace: " + e.getStackTrace().toString());
                    realm.cancelTransaction();
                } finally {
                    if (stream != null) {
                        stream.close();
                    }
                }
            } else
            {
                Log.e("Error: ", "File tidak ditemukan");
            }
        }

    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(DataPenerimaRealmIO.this).setMessage(
                R.string.exit_message).setTitle(
                R.string.exit_title).setCancelable(false)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int whichButton) {
                                setResult(RESULT_CANCELED);
                                Intent i = new Intent();
                                quit(false,i);
                            }
                        }).setNeutralButton(android.R.string.cancel,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int whichButton) {
                    }
                }).show();
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);
        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 3;
            final int halfWidth = width / 3;
            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 3;
            }
        }
        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromFile(String path, int reqWidth, int reqHeight) {
        //First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        // Calculate inSampleSize, Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        int inSampleSize = 4;
        if (height > reqHeight) {
            inSampleSize = Math.round((float) height / (float) reqHeight);
        }
        int expectedWidth = width / inSampleSize;
        if (expectedWidth > reqWidth) {
            //if(Math.round((float)width / (float)reqWidth) > inSampleSize)
            inSampleSize = Math.round((float) width / (float) reqWidth);
        }
        options.inSampleSize = inSampleSize;
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, options);
    }

    private void quit(boolean success, Intent i) {
        setResult((success) ? -1:0, i);
        this.finish();
    }

    private static String getGoogleMapThumbnail(double lati, double longi){
        StringBuffer sb = new StringBuffer();
        sb.append("https://maps.googleapis.com/maps/api/staticmap?");
        sb.append("&markers=color:red%7C");
        sb.append(Double.toString(longi));
        sb.append(",");
        sb.append(Double.toString(lati));
        sb.append("&zoom=14");
        sb.append("&size=400x400");
        sb.append("&scale=2");
        sb.append("&maptype=terrain");
        sb.append("&key=AIzaSyAygKRyqMYeABbc2smq0coZ6j8MPNkNjiU&img.jpg");
        return sb.toString();
    }

}
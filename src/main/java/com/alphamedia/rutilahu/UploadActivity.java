package com.alphamedia.rutilahu;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alphamedia.rutilahu.api.Common;
import com.alphamedia.rutilahu.api.FileType;
import com.alphamedia.rutilahu.api.ProgressListener;
import com.alphamedia.rutilahu.api.UploadFile;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.Calendar;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.exceptions.RealmMigrationNeededException;
import io.realm.internal.IOException;

// import com.squareup.okhttp.OkHttpClient;

public class UploadActivity extends Activity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private String filePath = null;
    private ProgressBar progressBar;
    private TextView txtPercentage;

    String filename;
    String fileZip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_upload);
        txtPercentage = (TextView) findViewById(R.id.txtPercentage);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        Calendar c = Calendar.getInstance();
        String imei = getIMEI(UploadActivity.this);
        String tgl = Common.getTgl();
        String fn = new StringBuilder().append(Config.FILE_DIR).append("upload-")
                .append(imei).append("-")
                .append(tgl).toString();
        filename = fn +".json";
        fileZip= fn + ".zip";

        UploadActivity.BackupFile bckFile = new UploadActivity.BackupFile();
        bckFile.execute(filename);

        try {
            SendFileTask sendFile = new SendFileTask(fileZip, FileType.ZIP);
            if (isConnected(this))
            {
                sendFile.execute();
            } else
            {
                Toast.makeText(getApplicationContext(), "Kesalahan: Tidak ada jaringan Internet", Toast.LENGTH_LONG).show();
                Log.e("Error","Tidak ada jaringan internet");
            }
        } catch (IOException e)
        {
            Log.e("Error: ", e.getMessage());
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_upload, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class SendFileTask extends AsyncTask<String, Integer, Long> {
        private ProgressListener listener;
        private String filePath;
        private FileType fileType;
        private ProgressDialog mUpload;

        public SendFileTask(String filePath, FileType fileType) {
            this.filePath = filePath;
            this.fileType = fileType;
        }

        @Override
        protected Long doInBackground(String... params) {
            long totsize = 0;
            long count = 0;
            File file = new File(filePath);
            totsize = file.length();
            Log.d("Upload FileSize[%d]", Long.toString(totsize));
            for(int i=0; i <= totsize; i++)
            {
                count += i;
                publishProgress((int) ((i / (float) totsize)) * 100);
            }

            JSONObject ret = UploadFile.uploadData(filePath);
            try {
                String json = ret.getString("result");
                if(json == "success") Toast.makeText(getApplicationContext(), "Upload Data Sukses.", Toast.LENGTH_LONG);
                Log.i("retJSON", json);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return count;
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            try {
                mUpload = new ProgressDialog(UploadActivity.this);
                mUpload.setTitle("Upload Data");
                mUpload.setIndeterminate(true);
                mUpload.setProgress(0);
                mUpload.setMax(100);
                mUpload.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                mUpload.setCancelable(false);
                mUpload.show();
            } catch (Exception e)
            {
                e.printStackTrace();
                mUpload.dismiss();
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            Log.d("progress[%d]", Integer.toString(values[0]));
            mUpload.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(Long result) {
            super.onPostExecute(result);
            mUpload.setMessage("Upload data " + result.toString() + " bytes. Finish.");
            SystemClock.sleep(1000);
            mUpload.dismiss();
            Toast.makeText(getApplicationContext(), "Upload file "+ fileZip +" "+
                    result.toString() + " bytes" +" selesai.",Toast.LENGTH_LONG).show();
            finish();
        }

    }

    // backup terlebih dahulu
    private class PenerimaSerializer implements JsonSerializer<Penerima> {
        @Override
        public JsonElement serialize(Penerima src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject object = new JsonObject();
            object.addProperty("id_penerima", src.getId_penerima());
            object.addProperty("tahun_terima", src.getTahun_terima());
            object.addProperty("provinsi", src.getProvinsi());
            object.addProperty("kabupaten", src.getKabupaten());
            object.addProperty("kecamatan", src.getKecamatan());
            object.addProperty("desa", src.getDesa());
            object.addProperty("no_urut", src.getKeterangan());
            object.addProperty("namalengkap", src.getNamalengkap());
            object.addProperty("jalan_desa", src.getJalan_desa());
            object.addProperty("rt", src.getRt());
            object.addProperty("rw", src.getRw());
            object.addProperty("ktp", src.getKtp());
            object.addProperty("kk", src.getKk());
            object.addProperty("latitude", src.getLatitude());
            object.addProperty("longitude", src.getLongitude());
            object.addProperty("img_foto_penerima", src.getImg_foto_penerima());
            object.addProperty("img_tampak_depan_rumah", src.getImg_tampak_depan_rumah());
            object.addProperty("img_tampak_samping_1", src.getImg_tampak_samping_1());
            object.addProperty("img_tampak_samping_2", src.getImg_tampak_samping_2());
            object.addProperty("img_tampak_belakang", src.getImg_tampak_belakang());
            object.addProperty("img_tampak_dapur", src.getImg_tampak_dapur());
            object.addProperty("img_tampak_jamban", src.getImg_tampak_jamban());
            object.addProperty("img_tampak_sumber_air", src.getImg_tampak_sumber_air());
            object.addProperty("keterangan", src.getKeterangan());
            object.addProperty("kode_desa", src.getKode_desa());
            object.addProperty("kode_kec", src.getKode_kec());
            object.addProperty("kode_kab", src.getKode_kab());
            object.addProperty("is_catat", src.getIs_catat());
            object.addProperty("tgl_update", src.getTgl_update());
            object.addProperty("tempat_lahir", src.getTempat_lahir());
            object.addProperty("tgl_lahir", src.getTgl_lahir());
            object.addProperty("jenis_kelamin", src.getJenis_kelamin());
            object.addProperty("devid", src.getdeviceID());
            return object;
        }

    }

    private void zip(String srcFilename, File targetFilename) throws java.io.IOException
    {
        FileInputStream fis = null;
        FileOutputStream os = new FileOutputStream(targetFilename);
        ZipOutputStream zos = new ZipOutputStream(os);
        int length;
        try {
            byte[] buffer = new byte[8192];
            fis = new FileInputStream(new File(srcFilename));
            ZipEntry entry = new ZipEntry(srcFilename);
            zos.putNextEntry(entry);
            while ((length = fis.read(buffer)) > 0) {
                zos.write(buffer, 0, length);
            }
            zos.closeEntry();
            fis.close();
        } catch (java.io.IOException eio) {
            eio.printStackTrace();
        } finally {
            zos.close();
        }
    }

    private class BackupFile extends AsyncTask<String, Integer, Long> {

        Realm realm;
        private ProgressDialog mBck;

        @Override
        protected Long doInBackground(String... namafile) {
            long fszip = 0;

            try {
                realm = Realm.getInstance(getApplicationContext());
            } catch (RealmMigrationNeededException ex) {
                ex.printStackTrace();
            }

            RealmResults<Penerima> result = null;

            try {
                RealmResults<Penerima> p = null;
                result = (RealmResults<Penerima>) loadPenerima();
                Log.i("Result size : ", Integer.toString(result.size()));
                p = result.where().findAll();
                // Create Gson builder
                GsonBuilder gsonBuilder = new GsonBuilder().setLenient();

                gsonBuilder.setExclusionStrategies(new ExclusionStrategy() {
                    @Override
                    public boolean shouldSkipField(FieldAttributes f) {
                        return f.getDeclaringClass().equals(RealmObject.class);
                    }

                    @Override
                    public boolean shouldSkipClass(Class<?> clazz) {
                        return false;
                    }
                });

                // Register adapter to builder
                try {
                    gsonBuilder.registerTypeAdapter(Class.forName("io.realm.PenerimaRealmProxy"), new UploadActivity.PenerimaSerializer());
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

                // Create gson
                Gson gson = gsonBuilder.create();
                String json = gson.toJson(p);
                try {
                    FileWriter writer = new FileWriter(filename);
                    writer.write(json);
                    writer.close();
                    File fzip = new File(filename);
                    long count = fzip.length();
                    for (int i = 0; i < count; i++) {
                        fszip += i;
                        publishProgress((int) ((i / (float) count)) * 100);
                    }
                } catch (java.io.IOException e) {
                    e.printStackTrace();
                }

            } catch (java.io.IOException e) {
                e.printStackTrace();
            }
            return fszip;
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            try {
                mBck = new ProgressDialog(UploadActivity.this);
                mBck.setTitle("Backup Data");
                mBck.setIndeterminate(true);
                mBck.setMax(100);
                //mBck.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                mBck.setCancelable(false);
                mBck.show();
            } catch (Exception e)
            {
                e.printStackTrace();
                mBck.dismiss();
            }
        }

        @Override
        protected void onProgressUpdate(Integer... args){
            super.onProgressUpdate(args);
            mBck.setProgress(args[0]);
        }

        @Override
        protected void onPostExecute(Long result) {
            super.onPostExecute(result);
            try {
                zip(filename, new File(fileZip));
            } catch (java.io.IOException e) {
                e.printStackTrace();
            }
            mBck.setMessage("Backup "+ result +" bytes.");
            SystemClock.sleep(3000);
            mBck.dismiss();
            Toast.makeText(getApplicationContext(), "Backup "+result.toString()+" bytes.", Toast.LENGTH_SHORT).show();
            finish();
        }

        public List<Penerima> loadPenerima() throws java.io.IOException {
            return realm.allObjects(Penerima.class);
        }

    }

    // end backup

    private static boolean isConnected(Context context){
        ConnectivityManager connectivityManager=(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=null;
        if (connectivityManager != null) {
            networkInfo=connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (!networkInfo.isAvailable()) {
                networkInfo=connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            }
        }
        return networkInfo == null ? false : networkInfo.isConnected();
    }

    private String getIMEI(Context context){
        TelephonyManager mngr = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
        String imei = mngr.getDeviceId();
        return imei;
    }

}
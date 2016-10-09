package com.alphamedia.rutilahu;

import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class DataClass extends AsyncTask<Void, Integer, Integer> {

    private static Realm realm;
    private RealmResults result = null;

    public static List<Penerima> datapenerima;

    private OnTaskCompleted listener;

        public DataClass(Realm realm, OnTaskCompleted listener) {
            this.realm = realm;
            this.listener=listener;
        }

        @Override
        protected Integer doInBackground(Void... params) {

            if(result != null){
                result.clear();
                this.realm.clear(Penerima.class);
            }

            this.realm.beginTransaction();

            try {
                loadJsonFromStream(this.realm);
            } catch (IOException e) {
                e.printStackTrace();
                //realm.cancelTransaction();
                //realm.close();
            }
            this.realm.commitTransaction();

            result = this.realm.where(Penerima.class).findAll();
            datapenerima = this.realm.allObjects(Penerima.class); //loadPenerima();
            Integer sum = result.size();
            //realm.close();
            return sum;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            Log.d("Update progress", Integer.toString(progress[0]));
        }

        @Override
        protected void onPostExecute(Integer sum) {
            super.onPostExecute(sum);
            listener.onTaskCompleted();
        }

        private void loadJsonFromStream(Realm realm) throws IOException {
            File initialFile = new File(Config.DATA_DIR);
            if(initialFile.exists())
            {
                InputStream stream = new FileInputStream(initialFile);
                try {
                    this.realm.createAllFromJson(Penerima.class, stream);
                } catch (IOException e) {
                    Log.e("Error: ", e.getMessage() + " - getStackTrace: " + e.getStackTrace().toString());
                } finally {
                    if (stream != null) {
                    }
                    stream.close();
                }
            } else
            {
            }
        }

    public static List<Penerima> loadPenerima() throws IOException {
        return realm.allObjects(Penerima.class);
    }

    public static List<Penerima> getData() throws IOException {
        return datapenerima;
    }

    public static Realm getRealm() {
        if(realm == null)
            realm = Realm.getDefaultInstance();
        return realm;
    }

}

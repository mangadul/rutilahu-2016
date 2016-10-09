package com.alphamedia.rutilahu;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alphamedia.rutilahu.api.Common;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class DetailActivity extends ActionBarActivity {

    private RealmConfiguration realmConfiguration;

    private Realm realm;

    private String e_ktp, e_status,
            e_nama, e_alamat,
            e_kecamatan, e_kabupaten;

    private Integer id_penerima;

    EditText txtloclong, txtloclat;

    private LocationManager lm;
    private LocationListener locationListener;
    Double loclong = null, loclat = null;
    private String imgloc, thumbloc;

    final private int CAMERA_REQUEST = 1333;
    final private int CAPTURE_IMAGE_THUMBNAIL_ACTIVITY_REQUEST_CODE = 1888;
    private static final int PHOTO_REQ_WIDTH = 1024;
    private static final int PHOTO_REQ_HEIGHT = 768;
    final private static String TAG = "IMGThumb";

    protected Uri uriimg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setDisplayShowHomeEnabled(true);

        realmConfiguration = new RealmConfiguration.Builder(this).build();
        Realm.setDefaultConfiguration(realmConfiguration);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            id_penerima = extras.getInt("id_penerima");
            e_ktp = extras.getString("ktp");
            e_status = extras.getString("status");
            e_nama = extras.getString("nama");
            e_alamat = extras.getString("alamat");
            e_kecamatan = extras.getString("kecamatan");
            e_kabupaten = extras.getString("kabupaten");
        }

        TextView nama = (TextView) findViewById(R.id.nama);
        TextView status = (TextView) findViewById(R.id.status);
        TextView ktp = (TextView) findViewById(R.id.ktp);
        TextView alamat = (TextView) findViewById(R.id.alamat);
        TextView kecamatan = (TextView) findViewById(R.id.kecamatan);
        TextView kabupaten = (TextView) findViewById(R.id.kabupaten);

        txtloclong = (EditText) findViewById(R.id.loclong);
        txtloclat = (EditText) findViewById(R.id.loclat);

        ktp.setText(e_ktp);
        nama.setText(id_penerima.toString() + " / " + e_nama);
        status.setText(e_status);
        alamat.setText(e_alamat);
        kecamatan.setText(e_kecamatan);
        kabupaten.setText(e_kabupaten);
        set_id(id_penerima);

        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String deviceId = telephonyManager.getDeviceId();
        final EditText devid = (EditText) findViewById(R.id.devid);
        devid.setText(deviceId);

        final EditText etPenerima = (EditText) findViewById(R.id.file_foto_penerima);
        final ImageButton btnPenerima = (ImageButton) findViewById(R.id.btn_foto_penerimaatas);
        btnPenerima.setOnClickListener(new fotoClick(etPenerima));

        if (etPenerima.getText().toString().length() > 0) {
            final String imguri = etPenerima.getText().toString();
            final ImageButton imgFotoPenerima = (ImageButton) findViewById(R.id.img_foto_penerima);
            imgFotoPenerima.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showImage(imguri, "Foto Penerima");
                }
            });
        }

        Button btnfotoPenerima = (Button) findViewById(R.id.foto_penerima);
        btnfotoPenerima.setOnClickListener(new fotoClick(etPenerima));

        Button btnfotoDepan = (Button) findViewById(R.id.btn_foto_depan);
        final EditText etDepan = (EditText) findViewById(R.id.et_tampak_depan);
        btnfotoDepan.setOnClickListener(new fotoClick(etDepan));

        Button btnfotoSamping1 = (Button) findViewById(R.id.btn_foto_tampak_samping1);
        final EditText etSamping1 = (EditText) findViewById(R.id.foto_tampak_samping1);
        btnfotoSamping1.setOnClickListener(new fotoClick(etSamping1));

        Button btnfotoSamping2 = (Button) findViewById(R.id.btn_foto_tampak_samping2);
        final EditText etSamping2 = (EditText) findViewById(R.id.foto_tampak_samping2);
        btnfotoSamping2.setOnClickListener(new fotoClick(etSamping2));

        Button btnfotoDapur = (Button) findViewById(R.id.btn_foto_dapur);
        final EditText etDapur = (EditText) findViewById(R.id.foto_dapur);
        btnfotoDapur.setOnClickListener(new fotoClick(etDapur));

        Button btnfotoJamban = (Button) findViewById(R.id.btn_foto_jamban);
        final EditText etJamban = (EditText) findViewById(R.id.foto_jamban);
        btnfotoJamban.setOnClickListener(new fotoClick(etJamban));

        Button btnSumberAir = (Button) findViewById(R.id.btn_foto_sumber_air);
        final EditText etSumberAir = (EditText) findViewById(R.id.foto_sumber_air);
        btnSumberAir.setOnClickListener(new fotoClick(etSumberAir));

        Button btnBelakang = (Button) findViewById(R.id.btn_foto_belakang);
        final EditText etBelakang = (EditText) findViewById(R.id.foto_belakang);
        btnBelakang.setOnClickListener(new fotoClick(etBelakang));

        final EditText etDevid = (EditText) findViewById(R.id.devid);
        final EditText etloclat = (EditText) findViewById(R.id.loclat);
        final EditText etloclong = (EditText) findViewById(R.id.loclong);

        final EditText editNama = (EditText) findViewById(R.id.edit_nama);
        final EditText editKtp = (EditText) findViewById(R.id.edit_ktp);
        final EditText editKK = (EditText) findViewById(R.id.edit_kk);
        final EditText etKet = (EditText) findViewById(R.id.keterangan);

        if (!e_nama.isEmpty()) {
            editNama.setText(e_nama);
            editNama.setKeyListener(null);
        }

        /*
        ArrayList<String> spinnerArray = new ArrayList<String>();
        spinnerArray.add("SUDAH DICATAT");
        spinnerArray.add("BELUM DICATAT");
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerArray);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setAdapter(spinnerArrayAdapter);
        */

        try {
            lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationListener = new MyLocationListener();
            lm.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    0,
                    0,
                    locationListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Button btnSimpan = (Button) findViewById(R.id.btnsimpan);
        btnSimpan.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        String fsDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

                        Realm realm = Realm.getDefaultInstance();

                        // define value
                        String sNama = (editNama.getText().toString().length() > 0) ? editNama.getText().toString() : "";
                        String sKTP = (editKtp.getText().toString().length() > 0) ? editKtp.getText().toString() : "";
                        String sKK = (editKK.getText().toString().length() > 0) ? editKK.getText().toString() : "";

                        String fp = (etPenerima.getText().toString().length() > 0) ? etPenerima.getText().toString() : "";
                        String fsdepan = (etDepan.getText().toString().length() > 0) ? etDepan.getText().toString() : "";
                        String fssamping1 = (etSamping1.getText().toString().length() > 0) ? etSamping1.getText().toString() : "";
                        String fssamping2 = (etSamping2.getText().toString().length() > 0) ? etSamping2.getText().toString() : "";
                        String fsdapur = (etDapur.getText().toString().length() > 0) ? etDapur.getText().toString() : "";
                        String fsjamban = (etJamban.getText().toString().length() > 0) ? etJamban.getText().toString() : "";
                        String fssumberair = (etSumberAir.getText().toString().length() > 0) ? etSumberAir.getText().toString() : "";
                        String fslong = (etloclong.getText().toString().length() > 0) ? etloclong.getText().toString() : "";
                        String fslat = (etloclat.getText().toString().length() > 0) ? etloclat.getText().toString() : "";
                        String sdevid = (etDevid.getText().toString().length() > 0) ? etDevid.getText().toString() : "";
                        String ket = (etKet.getText().toString().length() > 0) ? etKet.getText().toString() : "";
                        String sbelakang = (etBelakang.getText().toString().length() > 0) ? etBelakang.getText().toString() : "";

                        if (fp.equals("") || fsdepan.equals("") || fssamping1.equals("") || fssamping2.equals("") || fsdapur.equals("")
                                || fsjamban.equals("") || fssumberair.equals("") || sbelakang.equals("") || sKK.equals("")
                                || sNama.equals("") || sKTP.equals("") || fslong.equals("") || fslat.equals("") || sdevid.equals("")) {
                            Toast.makeText(getApplicationContext(),
                                    "Silahkan lengkapi isian terlebih dahulu!",
                                    Toast.LENGTH_SHORT).show();
                        } else {

                            try {

                                RealmResults<Penerima> results = realm.where(Penerima.class).equalTo("id_penerima", id_penerima).findAll();

                                Log.d("Data", Integer.toString(results.size()));

                                List<Penerima> list = new ArrayList<>();
                                list.addAll(results);
                                realm.beginTransaction();
                                for (Penerima obj : list) {
                                    obj.setId_penerima(id_penerima);
                                    obj.setNamalengkap(sNama);
                                    obj.setKtp(sKTP);
                                    obj.setKk(sKK);
                                    obj.setImg_foto_penerima(fp);
                                    obj.setImg_tampak_depan_rumah(fsdepan);
                                    obj.setImg_tampak_samping_1(fssamping1);
                                    obj.setImg_tampak_samping_2(fssamping2);
                                    obj.setImg_tampak_dapur(fsdapur);
                                    obj.setImg_tampak_jamban(fsjamban);
                                    obj.setImg_tampak_sumber_air(fssumberair);
                                    obj.setImg_tampak_belakang(sbelakang);
                                    obj.setLongitude(fslong);
                                    obj.setLatitude(fslat);
                                    obj.setTgl_update(fsDate);
                                    obj.setTgl_catat(fsDate);
                                    obj.setDeviceID(sdevid);
                                    obj.setKeterangan(ket);
                                    obj.setIs_catat(true);
                                }
                                realm.commitTransaction();

                                Log.d("KTP", sKTP);
                                Log.d("KK", sKK);

                                Toast.makeText(getApplicationContext(),
                                        new StringBuilder().append("Data ID ").append(id_penerima.toString())
                                                .append(" Nama ")
                                                .append(e_nama)
                                                .append(" berhasil diupdate").toString(), Toast.LENGTH_SHORT).show();

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
        );

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        realm = Realm.getInstance(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showImage(String fileloc, String txt) {
        final Dialog dialog = new Dialog(DetailActivity.this);
        dialog.setContentView(R.layout.imageview);
        dialog.setTitle("Lihat Gambar " + txt);
        TextView text = (TextView) dialog.findViewById(R.id.text);
        text.setText(txt);
        ImageView image = (ImageView) dialog.findViewById(R.id.image);
        String imguri = Config.FOTO_DIR + Integer.toString(id_penerima) + fileloc;
        Picasso.with(getApplicationContext())
                .load(new File(imguri))
                .error(R.drawable.ic_photo_black_18dp)
                .placeholder(R.drawable.ic_photo_black_18dp)
                .into(image);
        Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    private class fotoClick implements View.OnClickListener {
        EditText tv;
        String fid = "";

        public fotoClick(EditText tv) {
            this.tv = tv;
        }

        @Override
        public void onClick(View view) {
            if (this.tv.getText().toString().length() > 0) this.tv.setText("");
            fid = this.tv.getHint().toString().replace(" ", "_");
            Log.i("Ambil Gambar Manual", "fotoClick.onClick()");
            startCameraActivity(this.tv, setfname(fid));
        }
    }

    private String filephoto(EditText tv) {
        if (tv.getText().toString().length() > 0) tv.setText("");
        String fid = tv.getHint().toString().replace(" ", "_");
        String dirfoto = get_id().toString();
        String _spath = Config.FOTO_DIR + dirfoto + "/" + setfname(fid) + ".jpg";
        File file = new File(_spath);
        return file.getAbsolutePath();
    }

    protected String[] uriFile(EditText txtimgname, String fn) {
        String[] ret_arr = new String[2];
        createDirFoto();
        txtimgname.setText("");
        String dirfoto = get_id().toString();
        String _spath = Config.FOTO_DIR + dirfoto + "/" + fn + ".jpg";
        String _sthumb = Config.FOTO_DIR + dirfoto + "/thumb/thumb-" + fn + ".jpg";
        setPhotoLoc(_spath);
        setThumbLoc(_sthumb);
        File file = new File(_spath);
        File fthumb = new File(_sthumb);
        Uri outputFileUri = Uri.fromFile(file);
        Uri outputFileThumb = Uri.fromFile(fthumb);
        txtimgname.setText(fn + ".jpg");
        ret_arr[0] = outputFileUri.toString();
        ret_arr[1] = outputFileThumb.toString();
        return ret_arr;
    }

    protected void startCameraActivity(EditText txtimgname, String fn) {
        /*
        String[] ouri = uriFile(txtimgname, fn);
        Log.i("arrphoto", ouri[0]);
        */
        createDirFoto();
        txtimgname.setText("");
        String dirfoto = get_id().toString();
        String _spath = Config.FOTO_DIR + dirfoto + "/" + fn + ".jpg";
        setPhotoLoc(_spath);
        String _sthumb = Config.FOTO_DIR + dirfoto + "/thumb/thumb-" + fn + ".jpg";
        setThumbLoc(_sthumb);
        txtimgname.setText(fn + ".jpg");
        File file = new File(_spath);
        Uri imgPath = Uri.fromFile(file);
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imgPath);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, CAMERA_REQUEST);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        String photoPath = getPhotoLoc();
        String thumbPath = getThumbLoc();
        //Log.d("PhotoPath", photoPath);
        Log.d("ThumbPath", thumbPath);
        File file = new File(photoPath);
        File fthumb = new File(thumbPath);
        Uri outputFileUri = Uri.fromFile(file);
        Uri outputFileThumb = Uri.fromFile(fthumb);
        if (requestCode == CAMERA_REQUEST) {
            if (data != null) {
                if (data.getExtras() != null) {
                    // create photo thumbnail image
                    Bitmap bmp = (Bitmap) data.getExtras().get("data");
                    try {
                        File photoFile = null;
                        Bitmap bitmap = decodeSampledBitmapFromFile(outputFileUri.getPath(), PHOTO_REQ_WIDTH, PHOTO_REQ_HEIGHT);
                        if (photoFile == null) {
                            Log.d(TAG, "Error creating media file, check storage permissions: ");
                        }
                        try {
                            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                            File f = createImageFile(outputFileThumb.getPath());
                            f.createNewFile();
                            FileOutputStream fo = new FileOutputStream(f);
                            fo.write(bytes.toByteArray());
                            fo.close();
                        } catch (FileNotFoundException e) {
                            Log.d(TAG, "File not found: " + e.getMessage());
                        } catch (IOException e) {
                            Log.d(TAG, "Error accessing file: " + e.getMessage());
                        }
                        Log.d("PhotoPath", outputFileThumb.getPath());
                    } catch (NullPointerException npe) {
                        npe.printStackTrace();
                    }
                    /*
                    File destinationFile = new File(outputFileThumb.getPath());
                    Log.d(TAG, "the destination for image file is: " + destinationFile);
                    try {
                        FileOutputStream out = new FileOutputStream(destinationFile);
                        bmp.compress(Bitmap.CompressFormat.JPEG, 100, out);
                        out.flush();
                        out.close();
                    } catch (Exception e) {
                        Log.e(TAG, "ERROR:" + e.toString());
                    }
                    */
                }
            }
        }
    }

    private File createImageFile(String imgPath) throws IOException {
        File photo = new File(imgPath);
        return photo;
    }

    public static Bitmap decodeSampledBitmapFromFile(String path, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        // Calculate inSampleSize, Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        int inSampleSize = 1;
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

    private static Bitmap lessResolution(String filePath, int width, int height) {
        int reqHeight = height;
        int reqWidth = width;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }


    /*
    * mImageView.setImageBitmap(
    decodeSampledBitmapFromResource(getResources(), R.id.myimage, 100, 100));
    * */

    /*
    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 7;
        if (height > reqHeight || width > reqWidth) {
            // Calculate ratios of height and width to requested height and width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            // Choose the smallest ratio as inSampleSize value, this will guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }
    */

    private void setThumbLoc(String tl) {
        this.thumbloc = tl;
    }

    private String getThumbLoc() {
        return this.thumbloc;
    }

    private void setPhotoLoc(String f) {
        this.imgloc = f;
    }

    private String getPhotoLoc() {
        return this.imgloc;
    }

    protected String setfname(final String ctn) {
        String tgl = Common.getTgl();
        String fimg = ctn + "-" + tgl;
        return fimg;
    }

    private void set_id(Integer id_penerima) {
        this.id_penerima = id_penerima;
    }

    private Integer get_id() {
        return this.id_penerima;
    }

    /*
    private void set_ktp(String ktp)
    {
        this.e_ktp = ktp;
    }

    private String get_ktp()
    {
        return this.e_ktp;
    }
    */

    private void createDirFoto() {
        Integer idp = get_id();
        String photoDir = Config.FOTO_DIR + idp.toString();
        String thumbDir = Config.FOTO_DIR + idp.toString()+"/thumb/";
        File fr = new File(photoDir);
        File thumb = new File(thumbDir);
        if (!fr.exists()) {
            fr.mkdirs();
        } else
            Log.d("Error: ", "dir foto already exists");
        if (!thumb.exists()) {
            thumb.mkdirs();
        } else
            Log.d("Error: ", "dir foto already exists");
    }


    private class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location loc) {
            if (loc != null) {
                loclong = loc.getLongitude();
                loclat = loc.getLatitude();
                txtloclat.setText(String.format("%s", loclong.toString()));
                txtloclong.setText(String.format("%s", loclat.toString()));
            }
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

    }

}

/*
* copy result to another list
* ref: http://stackoverflow.com/questions/32559473/android-realm-iterators-exception
* http://blog-emildesign.rhcloud.com/?p=590
*/
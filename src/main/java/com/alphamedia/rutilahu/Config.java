package com.alphamedia.rutilahu;

import android.os.Environment;

public class Config {
    public static final String BASE_URL = "http://rutilahu.alphamedia.id";
    public static final String FILE_UPLOAD_URL = "http://rutilahu.alphamedia.id/index.php/transaksi/Rest";
    public static final String UPLOAD_URL = "http://rutilahu.alphamedia.id/index.php/transaksi/Upload/do_upload";
    public static final String POST_UPLOAD = "/index.php/transaksi/Upload/do_upload";
    public static final String FILE_DOWNLOAD_URL = "http://rutilahu.alphamedia.id/uploads/data.zip";
    public static final String APP_DIR = Environment.getExternalStorageDirectory().getPath() + "/fieldreport";
    public static final String REQ_DOWNLOAD_URL = "http://rutilahu.alphamedia.id/index.php/transaksi/Download/data";
    public static final String FILE_DIR = APP_DIR + "/file/";
    public static final String FOTO_DIR = APP_DIR + "/foto/";
    public static final String DATA_DIR = APP_DIR + "/file/data.json";
}
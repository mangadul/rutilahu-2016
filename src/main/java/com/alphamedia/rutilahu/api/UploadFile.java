package com.alphamedia.rutilahu.api;

import android.util.Log;

import com.alphamedia.rutilahu.Config;

import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/*
 * Created by Pratik Butani
 * https://github.com/pratikbutani/OKHTTPUploadImage/
 */

public class UploadFile {

    private static final String URL_UPLOAD = Config.UPLOAD_URL;

    /**
     * Upload File
     *
     * @param srcFile
     * @return
     */
    public static JSONObject uploadData(String srcFile) {

        try {
            File sourceFile = new File(srcFile);

            Log.d("TAG", "File...::::" + sourceFile + " : " + sourceFile.exists());

            final MediaType MEDIA_TYPE_IMAGE = MediaType.parse("image/*");
            final MediaType MEDIA_TYPE_FILE = MediaType.parse("multipart/form-data");

            String filename = srcFile.substring(srcFile.lastIndexOf("/")+1);

            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("userfile", filename, RequestBody.create(MEDIA_TYPE_FILE, sourceFile))
                    .addFormDataPart("path", srcFile)
                    .build();

            Request request = new Request.Builder()
                    .url(URL_UPLOAD)
                    .post(requestBody)
                    .build();

            OkHttpClient client = new OkHttpClient();
            Response response = client.newCall(request).execute();
            String res = response.body().string();
            Log.e("TAG", "Error: " + res);
            return new JSONObject(res);

        } catch (UnknownHostException | UnsupportedEncodingException e) {
            Log.e("TAG", "Error: " + e.getLocalizedMessage());
        } catch (Exception e) {
            Log.e("TAG", "Other Error: " + e.getLocalizedMessage());
        }
        return null;
    }
}
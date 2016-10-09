package com.alphamedia.rutilahu.api;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Common {

    @SuppressLint("SimpleDateFormat")
    public static String getTgl() {
        long msTime = System.currentTimeMillis();
        Date curDateTime = new Date(msTime);
        SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyHHmmss");
        String curDate = formatter.format(curDateTime);
        return curDate;
    }

    /*
    private void showImage(String fileloc, String txt) {
        final Dialog dialog = new Dialog(getApplicationContext());
        dialog.setContentView(R.layout.imageview);
        dialog.setTitle("Lihat Gambar " + txt);
        TextView text = (TextView) dialog.findViewById(R.id.text);
        text.setText(txt);
        ImageView image = (ImageView) dialog.findViewById(R.id.image);
        Picasso.with(getApplicationContext())
                .load(new File(fileloc))
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
    */


}

package com.alphamedia.rutilahu;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.alphamedia.rutilahu.api.CircleTransform;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.util.List;

// This adapter is strictly to interface with the GridView and doesn't
// particular show much interesting Realm functionality.

// Alternatively from this example,
// a developer could update the getView() to pull items from the Realm.

public class DataPenerimaAdapter extends BaseAdapter {

    public static final String TAG = DataPenerimaRealmIO.class.getName();

    private LayoutInflater inflater;

    private Target loadtarget;
    private String sts_sudah = "SUDAH";
    private String sts_belum = "BELUM";

    private List<Penerima> penerima = null;

    public DataPenerimaAdapter(Context context) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setData(List<Penerima> details) {
        this.penerima = details;
    }

    /*
    @Override
    public int getCount() {
        if (realmResults == null || realmResults.size() == 0) {
            return 0;
        }
        return realmResults.first().getMessages().size();
    }

    @Override
    public T getItem(int i) {
        if (realmResults == null || realmResults.size() == 0) {
            return null;
        }
        return realmResults.first().getMessages().get(i);
    }
    */

    @Override
    public int getCount() {
        /*
        if (penerima == null) {
            return 0;
        }
        */
        if (penerima == null || penerima.size() == 0) {
            return 0;
        }
        return penerima.size();
    }

    @Override
    public Object getItem(int position) {
        if (penerima == null || penerima.get(position) == null) {
            return null;
        }
        return penerima.get(position);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View currentView, ViewGroup parent) {
        if (currentView == null) {
            currentView = inflater.inflate(R.layout.penerima_realm_activity, parent, false);
        }
        Penerima dp = penerima.get(position);

        ImageView avatar = (ImageView) currentView.findViewById(R.id.avatar);
        File f = new File(new StringBuilder().append(Config.FOTO_DIR)
                .append(Integer.toString(dp.getId_penerima()))
                .append("/").append(dp.getImg_foto_penerima()).toString());
        Picasso.with(this.inflater.getContext())
                .load(f)
                .error(R.drawable.ic_person_outline_black_24dp)
                .placeholder((dp.getIs_catat() == false) ? R.drawable.ic_check_circle_black_18dp : R.drawable.ic_person_outline_black_24dp)
                //.transform(new CircleTransform())
                .into(avatar);

        ImageView imgnext = (ImageView) currentView.findViewById(R.id.detail);
        //imgnext.setImageResource(R.drawable.ic_navigate_next_black_36dp);
        Picasso.with(this.inflater.getContext())
                .load("#")
                .error(R.drawable.ic_error_black_24dp)
                .placeholder(R.drawable.ic_navigate_next_black_24dp)
                .transform(new CircleTransform())
                .into(imgnext);

        String alamat = new StringBuilder().append(dp.getJalan_desa())
                .append(" Rt. ")
                .append(dp.getRt())
                .append(" Rw. ")
                .append(dp.getRw())
                .append(" Desa ")
                .append(dp.getDesa())
                .toString();

        String catat = (dp.getIs_catat()) ? sts_sudah : sts_belum;
        if(dp.getIs_catat())
        {
            currentView.findViewById(R.id.status).setBackgroundColor(23);
        } else {
            currentView.findViewById(R.id.status).setBackgroundColor(9);
        }
        String noktp = (dp.getKtp().length() > 0) ? dp.getKtp() : "(DATA KTP KOSONG)";

        if (dp != null) {
            ((TextView) currentView.findViewById(R.id.nama)).setText(dp.getNamalengkap());
            ((TextView) currentView.findViewById(R.id.idpenerima)).setText(Integer.toString(dp.getId_penerima()));
            ((TextView) currentView.findViewById(R.id.ktp)).setText(noktp);
            ((TextView) currentView.findViewById(R.id.alamat)).setText(alamat);
            ((TextView) currentView.findViewById(R.id.kecamatan)).setText(dp.getKecamatan());
            ((TextView) currentView.findViewById(R.id.kabupaten)).setText(dp.getKabupaten());
            ((TextView) currentView.findViewById(R.id.tgl_update)).setText(dp.getTgl_update());
            ((TextView) currentView.findViewById(R.id.status)).setText(catat);
        }

        return currentView;
    }

    //@Override
    private void loadBitmap(String url) {

        if (loadtarget == null) loadtarget = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                handleLoadedBitmap(bitmap);
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }

            public void onBitmapFailed() {
            }
        };

        Picasso.with(this.inflater.getContext()).load(url).into(loadtarget);
    }

    public void handleLoadedBitmap(Bitmap b) {
    }


}
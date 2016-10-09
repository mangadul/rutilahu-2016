/**
 * Copyright 2015-present Amberfog
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Modified by Abdul Muin (muin.abdul@gmail.com)
 * Integrated with Realm with RealmList, RealmResult, Marker
 * Last script getLastKnownLocation() has found bugs (always return null caused NullPointerException) modification needed
 *
 */

package com.alphamedia.rutilahu.map;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alphamedia.rutilahu.Config;
import com.alphamedia.rutilahu.DataPenerimaAdapter;
import com.alphamedia.rutilahu.OnTaskCompleted;
import com.alphamedia.rutilahu.Penerima;
import com.alphamedia.rutilahu.R;
import com.alphamedia.rutilahu.api.SlidingUpPanelLayout;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.exceptions.RealmException;

import static android.support.v4.content.PermissionChecker.checkSelfPermission;


public class MainFragment extends Fragment implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        SlidingUpPanelLayout.PanelSlideListener, LocationListener {

    private static final String ARG_LOCATION = "arg.location";
    private static final long MIN_TIME_BW_UPDATES = 0;
    private static final float MIN_DISTANCE_CHANGE_FOR_UPDATES = 0;

    private LockableListView mListView;
    private SlidingUpPanelLayout mSlidingUpPanelLayout;

    private View mTransparentHeaderView;
    private View mTransparentView;
    private View mSpaceView;

    private LatLng mLocation;
    private Marker mLocationMarker;

    private SupportMapFragment mMapFragment;

    private GoogleMap mMap;
    private boolean mIsNeedLocationUpdate = true;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    Location location;

    private boolean isGPSEnabled = false;
    private boolean isNetworkEnabled = false;
    private boolean canGetLocation;
    private String noktp = "";

    RealmResults<Penerima> result = null;
    DataPenerimaAdapter adapter;

    private List<Penerima> datapenerima;

    Realm realm;
    private Double posisi_long, posisi_lat;

    public MainFragment() {
    }

    public static MainFragment newInstance(LatLng location) {
        MainFragment f = new MainFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_LOCATION, location);
        f.setArguments(args);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.map_fragment_main, container, false);

        mListView = (LockableListView) rootView.findViewById(android.R.id.list);
        mListView.setOverScrollMode(ListView.OVER_SCROLL_NEVER);

        mSlidingUpPanelLayout = (SlidingUpPanelLayout) rootView.findViewById(R.id.slidingLayout);
        mSlidingUpPanelLayout.setEnableDragViewTouchEvents(true);

        int mapHeight = getResources().getDimensionPixelSize(R.dimen.map_height);
        mSlidingUpPanelLayout.setPanelHeight(mapHeight); // you can use different height here
        mSlidingUpPanelLayout.setScrollableView(mListView, mapHeight);

        mSlidingUpPanelLayout.setPanelSlideListener(this);

        // transparent view at the top of ListView
        mTransparentView = rootView.findViewById(R.id.transparentView);

        // init header view for ListView
        mTransparentHeaderView = inflater.inflate(R.layout.map_transparent_header_view, mListView, false);
        mSpaceView = mTransparentHeaderView.findViewById(R.id.space);

        collapseMap();

        mSlidingUpPanelLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mSlidingUpPanelLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                mSlidingUpPanelLayout.onPanelDragged(0);
            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(getContext()).build();
        //Realm.deleteRealm(realmConfiguration);
        Realm.setDefaultConfiguration(realmConfiguration);

        mLocation = getArguments().getParcelable(ARG_LOCATION);
        if (mLocation == null) {
            mLocation = getLastKnownLocation(false);
            //mLocation = new  LatLng(-6.2086768,106.8086909);
        }

        mMapFragment = SupportMapFragment.newInstance();
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.mapContainer, mMapFragment, "map");
        fragmentTransaction.commit();

        mListView.addHeaderView(mTransparentHeaderView);

        if(realm == null) realm = Realm.getInstance(realmConfiguration);
        adapter = new DataPenerimaAdapter(getContext());
        GetDataMap dc = new GetDataMap(new OnTaskCompleted() {
            @Override
            public void onTaskCompleted() {
                try {
                    List<Penerima> data = null;
                    //data = loadPenerima();
                    RealmResults res = (RealmResults<Penerima>) loadPenerima();
                    data = res.where().equalTo("is_catat", true).findAll();
                    adapter.setData(data);
                    mListView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        dc.execute();

        mListView.invalidate();
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mSlidingUpPanelLayout.collapsePane();

                TextView idp = (TextView) view.findViewById(R.id.idpenerima);
                String idpenerima = idp.getText().toString();
                RealmResults<Penerima> result = null;
                try {
                    result = (RealmResults<Penerima>) loadPenerima();
                    Penerima p = result.where()
                            .equalTo("id_penerima", Integer.parseInt(idpenerima))
                            .findFirst();

                    setLokasi(Double.parseDouble(p.getLongitude()), Double.parseDouble(p.getLatitude()));

                    String nama = p.getNamalengkap();
                    String alamat = new StringBuilder()
                            .append("KTP: ")
                            .append(p.getKtp())
                            .append("\nKK: ")
                            .append(p.getKk())
                            .append("\n")
                            .append(p.getJalan_desa())
                            .append(" Rt. ")
                            .append(p.getRt())
                            .append(" Rw. ")
                            .append(p.getRw())
                            .append("\nDesa ")
                            .append(p.getDesa())
                            .append("\nKec. ")
                            .append(p.getKecamatan())
                            .append("\nKab. ")
                            .append(p.getKabupaten())
                            .toString();

                    LatLng posgps = new LatLng(Double.parseDouble(p.getLongitude()), Double.parseDouble(p.getLatitude()));
                    Marker marker = mMap.addMarker(new MarkerOptions()
                            .title(nama)
                            .position(posgps)
                            .snippet(alamat)
                            .icon(BitmapDescriptorFactory
                                    .fromResource(R.drawable.marker)));

                    mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

                        @Override
                        public View getInfoWindow(Marker arg0) {
                            return null;
                        }

                        @Override
                        public View getInfoContents(Marker marker) {

                            LinearLayout info = new LinearLayout(getContext());
                            info.setOrientation(LinearLayout.VERTICAL);

                            TextView title = new TextView(getContext());
                            title.setTextColor(Color.BLACK);
                            title.setGravity(Gravity.CENTER);
                            title.setTypeface(null, Typeface.BOLD);
                            title.setText(marker.getTitle());

                            TextView snippet = new TextView(getContext());
                            snippet.setTextColor(Color.GRAY);
                            snippet.setText(marker.getSnippet());

                            info.addView(title);
                            info.addView(snippet);

                            return info;
                        }
                    });
                    marker.showInfoWindow();
                    moveToLocation(posgps, false);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        setUpMapIfNeeded();
    }

    private void updateList(List<Penerima> p, DataPenerimaAdapter adapter)
    {
        adapter.setData(p);
        mListView.setAdapter(adapter);
    }

    private void setLokasi(Double lng, Double lat)
    {
        this.posisi_long = lng;
        this.posisi_lat = lat;
    }

    private LatLng getLokasi()
    {
        return new LatLng(this.posisi_long, this.posisi_lat);
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = mMapFragment.getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setCompassEnabled(true);
                mMap.getUiSettings().setZoomControlsEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
                final LatLng update = getLastKnownLocation();
                if (update != null) {
                    mMap.moveCamera(CameraUpdateFactory.newCameraPosition(CameraPosition.fromLatLngZoom(update, 11.0f)));
                }

                mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

                    @Override
                    public void onMapClick(LatLng latLng) {
                        mIsNeedLocationUpdate = false;
                        moveToLocation(latLng, false);
                    }

                });

            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // In case Google Play services has since become available.
        setUpMapIfNeeded();
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            realm = Realm.getInstance(getContext());
        } catch (RealmException e) {
            Log.e("Error: ", e.getMessage());
            //Realm.deleteRealmFile(getContext());
        }
        // Connect the client.
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        // Disconnecting the client invalidates it.
        mGoogleApiClient.disconnect();
        super.onStop();
        //realm.close();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //realm.close();
    }

    private LatLng getLastKnownLocation() {
        //return new LatLng(-6.2086768,106.8086909);
        return getLastKnownLocation(true);
    }

    /*
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lr = LocationRequest.create();
        lr.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        lc = new LocationClient(this.getActivity().getApplicationContext(),
                this, this);
        lc.connect();
    }
    */

    private LatLng getLastKnownLocation(boolean isMoveMarker) {
        try {
            // TheApp.getAppContext()
            //getAppContext().getSystemService(Context.LOCATION_SERVICE);
            LocationManager lm = (LocationManager) TheApp.getAppContext().getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
            // getting GPS status
            isGPSEnabled = lm
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            isNetworkEnabled = lm
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                Toast.makeText(getContext(), "Location service disabled.", Toast.LENGTH_SHORT).show();
            } else {
                this.canGetLocation = true;

                if (isGPSEnabled) {
                    if (checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                            && checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    }
                    lm.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES,
                            (android.location.LocationListener) this);

                    Log.d("GPS Enabled", "GPS Enabled");

                    if (lm != null) {
                        location = lm
                                .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        //updateGPSCoordinates();
                    }
                }

                // First get location from Network Provider
                if (isNetworkEnabled) {
                    if (location == null) {
                        lm.requestLocationUpdates(
                                LocationManager.NETWORK_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES,
                                (android.location.LocationListener) this);

                        Log.d("Network", "Network");

                        if (lm != null) {
                            location = lm
                                    .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                            //updateGPSCoordinates();
                        }
                    }
                }

                if (location != null) {
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    if (isMoveMarker) {
                        moveMarker(latLng);
                    }
                    return latLng;
                } else Toast.makeText(getContext(), "Silahkan aktifkan GPS terlebih dahulu", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e)
        {
            Log.e("Error : Location", "Impossible to connect to LocationManager", e);
        }

        return new LatLng(-6.2086768,106.8086909);
    }

    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    private void moveMarker(LatLng latLng) {
        if (mLocationMarker != null) {
            mLocationMarker.remove();
        }
        mLocationMarker = mMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_place_black_24dp))
                .position(latLng).anchor(0.5f, 0.5f));
    }

    private void moveToLocation(Location location) {
        if (location == null) {
            return;
        }
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        moveToLocation(latLng);
    }

    private void moveToLocation(LatLng latLng) {
        moveToLocation(latLng, true);
    }

    private void moveToLocation(LatLng latLng, final boolean moveCamera) {
        if (latLng == null) {
            return;
        }
        moveMarker(latLng);
        mLocation = latLng;
        mListView.post(new Runnable() {
            @Override
            public void run() {
                if (mMap != null && moveCamera) {
                    mMap.moveCamera(CameraUpdateFactory.newCameraPosition(CameraPosition.fromLatLngZoom(mLocation, 11.0f)));
                }
            }
        });
    }

    private void collapseMap() {
        mSpaceView.setVisibility(View.VISIBLE);
        mTransparentView.setVisibility(View.GONE);
        if (mMap != null && mLocation != null) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mLocation, 11f), 1000, null);
        }
        mListView.setScrollingEnabled(true);
    }

    private void expandMap() {
        mSpaceView.setVisibility(View.GONE);
        mTransparentView.setVisibility(View.INVISIBLE);
        if (mMap != null) {
            mMap.animateCamera(CameraUpdateFactory.zoomTo(14f), 1000, null);
        }
        mListView.setScrollingEnabled(false);
    }

    @Override
    public void onPanelSlide(View view, float v) {
    }

    @Override
    public void onPanelCollapsed(View view) {
        expandMap();
    }

    @Override
    public void onPanelExpanded(View view) {
        collapseMap();
    }

    @Override
    public void onPanelAnchored(View view) {

    }

    @Override
    public void onLocationChanged(Location location) {
        if (mIsNeedLocationUpdate) {
            moveToLocation(location);
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setNumUpdates(1);

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    private static class MyLocationListener implements LocationListener
    {
        private static Double lang;
        private static Double lat;

        public void onLocationChanged(Location loc) {
            if (loc != null) {
                MyLocationListener.lang = loc.getLongitude();
                MyLocationListener.lat = loc.getLatitude();
                //txtloclat.setText(String.format("%s", loclong.toString()));
                //txtloclong.setText(String.format("%s", loclat.toString()));
            }
        }

        public Double getLang()
        {
            return this.lang;
        }

        public Double getLat()
        {
            return this.lat;
        }

        protected void setLang(Double lng)
        {
            this.lang = lng;
        }

        protected void setLat(Double lat)
        {
            this.lat = lat;
        }

        public void onProviderDisabled(String provider) {
            // TODO Auto-generated method stub
        }

        public void onProviderEnabled(String provider) {
            // TODO Auto-generated method stub
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
            // TODO Auto-generated method stub
        }

    }

    private class GetDataMap extends AsyncTask<Void, Integer, Integer> {

        //private RealmResults result = null;
        private Realm realm;

        private OnTaskCompleted listener;

        public GetDataMap(OnTaskCompleted listener) {
            this.listener=listener;
        }

        @Override
        protected Integer doInBackground(Void... params) {

            realm = Realm.getDefaultInstance();

            if(result != null){
                result.clear();
                realm.clear(Penerima.class);
            }

            realm.beginTransaction();

            try {
                List<Penerima> list = realm.allObjects(Penerima.class);
                if(list.size() <= 0) loadJsonFromStream(realm);
            } catch (IOException e) {
                e.printStackTrace();
                realm.cancelTransaction();
                //realm.close();
            }
            realm.commitTransaction();

            //result = realm.where(Penerima.class).findAll();
            //datapenerima = realm.allObjects(Penerima.class);
            //Integer sum = result.size();
            //realm.close();
            return 1;
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
                    //realm.createAllFromJson(Penerima.class, stream);
                    realm.createOrUpdateAllFromJson(Penerima.class, stream);
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

        public List<Penerima> getData() throws IOException {
            return datapenerima;
        }

    }

    public List<Penerima> loadPenerima() throws IOException {
        return realm.allObjects(Penerima.class);
    }

    public void set_noKtp(String ktp)
    {
        this.noktp = ktp;
    }

    public String get_noKtp()
    {
        return this.noktp;
    }
}
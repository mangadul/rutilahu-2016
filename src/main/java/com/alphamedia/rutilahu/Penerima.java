package com.alphamedia.rutilahu;


import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Penerima extends RealmObject {

    @SerializedName("id_penerima") @PrimaryKey private int id_penerima;
    private long no_urut;
    private String ktp;
    private String kk;
    private String namalengkap;
    private String jenis_kelamin;
    private String tempat_lahir;
    private String tgl_lahir;
    private String jalan_desa;
    private String rt;
    private String rw;
    private String desa;
    private String kecamatan;
    private String kabupaten;
    private String provinsi;
    private String img_foto_penerima;
    private String img_tampak_depan_rumah;
    private String img_tampak_samping_1;
    private String img_tampak_samping_2;
    private String img_tampak_dapur;
    private String img_tampak_jamban;
    private String img_tampak_sumber_air;
    private String longitude;
    private String latitude;
    private int tahun_terima;
    private String keterangan;
    private String tgl_update;
    private String tgl_catat;
    private boolean is_catat;
    private String kode_desa;
    private String kode_kec;
    private String kode_kab;
    private String deviceID;
    private String img_tampak_belakang;

    public String getdeviceID() { return deviceID; }

    public int getId_penerima() { return id_penerima; }
    public long getNo_urut() { return no_urut; }
    public String getKtp() { return ktp; }
    public String getKk() { return kk; }
    public String getNamalengkap() { return namalengkap; }
    public String getJenis_kelamin() { return jenis_kelamin; }
    public String getTempat_lahir() { return tempat_lahir; }

    public String getTgl_lahir() { return tgl_lahir; }

    public String getDesa() { return desa; }
    public String getKecamatan() { return kecamatan; }
    public String getKabupaten() { return kabupaten; }
    public String getProvinsi() { return provinsi; }
    public String getJalan_desa() { return jalan_desa; }
    public String getImg_foto_penerima() { return img_foto_penerima; }
    public String getImg_tampak_depan_rumah() { return img_tampak_depan_rumah; }
    public String getImg_tampak_samping_1() { return img_tampak_samping_1; }
    public String getImg_tampak_samping_2() { return  img_tampak_samping_2; }
    public String getImg_tampak_dapur() { return img_tampak_dapur; }
    public String getImg_tampak_jamban() { return img_tampak_jamban; }
    public String getImg_tampak_sumber_air() { return img_tampak_sumber_air; }
    public String getLongitude() { return longitude; }
    public String getLatitude() { return latitude; }
    public int  getTahun_terima() { return tahun_terima; }
    public String getRt() { return rt; }
    public String getRw() { return rw; }
    public  String getKeterangan() { return  keterangan; }
    public String getTgl_update() { return tgl_update; }
    public boolean getIs_catat() { return is_catat; }
    public String getTgl_catat() { return tgl_catat; }

    public String getKode_desa() { return kode_desa; }
    public String getKode_kec() { return kode_kec; }
    public String getKode_kab() { return kode_kab; }

    public String getImg_tampak_belakang() { return img_tampak_belakang; }

    public void setDeviceID(String devid) {
        this.deviceID= devid;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setDesa(String desa) {
        this.desa= desa;
    }

    public void setIs_catat(boolean is_catat) {
        this.is_catat= is_catat;
    }

    public void setTgl_update(String tgl_update) {
        this.tgl_update= tgl_update;
    }

    public void setImg_foto_penerima(String img_foto_penerima) {
        this.img_foto_penerima= img_foto_penerima;
    }

    public void setImg_tampak_depan_rumah(String img_tampak_depan_rumah) {
        this.img_tampak_depan_rumah= img_tampak_depan_rumah;
    }

    public void setImg_tampak_samping_1(String img_tampak_samping_1) {
        this.img_tampak_samping_1= img_tampak_samping_1;
    }

    public void setImg_tampak_samping_2(String img_tampak_samping_2) {
        this.img_tampak_samping_2= img_tampak_samping_2;
    }

    public void setImg_tampak_dapur(String img_tampak_dapur) {
        this.img_tampak_dapur= img_tampak_dapur;
    }

    public void setImg_tampak_jamban(String img_tampak_jamban) {
        this.img_tampak_jamban= img_tampak_jamban;
    }

    public void setImg_tampak_sumber_air(String img_tampak_sumber_air) {
        this.img_tampak_sumber_air= img_tampak_sumber_air;
    }

    public void setKecamatan(String kecamatan) {
        this.kecamatan= kecamatan;
    }

    public void setKabupaten(String kabupaten) {
        this.kabupaten= kabupaten;
    }

    public void setProvinsi(String provinsi) {
        this.provinsi= provinsi;
    }

    public void setTempat_lahir(String tempat_lahir) {
        this.tempat_lahir= tempat_lahir;
    }

    public void setTgl_lahir(String tgl_lahir) {
        this.tgl_lahir= tgl_lahir;
    }

    public void setTgl_catat(String tgl_catat) {
        this.tgl_catat= tgl_catat;
    }

    public void setKtp(String ktp) {
        this.ktp= ktp;
    }

    public void setKk(String kk) {
        this.kk= kk;
    }

    public void setNamalengkap(String namalengkap) {
        this.namalengkap = namalengkap;
    }

    public void setId_penerima(int id_penerima){
        this.id_penerima = id_penerima;
    }

    public void setRt(String rt) {
        this.rt = rt;
    }

    public void setRw(String rw) {
        this.rw = rw;
    }

    public void setJenis_kelamin(String jenis_kelamin) {
        this.jenis_kelamin = jenis_kelamin;
    }

    public void setTahun_terima(int tahun_terima) {
        this.tahun_terima = tahun_terima;
    }

    public void setJalan_desa(String jalan_desa) {
        this.jalan_desa = jalan_desa;
    }

    public void setKeterangan(String keterangan)
    {
        this.keterangan = keterangan;
    }

    public void setNo_urut(long no_urut)
    {
        this.no_urut = no_urut;
    }

    public void setKode_desa(String kode_desa)
    {
        this.kode_desa = kode_desa;
    }

    public void setKode_kec(String kode_kec) { this.kode_kec = kode_kec; }

    public void setKode_kab(String kode_kab)
    {
        this.kode_kab = kode_kab;
    }

    public void setImg_tampak_belakang(String img_tampak_belakang) {
        this.img_tampak_belakang = img_tampak_belakang;
    }

}
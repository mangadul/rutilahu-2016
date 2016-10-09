package com.alphamedia.rutilahu.api;

import java.util.List;

import io.realm.Realm;

public class RealmReturn {

    private List<Realm> realms;

    public List<Realm> getRealms() {
        return realms;
    }

    public void setRealms(List<Realm> realms) {
        this.realms = realms;
    }

}
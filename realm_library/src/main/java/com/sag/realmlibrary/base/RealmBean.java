package com.sag.realmlibrary.base;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

/**
 * Created by SAG on 2016/10/31 0031.
 */

public class RealmBean extends RealmObject {

    @Ignore
    public static final String KEY = "key";

    @PrimaryKey
    public String key;

    public String message;


    public RealmBean() {
    }

    public RealmBean(String key, String message) {
        this.key = key;
        this.message = message;
    }

}

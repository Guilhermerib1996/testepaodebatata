package com.meuvesti.cliente.realm;

import android.os.Build;
import android.support.annotation.RequiresApi;

import java.io.Serializable;
import java.util.Objects;

import io.realm.RealmObject;

/**
 * Created by hersonrodrigues on 13/02/17.
 */
public class ColorRealm extends RealmObject implements Serializable {
    private String id;
    private String name;
    private String code;
    private String stamp;
    private String photo;
    private String stamp_file;
    private String domain_id;

    public ColorRealm(){}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getEstampa() {
        return stamp;
    }

    public void setEstampa(String estampa) {
        this.stamp = estampa;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getStampFile() {
        return stamp_file;
    }

    public void setStampFile(String stamp_file) {
        this.stamp_file = stamp_file;
    }

    public String getDomainId() {
        return domain_id;
    }

    public void setDomainId(String domain_id) {
        this.domain_id = domain_id;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ColorRealm that = (ColorRealm) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(name, that.name) &&
                Objects.equals(code, that.code) &&
                Objects.equals(stamp, that.stamp) &&
                Objects.equals(photo, that.photo) &&
                Objects.equals(stamp_file, that.stamp_file) &&
                Objects.equals(domain_id, that.domain_id);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public int hashCode() {

        return Objects.hash(id, name, code, stamp, photo, stamp_file, domain_id);
    }
}

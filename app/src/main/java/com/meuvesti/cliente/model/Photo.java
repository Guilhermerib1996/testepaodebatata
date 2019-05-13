package com.meuvesti.cliente.model;

import android.os.Build;
import android.support.annotation.RequiresApi;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Objects;

/**
 * Created by hersonrodrigues on 25/01/17.
 */

public class Photo implements Serializable {

    private String path;
    private String url;
    @SerializedName("small")
    private String urlSmall;
    @SerializedName("large")
    private String urlLarge;

    public Photo(){

    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrlSmall() {
        return urlSmall;
    }

    public void setUrlSmall(String urlSmall) {
        this.urlSmall = urlSmall;
    }

    public String getUrlLarge() {
        return urlLarge;
    }

    public void setUrlLarge(String urlLarge) {
        this.urlLarge = urlLarge;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Photo photo = (Photo) o;
        return Objects.equals(path, photo.path) &&
                Objects.equals(url, photo.url) &&
                Objects.equals(urlSmall, photo.urlSmall) &&
                Objects.equals(urlLarge, photo.urlLarge);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public int hashCode() {

        return Objects.hash(path, url, urlSmall, urlLarge);
    }
}

package com.meuvesti.cliente.model;

import android.os.Build;
import android.support.annotation.RequiresApi;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Objects;

/**
 * Created by Herson Rodrigues <hersonrodrigues@gmail.com> on 13/02/2018.
 */

public class ImageSize implements Serializable {
    @SerializedName("orig")
    @Expose
    private ImageSizeKind orig;

    @SerializedName("large")
    @Expose
    private ImageSizeKind large;

    @SerializedName("small")
    @Expose
    private ImageSizeKind small;

    public ImageSizeKind getOrig() {
        return orig;
    }

    public void setOrig(ImageSizeKind orig) {
        this.orig = orig;
    }

    public ImageSizeKind getLarge() {
        return large;
    }

    public void setLarge(ImageSizeKind large) {
        this.large = large;
    }

    public ImageSizeKind getSmall() {
        return small;
    }

    public void setSmall(ImageSizeKind small) {
        this.small = small;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImageSize imageSize = (ImageSize) o;
        return Objects.equals(orig, imageSize.orig) &&
                Objects.equals(large, imageSize.large) &&
                Objects.equals(small, imageSize.small);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public int hashCode() {

        return Objects.hash(orig, large, small);
    }
}
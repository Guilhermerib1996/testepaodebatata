package com.meuvesti.cliente.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Objects;

/**
 * Created by Herson Rodrigues <hersonrodrigues@gmail.com> on 13/02/2018.
 */

public class ImageSizeKind implements Serializable {

    @SerializedName("path")
    @Expose
    private String path;
    @SerializedName("width")
    @Expose
    private int width;
    @SerializedName("height")
    @Expose
    private int height;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public boolean isSquare() {
        return getHeight() == getWidth();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImageSizeKind that = (ImageSizeKind) o;
        return width == that.width &&
                height == that.height &&
                Objects.equals(path, that.path);
    }

    @Override
    public int hashCode() {

        return Objects.hash(path, width, height);
    }
}

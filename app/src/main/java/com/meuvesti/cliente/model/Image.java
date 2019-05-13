package com.meuvesti.cliente.model;

import android.os.Build;
import android.support.annotation.RequiresApi;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by Herson Rodrigues <hersonrodrigues@gmail.com> on 13/02/2018.
 */

public class Image implements Serializable {
    public static final int LARGE = 1;
    public static final int SMALL = 2;
    public static final int ORIGINAL = 3;

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("product_id")
    @Expose
    private String productId;

    @SerializedName("url")
    @Expose
    private String url;

    @SerializedName("filename")
    @Expose
    private String filename;

    @SerializedName("sizes")
    @Expose
    private ImageSize sizes;

    public static List<String> imageToString(List<Image> images, int type) {
        List<String> list = new ArrayList<>();
        if (images != null && !images.isEmpty()) {
            for (Image image : images) {
                if (type == LARGE) {
                    list.add(image.getUrlLarge());
                } else if (type == ORIGINAL) {
                    list.add(image.getUrlOrig());
                } else if (type == SMALL) {
                    list.add(image.getUrlSmall());
                }
            }
        }
        return list;
    }

    public static String getFirstSmall(List<Image> images) {
        if (images != null && !images.isEmpty()) {
            return images.get(0).getUrlSmall();
        }
        return null;
    }

    public static String getFirstLarger(List<Image> images) {
        if (images != null && !images.isEmpty()) {
            return images.get(0).getUrlLarge();
        }
        return null;
    }

    public static String getFirstOriginal(List<Image> images) {
        if (images != null && !images.isEmpty()) {
            return images.get(0).getUrlOrig();
        }
        return null;
    }

    public static List<String> toStringList(List<Image> images, int type) {
        List<String> list = new ArrayList<>();
        if (images != null && !images.isEmpty()) {
            for (Image image : images) {
                if (type == LARGE) {
                    list.add(image.getUrlLarge());
                } else if (type == ORIGINAL) {
                    list.add(image.getUrlOrig());
                } else if (type == SMALL) {
                    list.add(image.getUrlSmall());
                }
            }
        }
        return list;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public ImageSize getSizes() {
        return sizes;
    }

    public void setSizes(ImageSize sizes) {
        this.sizes = sizes;
    }

    public String getUrlLarge() {
        return getUrl() + getSizes().getLarge().getPath();
    }

    public String getUrlSmall() {
        return getUrl() + getSizes().getSmall().getPath();
    }

    public String getUrlOrig() {
        return getUrl() + getSizes().getOrig().getPath();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Image image = (Image) o;
        return Objects.equals(id, image.id) &&
                Objects.equals(productId, image.productId) &&
                Objects.equals(url, image.url) &&
                Objects.equals(filename, image.filename) &&
                Objects.equals(sizes, image.sizes);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public int hashCode() {

        return Objects.hash(id, productId, url, filename, sizes);
    }
}

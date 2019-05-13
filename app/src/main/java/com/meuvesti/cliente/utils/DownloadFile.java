package com.meuvesti.cliente.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.content.Loader;
import android.util.Log;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hersondf on 08/10/16.
 */
public class DownloadFile extends AsyncTask<Object, Object, Void> {
    private final Loader.OnLoadCompleteListener mOnLoadCompleteListener;
    private final List<String> mFotos;
    private final ProgressDialog mProgressDialog;
    private AbstractList<Uri> mPath = new ArrayList<>();
    private int mHasCountTotal = 0;
    private Context mContext;
    private int mCount = 0;

    public DownloadFile(Context context, List<String> fotos, Loader.OnLoadCompleteListener onLoadCompleteListener) {
        mContext = context;
        mOnLoadCompleteListener = onLoadCompleteListener;
        mFotos = fotos;

        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setMax(100);
        mProgressDialog.setCancelable(true);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setMessage("Preparando imagens..");
        mProgressDialog.show();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Object... aurl) {

        for (final String foto : mFotos) {

            Thread thread = new Thread() {
                @Override
                public void run() {
                    makeDownload(foto);
                }
            };

            thread.start();

        }

        return null;
    }

    @Nullable
    private void makeDownload(String urlString) {
        mCount = mCount + 1;
        int count;
        try {

            mProgressDialog.setMessage("Ajustando a foto " + mCount + "...");

            URL url = new URL(urlString);

            URLConnection conexion = url.openConnection();
            conexion.connect();

            String targetFileName = url.getPath().split("/")[url.getPath().split("/").length-1];//Change name and subname
            int lenghtOfFile = conexion.getContentLength();

            String PATH = Environment.getExternalStorageDirectory() + "/vesti_image_cache/";
            File folder = new File(PATH);

            if (!folder.exists()) {
                folder.mkdir();//If there is no folder it will be created.
            }
            InputStream input = new BufferedInputStream(url.openStream());
            OutputStream output = new FileOutputStream(PATH + targetFileName);
            byte data[] = new byte[1024];
            long total = 0;
            while ((count = input.read(data)) != -1) {
                total += count;
                publishProgress((int) (total * 100 / lenghtOfFile));
                output.write(data, 0, count);
            }

            output.flush();
            output.close();
            input.close();

            Log.i("Download", ">>>>>>>>>>>> >>>>>>> " + PATH + targetFileName);

            String path = PATH + targetFileName;

            mPath.add(Uri.parse(path));

        } catch (Exception e) {
            Log.e("Download", e.getMessage(), e);

        } finally {

            mHasCountTotal = mHasCountTotal + 1;

            Log.i("Download", ">>>>>>>> HASCOUNT: " + mHasCountTotal + " PRODUCTS: " + mFotos.size() + " >>>>>>>>> ");
            if (mHasCountTotal == mFotos.size()) {
                mProgressDialog.dismiss();
                Log.i("Download", ">>>>>>>> LOADING CALL >>>>>>>>> ");
                mOnLoadCompleteListener.onLoadComplete(null, mPath);
            }

        }
    }
}

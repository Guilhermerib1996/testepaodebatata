package com.meuvesti.cliente.iclass;

/**
 * Created by Herson Rodrigues <hersonrodrigues@gmail.com> on 23/02/2018.
 */

public interface OnSearch {
    void cancel();

    void proccess(String search);

    String getSearchText();
}

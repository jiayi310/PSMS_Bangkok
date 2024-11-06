package com.example.androidmobilestock_bangkok.Interface;

import com.example.androidmobilestock_bangkok.AC_Class;

public interface OnQuantityChangeListener {
    void onQuantityChanged(AC_Class.Item item, int quantity);
}

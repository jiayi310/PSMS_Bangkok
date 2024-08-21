package com.example.androidmobilestock.Interface;

import com.example.androidmobilestock.AC_Class;

public interface OnQuantityChangeListener {
    void onQuantityChanged(AC_Class.Item item, int quantity);
}

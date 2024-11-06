package com.example.androidmobilestock_bangkok.ui.main;

import android.util.Log;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Hash {
    //Hash function
    public static byte[] getHash(String password) {
        MessageDigest digest=null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e1) {
            Log.i("custDebug", e1.getMessage());
        }
        digest.reset();
        return digest.digest(password.getBytes());
    }

    //Convert hash to string
    public static String bin2hex(byte[] data) {
        return String.format("%0" + (data.length*2) + "X", new BigInteger(1, data));
    }
}

package com.zhihuianxin.secure;

import android.content.Context;

public class Secure {
    static{
        System.loadLibrary("ax-secure");
    }

    public void initialize(Context context){
        nativeInitialize(context);
    }

    synchronized native private int nativeInitialize(Context context);
    synchronized native public int setIsDebug(boolean isDebug);

    native public int printAppSignature();

    synchronized native public byte[] aesEncode(byte[] data, byte[] key);
    synchronized native public byte[] aesDecode(byte[] data, byte[] key);
    synchronized native public byte[] aesEncodeCBC(byte[] data, byte[] key, byte[] iv);
    synchronized native public byte[] aesDecodeCBC(byte[] data, byte[] key, byte[] iv);
    synchronized native public byte[] md5(byte[] data);

    static synchronized native public byte[] encodeMessageField(byte[] data);
    synchronized native public byte[] decodeMessageField(byte[] data);

    synchronized native public byte[] encodeMessage(byte[] data);
    synchronized native public byte[] decodeMessage(byte[] data);

    synchronized native public byte[] encodeLocal(byte[] data);
    synchronized native public byte[] decodeLocal(byte[] data);

    static synchronized native public byte[] signMessage(byte[] data);
}

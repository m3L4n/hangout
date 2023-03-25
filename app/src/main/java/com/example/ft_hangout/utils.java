package com.example.ft_hangout;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class utils {
    public static Bitmap getImage(byte[] image){
        return BitmapFactory.decodeByteArray(image,0, image.length);
    }
    public static byte[] getbytes(InputStream inputStream) throws IOException{
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int buffersize = 1024;
        byte[] buffer = new byte[buffersize];
        int len = 0;
        while ((len = inputStream.read(buffer)) != -1){
            byteBuffer.write(buffer, 0, len);
        }
        return  byteBuffer.toByteArray();

    }


}

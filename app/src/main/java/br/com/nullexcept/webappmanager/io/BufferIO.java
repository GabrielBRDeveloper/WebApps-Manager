package br.com.nullexcept.webappmanager.io;

import android.util.Base64;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class BufferIO {

    @NonNull
    @NotNull
    public static String ObjectToString(Serializable obj){
        try {
            ByteArrayOutputStream o = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(o);
            out.writeObject(obj);
            out.flush();
            o.flush();
            o.close();
            return Base64.encodeToString(o.toByteArray(), Base64.DEFAULT);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @NonNull
    @NotNull
    public static  <T extends Serializable> T ObjectFromString(String value){
        try {
            byte[] buffer = Base64.decode(value, Base64.DEFAULT);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(buffer);
            ObjectInputStream input = new ObjectInputStream(inputStream);
            T item = (T) input.readObject();
            inputStream.close();
            return item;
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}

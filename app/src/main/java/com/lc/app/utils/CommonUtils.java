package com.lc.app.utils;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.lc.app.model.Account;
import com.lc.app.model.CommonEntry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Orange on 18-4-15.
 * Email:addskya@163.com
 */
public class CommonUtils {


    public static void saveAccount(
            @NonNull String filePath,
            @NonNull CommonEntry entry)
            throws IOException {
        File folder = new File(filePath);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        File file = new File(filePath, entry.getUUID());
        ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(file));
        oos.writeObject(entry);
        oos.flush();
        oos.close();
    }

    public static void deleteAccount(@NonNull String filePath,
                                     @NonNull CommonEntry entry)
            throws IOException {
        File file = new File(filePath, entry.getUUID());
        if (file.delete()) {
            file.deleteOnExit();
        }
    }


    public static void updateAccount(@NonNull String filePath,
                                     @NonNull CommonEntry entry)
            throws IOException {
        deleteAccount(filePath, entry);
        saveAccount(filePath, entry);
    }

    public static List<CommonEntry> loadAccount(@NonNull String filePath)
            throws FileNotFoundException, IOException, ClassNotFoundException {
        File file = new File(filePath);
        File[] accounts = file.listFiles();
        List<CommonEntry> list = new ArrayList<>();
        if (accounts == null || accounts.length == 0) {
            return list;
        }

        for (File f : accounts) {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f));
            Object obj = ois.readObject();
            list.add((CommonEntry) obj);
        }

        return list;
    }
}

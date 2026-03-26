package my.edu.utar.assignment;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.security.Key;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class StorageHelper {

    private static final String PREF_NAME = "password_db";
    private static final String KEY = "data";
    private static final String KEY_FIRST_LAUNCH = "first_launch";
    private static final String ENCRYPTION_KEY = "MySecretKey12345"; // 16 bytes key for AES

    SharedPreferences prefs;
    Gson gson;

    public StorageHelper(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();

        // Check if first launch - pre-load sample data
        if (isFirstLaunch()) {
            preloadSampleData();
        }
    }

    private boolean isFirstLaunch() {
        return prefs.getBoolean(KEY_FIRST_LAUNCH, true);
    }

    private void preloadSampleData() {
        ArrayList<PasswordModel> sampleList = new ArrayList<>();

        // Add 5 sample entries
        sampleList.add(new PasswordModel("Google", "user@gmail.com", "google123"));
        sampleList.add(new PasswordModel("Facebook", "john@outlook.com", "4f5b6"));
        sampleList.add(new PasswordModel("Amazon", "john.doe@email.com", "amz789"));
        sampleList.add(new PasswordModel("Netflix", "streamer", "netflix2024"));
        sampleList.add(new PasswordModel("GitHub", "dev_coder", "github123"));

        saveAll(sampleList);

        // Mark first launch as false
        prefs.edit().putBoolean(KEY_FIRST_LAUNCH, false).apply();
    }

    // Encrypt data before saving
    public void saveAll(ArrayList<PasswordModel> list) {
        try {
            String json = gson.toJson(list);
            String encryptedData = encrypt(json);
            prefs.edit().putString(KEY, encryptedData).apply();
        } catch (Exception e) {
            e.printStackTrace();
            // Fallback to unencrypted storage if encryption fails
            String json = gson.toJson(list);
            prefs.edit().putString(KEY, json).apply();
        }
    }

    // Decrypt data when retrieving
    public ArrayList<PasswordModel> getAll() {
        String encryptedData = prefs.getString(KEY, null);

        if (encryptedData == null || encryptedData.isEmpty()) {
            return new ArrayList<>();
        }

        try {
            String decryptedData = decrypt(encryptedData);
            return gson.fromJson(decryptedData, new TypeToken<ArrayList<PasswordModel>>() {}.getType());
        } catch (Exception e) {
            e.printStackTrace();
            // Try to read as unencrypted data (for backward compatibility)
            try {
                return gson.fromJson(encryptedData, new TypeToken<ArrayList<PasswordModel>>() {}.getType());
            } catch (Exception ex) {
                return new ArrayList<>();
            }
        }
    }

    // AES Encryption
    private String encrypt(String data) throws Exception {
        Key key = generateKey();
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encryptedBytes = cipher.doFinal(data.getBytes("UTF-8"));
        return Base64.encodeToString(encryptedBytes, Base64.DEFAULT);
    }

    // AES Decryption
    private String decrypt(String encryptedData) throws Exception {
        Key key = generateKey();
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decodedBytes = Base64.decode(encryptedData, Base64.DEFAULT);
        byte[] decryptedBytes = cipher.doFinal(decodedBytes);
        return new String(decryptedBytes, "UTF-8");
    }

    private Key generateKey() {
        return new SecretKeySpec(ENCRYPTION_KEY.getBytes(), "AES");
    }

    // Method to clear all data
    public void clearAllData() {
        prefs.edit().clear().apply();
    }

    // Delete a specific entry
    public void deleteEntry(int position) {
        ArrayList<PasswordModel> list = getAll();
        if (position >= 0 && position < list.size()) {
            list.remove(position);
            saveAll(list);
        }
    }

    // Update an entry
    public void updateEntry(int position, PasswordModel updatedEntry) {
        ArrayList<PasswordModel> list = getAll();
        if (position >= 0 && position < list.size()) {
            list.set(position, updatedEntry);
            saveAll(list);
        }
    }

    // Add new entry
    public void addEntry(PasswordModel newEntry) {
        ArrayList<PasswordModel> list = getAll();
        list.add(newEntry);
        saveAll(list);
    }
}
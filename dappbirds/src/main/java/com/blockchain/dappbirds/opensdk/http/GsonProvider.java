package com.blockchain.dappbirds.opensdk.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonProvider {
    private Gson gson;

    private static GsonProvider instance;

    private GsonProvider() {
        gson = new GsonBuilder()
                .setLenient()
                .disableHtmlEscaping()
                .setPrettyPrinting()
                .serializeNulls()
                .create();
    }

    public static GsonProvider getInstance() {
        if (instance == null) {
            synchronized (GsonProvider.class) {
                if (instance == null) {
                    instance = new GsonProvider();
                }
            }
        }
        return instance;
    }

    public Gson getGson() {
        return gson;
    }
}
package com.blockchain.dappbirds.opensdk.http;

import android.content.Intent;
import android.text.TextUtils;

import com.blockchain.dappbirds.opensdk.DappBirdsSdk;
import com.blockchain.dappbirds.opensdk.db.AbSharedUtil;
import com.blockchain.dappbirds.opensdk.db.cache.DiskCache;
import com.blockchain.dappbirds.opensdk.utils.GeneralUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.zhy.http.okhttp.callback.Callback;

import org.json.JSONObject;

import java.lang.reflect.ParameterizedType;
import java.net.UnknownHostException;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public abstract class JsonCallback<T> extends Callback<T> {

    private long expireTime = -1L;        //缓存时间
    private Class<T> entityClass;

    public JsonCallback() {
        this(-1L);
    }

    public JsonCallback(long expireTime) {
        this.expireTime = expireTime;
        entityClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    @Override
    public void onError(Call call, Exception e, int id) {
        if (e != null) {
            if (e instanceof UnknownHostException
                    && expireTime > -1) {
                //无网络连接
                String cacheKey = getCacheKey(call.request());
                String cacheContent = DiskCache.getInstance(DappBirdsSdk.getContext()).get(cacheKey);
                if (!TextUtils.isEmpty(cacheContent)) {
                    try {

                        if (entityClass == String.class) {
                            onResponse((T) cacheContent, id);
                            return;
                        }

                        T model = GsonProvider.getInstance().getGson().fromJson(cacheContent, entityClass);

                        onResponse(model, id);
                        return;

                    } catch (Exception exception) {
                    }
                }
            }
        }
        onFail(call, e, id);
    }

    @Override
    public T parseNetworkResponse(Response response, int id) throws Exception {
        String string = response.body().string();

        //Cache
        if (expireTime > -1 && !TextUtils.isEmpty(string)) {
            String cacheKey = getCacheKey(response.request());
            DiskCache.getInstance(DappBirdsSdk.getContext()).put(cacheKey, string, expireTime);
        }

        if (entityClass == String.class) {
            return (T) string;
        }
        return GsonProvider.getInstance().getGson().fromJson(string, entityClass);
    }

    public abstract void onFail(Call call, Exception e, int id);

    /**
     * 获取缓存的key
     *
     * @param request
     * @return
     */
    private String getCacheKey(Request request) {
        String url = request.url().toString();
        RequestBody requestBody = request.body();
        if (requestBody != null) {
            MediaType mediaType = requestBody.contentType();
            if (mediaType != null) {
                if (GeneralUtils.isText(mediaType)) {
                    String bodyStr = GeneralUtils.bodyToString(request);
                    return new StringBuilder(url).append(bodyStr).toString();
                }
            }
        }
        return url;
    }
}

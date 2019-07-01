package com.blockchain.dappbirds.opensdk.http;

import android.content.Context;

import com.blockchain.dappbirds.opensdk.db.AbSharedUtil;

import java.util.HashMap;

import static com.blockchain.dappbirds.opensdk.db.Contants.key_app_id;
import static com.blockchain.dappbirds.opensdk.db.Contants.key_chain_type;
import static com.blockchain.dappbirds.opensdk.db.Contants.key_openid;

public class SetPublicParam {
    /**
     * 设置一些公共的请求参数
     *
     * @param context
     * @return 键值队
     */
    public HashMap<String, String> setSomeParam(Context context) {
        HashMap<String, String> map = new HashMap<>();
        if (context == null) {
            return map;
        } else {
            try {
                map.put(key_app_id, AbSharedUtil.getString(context, key_app_id, ""));
                map.put(key_openid, AbSharedUtil.getString(context, key_openid, ""));
                map.put(key_chain_type, AbSharedUtil.getString(context, key_chain_type, ""));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return map;
    }

    public static SetPublicParam getInstance() {
        if (mSetPublicParm == null) {
            mSetPublicParm = new SetPublicParam();
        }
        return mSetPublicParm;
    }

    private static SetPublicParam mSetPublicParm;

    private SetPublicParam() {
    }
}

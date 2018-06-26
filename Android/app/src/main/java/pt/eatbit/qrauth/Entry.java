package pt.eatbit.qrauth;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sousa on 26-06-2018.
 */

public class Entry {
    //nao sei se vale a pena fazer isto... facilita o uso mas entope memória
    public String getWebsite(String str) throws JSONException {
        return new JSONObject(str).getString("website");
    }

    public String getUsername(String str) throws JSONException {
        return new JSONObject(str).getString("website");
    }
}

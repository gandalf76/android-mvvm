package alex.com.myplaces.data.network.volley;

import android.content.Context;
import android.support.annotation.VisibleForTesting;

import com.android.volley.RequestQueue;

import alex.com.myplaces.data.network.NetworkService;

public class VolleyNetworkService implements NetworkService {

    private static VolleyNetworkService instance;

    private Context context;

    private VolleyNetworkService(Context context) {
        this.context = context;
    }

    public static VolleyNetworkService getInstance(Context context) {

        if (instance == null) {
            synchronized (VolleyNetworkService.class) {
                if (instance == null) {
                    instance = new VolleyNetworkService(context);
                }
            }
        }
        return instance;
    }

    public RequestQueue getRequestQueue() {
        return VolleySingleton.getInstance(this.context).getRequestQueue();
    }

    @VisibleForTesting
    public static void destroyInstance() {
        instance = null;
    }
}

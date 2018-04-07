package xy.richtexteditor;

import android.app.ActivityManager;
import android.app.Application;

/**
 * Created by Xieying on 2018/4/5
 * Function:
 */
public class MyApplication extends Application {

    private static MyApplication mInstance;

    private ActivityManager activityManager;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        initFresco();

    }

    private void initFresco() {


    }

    public static MyApplication getInstance() {
        if (mInstance == null)
            mInstance = new MyApplication();
        return mInstance;
    }

}

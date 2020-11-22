package udacity.gradle.builditbigger;

import android.test.AndroidTestCase;
import android.util.Log;

import junit.framework.Assert;

/**
 * Created by user on 14/8/17.
 */

public class TestString extends AndroidTestCase {

    private static final String TAG = "yes";

    public void test() {
        Log.v(TAG, "Test Has Started");
        String result = null;
            EndpointsAsyncTask endpointsAsyncTask = new EndpointsAsyncTask(getContext(), null);
            endpointsAsyncTask.execute();
            try {
                result = endpointsAsyncTask.get();
                Log.v(TAG, "String: " + result);
            } catch (Exception e) {
                Log.v(TAG,"Test failed");
                e.printStackTrace();
                 new AssertionError();
                Assert.fail();
            }
          assertNotNull(result);
    }

}
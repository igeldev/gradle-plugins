package igel.example_android_application;

import android.os.RemoteException;
import org.junit.Assert;
import org.junit.Test;

public class ExampleServiceTest {

    private final IExampleService.Stub stub = new IExampleService.Stub() {
        @Override
        public int test(String object) throws RemoteException {
            // does nothing
            return 0;
        }
    };

    @Test
    public void serviceTest() throws RemoteException {
        Assert.assertEquals(0, stub.test("Test access AIDL classes from test."));
    }

}

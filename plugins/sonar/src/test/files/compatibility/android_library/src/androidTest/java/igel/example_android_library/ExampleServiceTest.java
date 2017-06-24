package igel.example_android_library;

import android.os.RemoteException;
import org.junit.Assert;
import org.junit.Test;

public class ExampleServiceTest {

    private final IExampleService.Stub stub = new IExampleService.Stub() {
        @Override
        public String test(String object) throws RemoteException {
            // does nothing
            return null;
        }
    };

    @Test
    public void serviceTest() throws RemoteException {
        Assert.assertNull(stub.test("Test access AIDL classes from test."));
    }

}

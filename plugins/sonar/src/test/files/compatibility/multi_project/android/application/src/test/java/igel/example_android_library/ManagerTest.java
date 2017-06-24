package igel.example_android_library;

import igel.example_android_application.Manager;
import org.junit.Assert;
import org.junit.Test;

public class ManagerTest {

    @Test
    public void nullCheck() {
        Manager manager = new Manager(null);

        Assert.assertNull(manager.getContext());

        try {
            manager.getName();
            Assert.fail("manager.getName should fail");
        } catch (NullPointerException ignored) {
        }

        Assert.assertFalse(manager.getConfigBoolean());
        Assert.assertFalse(manager.getConfigName().isEmpty());
    }

}

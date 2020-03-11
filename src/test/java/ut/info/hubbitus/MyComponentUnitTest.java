package ut.info.hubbitus;

import org.junit.Test;
import info.hubbitus.api.MyPluginComponent;
import info.hubbitus.impl.MyPluginComponentImpl;

import static org.junit.Assert.assertEquals;

public class MyComponentUnitTest
{
    @Test
    public void testMyName()
    {
        MyPluginComponent component = new MyPluginComponentImpl(null);
        assertEquals("names do not match!", "myComponent", component.getName());
    }
}
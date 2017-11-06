import org.junit.Assert;
import org.junit.Test;

/**
 * Created by dude on 29.10.2017.
 */
public class Tests extends Assert{

    @Test
    public void all(){
        one();
        two();
    }


    @Test
    public void one(){
        assertEquals(2, 2);
    }

    @Test
    public void two(){
        assertEquals(3, 3);
    }




}

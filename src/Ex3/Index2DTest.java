package Ex3;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;


public class Index2DTest
{
    private Pixel2D _m0, _m1, _m2, _m3, _m4;


    @BeforeEach
    public void setup()
    {
        _m0 = new Index2D(0,0);
        _m1 = new Index2D(4,0);
        _m2 = new Index2D(_m1);
        _m3 = new Index2D(3,4);
        _m4 = new Index2D(_m2);
    }

    @Test
    void testDistance()
    {
        assertEquals(_m1.getX(), _m0.distance2D(_m1));
        assertEquals(5, _m3.distance2D(_m0));
    }

    @Test
    void testToString()
    {
        assertEquals("3,4", _m3.toString());
        assertEquals("4,0", _m2.toString());
    }

    @Test
    void testEquals()
    {
        assertEquals(_m1, _m2);
        assertEquals(_m2, _m1);
        assertEquals(_m4, _m2);
        assertEquals(_m1, _m4);
        assertNotEquals(null, _m0);
    }
}

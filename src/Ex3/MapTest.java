package Ex3;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
/**
 * Intro2CS, 2026A, this is a very
 */
class MapTest
{
    private final int[][] _map_3x3_1 = {{0,1,0}, {1,0,1}, {0,1,0}};
    private final int[][] _map_4x4_1 = {{0,1,0,0}, {0,1,0,0}, {0,1,1,0}, {0,0,0,0}};
    private final int[][] _map_4x4_2 = {{0,0,0,0}, {0,0,0,0}, {0,0,0,0}, {0,0,0,0}};
    private final int[][] _map_4x4_3 = {{0,1,0,0}, {3,1,0,0}, {2,1,1,1}, {2,4,10,6}};
    private Map2D _m0, _m1, _m2, _m3;

    @BeforeEach
    public void setup()
    {
        _m0 = new Map(_map_4x4_1);
        _m1 = new Map(_map_4x4_2);
        _m2 = new Map(_map_4x4_3);
        _m3 = new Map(4);
    }

    @Test
    void testIsInside()
    {
        Pixel2D p1 = new Index2D(-1,2);
        Pixel2D p2 = new Index2D(2,0);
        Pixel2D p3 = new Index2D(3,5);

        assertFalse(_m0.isInside(p1));
        assertTrue(_m0.isInside(p2));
        assertFalse(_m0.isInside(p3));
        assertThrowsExactly(NullPointerException.class, () -> _m0.isInside(null));
    }

    @Test
    void testAddMap2D()
    {
        int[][] arr1 = {{1,1,1,1}, {1,1,1,1}, {1,1,1,1}, {1,1,1,1}};

        _m2.init(arr1);
        _m3.addMap2D(_m2);

        assertEquals(_m2, _m3);
        assertThrowsExactly(NullPointerException.class, () -> _m0.addMap2D(null));
    }

    @Test
    void testMul()
    {
        int[][] arr1 = {{1,1,1,1}, {1,1,1,1}, {1,1,1,1}, {1,1,1,1}};

        _m0.init(arr1);
        _m1.init(arr1);
        _m2.init(arr1);
        _m0.mul(5.5);
        _m1.mul(0.5);
        _m2.mul(5);

        assertEquals(_m0, _m2);
        assertEquals(_m1, _m3);
    }

    @Test
    void testEquals()
    {
        assertEquals(_m3,_m1);
        assertNotEquals(_m0,_m1);
        assertNotEquals(null, _m0);
    }

    @Test
    void testShortestPath()
    {
        Pixel2D[] resArr1 = {new Index2D(0,0),new Index2D(0,1),new Index2D(0,2),new Index2D(0,3), new Index2D(1,3)
                ,new Index2D(2,3),new Index2D(3,3),new Index2D(3,2),new Index2D(3,1),new Index2D(3,0)};
        Pixel2D[] resArr2 = {new Index2D(3,0),new Index2D(3,1),new Index2D(3,2),new Index2D(3,3), new Index2D(2,3)
                ,new Index2D(1,3),new Index2D(0,3)};
        Pixel2D[] resArr4 = {new Index2D(0,0),new Index2D(3,0)};

        ArrayList<Pixel2D> res1 = new ArrayList<>(Arrays.asList(resArr1));
        ArrayList<Pixel2D> res2 = new ArrayList<>(Arrays.asList(resArr2));
        ArrayList<Pixel2D> res3 = null;
        ArrayList<Pixel2D> res4 = new ArrayList<>(Arrays.asList(resArr4));

        _m0.setCyclic(false);
        _m1.setCyclic(false);
        _m2.setCyclic(false);

        assertTrue(isEqualArray(res1, _m0.shortestPath(new Index2D(0, 0), new Index2D(3, 0), 1)));
        assertTrue(isEqualArray(res2, _m1.shortestPath(new Index2D(3, 0), new Index2D(0, 3), 1)));
        assertTrue(isEqualArray(res3, _m2.shortestPath(new Index2D(0, 0), new Index2D(3, 0), 1)));

        _m2.setCyclic(true);
        assertTrue(isEqualArray(res4, _m2.shortestPath(new Index2D(0, 0), new Index2D(3, 0), 1)));
    }

    @Test
    void testAllDistance()
    {
        int[][] resArr1 = {{0,-1,10,9}, {1,-1,9,8}, {2,-1,-1,7}, {3,4,5,6}};
        int[][] resArr2 = {{0,1,2,3}, {1,2,3,4}, {2,3,4,5}, {3,4,5,6}};
        int[][] resArr3 = {{0,-1,-1,-1}, {1,-1,-1,-1}, {2,-1,-1,-1}, {3,4,5,6}};
        int[][] resArr4 = {{0, -1, 2, 1}, {1, -1, 3, 2}, {2, -1, -1, -1}, {1, 2, 3, 2}};

        Map2D res1 = new Map(resArr1);
        Map2D res2 = new Map(resArr2);
        Map2D res3 = new Map(resArr3);
        Map2D res4 = new Map(resArr4);

        _m0.setCyclic(false);
        _m1.setCyclic(false);
        _m2.setCyclic(false);

        assertEquals(res1, _m0.allDistance(new Index2D(0, 0), 1));
        assertEquals(res2, _m1.allDistance(new Index2D(0, 0), 1));
        assertEquals(res3, _m2.allDistance(new Index2D(0, 0), 1));

        _m2.setCyclic(true);
        assertEquals(res4, _m2.allDistance(new Index2D(0, 0), 1));
    }

    boolean isEqualArray(ArrayList<Pixel2D> arr1, ArrayList<Pixel2D> arr2)
    {
        if(arr1 == null || arr2 == null)
        {
            return arr1 == null && arr2 == null;
        }
        if (arr1.size() != arr2.size())
        {
            return false;
        }
        for (int i = 0; i < arr1.size(); i++)
        {
            if (!arr1.get(i).equals(arr2.get(i)))
            {
                return false;
            }
        }
        return true;
    }
}
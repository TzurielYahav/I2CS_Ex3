package ClientSide;

/**
 * This interface represents an integer based coordinate of a 2D raster (aka a 2D matrix).
 */
public interface Pixel2D
{
    /**
     * @return the X coordinate (integer) of the pixel.
     */
    int getX();
    /**
     * @return the Y coordinate (integer) of the pixel.
     */
    int getY();

    /**
     * This method computes the 2D (Euclidean) distance beteen this pixel and p2 pixel, i.e., (Math.sqrt(dx*dx+dy*dy))
     * @throws RuntimeException if p2==null.
     * @return the 2D Euclidean distance between the pixels.
     */
    double distance2D(Pixel2D p2);

    /**
     *
     * @return a String representation of this coordinate.
     */
    String toString();

}

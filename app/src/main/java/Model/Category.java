package Model;

/**
 * Created by Leyond on 19/1/2017.
 */

public class Category {
    private String name;
    private String encodedimage;

    public Category() {

    }

    public Category(String name, String encodedimage) {
        this.name = name;
        this.encodedimage = encodedimage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEncodedimage() {
        return encodedimage;
    }

    public void setEncodedimage(String encodedimage) {
        this.encodedimage = encodedimage;
    }
}

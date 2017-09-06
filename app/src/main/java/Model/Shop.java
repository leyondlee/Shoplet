package Model;

import java.io.Serializable;

/**
 * Created by Leyond on 14/1/2017.
 */

public class Shop implements Serializable {
    private String name;
    private String contact;
    private String postalcode;
    private String unitno;
    private String category;
    private String description;
    private String encodedimage;

    public Shop() {

    }

    public Shop(String name, String contact, String postalcode, String unitno, String category, String description, String encodedimage) {
        this.name = name;
        this.contact = contact;
        this.postalcode = postalcode;
        this.unitno = unitno;
        this.category = category;
        this.description = description;
        this.encodedimage = encodedimage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getPostalcode() {
        return postalcode;
    }

    public void setPostalcode(String postalcode) {
        this.postalcode = postalcode;
    }

    public String getUnitno() {
        return unitno;
    }

    public void setUnitno(String unitno) {
        this.unitno = unitno;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEncodedimage() {
        return encodedimage;
    }

    public void setEncodedimage(String encodedimage) {
        this.encodedimage = encodedimage;
    }
}

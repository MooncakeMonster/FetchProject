package mooncakemonster.orbitalcalendar.picoftheday;

/**
 * This class creates items for image storage.
 */
public class PictureItem {
    private int id;
    private String title, date, caption, image;

    public PictureItem() {

    }

    public PictureItem(int id, String title, String date, String caption, String image) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.caption = caption;
        this.image = image;
    }

    public int getID() {
        return id;
    }
    
    public void setID() {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle() {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate() {
        this.date = date;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption() {
        this.caption = caption;
    }

    public String getImage() {
        return image;
    }

    public void setImage() {
        this.image = image;
    }
}

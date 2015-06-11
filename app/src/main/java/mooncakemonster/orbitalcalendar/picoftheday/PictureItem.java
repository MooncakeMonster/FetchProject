package mooncakemonster.orbitalcalendar.picoftheday;

/**
 * Created by BAOJUN on 11/6/15.
 */
public class PictureItem {
    private int id, image;
    private String title, date, caption;

    public PictureItem() {

    }

    public PictureItem(String title, String date, String caption, int image) {
        this.title = title;
        this.date = date;
        this.caption = caption;
        this.image = image;
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

    public int getImage() {
        return image;
    }

    public void setImage() {
        this.image = image;
    }
}

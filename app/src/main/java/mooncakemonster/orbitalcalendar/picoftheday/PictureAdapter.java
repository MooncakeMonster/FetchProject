package mooncakemonster.orbitalcalendar.picoftheday;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import mooncakemonster.orbitalcalendar.R;

/**
 * Created by BAOJUN on 11/6/15.
 */
public class PictureAdapter extends ArrayAdapter{
    private List list = new ArrayList();

    public PictureAdapter(Context context, int resource) {
        super(context, resource);
    }

    public void add(PictureItem object) {
        list.add(object);
        super.add(object);
    }

    static class ImgHolder {
        ImageView pic_icon;
        TextView pic_title;
        TextView pic_date;
        TextView pic_caption;
        ImageView pic_image;
    }

    @Override
    public int getCount() {
        return this.list.size();
    }

    @Override
    public Object getItem(int position) {
        return this.list.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ImgHolder holder;

        if(convertView == null) {
            LayoutInflater inflator = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflator.inflate(R.layout.row_feed, parent, false);
            holder = new ImgHolder();

            //holder.pic_icon = (ImageView) row.findViewById(R.id.pic_icon);
            holder.pic_title = (TextView) row.findViewById(R.id.pic_title);
            holder.pic_date = (TextView) row.findViewById(R.id.pic_timestamp);
            holder.pic_caption = (TextView) row.findViewById(R.id.pic_caption);
            holder.pic_image = (ImageView) row.findViewById(R.id.pic_image);

            row.setTag(holder);
        }

        else holder = (ImgHolder) row.getTag();

        PictureItem picture = (PictureItem) getItem(position);
        //holder.pic_icon.setImageResource(picture.getImage());
        holder.pic_image.setImageResource(picture.getImage());
        holder.pic_title.setText(picture.getTitle());
        holder.pic_date.setText(picture.getDate());
        holder.pic_caption.setText(picture.getCaption());

        return row;
    }
}

package mooncakemonster.orbitalcalendar.friendlist;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.ByteArrayOutputStream;
import java.util.List;

import mooncakemonster.orbitalcalendar.R;
import mooncakemonster.orbitalcalendar.database.Constant;

/**
 * Created by BAOJUN on 6/7/15.
 */
public class FriendlistAdapter extends ArrayAdapter<FriendItem> {

    private List<FriendItem> objects;

    public FriendlistAdapter(Context context, int resource, List<FriendItem> objects) {
        super(context, resource, objects);
        this.objects = objects;
    }

    static class Holder {
        ImageView friend_image;
        TextView friend_username;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        Holder holder;

        if(row == null) {
            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.row_friendlist, parent, false);
        }

        FriendItem friend = objects.get(position);

        if(friend != null) {
            holder = new Holder();
            holder.friend_image = (ImageView) row.findViewById(R.id.friend_image);
            holder.friend_username = (TextView) row.findViewById(R.id.friend_username);

            Bitmap bitmap = Constant.bytesToBitmap(friend.getImage());
            Drawable drawable = new BitmapDrawable(getContext().getResources(), bitmap);
            DisplayImageOptions options = new DisplayImageOptions.Builder()
                    .showImageOnLoading(drawable)
                    .showImageForEmptyUri(R.drawable.profile)
                    .showImageOnFail(R.drawable.profile)
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .considerExifParams(true)
                    .build();

            ImageLoader.getInstance().displayImage(getImageUri(getContext(), bitmap).toString(), holder.friend_image, options);
            holder.friend_username.setText(friend.getUsername());

            row.setTag(holder);
        }

        return row;
    }

    // This method converts bitmap to URI.
    public Uri getImageUri(Context context, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
}

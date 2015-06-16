package mooncakemonster.orbitalcalendar.picoftheday;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;

import mooncakemonster.orbitalcalendar.R;

/**
 * This class displays the images uploaded by users in listview.
 */
public class PictureFragment extends Fragment {

    private ListView listView;
    private int smiley_id;
    private String title, date, caption, image;
    private ImageButton addPicButton;
    private SwipeRefreshLayout swipeRefreshLayout;
    private PictureAdapter adapter;
    private TableDatabase tableDatabase;
    private Cursor cursor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_picture, container, false);

        // Set listview
        listView = (ListView) rootView.findViewById(R.id.piclistView);
        adapter = new PictureAdapter(getActivity().getApplicationContext(), R.layout.row_feed);
        listView.setAdapter(adapter);

        // Retrieve data from database
        tableDatabase = new TableDatabase(getActivity());
        // Get rows of database
        cursor = tableDatabase.getInformation(tableDatabase);

        // Start from the last so that listview displays latest image first
        // Check for existing rows
        if(cursor.moveToLast()) {
            do {
                // Get items from each column
                smiley_id = cursor.getInt(0);
                title = cursor.getString(1);
                date = cursor.getString(2);
                caption = cursor.getString(3);
                image = cursor.getString(4);

                // Saves images added by user into listview
                PictureItem pictureItem = new PictureItem(smiley_id, title, date, caption, Uri.parse(image));
                adapter.add(pictureItem);
            } while (cursor.moveToPrevious());
        }

        cursor.close();

        // Lead user to CreatePicture activity to insert image to listview
        addPicButton = (ImageButton) rootView.findViewById(R.id.addPictureButton);
        addPicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity().getApplicationContext(), CreatePicture.class));
            }
        });

        return rootView;
    }

}

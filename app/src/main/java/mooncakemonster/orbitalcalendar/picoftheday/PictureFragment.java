package mooncakemonster.orbitalcalendar.picoftheday;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;

import mooncakemonster.orbitalcalendar.R;

/**
 * This class displays the images uploaded by users in listview.
 */
public class PictureFragment extends Fragment{

    private ListView listView;
    int smiley_id;
    String title, date, caption, image;


    private ImageButton addPicButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_picture, container, false);

        //Set listview
        listView = (ListView) rootView.findViewById(R.id.piclistView);
        PictureAdapter adapter = new PictureAdapter(getActivity().getApplicationContext(), R.layout.row_feed);
        listView.setAdapter(adapter);

        // Retrieve data from database
        TableDatabase tableDatabase = new TableDatabase(getActivity());

        // Get rows of database
        Cursor cursor = tableDatabase.getInformation(tableDatabase);

        // Check for existing rows
        while(cursor.moveToNext()) {
            // Get items from each column
            smiley_id = cursor.getInt(0);
            title = cursor.getString(1);
            date = cursor.getString(2);
            caption = cursor.getString(3);
            image = cursor.getString(4);

            // Saves images added by user into listview
            PictureItem pictureItem = new PictureItem(smiley_id, title, date, caption, image);
            adapter.add(pictureItem);
        }

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

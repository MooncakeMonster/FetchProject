package mooncakemonster.orbitalcalendar.picoftheday;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import mooncakemonster.orbitalcalendar.R;

/**
 * Created by BAOJUN on 11/6/15.
 */
public class PictureFragment extends Fragment{

    private ListView listView;
    //int[] imgResource1 = { R.mipmap.red, R.mipmap.blue, R.mipmap.green, R.mipmap.purple, R.mipmap.orange };
    String[] picTitle = { "Good day", "Good day", "Good day", "Good day", "Good day" };
    String[] picDate = { "1pm", "2pm", "3pm", "4am", "Whole day"};
    String[] picCaption = { "BBQ", "Birthday", "Meeting", "Sleep", "Slack" };
    int[] imgResource2 = { R.drawable.purplesky, R.drawable.purplesky, R.drawable.purplesky, R.drawable.purplesky, R.drawable.purplesky };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_picture, container, false);

        //Get selected date's event list
        listView = (ListView) rootView.findViewById(R.id.piclistView);
        PictureAdapter adapter = new PictureAdapter(getActivity().getApplicationContext(), R.layout.row_feed);
        listView.setAdapter(adapter);

        int i = 0;
        for(String Name: picTitle) {
            PictureItem event = new PictureItem(Name, picDate[i], picCaption[i], imgResource2[i]);
            adapter.add(event);
            i++;
        }


        return rootView;
    }
}

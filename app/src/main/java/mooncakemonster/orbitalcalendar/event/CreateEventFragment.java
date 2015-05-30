package mooncakemonster.orbitalcalendar.event;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class CreateEventFragment extends DialogFragment {

    private FragmentActivity myContext;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        //TODO;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = null;// = inflater.inflate(CreateEventFragment.xml);
        getDialog().setTitle("Create A New Event");
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        myContext = (FragmentActivity) activity;
        super.onAttach(activity);
    }
}

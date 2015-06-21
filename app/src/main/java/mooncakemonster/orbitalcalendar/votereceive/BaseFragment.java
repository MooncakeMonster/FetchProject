package mooncakemonster.orbitalcalendar.votereceive;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.MenuItem;

/**
 * Created by BAOJUN on 21/6/15.
 */
public class BaseFragment extends ListFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                getActivity().onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

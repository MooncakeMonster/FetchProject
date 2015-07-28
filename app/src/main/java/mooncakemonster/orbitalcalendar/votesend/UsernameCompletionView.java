package mooncakemonster.orbitalcalendar.votesend;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tokenautocomplete.TokenCompleteTextView;

import mooncakemonster.orbitalcalendar.R;

/**
 * Created by BAOJUN on 28/7/15.
 */
public class UsernameCompletionView extends TokenCompleteTextView<String> {

    public UsernameCompletionView(Context context) {
        super(context);
    }

    public UsernameCompletionView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public UsernameCompletionView(Context context, AttributeSet attributeSet, int defStyle) {
        super(context, attributeSet, defStyle);
    }

    @Override
    protected View getViewForObject(String s) {
        LayoutInflater l = (LayoutInflater)getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        LinearLayout view = (LinearLayout)l.inflate(R.layout.row_usernameview, (ViewGroup)UsernameCompletionView.this.getParent(), false);
        ((TextView)view.findViewById(R.id.name)).setText(s);

        return view;
    }

    @Override
    protected String defaultObject(String s) {
        return s;
    }
}

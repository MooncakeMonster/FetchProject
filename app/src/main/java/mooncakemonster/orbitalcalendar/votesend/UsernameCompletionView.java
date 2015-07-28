package mooncakemonster.orbitalcalendar.votesend;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.tokenautocomplete.TokenCompleteTextView;

import mooncakemonster.orbitalcalendar.R;
import mooncakemonster.orbitalcalendar.cloudant.CloudantConnect;
import mooncakemonster.orbitalcalendar.database.Constant;
import mooncakemonster.orbitalcalendar.friendlist.FriendDatabase;
import mooncakemonster.orbitalcalendar.profilepicture.RoundImage;

/**
 * Created by BAOJUN on 28/7/15.
 */
public class UsernameCompletionView extends TokenCompleteTextView<String> {
    private CloudantConnect cloudantConnect;
    private FriendDatabase friendDatabase;

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

        SimpleDraweeView simpleDraweeView = (SimpleDraweeView)view.findViewById(R.id.username_image);
        if (cloudantConnect == null)
            this.cloudantConnect = new CloudantConnect(getContext(), "user");

        friendDatabase = new FriendDatabase(getContext());

        try {
            if (friendDatabase.checkUsername(friendDatabase, s)) {
                RoundImage roundImage = new RoundImage(cloudantConnect.retrieveUserImage(s));
                simpleDraweeView.setImageDrawable(roundImage);
            } else {
                Constant.alertUser(getContext(), "Invalid username", "Please ensure that the username you entered is valid.");
            }
        } catch (Exception e) {
            Log.e(TAG, "OOM error");
        }

        ((TextView) view.findViewById(R.id.username_name)).setText(s);

        return view;
    }

    @Override
    protected String defaultObject(String s) {
        return s;
    }
}

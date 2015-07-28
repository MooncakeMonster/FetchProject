package mooncakemonster.orbitalcalendar.friendsfeed;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import it.carlom.stikkyheader.core.StikkyHeaderBuilder;
import it.carlom.stikkyheader.core.animator.AnimatorBuilder;
import it.carlom.stikkyheader.core.animator.HeaderStikkyAnimator;
import mooncakemonster.orbitalcalendar.R;
import mooncakemonster.orbitalcalendar.cloudant.CloudantConnect;
import mooncakemonster.orbitalcalendar.friendlist.FriendItem;
import mooncakemonster.orbitalcalendar.notifications.NotificationDatabase;
import mooncakemonster.orbitalcalendar.notifications.NotificationItem;

public class FriendsFeedActivity extends Activity {

    private static final String TAG = FriendsFeedActivity.class.getSimpleName();
    private NotificationDatabase notificationDatabase;
    private List<NotificationItem> allNotifications;
    private CloudantConnect cloudantConnect;
    private ListView listView;
    private FriendsFeedAdapter adapter;
    private FriendItem friendItem;
    private TextView friendsfeed_empty;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friendsfeed);

        Fresco.initialize(this);

        Intent intent = getIntent();
        final Bundle bundle = intent.getExtras();
        friendItem = (FriendItem) bundle.getSerializable("friend_item");

        notificationDatabase = new NotificationDatabase(this);
        // Get all notifications
        allNotifications = notificationDatabase.getUserNotifications(notificationDatabase, friendItem.getUsername());
        // Get adapter view
        adapter = new FriendsFeedAdapter(this, R.layout.row_notifications, allNotifications);
        adapter.clear();
        adapter.addAll(notificationDatabase.getAllNotifications(notificationDatabase));
        listView = (ListView) findViewById(R.id.friendsfeed_list);
        listView.setAdapter(adapter);

        friendsfeed_empty = (TextView) findViewById(R.id.friendsfeed_empty);
        if(allNotifications.size() > 0) friendsfeed_empty.setVisibility(View.INVISIBLE);

        StikkyHeaderBuilder.stickTo(listView)
                .setHeader(R.id.header, (ViewGroup) findViewById(android.R.id.content))
                .minHeightHeader(500)
                .animator(new ParallaxStikkyAnimator())
                .build();
    }

    private class ParallaxStikkyAnimator extends HeaderStikkyAnimator {

        @Override
        public AnimatorBuilder getAnimatorBuilder() {
            View header_image = getHeader().findViewById(R.id.header_image);

            if (cloudantConnect == null) cloudantConnect = new CloudantConnect(getApplicationContext(), "user");

            SimpleDraweeView imageView = (SimpleDraweeView) findViewById(R.id.header_image);
            imageView.setImageBitmap(cloudantConnect.retrieveUserImage(friendItem.getUsername()));

            return AnimatorBuilder.create().applyVerticalParallax(header_image)
                    .applyFade(getHeader().findViewById(R.id.header_image), 0f);
        }
    }
}

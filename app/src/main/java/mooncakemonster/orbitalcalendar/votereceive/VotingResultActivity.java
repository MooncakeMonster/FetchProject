package mooncakemonster.orbitalcalendar.votereceive;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.tjerkw.slideexpandable.library.SlideExpandableListAdapter;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import mooncakemonster.orbitalcalendar.R;
import mooncakemonster.orbitalcalendar.cloudant.CloudantConnect;
import mooncakemonster.orbitalcalendar.database.Constant;
import mooncakemonster.orbitalcalendar.voteresult.ResultAdapter;
import mooncakemonster.orbitalcalendar.voteresult.ResultDatabase;
import mooncakemonster.orbitalcalendar.voteresult.ResultItem;
import mooncakemonster.orbitalcalendar.votesend.VoteItem;
import mooncakemonster.orbitalcalendar.votesend.VotingDatabase;

/**
 * Created by BAOJUN on 12/7/15.
 */
public class VotingResultActivity extends ActionBarActivity {

    CloudantConnect cloudantConnect;

    VotingDatabase votingDatabase;
    ResultDatabase resultDatabase;

    private ListView listView;
    ResultAdapter resultAdapter;
    private List<ResultItem> list;

    private TextView event_title, event_location;
    private Button sort_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voting_result);

        getSupportActionBar().setElevation(0);

        if (cloudantConnect == null)
            this.cloudantConnect = new CloudantConnect(this, "user");

        votingDatabase = new VotingDatabase(this);
        resultDatabase = new ResultDatabase(this);

        resultDatabase = new ResultDatabase(this);

        //(1) Get intent that started this activity
        Intent intent = getIntent();
        final Bundle bundle = intent.getExtras();
        final VoteItem voteItem = (VoteItem) bundle.getSerializable("voteItem");

        if(voteItem != null) {
            View header = getLayoutInflater().inflate(R.layout.header_voting_result, null);
            listView = (ListView) findViewById(R.id.vote_result_list);
            listView.addHeaderView(header);
            resultAdapter = new ResultAdapter(this, R.layout.row_vote_result,
                    resultDatabase.getAllTargetResults(resultDatabase, Integer.parseInt(voteItem.getEventId())));
            listView.setAdapter(new SlideExpandableListAdapter(resultAdapter, R.id.confirm_send_date, R.id.expandable_vote_result));
            resultAdapter.notifyDataSetChanged();

            event_title = (TextView) header.findViewById(R.id.vote_result_title);
            event_location = (TextView) header.findViewById(R.id.vote_result_location);
            sort_list = (Button) header.findViewById(R.id.vote_result_sort);

            event_title.setText(voteItem.getEvent_title());
            event_location.setText("@ " + voteItem.getEvent_location());
        }

        // Sort the list according to user's preference
        sort_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] sort_type = {"Sort by total votes", "Sort by date", "Sort by time"};
                list = resultDatabase.getAllTargetResults(resultDatabase, Integer.parseInt(voteItem.getEventId()));

                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(VotingResultActivity.this);
                alertBuilder.setTitle("Sort Voting List").setSingleChoiceItems(sort_type, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch(which) {
                            case 0:
                                // (1) Sort according to total votes
                                Collections.sort(list, new Comparator<ResultItem>() {
                                    @Override
                                    public int compare(ResultItem lhs, ResultItem rhs) {
                                        int this_total = Integer.parseInt(lhs.getTotal());
                                        int that_total = Integer.parseInt(rhs.getTotal());

                                        if(this_total < that_total) return -1;
                                        else if(this_total > that_total) return 1;

                                        return 0;
                                    }
                                });

                                resultAdapter.clear();
                                resultAdapter.addAll(list);
                                resultAdapter.notifyDataSetChanged();
                                break;
                            case 1:
                                // (2) Sort according to date
                                Collections.sort(list, new Comparator<ResultItem>() {
                                    @Override
                                    public int compare(ResultItem lhs, ResultItem rhs) {
                                        long this_start_date = Constant.stringToMillisecond(lhs.getStart_date(), lhs.getStart_time(), Constant.DATEFORMATTER, Constant.TIMEFORMATTER);
                                        long that_start_date = Constant.stringToMillisecond(rhs.getStart_date(), rhs.getStart_time(), Constant.DATEFORMATTER, Constant.TIMEFORMATTER);

                                        if(this_start_date < that_start_date) return -1;
                                        else if(this_start_date > that_start_date) return 1;

                                        return 0;
                                    }
                                });

                                resultAdapter.clear();
                                resultAdapter.addAll(list);
                                resultAdapter.notifyDataSetChanged();
                                break;
                            case 2:
                                // (3) Sort according to time
                                Collections.sort(list, new Comparator<ResultItem>() {
                                    @Override
                                    public int compare(ResultItem lhs, ResultItem rhs) {
                                        long this_start_time = Constant.stringToMillisecond(lhs.getStart_time(), Constant.TIMEFORMATTER);
                                        long that_start_time = Constant.stringToMillisecond(rhs.getStart_time(), Constant.TIMEFORMATTER);

                                        if (this_start_time < that_start_time) return -1;
                                        else if (this_start_time > that_start_time) return 1;

                                        return 0;
                                    }
                                });

                                resultAdapter.clear();
                                resultAdapter.addAll(list);
                                resultAdapter.notifyDataSetChanged();
                                break;
                        }
                        dialog.dismiss();
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                Dialog dialog = alertBuilder.create();
                dialog.show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_cross, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_close) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

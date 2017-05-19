package com.example.theopsyphertxt.tibbit;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.List;

/**
 * Created by TheoPsyphertxt on 23/03/2017.
 */

public class FriendsFragment extends ListFragment {

    public final static String TAG = FriendsFragment.class.getSimpleName ();

    protected ParseRelation<ParseUser> mFriendRelations;
    protected List<ParseUser> mFriends;
    protected ParseUser mCurrentUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate (R.layout.fragment_friends, container, false);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume ();

        mCurrentUser = ParseUser.getCurrentUser ();
        mFriendRelations = mCurrentUser.getRelation (ParseConstants.KEY_FRIENDS_RELATION);
        ParseQuery<ParseUser> query = mFriendRelations.getQuery ();
        query.addAscendingOrder (ParseConstants.KEY_USERNAME);
        query.findInBackground (new FindCallback<ParseUser> () {
            @Override
            public void done(List<ParseUser> friends, ParseException e) {
                if (e == null) {
                    mFriends = friends;
                    String [] usernames = new String [mFriends.size ()];
                    int i = 0;
                    for (ParseUser user:mFriends) {
                        usernames[i] = user.getUsername ();
                        i++;
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String> (
                            getListView ().getContext (), android.R.layout.simple_list_item_1, usernames);
                    setListAdapter(adapter);
                }
                else {
                    Log.e (TAG,e.getMessage ());
                    AlertDialog.Builder builder = new AlertDialog.Builder(getListView ().getContext () );
                    builder.setMessage(e.getMessage())
                            .setTitle(R.string.error_title)
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });
    }
}

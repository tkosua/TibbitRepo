package com.example.theopsyphertxt.tibbit;

import android.app.ListActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

public class EditFriends extends ListActivity {

    public static final String TAG = EditFriends.class.getSimpleName ();

    protected ParseRelation<ParseUser> mFriendRelations;
    protected List<ParseUser> mUsers;
    protected ParseUser mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_edit_friends);

        getListView ().setChoiceMode (ListView.CHOICE_MODE_MULTIPLE);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mCurrentUser = ParseUser.getCurrentUser ();
        mFriendRelations = mCurrentUser.getRelation (ParseConstants.KEY_FRIENDS_RELATION);

        ParseQuery<ParseUser> query = ParseUser.getQuery ();
        query.orderByAscending (ParseConstants.KEY_USERNAME);
        query.setLimit (1000);
        query.findInBackground (new FindCallback<ParseUser> () {
            @Override
            public void done(List<ParseUser> users, ParseException e) {
                if(e == null) {
                    //Success
                    mUsers = users;
                    String [] usernames = new String [mUsers.size ()];
                    int i = 0;
                    for (ParseUser user:mUsers) {
                        usernames[i] = user.getUsername ();
                        i++;
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String> (
                            EditFriends.this, android.R.layout.simple_list_item_checked, usernames);
                    setListAdapter(adapter);

                    addFriendCheckmarks();
                }
                else {
                    Log.e (TAG,e.getMessage ());
                    AlertDialog.Builder builder = new AlertDialog.Builder(EditFriends.this);
                    builder.setMessage(e.getMessage())
                            .setTitle(R.string.error_title)
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });
    }

    @Override
    protected void onListItemClick(ListView l, View v, int postion, long id) {
        super.onListItemClick (l, v, postion, id);

        if(getListView().isItemChecked(postion)) {
            //add friend
            mFriendRelations.add (mUsers.get(postion)); }
        else {
            //remove friend
            mFriendRelations.remove (mUsers.get (postion)); }
        mCurrentUser.saveInBackground (new SaveCallback () {
            @Override
            public void done(ParseException e) {
                if (e != null)
                    Log.e (TAG, e.getMessage ());
            }
        });
    }

    private void addFriendCheckmarks() {
     mFriendRelations.getQuery ().findInBackground (new FindCallback<ParseUser> () {
         @Override
         public void done(List<ParseUser> friends, ParseException e) {
            if (e == null) {
                //list returned - look for a match
                for (int i=0; i<mUsers.size(); i++) {
                    ParseUser user = mUsers.get(i);
                    // above loops through users(friends list)
                    for(ParseUser friend: friends ) {
                        if (friend.getObjectId ().equals (user.getObjectId ()))
                            getListView ().setItemChecked (i, true);
                    }
                }
            }
             else {
                Log.e (TAG, e.getMessage ());
            }
         }
     });
    }

}

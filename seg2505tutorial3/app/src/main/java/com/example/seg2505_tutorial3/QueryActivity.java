package com.example.seg2505_tutorial3;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class QueryActivity extends AppCompatActivity {

    private static final String TAG = "QueryActivity";

    private DatabaseReference databaseReference;
    private DatabaseReference mMessagesRef;
    private Query mMessagesQuery;

    private ValueEventListener mMessagesListener;
    private ChildEventListener mMessagesQueryListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query);

        // Get a reference to the Firebase Database
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    public String getUid() {
        return "42";
    }

    public void basicListen() {
        // [START basic_listen]
        // Get a reference to Messages and attach a listener
        mMessagesRef = databaseReference.child("messages");
        mMessagesListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // New data at this path. This method will be called after every change in the
                // data at this path or a subpath.

                // Get the data as Message objects
                Log.d(TAG, "Number of messages: " + dataSnapshot.getChildrenCount());
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    // Extract a Message object from the DataSnapshot
                    Message message = child.getValue(Message.class);

                    // Use the Message
                    // [START_EXCLUDE]
                    Log.d(TAG, "message text:" + message.getText());
                    Log.d(TAG, "message sender name:" + message.getName());
                    // [END_EXCLUDE]
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Could not successfully listen for data, log the error
                Log.e(TAG, "messages:onCancelled:" + error.getMessage());
            }
        };
        mMessagesRef.addValueEventListener(mMessagesListener);
        // [END basic_listen]
    }

    public void basicQuery() {
        // [START basic_query]
        // My top posts by number of stars
        String myUserId = getUid();
        Query myTopPostsQuery = databaseReference.child("user-posts").child(myUserId)
                .orderByChild("starCount");
        myTopPostsQuery.addChildEventListener(new ChildEventListener() {
            // TODO: implement the ChildEventListener methods as documented above
            // [START_EXCLUDE]
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) { }
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s) { }
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) { }
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String s) { }
            public void onCancelled(@NonNull DatabaseError databaseError) { }
            // [END_EXCLUDE]
        });
        // [END basic_query]
    }

    public void basicQueryValueListener() {
        String myUserId = getUid();
        Query myTopPostsQuery = databaseReference.child("user-posts").child(myUserId)
                .orderByChild("starCount");

        // [START basic_query_value_listener]
        // My top posts by number of stars
        myTopPostsQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    // TODO: handle the post
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        });
        // [END basic_query_value_listener]
    }

    public void cleanBasicListener() {
        // Clean up value listener
        // [START clean_basic_listen]
        mMessagesRef.removeEventListener(mMessagesListener);
        // [END clean_basic_listen]
    }

    public void cleanBasicQuery() {
        // Clean up query listener
        // [START clean_basic_query]
        mMessagesQuery.removeEventListener(mMessagesQueryListener);
        // [END clean_basic_query]
    }

    public void orderByNested() {
        // [START rtdb_order_by_nested]
        // Most viewed posts
        Query myMostViewedPostsQuery = databaseReference.child("posts")
                .orderByChild("metrics/views");
        myMostViewedPostsQuery.addChildEventListener(new ChildEventListener() {
            // TODO: implement the ChildEventListener methods as documented above
            // [START_EXCLUDE]
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {}

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
            // [END_EXCLUDE]
        });
        // [END rtdb_order_by_nested]
    }

    private void childEventListenerRecycler() {
        final Context mContext = this;
        // [START child_event_listener_recycler]
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());

                // A new comment has been added, add it to the displayed list
                Comment comment = dataSnapshot.getValue(Comment.class);

                // ...
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());

                // A comment has changed, use the key to determine if we are displaying this
                // comment and if so displayed the changed comment.
                Comment newComment = dataSnapshot.getValue(Comment.class);
                String commentKey = dataSnapshot.getKey();

                // ...
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());

                // A comment has changed, use the key to determine if we are displaying this
                // comment and if so remove it.
                String commentKey = dataSnapshot.getKey();

                // ...
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildMoved:" + dataSnapshot.getKey());

                // A comment has changed position, use the key to determine if we are
                // displaying this comment and if so move it.
                Comment movedComment = dataSnapshot.getValue(Comment.class);
                String commentKey = dataSnapshot.getKey();

                // ...
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "postComments:onCancelled", databaseError.toException());
                Toast.makeText(mContext, "Failed to load comments.",
                        Toast.LENGTH_SHORT).show();
            }
        };
        databaseReference.addChildEventListener(childEventListener);
        // [END child_event_listener_recycler]
    }

    private void recentPostsQuery() {
        // [START recent_posts_query]
        // Last 100 posts, these are automatically the 100 most recent
        // due to sorting by push() keys
        Query recentPostsQuery = databaseReference.child("posts")
                .limitToFirst(100);
        // [END recent_posts_query]
    }

    @Override
    public void onStart() {
        super.onStart();
        basicListen();
        basicQuery();
        basicQueryValueListener();
    }

    @Override
    public void onStop() {
        super.onStop();
        cleanBasicListener();
        cleanBasicQuery();
    }
}

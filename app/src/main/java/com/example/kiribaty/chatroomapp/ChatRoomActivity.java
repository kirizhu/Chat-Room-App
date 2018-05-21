package com.example.kiribaty.chatroomapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class ChatRoomActivity extends AppCompatActivity {
    //view
    private Button mRoomBtn;
    private EditText mRoomEdt;
    private ListView mRoomView;
    /*private String mUserName;*/

    //Lista för alla chatrum
    ArrayList<String> mRoomList = new ArrayList<>();
    ArrayAdapter<String> mArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        mRoomBtn =  findViewById(R.id.RoomBtn);
        mRoomEdt =  findViewById(R.id.RoomEdt);
        mRoomView =  findViewById(R.id.RoomView);

        //Adapter för att hålla alla chatrum i en listview med simpel layout
        mArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,mRoomList);

        //sätter adaptern till listviewn
        mRoomView.setAdapter(mArrayAdapter);


    mRoomBtn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            //Skickar vår input från EditText och
            DatabaseReference database = FirebaseDatabase.getInstance().getReference(mRoomEdt.getText().toString());
            database.setValue("");

            mRoomEdt.setText("");
            mRoomEdt.requestFocus();
        }
    });
        //referens till chatrummet som vi har skapat
        DatabaseReference database = FirebaseDatabase.getInstance().getReference(mRoomEdt.getText().toString());

        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //itererar genom databasen och hämtar namnen på våra chatrum för att sedan visa detta i vår listview
                Set<String> set = new HashSet<String>();
                Iterator i = dataSnapshot.getChildren().iterator();
                while (i.hasNext()){
                    set.add(((DataSnapshot)i.next()).getKey());
                }
                    //rensar uppdaterar våran listview
                    mRoomList.clear();
                    mRoomList.addAll(set);
                    mArrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //Tar oss till chat aktiviteten och skickar information om chatrummets namn
        mRoomView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent chatIntent = new Intent(ChatRoomActivity.this, ChatActivity.class);
                /*chatIntent.putExtra("userName",mUserName);*/
                chatIntent.putExtra("roomName", ((TextView)view).getText().toString());
                startActivity(chatIntent);

            }
        });
    }//overflow menu
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settingsmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //skickar dig till DeleteActivity och skickar med DATA for att populera listviewn i den aktiviteten
            case R.id.DeleteRoom:
                Intent deleteIntent = new Intent(ChatRoomActivity.this, DeleteActivity.class);
                startActivity(deleteIntent);
                deleteIntent.putExtra("RoomList",mRoomList);
                deleteIntent.putExtra("RoomName",mRoomEdt.getText().toString());
                startActivity(deleteIntent);
                return true;
                //Stäng ner app
            case R.id.CloseApp:
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

            default:
                return super.onOptionsItemSelected(item);
        }
    }

}

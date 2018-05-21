package com.example.kiribaty.chatroomapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {
    //variabler
    String mTempKey;
    String mChatMessage, mChatUserName;
    //view
    private TextView mChatTv;
    private EditText mChatEdt;
    private String myUserName;
    //firebase
    private DatabaseReference mDatabaseReference;
    private DatabaseReference mUserReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Button mChatBtn = findViewById(R.id.ChatBtn);
        mChatEdt = findViewById(R.id.ChatEdt);
        mChatTv = findViewById(R.id.ChatTv);

        //hämtar data från ChatRoom aktiviteten för att sätta titel på vårat chattrum och visa det i actionbaren
        String mRoomName = getIntent().getExtras().get("roomName").toString();
        /*myUserName = getIntent().getExtras().get("userName").toString();*/
        setTitle("Room - " + mRoomName);

        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child(mRoomName);
        requestUser();

        mChatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Skapar en temporär key
                Map<String, Object> mKeyMap = new HashMap<String, Object>();
                mTempKey = mDatabaseReference.push().getKey();
                mDatabaseReference.updateChildren(mKeyMap);
                //Vi skapar en child för varje meddelande som skickas Användarna förblir anonyma
                mUserReference = mDatabaseReference.child(mTempKey);
                Map<String, Object> mChatMap = new HashMap<String, Object>();
                mChatMap.put("name", myUserName);
                mChatMap.put("messages", mChatEdt.getText().toString());
                mUserReference.updateChildren(mChatMap);

                mChatEdt.setText("");
                mChatEdt.requestFocus();

            }
        });
        // lyssnare på event updateringar i vår databas för att sedan visa det i vår listview
        mDatabaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                DisplayMessages(dataSnapshot);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.chatmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            //kallar på vår metod för att ändra användarnamn
            case R.id.ChangeName:
                ChangeUserName();
                return true;
                //Stänger ner appen
            case R.id.CloseApp:
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    //metod för att begära användarnamn från användaren via en alertDialog
    public void requestUser() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(ChatActivity.this);
        mBuilder.setTitle("Enter username please");
        final EditText inputEdt = new EditText(ChatActivity.this);
        mBuilder.setView(inputEdt);

        mBuilder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                myUserName = inputEdt.getText().toString();
            }
        });
//ska inte gå att avbryta för att om man inte väljer något användarnamn och försöker skriva något så kastas man ut ur aktiviten
        mBuilder.setCancelable(false);
        mBuilder.show();
    }
//metod för att byta användarnamn medan man är inne i chatrummet
    public void ChangeUserName() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(ChatActivity.this);
        mBuilder.setTitle("Enter username please");
        final EditText inputEdt = new EditText(ChatActivity.this);
        mBuilder.setView(inputEdt);
        mBuilder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                myUserName = inputEdt.getText().toString();
            }
        });
        mBuilder.setCancelable(true);
        mBuilder.show();
    }
    //Metod för att populera våran chatt aktivitet med textviewn
    private void DisplayMessages(DataSnapshot dataSnapshot) {

        Iterator iterator = dataSnapshot.getChildren().iterator();
        while (iterator.hasNext()) {
            mChatMessage = (String) ((DataSnapshot) iterator.next()).getValue(); //cast needed
            mChatUserName = (String) ((DataSnapshot) iterator.next()).getValue(); //cast needed
            mChatTv.append(mChatUserName + ": " + mChatMessage + "\n \n");


        }
    }
}

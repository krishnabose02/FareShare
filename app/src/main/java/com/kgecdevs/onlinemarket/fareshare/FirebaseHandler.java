package com.kgecdevs.onlinemarket.fareshare;

import android.content.Context;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.lang.reflect.Array;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Created by Aditya on 21-12-2017.
 */

public class FirebaseHandler {
static ArrayList<String> recentUpdates;
static ArrayList<String> money0, money1,money2;
static int rupak, krishna, mayukh;
static int[] num;
static int[][] mm;
private static Context context;



    public static ArrayList<String> getRecentUpdates(Context con)
    {
        context = con;
        recentUpdates = new ArrayList<>();
        //to do real coding here

        DatabaseReference dref = FirebaseDatabase.getInstance().getReference().child("joblesscoders");

        dref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String data = (String)dataSnapshot.getValue();
                addToList(data);
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
        return recentUpdates;
    }

    private static void addToList(String data) {
        recentUpdates.add(data);
    }

    public static boolean uploadExpense(Context con, String s)
    {
        FirebaseDatabase fbase = FirebaseDatabase.getInstance();
        DatabaseReference dref = fbase.getReference().child("joblesscoders");
        dref.push().setValue(s);
        return true;
    }

    public static void updateMatrix(int kri, int ma, int ru) {
        krishna = kri;
        mayukh = ma;
        rupak = ru;
        num = new int[]{kri, ma, ru};
        DatabaseReference dref = FirebaseDatabase.getInstance().getReference().child("matrix");
        money1 = new ArrayList<>();
        money2 = new ArrayList<>();
        money0 = new ArrayList<>();
        Log.e("matrix","inside matrix");
        dref.child("0").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                addMoney0(dataSnapshot.getValue().toString());
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
        dref.child("1").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                addMoney1(dataSnapshot.getValue().toString());
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
        dref.child("2").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                addMoney2(dataSnapshot.getValue().toString());
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

    public static void addMoney0(String s)
    {
        money0.add(s);
    }
    public static void addMoney1(String s)
    {
        money1.add(s);
    }
    public static void addMoney2(String s)
    {
        money2.add(s);
        if(money2.size()==3)
            toArray();
    }

    private static void toArray() {
        mm=new int[3][3];
        for(int i=0;i<3;i++)
        {
            mm[0][i]=Integer.parseInt(money0.get(i));
        }
        for(int i=0;i<3;i++)
        {
            mm[1][i]=Integer.parseInt(money1.get(i));
        }
        for(int i=0;i<3;i++)
        {
            mm[2][i]=Integer.parseInt(money2.get(i));
        }
        update();
    }

    private static void update()
    {
        String name="";
        int id;
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null)
            name = user.getDisplayName().charAt(0)+"";

        if(name.equalsIgnoreCase("k"))id = 0;
        else if(name.equalsIgnoreCase("m")) id = 1;
        else id = 2;

        for(int usr=0;usr<3;usr++)
        {
            for(int p=0;p<3;p++)
            {
                if(id==usr)
                {
                    mm[usr][p]+=num[p];
                }
                else {
                    mm[usr][p]-=num[p];
                }
            }
            mm[usr][usr]=0;
        }

        money0 = new ArrayList<>();
        money1 = new ArrayList<>();
        money2 = new ArrayList<>();


        for(int i=0;i<3;i++)
        {
            money0.add(mm[0][i]+"");
        }
        for(int i=0;i<3;i++)
        {
            money1.add(mm[1][i]+"");
        }
        for(int i=0;i<3;i++)
        {
            money2.add(mm[2][i]+"");
        }
        upload();
    }

    private static void upload() {
        //this method finally uploads the new values! what a mess


        Log.e("matrix","writing matrix");
        DatabaseReference dref = FirebaseDatabase.getInstance().getReference().child("matrix");
        dref.child("0").setValue(money0);
        dref.child("1").setValue(money1);
        dref.child("2").setValue(money2).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                makeToast("Failed to upload");
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                makeToast("Data updated successfully");
            }
        })
        ;

        Log.e("matrix","matrix written");
    }

    private static void makeToast(String text)
    {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }
}

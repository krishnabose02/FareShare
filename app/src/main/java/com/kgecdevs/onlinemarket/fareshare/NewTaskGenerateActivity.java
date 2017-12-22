package com.kgecdevs.onlinemarket.fareshare;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.effect.Effect;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class NewTaskGenerateActivity extends AppCompatActivity {
    private TextView rupaksummary, mayukhsummary, krishnasummary;
    private boolean rchecked, mchecked, kchecked;
    private LinearLayout rootLayout;
    private EditText am;
    private int count=3;
    private EditText desc;

    protected void onDestroy()
    {
        super.onDestroy();
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task_generate);
        krishnasummary = findViewById(R.id.p1summary);
        rupaksummary = findViewById(R.id.p3summary);
        mayukhsummary = findViewById(R.id.p2summary);
        krishnasummary.setText("0");
        mayukhsummary.setText("0");
        rupaksummary.setText("0");


        rchecked=kchecked=mchecked=true;

        Switch pfs = findViewById(R.id.payforall);
        pfs.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                showMemberChecker(b);
            }
        });

        am = (EditText)findViewById(R.id.amount);
        am.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                splitBill();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        desc = findViewById(R.id.description);
        rootLayout=findViewById(R.id.taskroot);
    }

    private void showMemberChecker(boolean b) {
        CardView mem = findViewById(R.id.memberselector);
        if(b)
            mem.setVisibility(View.VISIBLE);
        else
            mem.setVisibility(View.GONE);

        rchecked=kchecked=mchecked=true;
        splitBill();
    }

    private void splitBill() {

        if(count==0)
        {
            Snackbar bar = Snackbar.make(rootLayout, "Please select atleast one member", Snackbar.LENGTH_SHORT);
            bar.show();
            return;
        }

        String ams = ((EditText)findViewById(R.id.amount)).getText().toString();
        if(ams.isEmpty()) return;
        int fare = Integer.parseInt(ams);
        int part = fare/count;

        if(rchecked) rupaksummary.setText(""+part);
        else rupaksummary.setText("0");

        if(mchecked) mayukhsummary.setText(""+part);
        else mayukhsummary.setText("0");

        if(kchecked) krishnasummary.setText(""+part);
        else krishnasummary.setText("0");
    }

    public void onCheckedBoxClicked(View v)
    {
        int id = v.getId();
        switch (id)
        {
            case R.id.rcheck:   rchecked=((CheckBox)v).isChecked();
                                if(rchecked)count++;
                                else count--;
                break;
            case R.id.kcheck:   kchecked=((CheckBox)v).isChecked();
                                if(kchecked)count++;
                                else count--;
                break;
            case R.id.mcheck:   mchecked=((CheckBox)v).isChecked();
                                if(mchecked)count++;
                                else count--;
                break;
        }
        splitBill();
    }

    public void createDialog(View view)
    {
        String money = am.getText().toString();
        if(money.isEmpty() || money.equals("0"))
        {
            Toast.makeText(this, "Please add amount", Toast.LENGTH_SHORT).show();
            return;
        }
        if(count == 0)
        {
            Toast.makeText(this, "Please add members", Toast.LENGTH_SHORT).show();
            return;
        }
        AlertDialog.Builder ab=new AlertDialog.Builder(this);
        ab.setMessage(generateText());
        ab.setTitle("Verify the bill");
        ab.setPositiveButton("upload", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                addExpense();
            }
        });
        ab.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        ab.show();
    }

    public String generateText()
    {
        Switch pfa;
        pfa = findViewById(R.id.payforall);

        //looking for description
        String description="";
        description=desc.getText().toString();
        if(!description.isEmpty())
            description=" ("+description+")";
        int amount=0;

        try
        {
            amount = Integer.parseInt(((EditText)findViewById(R.id.amount)).getText().toString());
        }
        catch (Exception e)
        {
            Toast.makeText(this, "Please add amount of expense", Toast.LENGTH_SHORT).show();
            return "0";
        }
        String firstname = (FirebaseAuth.getInstance().getCurrentUser().getDisplayName().split(" "))[0];
        if(!pfa.isChecked())
            return (firstname+" paid \u20B9"+ amount+" for all"+description);
        else
        {
            String paidfor = "";
            if(rchecked) paidfor+="Rupak ";

            if(mchecked) paidfor+="Mayukh ";

            if(kchecked) paidfor+="Krishna";

            return (firstname+" paid \u20B9"+amount+" for "+paidfor+description);
        }


    }

    public void addExpense() {
        boolean status = FirebaseHandler.uploadExpense(this, generateText());

        //This module finally calculates the amount one need to pay
        int amount = Integer.parseInt(((EditText)findViewById(R.id.amount)).getText().toString());
        int kri, ma, ru, part;
        kri = ma = ru = 0;
        part = amount/count;
        if(kchecked) kri = part;
        if(rchecked) ru = part;
        if(mchecked) ma = part;
        FirebaseHandler.updateMatrix(kri, ma, ru);
        if(!status)
        {
            Snackbar bar = Snackbar.make(rootLayout, "Upload failed", Snackbar.LENGTH_SHORT);
            bar.show();
            return;
        }
        startActivity(new Intent(this, HomeActivity.class));
        Toast.makeText(this, "Upload successful", Toast.LENGTH_SHORT).show();
        finish();
    }
}

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
    private TextView ajsummary, kbsummary, pgsummary,sjsummary,snsummary,srsummary;
    private boolean ajchecked, kbchecked, pgchecked,sjchecked,snchecked,srchecked;
    private LinearLayout rootLayout;
    private EditText am;
    private int count=6;
    private EditText desc;

    private int sumid[] = new int[]{R.id.ajsummary, R.id.kbsummary, R.id.pgsummary, R.id.sjsummary, R.id.snsummary, R.id.srsummary};
    private TextView sumtext[] = new TextView[6];
    private boolean checked[] = new boolean[6];

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
        for(int i=0; i<6;i++)
        {
            sumtext[i] = findViewById(sumid[i]);
            sumtext[i].setText("0");
            checked[i] = true;
        }

        Switch pfs = findViewById(R.id.payforall);
        pfs.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                showMemberChecker(b);
            }
        });

        am = findViewById(R.id.amount);
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

        for(int i=0;i<6;i++)
        {
            checked[i] = true;
        }
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

        for(int i=0;i<6;i++)
        {
            if(checked[i])
            {
                sumtext[i].setText(""+part);
            }
            else
            {
                sumtext[i].setText("0");
            }
        }
    }

    public void onCheckedBoxClicked(View v)
    {
        int id = v.getId();
        switch (id)
        {
            case R.id.ajcheck:   checked[0]=((CheckBox)v).isChecked();
                                if(checked[0])count++;
                                else count--;
                break;
            case R.id.kbcheck:   checked[1]=((CheckBox)v).isChecked();
                                if(checked[1])count++;
                                else count--;
                                break;
            case R.id.pgcheck:  checked[2]=((CheckBox)v).isChecked();
                                if(checked[2])count++;
                                else count--;
                                break;
            case R.id.sjcheck:   checked[3]=((CheckBox)v).isChecked();
                                if(checked[3])count++;
                                else count--;
                                break;
            case R.id.sncheck:   checked[4]=((CheckBox)v).isChecked();
                                if(checked[4])count++;
                                else count--;
                                break;
            case R.id.srcheck:  checked[5]=((CheckBox)v).isChecked();
                                if(checked[5])count++;
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


    private int checkIDs[] = new int[]{R.id.ajcheck,R.id.kbcheck,R.id.pgcheck,R.id.sjcheck,R.id.sncheck,R.id.srcheck};
    private String names[] = new String[]{"Abhishek","Krishna","Pallab","Sourab","Souradip","Sumit"};

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
            for(int i = 0; i<6;i++)
            {
                if(checked[i]) paidfor+=names[i]+" ";
            }

            return (firstname+" paid \u20B9"+amount+" for "+paidfor+description);
        }


    }

    public void addExpense() {
        boolean status = FirebaseHandler.uploadExpense(this, generateText());

        //This module finally calculates the amount one need to pay
        int amount = Integer.parseInt(((EditText)findViewById(R.id.amount)).getText().toString());
        int kri, ma, ru, part;
        kri = ma = ru = 0;
        int divs[] = new int[6];
        part = amount/count;
        for(int i=0;i<6;i++)
        {
            if(checked[i])
                divs[i] = part;
        }
        FirebaseHandler.updateMatrix(kri, ma, ru);//TODO rewrite for 6 people
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

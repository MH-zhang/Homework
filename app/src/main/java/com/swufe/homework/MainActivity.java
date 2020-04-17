package com.swufe.homework;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    public TextView TvResult;
   public EditText EtInput;
    public Button BtCF;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EtInput = (EditText) findViewById(R.id.value_hint);
        BtCF = (Button)findViewById(R.id.celsius_to_fahren);
        TvResult = (TextView)findViewById(R.id.tv_result);

        BtCF.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
       if(checkValidInput()) {
           showResult();
       }

    }

    private boolean checkValidInput(){
        if(EtInput.getText().length()==0){
            String errorMsg = getResources().getString(R.string.msg_error_input);
            Toast.makeText(this,errorMsg,Toast.LENGTH_LONG).show();
            return false;
        }else{
            return true;
        }
    }

    private void showResult(){
       float inputValue = Float.parseFloat(EtInput.getText().toString());
        float Result =(inputValue*1.8f)+32.0f;
        TvResult.setText(""+Result+"华氏度");
    }
}


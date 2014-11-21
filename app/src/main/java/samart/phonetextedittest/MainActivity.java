package samart.phonetextedittest;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PhoneEditText phoneEditText = (PhoneEditText)findViewById(R.id.phoneEditText);

        //sets fixed part of phone number, e.g. +7
        phoneEditText.setFixedPart("8");

        //sets remaining part of phone number
        phoneEditText.setDigits("9062738275");

        //obtain digit string of phone number, e.g.
        // 89067285686
        String digitsString = phoneEditText.getDigitString();

        //obtain formatted representation of phone number,
        // e.g. +7 (906) 728-56-86
        String formattedPhoneNumber = phoneEditText.getFormattedPhoneNumber();


        LogUtils.msg(digitsString);
        LogUtils.msg(formattedPhoneNumber);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

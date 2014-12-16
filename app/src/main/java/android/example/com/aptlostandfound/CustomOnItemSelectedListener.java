package android.example.com.aptlostandfound;

import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

/**
 * Created by Zhang on 2014/12/9.
 */
public class CustomOnItemSelectedListener implements AdapterView.OnItemSelectedListener {

    public void onItemSelected(AdapterView<?> parent, View view, int pos,
                               long id) {

//        Toast.makeText(parent.getContext(),
//                "On Item Select : \n" + parent.getItemAtPosition(pos).toString(),
//                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub

    }

}

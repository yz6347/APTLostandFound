package android.example.com.aptlostandfound;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;

public class MainActivity extends Activity implements
    LoginFragment.OnLoginSelectedListener,
    PostFragment.OnPostSelectedListener,
    ViewAllFragment.OnViewAllSelectedListener,
    ViewSingleFragment.OnViewSingleSelectedListener,
    CameraFragment.OnCameraSelectedListener{

    public static String USER_NAME = "";
    public static String theNAME = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new LoginFragment())
                    .addToBackStack("Login")
//                    .add(R.id.container, new PostFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPostSelected() {
        getFragmentManager().beginTransaction()
                .replace(R.id.container, new PostFragment())
                .addToBackStack("Post")
                .commit();
    }

    @Override
    public void onViewAllSelected() {
        getFragmentManager().beginTransaction()
                .replace(R.id.container, new ViewAllFragment())
                .addToBackStack("ViewAll")
                .commit();
    }

    @Override
    public void onLoginSelected() {
        getFragmentManager().beginTransaction()
                .replace(R.id.container, new LoginFragment())
                .addToBackStack("Login")
                .commit();
    }

    @Override
    public void onViewSingleSelected() {
        getFragmentManager().beginTransaction()
                .replace(R.id.container, new ViewSingleFragment())
                .addToBackStack("ViewSingle")
                .commit();
    }

}

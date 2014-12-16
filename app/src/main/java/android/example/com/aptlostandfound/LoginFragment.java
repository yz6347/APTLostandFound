package android.example.com.aptlostandfound;

import android.app.Activity;
import android.app.FragmentManager;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;


public class LoginFragment extends Fragment {

    private OnLoginSelectedListener mListener;

    private AsyncHttpClient httpClient = new AsyncHttpClient();
    private static String LOGIN_URL = "http://aptlostfound.appspot.com/signInAPI";
    private static String REGISTER_URL = "http://aptlostfound.appspot.com/registerAPI";

    private Button btnLogin, btnRegister;
    private EditText emailBox, pwdBox;
    private String userEmail, password;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        FragmentManager fm = getActivity().getFragmentManager();
//        for(int i=0; i<fm.getBackStackEntryCount(); i++){
//            fm.popBackStack();
//        }
        // ?? how to clear backstack?
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview = inflater.inflate(R.layout.fragment_login, container, false);
        btnLogin = (Button)rootview.findViewById(R.id.LOGIN_btn_login);
        btnRegister = (Button)rootview.findViewById(R.id.LOGIN_btn_register);
        emailBox = (EditText)rootview.findViewById(R.id.LOGIN_email);
        pwdBox = (EditText)rootview.findViewById(R.id.LOGIN_pwd);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userEmail = emailBox.getText().toString();
                password = pwdBox.getText().toString();
                RequestParams signinParams = new RequestParams();
                signinParams.put("email", userEmail);
                signinParams.put("pw", password);

                httpClient.post(LOGIN_URL, signinParams, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int i, Header[] headers, byte[] bytes) {
                        // TO-DO:go to view all
                        String loginResponse = new String(bytes);
                        Log.d("loginResponse!!!!!!!!!!", loginResponse);    // test
                        try {
                            JSONObject loginJSON = new JSONObject(loginResponse);
                            final String loginStatus = loginJSON.getString("status");
                            Log.d("loginStatus!!!!!!!!!!", loginStatus);
//                            mListener.onPostSelected();
                            if (loginStatus.equals("success")){
                                Log.d("loginSuccess!!!!!!!!", userEmail);
//                                mListener.onPostSelected();
                                MainActivity.USER_NAME = userEmail;
                                int nameIdx = MainActivity.USER_NAME.lastIndexOf("@");
                                MainActivity.theNAME = MainActivity.USER_NAME.substring(0, nameIdx);
                                Toast.makeText(getActivity().getBaseContext(), "Hello! " + MainActivity.theNAME + "!", Toast.LENGTH_LONG).show();
                                mListener.onViewAllSelected();
                            } else {
                                String errorMSG = loginJSON.getString("error");
                                Toast.makeText(getActivity().getBaseContext(), "Login Failed \n" + errorMSG, Toast.LENGTH_LONG).show();
                                Log.d("loginFailed!!!!!!!!!!", errorMSG);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                        // failure message
                        Log.d("Login Failure!!!!", "not connected");
                        Toast.makeText(getActivity().getBaseContext(), "Login Failed \nUnable to Connect", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userEmail = emailBox.getText().toString();
                password = pwdBox.getText().toString();
                RequestParams signinParams = new RequestParams();
                signinParams.put("email", userEmail);
                signinParams.put("pw", password);

                httpClient.post(REGISTER_URL, signinParams, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int i, Header[] headers, byte[] bytes) {
                        // TO-DO:go to view all
//                        mListener.onPostSelected();
                        MainActivity.USER_NAME = userEmail;
                        int nameIdx = MainActivity.USER_NAME.lastIndexOf("@");
                        MainActivity.theNAME = MainActivity.USER_NAME.substring(0, nameIdx);
                        String toastMSG = "Register success!\nWelcome! " + MainActivity.theNAME + "!";
                        Toast.makeText(getActivity().getBaseContext(), toastMSG, Toast.LENGTH_LONG).show();
                        mListener.onViewAllSelected();
                    }

                    @Override
                    public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                        // failure message
                        Toast.makeText(getActivity(), "login failed", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        return rootview;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnLoginSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnLoginSelectedListener {
        public void onLoginSelected();
        public void onPostSelected();   // go to post
        public void onViewAllSelected();    // go to view all
    }

}

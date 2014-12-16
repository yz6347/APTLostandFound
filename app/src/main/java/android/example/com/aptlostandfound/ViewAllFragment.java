package android.example.com.aptlostandfound;

import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ViewAllFragment extends Fragment {

    public static PostClass singlePost;

    private Button btnReport, btnSearch, btnNearby;
    private RadioGroup rdGRP;
    private RadioButton rdbtn;
    private Spinner cateSpinner;
    private TextView txtUserName, txtLogout;

    private ListView listView;
    private ArrayList<PostClass> postArray = new ArrayList<PostClass>();

    private AsyncHttpClient httpClient = new AsyncHttpClient();
    private static String SEARCH_LOST_URL = "http://aptlostfound.appspot.com/searchAPI/lost";
    private static String SEARCH_FOUND_URL = "http://aptlostfound.appspot.com/searchAPI/found";
    private static String NearBy_URL = "http://aptlostfound.appspot.com/nearbyAPI";
    private String SEARCH_URL;


//    private AsyncHttpClient httpClientGeoCoding = new AsyncHttpClient();
    private LocationManager locationManager;
    private LocationListener mLocationListener;
    private double myLat, myLng;
//    private static String APIKEY = "&key=AIzaSyDKjMxzn3N4SvQb2miF3kpGvR1LcifZI_M";
//    private static String REVGEOBASE = "https://maps.googleapis.com/maps/api/geocode/json?latlng=";

    private OnViewAllSelectedListener mListener;

    public ViewAllFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(MainActivity.USER_NAME.isEmpty()){
            mListener.onLoginSelected();
        }

//        // dummy data
//        String IMGURL = "http://lh4.ggpht.com/WhgEDYyh3qjduzkvjRC2OypRqT-OhiaVF2CVta7G7BepMethdfjmmG-MCdQV_WadvNh1gIk1diOelTQETvtbseoP";
        PostClass dummypost = new PostClass("Lost", "Hit search to refresh", "this is dummy data", "cate", "");
        dummypost.setAuthor("a dummy user");
//        PostClass secondpost = new PostClass("found", "2 title", "2 desp", "cate2", IMGURL);
//        PostClass thirdpost = new PostClass("Lost", "3 title", "3 desp", "cate3", IMGURL);
//        PostClass finalpost = new PostClass("found", "4 title", "4 desp", "cate4", IMGURL);
//
        postArray.add(dummypost);
//        postArray.add(secondpost);
//        postArray.add(thirdpost);
//        postArray.add(finalpost);
//        // dummy data
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootview = inflater.inflate(R.layout.fragment_view_all, container, false);

        txtUserName = (TextView)rootview.findViewById(R.id.VIEWALL_txt_helloUser);
        txtLogout = (TextView)rootview.findViewById(R.id.VIEWALL_txt_logout);
        btnReport = (Button)rootview.findViewById(R.id.VIEWALL_btn_report);
        btnSearch = (Button)rootview.findViewById(R.id.VIEWALL_btn_search);
        btnNearby = (Button)rootview.findViewById(R.id.VIEWALL_btn_neayby);
        rdGRP = (RadioGroup)rootview.findViewById(R.id.VIEWALL_rdGRP);

        cateSpinner = (Spinner)rootview.findViewById(R.id.VIEWALL_spinner);
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.cate_spinner, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cateSpinner.setAdapter(spinnerAdapter);
        cateSpinner.setOnItemSelectedListener(new CustomOnItemSelectedListener());

        txtUserName.setText(MainActivity.theNAME + "!");
        txtLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.USER_NAME = "";
                Toast.makeText(getActivity().getBaseContext(), "Log out success!", Toast.LENGTH_SHORT).show();
                mListener.onLoginSelected();
            }
        });

        listView = (ListView)rootview.findViewById(R.id.VIEWALL_list);
        final PostListViewAdapter adapter = new PostListViewAdapter(getActivity().getBaseContext(), postArray);  // test using dummy data
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // go to view single
                PostClass clickedItem = postArray.get(position);
                singlePost = clickedItem;
                String clickedTitle = clickedItem.getTitle();
                Log.d("List Clicked!!!!!!!!!", clickedTitle);
                mListener.onViewSingleSelected();
            }
        });

        btnReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onPostSelected();
            }
        });

        btnNearby.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                postArray.clear();


                int radioIdx = rdGRP.getCheckedRadioButtonId();
                rdbtn = (RadioButton)rootview.findViewById(radioIdx);

                getCurrentLocation();
                Log.d("your current location", String.valueOf(myLat) + ", " + String.valueOf(myLat));

                RequestParams nearbyParams = new RequestParams();
                nearbyParams.put("latitude", myLat);
                nearbyParams.put("longitude", myLng);

                httpClient.get(NearBy_URL, nearbyParams, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        String nearDesc, nearTitle, nearCate, nearLost, nearImg, nearEmail;
                        PostClass nearPost;
                        String nearbyKind = rdbtn.getText().toString();                       // lost or found

                        String response = new String(responseBody);
                        int numItems;
                        Log.d("Nearby response!!!!!!!!!!!", response);

                        try {
                            JSONObject nearJSON = new JSONObject(response);
                            if(nearbyKind.equals("Found")){
                                JSONArray foundArray = nearJSON.getJSONArray("found");
                                Log.d("found Array!!!!!", foundArray.toString());
                                numItems = foundArray.length();
                                for(int i=0; i<numItems; i++){
                                    JSONArray singleItem = foundArray.getJSONArray(i);
                                    nearDesc = singleItem.getString(3);
                                    nearTitle = singleItem.getString(1);
                                    nearLost = "Found";
                                    nearCate = singleItem.getString(5);
                                    nearImg = singleItem.getString(4);
                                    nearEmail = singleItem.getString(7);
                                    nearPost = new PostClass(nearLost, nearTitle, nearDesc, nearCate, nearImg);
                                    nearPost.setAuthor(nearEmail);
                                    Log.d("Near Item!!!!!!!!!", nearPost.getCate() + nearPost.getTitle());
                                    postArray.add(nearPost);
                                    adapter.notifyDataSetChanged();
                                }
                            }else{
                                JSONArray lostArray = nearJSON.getJSONArray("lost");
                                Log.d("found Array!!!!!", lostArray.toString());
                                numItems = lostArray.length();
                                for(int i=0; i<numItems; i++){
                                    JSONArray singleItem = lostArray.getJSONArray(i);
                                    nearDesc = singleItem.getString(3);
                                    nearTitle = singleItem.getString(1);
                                    nearLost = "Found";
                                    nearCate = singleItem.getString(5);
                                    nearImg = singleItem.getString(4);
                                    nearEmail = singleItem.getString(7);
                                    nearPost = new PostClass(nearLost, nearTitle, nearDesc, nearCate, nearImg);
                                    nearPost.setAuthor(nearEmail);
                                    Log.d("Near Item!!!!!!!!!", nearPost.getCate() + nearPost.getTitle());
                                    postArray.add(nearPost);
                                    adapter.notifyDataSetChanged();
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                    }
                });
//                adapter.notifyDataSetChanged();
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                postArray.clear();

                EditText keywordBox = (EditText)rootview.findViewById(R.id.VIEWALL_txt_keyword);
                String keyword = keywordBox.getText().toString();

                int radioIdx = rdGRP.getCheckedRadioButtonId();
                rdbtn = (RadioButton)rootview.findViewById(radioIdx);
                String postLost = rdbtn.getText().toString();                       // lost or found

                if(postLost.equals("Lost")){
                    SEARCH_URL = SEARCH_LOST_URL;
                }else{
                    SEARCH_URL = SEARCH_FOUND_URL;
                }

                String searchCate = cateSpinner.getSelectedItem().toString();

                RequestParams searchParams = new RequestParams();
                searchParams.put("keyword", keyword);
                searchParams.put("cate", searchCate);

                httpClient.post(SEARCH_URL, searchParams, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        String response = new String(responseBody);
                        Log.d("Search response!!!!!!!!!!!", response);
                        String itemDesc, itemTitle, itemCate, itemLost, itemImg, itemEmail;
                        PostClass newPost;

                        try {
                            JSONArray itemJSON = new JSONArray(response);
                            int numItems = itemJSON.length();
                            for(int i=0; i<numItems; i++){
                                itemDesc = itemJSON.getJSONObject(i).get("desc").toString();
                                itemTitle = itemJSON.getJSONObject(i).get("title").toString();
                                itemCate = itemJSON.getJSONObject(i).get("cate").toString();
                                itemLost = "Lost";
                                itemImg = itemJSON.getJSONObject(i).get("img").toString();
                                itemEmail = itemJSON.getJSONObject(i).get("email").toString();

                                newPost = new PostClass(itemLost, itemTitle, itemDesc, itemCate, itemImg);
                                newPost.setAuthor(itemEmail);
                                postArray.add(newPost);
                                Log.d("postArray!!!!!!!!!", postArray.get(i).getCate() + postArray.get(i).getDesp() + postArray.get(i).getTitle() + postArray.get(i).getImgURL());
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                    }
                });
            }
        });

        return rootview;
    }

    public void getCurrentLocation(){
        // Get the location manager
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        // Define the criteria how to select the locatioin provider -> use
        // default
        Criteria criteria = new Criteria();     // not used
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);     // not used
//        provider = locationManager.getBestProvider(criteria, true);
        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                myLat = location.getLatitude();
                myLng = location.getLongitude();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 300, 10, mLocationListener);
    }



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnViewAllSelectedListener) activity;
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

    public interface OnViewAllSelectedListener {
        public void onPostSelected();       // go to post by report
        public void onViewSingleSelected(); // go to single view by click item
        public void onLoginSelected();      // go to log in
    }

}

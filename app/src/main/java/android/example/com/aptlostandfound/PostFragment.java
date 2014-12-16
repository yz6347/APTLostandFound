package android.example.com.aptlostandfound;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

public class PostFragment extends Fragment {

    private OnPostSelectedListener mListener;
    private LocationManager locationManager;
    private LocationListener mLocationListener;
    private String provider;

    private AsyncHttpClient httpClientGeoCoding = new AsyncHttpClient();
    private AsyncHttpClient httpClientBlobURL = new AsyncHttpClient();
    private AsyncHttpClient httpClientRevGeo = new AsyncHttpClient();
    private AsyncHttpClient httpClientUploadAPI = new AsyncHttpClient();

    private Button btnSubmit, btnAddPic;
    private EditText txtTitle, txtDesp, txtGeo, txtDate;
    private Spinner cateSpinner;
    private TextView txtUseCurLoc, txtLogout, txtUerName, txtUseAddress;
    private RadioButton radioBtn;
    private RadioGroup radioGrp;
    private String postTitle, postCate, postDesp, postGeo, postDate, postResult, postLost, geoUrl, postFile;
    private ImageView imgView;
    private int myear, mmonth, mday;
    private double lat, lng;
    private String filePath;

    private PostClass postinfo;

    private static String APIKEY = "&key=AIzaSyDKjMxzn3N4SvQb2miF3kpGvR1LcifZI_M";
    private static String GEOBASE = "https://maps.googleapis.com/maps/api/geocode/json?address=";
    private static String REVGEOBASE = "https://maps.googleapis.com/maps/api/geocode/json?latlng=";

    private static String BLOB_BASE = "http://aptlostfound.appspot.com/blobUrl/";
//    private static String POST_UPLOAD_BASE = "http://aptlostfound.appspot.com/upload/";
    private String UPLOAD_URL;

    public PostFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(MainActivity.USER_NAME.isEmpty()){
            mListener.onLoginSelected();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootview = inflater.inflate(R.layout.fragment_post, container, false);

        btnSubmit = (Button)rootview.findViewById(R.id.POST_btn_submit);
        txtTitle = (EditText)rootview.findViewById(R.id.POST_txt_title);
        txtDesp = (EditText)rootview.findViewById(R.id.POST_txt_desp);
        txtGeo = (EditText)rootview.findViewById(R.id.POST_txt_geo);
        radioGrp = (RadioGroup)rootview.findViewById(R.id.POST_rdbtn_group);
        btnAddPic = (Button)rootview.findViewById(R.id.POST_btn_addPic);
        imgView = (ImageView)rootview.findViewById(R.id.POST_img);
        txtDate = (EditText)rootview.findViewById(R.id.POST_txt_setDate);
        txtUseCurLoc = (TextView)rootview.findViewById(R.id.POST_txt_useLocation);
        txtUerName = (TextView)rootview.findViewById(R.id.POST_txt_helloUser);
        txtLogout = (TextView)rootview.findViewById(R.id.POST_txt_logout);
        txtUseAddress = (TextView)rootview.findViewById(R.id.POST_txt_useAddress);

        cateSpinner = (Spinner)rootview.findViewById(R.id.POST_spinner);
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.cate_spinner, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cateSpinner.setAdapter(spinnerAdapter);
        cateSpinner.setOnItemSelectedListener(new CustomOnItemSelectedListener());

        txtUerName.setText(MainActivity.theNAME + "!");
        txtLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.USER_NAME = "";
                Toast.makeText(getActivity().getBaseContext(), "Log out success!", Toast.LENGTH_SHORT).show();
                mListener.onLoginSelected();
            }
        });

        txtDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayDate(v);
            }
        });

        txtUseCurLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrentLocation();
            }
        });

        txtUseAddress.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                postGeo = txtGeo.getText().toString();                          // address
                if(postGeo.isEmpty()){
                    Toast.makeText(getActivity().getBaseContext(), "Please enter address!", Toast.LENGTH_LONG).show();
                }else{
                    String geoRequest = postGeo.replace(" ", "+");
                    geoUrl = GEOBASE + geoRequest + APIKEY;

                    httpClientGeoCoding.get(geoUrl, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            String source = new String(responseBody);
                            Log.d("geoinfo", source);

                            try {
                                JSONObject geoJSON = new JSONObject(source);
                                String geoStatus = geoJSON.getString("status");

                                JSONArray geoResults = geoJSON.getJSONArray("results");
                                JSONObject geoResultsJSON = geoResults.getJSONObject(0);
                                String formattedAddress = geoResultsJSON.getString("formatted_address");
                                postGeo = formattedAddress;
                                txtGeo.setText(postGeo);

//                            getBlobUrl();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            Toast.makeText(getActivity().getBaseContext(), "Failed to get geo info", Toast.LENGTH_LONG).show();
                        }

                    });
                }
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postTitle = txtTitle.getText().toString();                      // title
                postCate = cateSpinner.getSelectedItem().toString();            // category
                postDesp = txtDesp.getText().toString();                        // description
                postGeo = txtGeo.getText().toString();                          // address
                postDate = DateFormat.getDateTimeInstance().format(new Date());  // date info

                int radioIdx = radioGrp.getCheckedRadioButtonId();
                radioBtn = (RadioButton)rootview.findViewById(radioIdx);
                postLost = radioBtn.getText().toString();                       // lost or found

//                txtResult.setText(postTitle + "  " + postCate + "  " + postDesp + "  " + postGeo + "  " + postDate + " " + postLost + " " + MainActivity.USER_NAME);  // test

                if(postTitle.isEmpty()){
                    Toast.makeText(getActivity().getBaseContext(), "Title cannot be empty!", Toast.LENGTH_LONG).show();
                }
                else if(postDate.isEmpty()){
                    Toast.makeText(getActivity().getBaseContext(), "Please select date!", Toast.LENGTH_LONG).show();
                }
                else if(postDesp.isEmpty()){
                    Toast.makeText(getActivity().getBaseContext(), "Description cannot be empty!", Toast.LENGTH_LONG).show();
                }
                else if(postGeo.isEmpty()){
                    Toast.makeText(getActivity().getBaseContext(), "Please enter address!", Toast.LENGTH_LONG).show();
                }
                else{
                    String postGeoRequest = postGeo.replace(" ", "+");
                    geoUrl = GEOBASE + postGeoRequest + APIKEY;

                    // get geoinfo from geocoding
                    httpClientGeoCoding.get(geoUrl, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            String source = new String(responseBody);
                            Log.d("geoinfo", source);

                            try {
                                JSONObject geoJSON = new JSONObject(source);
                                String geoStatus = geoJSON.getString("status");

                                JSONArray geoResults = geoJSON.getJSONArray("results");
                                JSONObject geoResultsJSON = geoResults.getJSONObject(0);
                                String formattedAddress = geoResultsJSON.getString("formatted_address");
                                postGeo = formattedAddress;
                                txtGeo.setText(postGeo);

                                JSONObject geometryJSON = geoResultsJSON.getJSONObject("geometry");
                                JSONObject locationJSON = geometryJSON.getJSONObject("location");
                                lat = locationJSON.getDouble("lat");
                                lng = locationJSON.getDouble("lng");
                                String templatlng = String.valueOf(lat) + " " + String.valueOf(lng);

                                Log.d("geoStatus!!!!!!!!!!", geoStatus);
                                Log.d("formattedAddress!!!!!!!!", formattedAddress);
                                Log.d("Lat and Lng !!!!!!!!!!", templatlng);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            Toast.makeText(getActivity().getBaseContext(), "Failed to get geo info", Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onFinish(){
                            // getBlobUrl
                            getBlobUrl();
                        }
                    });
                    // get geoinfo
                }

            }
        });

        btnAddPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        return rootview;
    }

    private void selectImage() {
        final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };
        AlertDialog.Builder picDialog = new AlertDialog.Builder(getActivity());
        picDialog.setTitle("Add Photo!");
        picDialog.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if(options[item].equals("Take Photo")){
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File f = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg");
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                    startActivityForResult(intent, 1);
                }
                else if(options[item].equals("Choose from Gallery")){
                    Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 2);
                }
                else if(options[item].equals("Cancel")){
                    dialog.dismiss();
                }
            }
        });
        picDialog.show();
    }

    private void getBlobUrl(){

        // get blob url
        String blobkind = postLost.toLowerCase();
        blobkind = BLOB_BASE + blobkind;
        Log.d("blobkind!!!!!!!!!!", blobkind);
        httpClientBlobURL.get(blobkind, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
                Log.d("BlobURL return!!!!!!!!!!!!!!!!!!!!!", response);
                try {
                    JSONObject UrlJson = new JSONObject(response);
                    UPLOAD_URL = UrlJson.getString("url");
                    Log.d("upload url!!!!!!!!!!!!!!!!!!!!!", UPLOAD_URL);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d("getBlobUrl failed !!!!!!!", "getBlobUrl failed !!!!!!!");
            }

            @Override
            public void onFinish(){
                uploadPost(UPLOAD_URL);
            }
        });
    }

    private void uploadPost(String urlString){
        // upload
//        HttpClient httpClientUploadAPI = new DefaultHttpClient();

//        HttpPost httppost = new HttpPost(urlString);
        httpClientUploadAPI.addHeader("Content-type", "multipart/form-data");

        // this part is not working
        RequestParams uploadParams = new RequestParams();
        uploadParams.put("date", postDate);
        uploadParams.put("category", postCate);
        uploadParams.put("title", postTitle);
        uploadParams.put("description", postDesp);
        uploadParams.put("address", postGeo);
//        try {
//            File file = new File(Environment.getExternalStorageDirectory().getPath() + filePath);
//            if(!file.exists()){
//                file.createNewFile();
//            }
//            uploadParams.put("file", file);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        String location;
        location = "(" + String.valueOf(lat) + "," + String.valueOf(lng) + ")";
        uploadParams.put("location", location);

        httpClientUploadAPI.post(urlString, uploadParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
                Log.d("UPLOAD success!!!!", response);
                mListener.onViewAllSelected();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String response = new String(responseBody);
                Log.d("UPLOAD fail!!!!!", response);

            }
        });
        // this part is not working

    }

    // add a picture related
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_OK) {
            if (requestCode == 1) {         // take by camera
                File f = new File(Environment.getExternalStorageDirectory().toString());
                for (File temp : f.listFiles()) {
                    if (temp.getName().equals("APTimg.jpg")) {
                        f = temp;
                        break;
                    }
                }
                try {
                    Bitmap bitmap;
                    BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();

                    bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(),
                            bitmapOptions);

                    imgView.setImageBitmap(bitmap);

                    String path = android.os.Environment
                            .getExternalStorageDirectory()
                            + File.separator
                            + "APT" + File.separator + "default";
                    OutputStream outFile = null;
                    File file = new File(path, String.valueOf(System.currentTimeMillis()) + ".jpg");
                    filePath = file.getAbsolutePath();      //!!!
                    Log.d("File path!!!!!", filePath);
                    try {
                        outFile = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outFile);
                        outFile.flush();
                        outFile.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (requestCode == 2) {      // select from gallary

                Uri selectedImage = data.getData();
                String[] thefilePath = { MediaStore.Images.Media.DATA };
                Cursor c = getActivity().getContentResolver().query(selectedImage, thefilePath, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(thefilePath[0]);
                String picturePath = c.getString(columnIndex);
                c.close();
                Log.d("selected image path!!!!!!!!!", picturePath);
                filePath = picturePath;
                Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));
                imgView.setImageBitmap(thumbnail);
            }
        }
    }
    // add a picture related

    // select date
    private void displayDate(View v){
        final Calendar c = Calendar.getInstance();
        myear = c.get(Calendar.YEAR);
        mmonth = c.get(Calendar.MONTH);
        mday = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dpd = new DatePickerDialog(getActivity(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,int monthOfYear, int dayOfMonth){
                        String dateSet = String.valueOf(monthOfYear) + "-" + String.valueOf(dayOfMonth) + "-" + String.valueOf(year);
                        txtDate.setText(dateSet);
                    }
                }, myear, mmonth, mday);
        dpd.show();
    }
    // select date

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnPostSelectedListener) activity;
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
                lat = location.getLatitude();
                lng = location.getLongitude();
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
        Location location = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);
        if (location != null) {
            mLocationListener.onLocationChanged(location);
            // reverse geocoding
            String revGeoUrl = REVGEOBASE + String.valueOf(lat) + "," + String.valueOf(lng) + APIKEY;
            Log.d("Reverse Geocoding!!!!!!!", revGeoUrl);

            httpClientGeoCoding.post(revGeoUrl, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    String response = new String(responseBody);
                    try {
                        JSONObject resultsJSON = new JSONObject(response);
                        JSONArray resultsArray = resultsJSON.getJSONArray("results");
                        JSONObject locationAddress = resultsArray.getJSONObject(0);
                        String formattedAddress = locationAddress.getString("formatted_address");
                        postGeo = formattedAddress;
                        txtGeo.setText(formattedAddress);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                }
            });
            // reverse geocoding
        } else {
            txtGeo.setText("Location not available");
        }
    }


    public interface OnPostSelectedListener {
//        public void onCameraSelected();     // go to camera
        public void onViewAllSelected();    // go to view all
        public void onLoginSelected();      // go to login

    }

}

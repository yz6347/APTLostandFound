package android.example.com.aptlostandfound;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

public class CameraFragment extends Fragment {

    private Button btnTakePic, btnUsePic, btnViewAll;
    private ImageView imgPic;

    private OnCameraSelectedListener mListener;

    public CameraFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootview = inflater.inflate(R.layout.fragment_camera, container, false);

        btnTakePic = (Button)rootview.findViewById(R.id.CAMERA_btn_takepic);
        btnUsePic = (Button)rootview.findViewById(R.id.CAMERA_btn_usepic);
        btnViewAll = (Button)rootview.findViewById(R.id.CAMERA_btn_viewall);

        btnViewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onViewAllSelected();
            }
        });

        btnUsePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onPostSelected();
            }
        });

        return rootview;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnCameraSelectedListener) activity;
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

    public interface OnCameraSelectedListener {
        public void onViewAllSelected();    // go to view all
        public void onPostSelected();       // go to post
    }

}

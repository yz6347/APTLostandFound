package android.example.com.aptlostandfound;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

public class ViewSingleFragment extends Fragment {

    private PostClass selectedItem;

    private TextView txtTitle, txtEmail, txtCate, txtDesc, txtUserName, txtLogout;
    private ImageView postImg;

    private OnViewSingleSelectedListener mListener;

    public ViewSingleFragment() {
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
        Activity activity = getActivity();
        final View rootview = inflater.inflate(R.layout.fragment_view_single, container, false);

        selectedItem = ViewAllFragment.singlePost;

        txtTitle = (TextView)rootview.findViewById(R.id.VIEWSGL_txt_title);
        txtCate = (TextView)rootview.findViewById(R.id.VIEWSGL_txt_category);
        txtDesc = (TextView)rootview.findViewById(R.id.VIEWSGL_txt_desp);
        txtEmail = (TextView)rootview.findViewById(R.id.VIEWSGL_txt_author);
        postImg = (ImageView)rootview.findViewById(R.id.VIEWSGL_img);

        txtUserName = (TextView)rootview.findViewById(R.id.VIEWSGL_txt_helloUser);
        txtLogout = (TextView)rootview.findViewById(R.id.VIEWSGL_txt_logout);

        txtUserName.setText(MainActivity.theNAME + "!");
        txtLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.USER_NAME = "";
                Toast.makeText(getActivity().getBaseContext(), "Log out success!", Toast.LENGTH_SHORT).show();
                mListener.onLoginSelected();
            }
        });

        txtTitle.setText(selectedItem.getTitle());
        txtDesc.setText(selectedItem.getDesp());
        txtCate.setText(selectedItem.getCate());
        txtEmail.setText(selectedItem.getAuthor());

        String imgUrl = selectedItem.getImgURL();
        if(!imgUrl.isEmpty()){
            Picasso.with(activity).load(selectedItem.getImgURL()).into(postImg);
        }else{
            postImg.setImageResource(R.drawable.ic_launcher);
        }


        return rootview;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnViewSingleSelectedListener) activity;
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

    public interface OnViewSingleSelectedListener {
        public void onLoginSelected();
    }

}

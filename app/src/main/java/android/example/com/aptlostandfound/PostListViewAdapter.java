package android.example.com.aptlostandfound;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Zhang on 2014/12/12.
 */
public class PostListViewAdapter extends BaseAdapter {

    Context context;
    protected List<PostClass> postList;
    LayoutInflater inflater;

    public PostListViewAdapter(Context context, List<PostClass> postList){
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.postList = postList;
    }

    @Override
    public int getCount() {
        return postList.size();
    }

    @Override
    public PostClass getItem(int position) {
        return postList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if(convertView == null){
            holder = new ViewHolder();
            convertView = this.inflater.inflate(R.layout.listview_row, parent, false);

            holder.txtTitle = (TextView)convertView.findViewById(R.id.LIST_txt_ln1);
            holder.txtDesp = (TextView)convertView.findViewById(R.id.LIST_txt_ln2);
            holder.imgPost = (ImageView)convertView.findViewById(R.id.LIST_imgView);

            convertView.setTag(holder);
        }else{
            holder = (ViewHolder)convertView.getTag();
        }

        PostClass post = postList.get(position);
        holder.txtTitle.setText(post.getTitle());
        holder.txtDesp.setText(post.getDesp());
//        holder.imgPost.setImageResource(post.getPostImg());
        String imgURL = post.getImgURL();
//        String imgURL = "http://lh4.ggpht.com/WhgEDYyh3qjduzkvjRC2OypRqT-OhiaVF2CVta7G7BepMethdfjmmG-MCdQV_WadvNh1gIk1diOelTQETvtbseoP";
        if(!imgURL.isEmpty()){
            Picasso.with(context).load(imgURL).into(holder.imgPost);
        }
        else{
            holder.imgPost.setImageResource(R.drawable.ic_launcher);
        }

        return convertView;
    }

    private class ViewHolder{
        TextView txtTitle;
        TextView txtDesp;
        ImageView imgPost;
    }
}

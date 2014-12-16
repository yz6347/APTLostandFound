package android.example.com.aptlostandfound;

/**
 * Created by Zhang on 2014/12/12.
 */
public class PostClass {

    private String kind, title, author, desp, cate, imgURL, date;
    private double[] loc;

    public PostClass(String kind, String title, String desp, String cate, String imgURL){
        this.kind = kind;
        this.title = title;
        this.cate = cate;
        this.desp = desp;
        this.imgURL = imgURL;
    }

    public PostClass(String kind, String title, String author, String desp, String cate, String imgURL, String date, double[] loc){
        this.kind = kind;
        this.title = title;
        this.author = author;
        this.cate = cate;
        this.desp = desp;
        this.imgURL = imgURL;
        this.date = date;
        this.loc = loc;
    }

    public void setKind(String kind){
        this.kind = kind;
    }
    public void setTitle(String title){
        this.title = title;
    }
    public void setAuthor(String author){
        this.author = author;
    }
    public void setDesp(String desp){
        this.desp = desp;
    }
    public void setCate(String cate){
        this.cate = cate;
    }
    public void setImgURL(String imgURL){
        this.imgURL = imgURL;
    }
    public void setDate(String date){
        this.date = date;
    }
    public void setLoc(double lat, double lng){
        this.loc[0] = lat;
        this.loc[1] = lng;
    }


    public String getKind(){
        return kind;
    }
    public String getTitle(){
        return title;
    }
    public String getAuthor(){
        return author;
    }
    public String getDesp(){
        return desp;
    }
    public String getCate(){
        return cate;
    }
    public String getImgURL(){
        return imgURL;
    }
    public String getDate(){
        return date;
    }
    public double[] getLoc(){
        return loc;
    }
    public double getLat(){
        return loc[0];
    }
    public double getLng(){
        return loc[1];
    }



}

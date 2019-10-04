package yopy.modutalk.model;

public class UserModel {
    private String userid;
    private String uid;
    private String usernm;
    private String token;
    private String userphoto;
    private String usermsg;
    private String search;

    public UserModel() {

    }
    //private String status;

   // public String getStatus(){return status;}
    //public void setStatus(String status){this.status=status;}

    public String getSearch() {return search;}

    public void setSearch(String search){this.search=search;}

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsernm() {
        return usernm;
    }

    public void setUsernm(String usernm) {
        this.usernm = usernm;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserphoto() {
        return userphoto;
    }

    public void setUserphoto(String userphoto) {
        this.userphoto = userphoto;
    }

    public String getUsermsg() {
        return usermsg;
    }

    public void setUsermsg(String usermsg) {
        this.usermsg = usermsg;
    }


}

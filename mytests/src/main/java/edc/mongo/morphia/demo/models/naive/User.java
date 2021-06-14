package edc.mongo.morphia.demo.models.naive;

public class User {

    public String id;

    String fullName;

    transient String profileUrl;

    public User(String id, String fullName) {
        this.id = id;
        this.fullName = fullName;
        this.profileUrl = "fb.net/assets/profile/" + id + "/profile.jpg";
    }
}

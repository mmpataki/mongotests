package edc.mongo.morphia.demo.models.annotations;

import dev.morphia.annotations.*;

@Entity("users")
@Indexes(
    @Index(options = @IndexOptions(name = "uid_idx"), fields = @Field("fullName"))
)
public class User {
    @Id
    public String id;

    String fullName;

    transient String profileUrl;

    public User(String id, String fullName) {
        //this.id = id;
        this.fullName = fullName;
        this.profileUrl = "fb.net/assets/profile/" + id + "/profile.jpg";
    }

    User() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", fullName='" + fullName + '\'' +
                ", profileUrl='" + profileUrl + '\'' +
                '}';
    }
}

package edc.mongo.morphia.demo.models.naive;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class Post {

    User owner;
    String content;
    Date time = new Date();
    long views = 0;
    List<Comment> comments;

    public Post(User owner, String content, Comment... cmnts) {
        this.content = content;
        this.owner = owner;
        this.comments = Arrays.asList(cmnts);
    }

    public static Post makeDummyPost(User poster, User uc1, User uc2) {
        return new Post(
                poster, "hi friends!",
                new Comment(uc1, "hi"), new Comment(uc2, "hey")
        );
    }
}
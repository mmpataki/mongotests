package edc.mongo.morphia.demo.models.annotations;

import java.util.*;
import dev.morphia.annotations.*;

@Entity("posts")
public class Post {

    @Id
    String id;

    String content;

    @Reference
    User owner;

    @Property("postTime")
    Date time = new Date();

    @Property("postViewCount")
    long views = 0;

    List<Comment> comments;

    public Post(User owner, String content, Comment... comments) {
        id = owner.id + "-" + System.currentTimeMillis();
        this.content = content;
        this.owner = owner;
        this.comments = Arrays.asList(comments);
    }

    public static Post makeDummyPost(User poster, User uc1, User uc2) {
        return new Post(
                poster, "hi friends!",
                new Comment(uc1, "hi"), new Comment(uc2, "hey")
        );
    }

}

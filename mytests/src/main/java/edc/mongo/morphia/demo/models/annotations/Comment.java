package edc.mongo.morphia.demo.models.annotations;

import dev.morphia.annotations.Reference;

public class Comment {

    @Reference
    User commenter;

    String comment;

    long time;

    public Comment(User commenter, String comment) {
        this.commenter = commenter;
        this.comment = comment;
        this.time = System.currentTimeMillis();
    }

}

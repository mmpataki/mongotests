package edc.mongo.morphia.demo.models.naive;

public class Comment {

    User commenter;

    String comment;

    long time;

    public Comment(User commenter, String comment) {
        this.commenter = commenter;
        this.comment = comment;
        this.time = System.currentTimeMillis();
    }

}

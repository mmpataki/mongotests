package edc.mongo.morphia.demo.models.naive;

import java.util.Arrays;
import java.util.List;

public class Factory {

    public static List<Object> makeObjects() {

        User poster = new User("mpataki", "Madhusoodan Pataki");
        User cmntr1 = new User("johndoe", "John Doe");
        User cmntr2 = new User("janedoe", "Jane Doe");

        Post naivePost = Post.makeDummyPost(poster, cmntr1, cmntr2);

        return Arrays.asList(naivePost);
    }

}

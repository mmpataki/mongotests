# Morphia

Morphia is a object to document (and vice versa) conversion library used by applications to convert the POJOs to Mongo documents and vice versa. This document tries to outline the usage of this library.

## Example

1. Let's take an example of a application which allows registered users to posts blogs and also to comment on them

2. Here are the models (POJO) application uses

    ### Post.java
    ```java
    package test.morphia;
    import java.util.*;
    
    public class Post {

        User owner;
        String content;
        Date time = new Date();
        long views = 0;
        List<Comment> comments;

        public Post(User owner, String content, Comment ...comments) {
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
    ```

    ### Comment.java
    ```java
    package test.morphia;
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
    ```

    ### User.java
    ```java
    package test.morphia;
    public class User {

        String id, fullName, profileUrl;

        public User(String id, String fullName) {
            this.id = id;
            this.fullName = fullName;
            this.profileUrl = "/assets/profile/" + id + "/profile.jpg";
        }
    }
    ```

3. Here is a sample code generating the above objects.

    ### Factory.java
    ```java
    public static List<Object> makeObjects() {

        User poster = new User("mpataki", "Madhusoodan Pataki");
        User cmntr1 = new User("johndoe", "John Doe");
        User cmntr2 = new User("janedoe", "Jane Doe");

        Post naivePost = Post.makeDummyPost(poster, cmntr1, cmntr2);

        return Arrays.asList(naivePost);
    }
    ```

4. Morphia provides a easy way to save these objects in a very convinient manner. Here is a snippet...

    ```java
    Datastore dstore = ...;
    dstore.save(Factory.makeObjects());
    ```

5. Great! Now, how do you initialize the `dstore`?

    1. Create a mongo client

        ```java
        MongoClient mclient = new MongoClient("localhost", 27017);
        ```

    2. Create a morphia instance and let it know about the types of objects you are saving (it does a package scan).

        ```java
        Morphia morphia = new Morphia();
        morphia.mapPackage("test.morphia");
        ```

    3. Create a `Datastore` (Mimicking the database)

        ```java
        Datastore dstore = morphia.createDatastore(mclient, "mydatabase");
        ```

    4. Make sure indexes are created.

        ```java
        dstore.ensureIndexes();
        ```

6. Let's see how these objects are saved in the mongo?
    ```
    mongos> use mydatabase
    mongos> show collections
    Post
    mongos> db.Post.find().pretty()
    {
            "_id" : ObjectId("60c75e6f06ad0826620e2075"),
            "className" : "edc.mongo.morphia.demo.models.naive.Post",
            "owner" : {
                    "id" : "mpataki",
                    "fullName" : "Madhusoodan Pataki"
            },
            "content" : "hi friends!",
            "time" : ISODate("2021-06-14T13:49:35.895Z"),
            "views" : NumberLong(0),
            "comments" : [
                    {
                            "commenter" : {
                                    "id" : "johndoe",
                                    "fullName" : "John Doe"
                            },
                            "comment" : "hi",
                            "time" : NumberLong("1623678575895")
                    },
                    {
                            "commenter" : {
                                    "id" : "janedoe",
                                    "fullName" : "Jane Doe"
                            },
                            "comment" : "hey",
                            "time" : NumberLong("1623678575895")
                    }
            ]
    }
    ```

7. But shouldn't the `user` objects be in a different collection (because it's getting repeated). So how do we configure the `morphia` to create different collections for these objects?


8. Let's think for a minute, is there something specific about these type objects which you want to store in different collections?

    > Yes they can live independently

9. Morphius calls these `Entities` and stores them in different collections.

10. Now the question is how do you tell `morphia` that `User` is a entity?

11. Morphia provides annotations to do that. You mark the classes which you want to be entities as `Entity`. Here is an example

    ```java
    @Entity
    public class Post {
        ...
    }
    ```

12. Ok, now that the `User` is a separate entity, how will `Post` link to it? 

13. We can embed a `DBRef` field in the `Post` object which has pointers to the __collection__ and __id__ of the referred object. Here is how it looks
    ```
    mongos> db.fbposts.findOne()
    {
            "content" : "hi friends!",
            "owner" : DBRef("users", "mpataki"),
            ...
    ```

14. So here is the complete example.

    ### Post.java
    ```java
    package test.morphia;

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
    ```

    Let's take a closer look at the code .

    1. The annotation `@Entity("posts")` tells the morphia to identify Post as an `Entity` and use `posts` as the name of the collection.

    2. Annotation `@Id` identifies the field which can be used as a identifier for the documents of type `Post`

    3. `@Reference` marks the fields for which a `DBRef` has to be stored in the parent document.

    4. `@Property("postTime")` make morphia use the name `postTime` for the field used to store the value of time. Same goes for other usage of `Property`

15. Got it, now how about __index creation__? Does `morphia` handle that too? - __Yes__, it does.

16. So how does it know the index fields? - You have to tell that explicitly to `morphia`. Here is an example.

    ### User.java
    ```java
    package test.morphia;

    import dev.morphia.annotations.*;

    @Entity("users")
    @Indexes(
        @Index(options = @IndexOptions(name = "fn_idx"), fields = @Field("fullName"))
    )
    public class User {
        @Id
        public String id;

        String fullName;

        transient String profileUrl;

        public User(String id, String fullName) {
            this.id = id;
            this.fullName = fullName;
            this.profileUrl = "fb.net/assets/profile/" + id + "/profile.jpg";
        }

        /* empty constructor used while de-serializing*/
        User() {}

        /* ... getters, setters, toString ... */
    }
    ```

    ### Code overview

    1. `@Indexes` : annotates the class with index definitions
    2. `@Index` : define the indexes
        - __options__ : specifies the options used to create Index.
            - __name__ : specifies the name of the index.
        - __fields__ : specifies the list of fields (java bean field names) used for indexing.
    3. `transient` marks the field to be excluded in the document.


17. Great, now let's look at the way these are stored in the mongodb

    ```
    mongos> show collections
    posts
    users
    mongos> db.users.find().pretty()
    {
            "_id" : ObjectId("60c76ac673d3f43083d65ae6"),
            "className" : "test.morphia.User",
            "fullName" : "Madhusoodan Pataki"
    }
    {
            "_id" : ObjectId("60c76ac673d3f43083d65ae7"),
            "className" : "test.morphia.User",
            "fullName" : "John Doe"
    }
    {
            "_id" : ObjectId("60c76ac673d3f43083d65ae8"),
            "className" : "test.morphia.User",
            "fullName" : "Jane Doe"
    }
    mongos> db.posts.find().pretty()
    {
            "_id" : "null-1623681733951",
            "className" : "test.morphia.Post",
            "content" : "hi friends!",
            "owner" : DBRef("users", "60c76ac673d3f43083d65ae6"),
            "postTime" : ISODate("2021-06-14T14:42:13.951Z"),
            "postViewCount" : NumberLong(0),
            "comments" : [
                    {
                            "commenter" : DBRef("users", "60c76ac673d3f43083d65ae7"),
                            "comment" : "hi",
                            "time" : NumberLong("1623681733951")
                    },
                    {
                            "commenter" : DBRef("users", "60c76ac673d3f43083d65ae8"),
                            "comment" : "hey",
                            "time" : NumberLong("1623681733951")
                    }
            ]
    }
    ```


18. Ok, let's see how querying works. We want to query some user with `id` = `mpataki` and print them. You can read more about the query building and operators [here](https://morphia.dev/morphia/2.1/querying-old.html).

    ```java
    Datastore dstore = // ... initialize
    Query<User> qMpataki = dstore.createQuery(User.class).filter("id =", "mpataki");
    qMpataki.find().forEachRemaining(x -> {
        System.out.println(x);
    });
    ```

19. The morphia also provides a update API. Updates are carried out by specifying update operations on results of a query. Here is an example where we are updating the first name of a user object

    ```java
    Datastore dstore = // ... initialize
    
    /* objects to be updated */
    Query<User> qMpataki = dstore.createQuery(User.class).filter("id =", "mpataki");
    
    /* update operations */
    UpdateOperations<User> upds = ds2.createUpdateOperations(User.class)
        .set("fullName", "Madhusoodan P");

    /* actual update */
    dstore.update(qMpataki, upds);
    ```

## Caveats while using the `morphia` API

1. Not saving the foriegn entities explictly can cause dangling references
    - let's say you saved `Post` object but not the `User` objects then there will be dangling references (`DBRef`) for the user objects in the `Post` documents.

2. Reusing query objects.
    - If you use same query object to check the documents after an update operation, you will see cached (un-updated) results. Need to check if its a bug in `morphia`


## References

1. [Morphia docs](https://morphia.dev/morphia/1.6/index.html)

2. [EDC code](MongoConnector.java)


package edc.mongo.morphia.demo;

import com.mongodb.MongoClient;
import dev.morphia.Datastore;
import dev.morphia.Morphia;
import dev.morphia.query.Query;
import dev.morphia.query.UpdateOperations;
import dev.morphia.query.internal.MorphiaCursor;
import edc.mongo.morphia.demo.models.annotations.User;

import java.util.List;

public class MorphiaDemo {

    public static void main(String[] args) {
        new MorphiaDemo().run();
    }

    MongoClient mclient;
    Morphia morphia;

    public void run() {

        /* package containing model classes */
        String pkgName = this.getClass().getPackage().getName() + ".models";

        /* create a mongo client */
        mclient = new MongoClient("localhost", 27017);

        /* create morphia and register the classes */
        morphia = new Morphia();
        morphia.mapPackage(pkgName);

        /* make objs with no annotations */
        List<Object> naiveObjs = edc.mongo.morphia.demo.models.naive.Factory.makeObjects();

        Datastore ds1 = getDS("morphia-song");

        ds1.save(naiveObjs);

        /* make objs marked with rt-annotations */
        List<Object> annotatedObjs = edc.mongo.morphia.demo.models.annotations.Factory.makeObjects();

        Datastore ds2 = getDS("morphia-duet");

        ds2.save(annotatedObjs);

        /* queries */
        Query<User> qMpataki = ds2.createQuery(User.class).filter("id =", "mpataki");

        /* search and print */
        execute(qMpataki);

        /* update */
        UpdateOperations<User> upd = ds2.createUpdateOperations(User.class)
                .set("fullName", "Madhusoodan P");

        ds2.update(qMpataki, upd);

        /* print again */
        execute(qMpataki);
    }

    private void execute(Query<User> q) {
        MorphiaCursor<User> curs = q.find();

        curs.forEachRemaining(x -> {
            System.out.println(x);
        });
    }

    public Datastore getDS(String dbName) {

        /* handle to store the objects */
        Datastore dstoreNaive = morphia.createDatastore(mclient, dbName);

        /* create indexes needed */
        dstoreNaive.ensureIndexes();

        return dstoreNaive;
    }

}

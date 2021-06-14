# Sharding

\- is the process of splitting the collection data in to multiple pieces so that these pieces can be served by different nodes.

### Terminology
- __node__ : a mongod process
- __chunk__ : a piece of collection

## Architecture

### Componenets
- __shard__ _[N]_ : a node or a replicaset which bears few __chunks__ of sharded collections.
- __configserver__ _[1]_: a node or a replicaset which bears the information about the chunk and their placements in the cluster
- __mongos__ _[M]_: a process which acts as a router & aggregator for client requests.
- __client__ _[X]_
- __sharded database__ _[Y]_
    - __sharded collections__ _[Z]_
        - __shardkey__ _[1]_ : a field or a set of fields which are used to decide a chunk for given document. These field[s] should always be present on all documents of the collection.
        - __chunks__ _[ 1 .. |`uniq(shardkey)`| ]_

## Operations (Interactions)

### Read
1. client connects to mongos to execute queries on collections
2. mongos communicates with configserver to know where the chunks to serve this query exists.
3. mongos communicates with each such shard to fetch the results of the same query.
4. If query contains a filter for shardkey, the query can be served by __targetted shards__ or it becomes a __scatter-gather query__.
5. If required the mongos processes the responses and returns back a cummulative response.

### Write
1. client connects to mongos to insert document in collection
2. mongos communicates with configserver to know where the chunk this document belongs to exists.
3. mongos communicates with that shard to insert the document

## Shard key
It's important to choose the shardkey wisely as cardinality of shard key it decides the cardinality and size of the chunks of a collection which affects the performance. Also it shouldn't be a monotonically increasing value such as timestamp as these create `write hotspots`.

## Deployment
### Prerequisites
- A collection of nodes / replicasets

### Process
- Start the configserver with the below config added
    ```
    sharding:
        clusterRole: configsvr
    ```
- Start the `mongos` with below config added
    ```
    sharding:
        configDB: <mongo-conn-string-to-config-server>
    ```
- Start the shards with the below config added
    ```
    sharding:
        clusterRole: shardsvr
    ```
- Connect to `mongos` and execute `addShard` for each shard you want to add.
    ```
    $ mongo --host <mongos-host> --port <mongos-port>
    > sh.addShard("<connection string of shard>")
    ```
- Enable sharding for database
    ```
    > sh.enableSharding("databasename")
    ```
- Create a index on shard key and shard
    ```
    > db.collection_name1.createIndex({shardKey1: 1})
    > sh.shardCollection("databasename.collection_name1", {shardKey1: 1})
    > sh.shardCollection("databasename.collection_name2", {shardKey2: "hashed"})
    ```
- Check the status of sharding
    ```
    > sh.status()
    ```

employee
addharid name
a1
a11
a2
a3

a4
a44
a5




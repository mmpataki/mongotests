# Replication

\- is the process of replicating the documents among a group of configured mongo nodes. It's used to achieve
- high availability
- fault tolerance


### Terminology

- __Node__ : a mongod process
- __Replicaset__ : a group of nodes which work together replicate the docs
- __MDL__ : More details later
- __Primary node__ : a mongo node responsible for carrying out client operations
- __Secondary node__ : a mongo node responsible for replicating the data written to primary node
- __Arbiter node__ : a mongo node which exists to make election easier (to make the replicaset node count odd)

## Architecture
- Nodes which are present in a replicaset hold a election and decide a __primary node__.
- New nodes can be added to replicaset and they will become __secondary__ / __arbiter__.
- Primary node accepts all the operations from client and runs them.
- Secondary nodes apply these same changes to their oplog as well _(MDL)_.
- Secondary nodes heartbeat to the primary and if they find out that the primary is unavailable, they elect a new primary _(MDL)_

## Operations in a replicaset
### Prerequisites
- a connection string for the client to connect, which can be pointing to [___reqd___]
    - the replicaset
    - a particular node in replicaset

- a read / write concern [___optional___] _(MDL)_

### Write
- Client connects with the connection string of replicaset to __primary__
- Issues a write request with the write concern
- Primary serves this request and responds back once write concern is addrressed. _(MDL)_

### Read
- Client connects to a node (__primary__ if used replicaset connection string)
- Issues a read request
- The node responds to the request based on the read concern

## Deployment

### Prerequisites
1. __Odd__ number of nodes.
2. A __name__ for the replicaset.

### Method
- Assume you want to make a replicaset of size 3 and node addresses are 
    - abc.com:10001
    - abc.com:10002
    - abc.com:10003
- Add the replicaset name to the configuration of all the nodes in the replicaset.
- Restart the nodes.
- From a mongo shell 
    - connect to one of the mongo server
        ```
        $ mongo mongodb://abc.com:10001
        > rs.initiate()
        ```
    - switch to the db `admin` and run
        ```
        > use admin
        > rs.add("abc.com:10002")
        > rs.add("abc.com:10003")
        ```
    - check the status
        ```
        > rs.status()
        ```

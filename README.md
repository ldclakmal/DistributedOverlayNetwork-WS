# Content Searching in a Distributed Overlay Network
## CS4262 Distributed Systems

Simple overlay-based solution that allows a set of nodes to share contents (e.g., music files) among each other. Consider a set of nodes connected via some overlay topology.
Each of the nodes has a set of files that it is willing to share with other nodes. A node in the system (X) that is looking for a particular file issues a query to identify a node (Y) containing that particular file.
Once the node is identified, the file can be exchanged between X and Y.

# Dependencies

---
- java_version  : 1.8
- maven_version : 3.5
---

## Set up the project

- Make a clone of this project

- Build the project in order to create `.jar` file
```
$ mvn clean install
```

- Execute `.jar` file created in `/target` directory in each node providing following attributes

```
-b  ip of the bootstrapserver
-i  ip of the node
-p  port of the node
-u  username of the node
```

#### Example

```
$ java -jar node.jar -b 127.0.0.1 -i 127.0.0.1 -p 44411 -u node1
$ java -jar node.jar -b 127.0.0.1 -i 127.0.0.1 -p 44422 -u node2
$ java -jar node.jar -b 127.0.0.1 -i 127.0.0.1 -p 44433 -u node3
```

### Notes
Before start nodes bootstrap server should start.

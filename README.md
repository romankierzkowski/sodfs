Self-organizing Distributed File System
=====

**SoDFS** is an outcome of my Master Thesis. It is distributed file system which consist of number of nodes which stores file replicas. The SoDFS relocates the replicas according to distributed algorithm of my design. The algorithm called SoRPA (Self-organizing Replica Placement Algorithm) is decentralized - the file placement emerges form the autonomous decision of each nodes. I have developed the system during my stay at Stuttgart University and tested its performance in NET emulation testbed.

License
--------

SoDFS is avilable under **GPL**.


Features
--------

* SMB/CIFS, NFS interface.
* Fault tolerant - files stored across nodes in multiple replicas.
* Self-organizing Replica Placement Algorithm.
* Two models of read/write consistency: total ordering, FIFO.
* Writes possible during replica relocation.


Repo 
-----

The SoDFS node is run as a plugin of [Alfresco JLAN](http://sourceforge.net/projects/alfresco/files/JLAN/), which is also available under GPL. The contents of this repo:

* server - The file system engine - metadata server & node plugin.
* test-client - Test client for SoDFS. Used for performance tests.
* master_thesis.pdf - My Master's Thesis on SoDFS.

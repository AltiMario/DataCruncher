# SeerDataCruncher release 1.1
SeerDataCruncher is a Data Quality Firewall, Data Quality Monitor and ETL middleware to manage data streams on the fly.


----------------------------- 
### To run

1. Download last stable [war](http://see-r.com/wp-content/upload/SeerDataCruncher-1.1.war) or generate it from the source code.

2. Create a new SQL database called "datacruncher", we use MySQL as default but you can choose whatever you like.

3. Modify the persistence.xml (into the war file) using your db credentials

4. Install Mongodb to store the datastreams (optional)


-----------------------------
### To access

The credential for the access (required via a login popup) is admin:admin

-----------------------------
### JVM configuration parameters

-Xms512m

-Xmx1024m

-Xss1024k

-XX:MaxPermSize=256m

-XX:NewSize=256m

-----------------------------------------------
### Architecture

![datacruncher_architecture.png](http://see-r.com/wp-content/uploads/2016/04/architecture.png)

# SeerDataCruncher release 1.1
SeerDataCruncher is a Data Quality Firewall, Data Quality Monitor and ETL middleware to manage data streams on the fly.


-----------------------------
### To run in a standard way

1. Download the last stable [WAR](http://see-r.com/wp-content/uploads/SeerDataCruncher-1.1.war) or build it from the source code.

2. Create a new SQL database called "datacruncher", we use MySQL as default but you can choose whatever you like.

3. Modify the persistence.xml (into the war file) using your db credentials

4. Install Mongodb to store the datastreams (optional)

5. Deploy the war on Tomcat

### or run via docker

docker run altimario/seerdatacruncher

-----------------------------
### To access

The credentials required by the login popup are
admin:admin

-----------------------------
### JVM configuration parameters for better performance

-Xms512m

-Xmx1024m

-Xss1024k

-XX:MaxPermSize=256m

-XX:NewSize=256m

-----------------------------------------------
### Architecture

![datacruncher_architecture.png](http://see-r.com/wp-content/uploads/2016/04/architecture.png)

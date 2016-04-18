# SeerDataCruncher release 1.1
SeerDataCruncher is a Data Quality Firewall, Data Quality Monitor and ETL middleware to manage data streams on the fly.


----------------------------- 
**To run**

1. Modify the persistence.xml and create an empty SQL db (we use MySQL as default) called datacruncher

2. Install Mongodb


----------------------------- 
**To access**

The credential for the access (the requested info of the login popup) is admin:admin

----------------------------- 
**JVM configuration parameters**

-Xms512m

-Xmx1024m

-Xss1024k

-XX:MaxPermSize=256m

-XX:NewSize=256m

-----------------------------------------------
**Architecture**

![datacruncher_architecture.png](http://see-r.com/wp-content/uploads/2016/01/architecture.png)

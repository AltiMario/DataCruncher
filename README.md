# SeerDataCruncher release 1.1
SeerDataCruncher is a Data Quality Firewall, Data Quality Monitor and ETL middleware to manage data streams on the fly.


----------------------------- 
**To run**

1. Install MySQL

2. Modify the persistence.xml and create an empty db (MySQL by default) called datacruncher

3. Install Mongodb


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

------------------------------------------------
**New item in menu**

For adding new menu item we need to do following:

1) Add the entry in Activity.java

2) Add same  entries for required roles in RoleActivityDao.java

3) clear the following db tables:

    a- jv_activity

    b- jv_role_activity

4) add js code in index.js related to the new entity

-----------------------------------------------
**Architecture**

![datacruncher_architecture.png](http://see-r.com/wp-content/uploads/2016/01/architecture.png)

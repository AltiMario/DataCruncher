# SeerDataCruncher release 1.1
SeerDataCruncher is a Data Quality Firewall, Data Quality Monitor and ETL middleware to manage data streams on the fly.


----------------------------- 
**SeerDataCruncher modules**

There's one parent-module (seerdatacruncher-parent) which has only one pom and no other files. This pom is parent for all child modules, and has dependencies which are common for all child modules. This means that all new common dependencies must be introduced in this pom.

1. To build project first of all you should checkout parent-module and then seerdatacruncher from git into local workspace.

2. Build projects in this order: seerdatacruncher-parent, seerdatacruncher. The maven command can be like this: 'mvn clean install -U'

3. Modify the persistence.xml and create an empty db (MySQL by default) called datacruncher

4. If 2 builds are successfully completed, you'll find war in seerdatacruncher/target. It can be deployed on tomcat (version > 7.0).

5. Install mongodb


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

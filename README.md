# DataCruncher 
DataCruncher is a Data Quality Firewall suite.

It includes an advanced UI for schema definition, governance, real-time data monitoring, ETL, business rules, automatic forms generation, alerting, mobile interface.

## What's new on the release 2.0
After long time on idle, the project is back on activity!!!

Two big feature are just released; the client side data validation and the automatic forms generation.

New things are coming to make it more updated, secure, stable and scalable.

-----------------------------
### To run manually

1. Build it from the source code

2. Create a new SQL database called "datacruncher", we use MySQL as default but you can choose whatever you like.

3. Modify the persistence.xml (into the war file) using your db credentials

4. Install Mongodb to store the datastreams (optional)

5. Deploy the war on Tomcat

### or run via docker

  - `docker-compose up -d`
  - data and logs will be stored in `./data`


-----------------------------
### To access

The credentials required by the login popup are
admin:admin

-----------------------------
## Development info

### JVM configuration parameters for better performance

-Xms512m

-Xmx1024m

-Xss1024k

-XX:MaxPermSize=256m

-XX:NewSize=256m

### Dependencies

Project uses many libraries, so to avoid conflicts on transitive dependencies we prefer to use version range dependencies.

1. When we know minimum version that is required to compile and work successfully, we use open range with fixed minimum value:
```
<dependency>
    <groupId>commons-fileupload</groupId>
    <artifactId>commons-fileupload</artifactId>
    <version>[1.3.3,)</version>
</dependency>
```
This means we require commons-fileupload library version at least 1.3.3 or higher.
Minimum version can be easily determined by @since Javadoc of used features or Github alert insights that suggest minimum version where all security vulnerabilities are fixed.

2. When we know that newer versions of library break our application, we either limit maximum acceptable version or use fixed version of library^
```
<dependency>
    <groupId>com.sun.xml.bind</groupId>
    <artifactId>jaxb-xjc</artifactId>
    <version>2.1.17</version>
</dependency>
```

Read more about Maven version ranges: https://docs.oracle.com/middleware/1212/core/MAVEN/maven_version.htm#MAVEN402

### Testing

You can skip unit-testing phase during build using this command:
```mvn clean package -Dmaven.test.skip=true```

Some tests use embedded HTTP server (Jetty). Server is started on port described by "jetty.port" property in src/test/resources/test.properties file:
```jetty.port=8999```

Check that this port is free before tests run or specify different port number.


### New item in menu

For adding new menu item we need to do following:

1) Add the entry in Activity.java

2) Add same  entries for required roles in RoleActivityDao.java

3) clear the following db tables:

    a- jv_activity

    b- jv_role_activity

4) add js code in index.js related to the new entity


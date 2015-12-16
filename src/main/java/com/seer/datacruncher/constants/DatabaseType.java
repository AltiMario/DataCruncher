/*
 * Copyright (c) 2015  www.see-r.com
 * All rights reserved
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */


package com.seer.datacruncher.constants;
/*
Do not change, they are used in frontend and backend
*/

public final class DatabaseType {
    public static final int MySQL = 1;
    public static final String MySQL_driver = "com.mysql.jdbc.Driver";
    public static final String MySQL_url = "jdbc:mysql://";
    public static final String MySQL_conn = "/";

    public static final int ORACLE = 2;
    public static final String ORACLE_driver = "oracle.jdbc.driver.OracleDriver";
    public static final String ORACLE_url = "jdbc:oracle:thin:@//";
    public static final String ORACLE_conn = "/";

    public static final int SQLServer = 3;
    public static final String SQLServer_driver = "net.sourceforge.jtds.jdbc.Driver";
    public static final String SQLServer_url = "jdbc:jtds:sqlserver://";
    public static final String SQLServer_conn = "/";

    public static final int PostgreSQL = 4;
    public static final String PostgreSQL_driver = "org.postgresql.Driver";
    public static final String PostgreSQL_url = "jdbc:postgresql://";
    public static final String PostgreSQL_conn = "/";

    public static final int DB2 = 5;
    public static final String DB2_driver = "com.ibm.db2.jcc.DB2Driver";
    public static final String DB2_url = "jdbc:db2://";
    public static final String DB2_conn = "/";

    public static final int SQLite = 6;
    public static final String SQLite_driver = "org.sqlite.JDBC";
    public static final String SQLite_url = "jdbc:sqlite:";
    public static final String SQLite_conn = "/";

    public static final int Firebird = 7;
    public static final String Firebird_driver = "org.firebirdsql.jdbc.FBDriver";
    public static final String Firebird_url = "jdbc:firebirdsql://";
    public static final String Firebird_conn = "/";

    public static final int SAPDB = 8;
    public static final String SAPDB_driver = "com.sap.dbtech.jdbc.DriverSapDB";
    public static final String SAPDB_url = "jdbc:sapdb://";
    public static final String SAPDB_conn = "/";

    public static final int HSQLDB = 9;
    public static final String HSQLDB_driver = "org.hsqldb.jdbcDriver";
    public static final String HSQLDB_url = "jdbc:hsqldb:";
    public static final String HSQLDB_conn = "/";
}

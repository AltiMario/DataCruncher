/*
 * Copyright (c) 2019  Altimari Mario
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

Ext.namespace('Analysis.data');

Analysis.data.db_types = [
        ['oracle_native', 'Oracle Native Client', 'Oracle Native Client'],
        ['oracle_windows', 'Oracle Windows Bridge', 'Oracle Windows Bridge'],
        ['mysql_client', 'Mysql Native Client', 'Mysql Native Client'],
        ['mysql_windows', 'Mysql Windows Bridge', 'Mysql Windows Bridge'],
        ['sqlserver_bridge', 'SQLServer Windows Bridge', 'SQLServer Windows Bridge'],
        ['access_bridege', 'Access Windows Bridge', 'Access Windows Bridge'],
        ['other_jdbc', 'Others (JDBC Bridge)', 'Others (JDBC Bridge)'],
        ['other_bridge', 'Others (Windows Bridge)', 'Others (Windows Bridge)']
       
        
    ];

/*
 * DataCruncher
 * Copyright (c) Mario Altimari. All rights reserved.
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
 *
 */

package com.datacruncher.validation;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import com.datacruncher.persistence.manager.QuickDBRecognizer;
import org.apache.log4j.Logger;

import com.datacruncher.connection.ConnectionPoolsSet;
import com.datacruncher.constants.FieldType;
import com.datacruncher.jpa.dao.DaoSet;
import com.datacruncher.jpa.entity.SchemaEntity;
import com.datacruncher.jpa.entity.SchemaFieldEntity;
import com.datacruncher.utils.generic.I18n;

public class IndexIncrementalValidation implements DaoSet {

	private static Logger log = Logger.getLogger(IndexIncrementalValidation.class);

	private IndexIncrementalValidation() {
	}

	public static String validate(long schemaId) {

		class Pair {
			int id;
			BigDecimal value;
		}

		SchemaEntity schemaEntity = schemasDao.find(schemaId);

		if (!schemaEntity.getIsIndexedIncrement()) {
			return null;
		}

		if (!schemaEntity.getPublishToDb()) {
			return null;
		}

		if (schemaEntity.getIdDatabase() <= 0) {
			return null;
		}

		String message = null;
		List<SchemaFieldEntity> list = schemasDao.retrieveAllLeaves(schemaEntity.getIdSchema());
		String schemaName = QuickDBRecognizer.getSchemaNamePlusVersion(schemaEntity);
		Connection connection = ConnectionPoolsSet.getConnection(schemaEntity.getIdDatabase());

		try {

			for (SchemaFieldEntity ent : list) {

				if (!ent.isIndexIncremental()) {
					continue;
				}

				if (ent.getIdFieldType() != FieldType.numeric) {
					continue;
				}

				String fieldName = ent.getName();
				String sql = MessageFormat.format("SELECT id, {0} FROM {1}", fieldName, schemaName);
				Statement statement = connection.createStatement();
				ResultSet rs = statement.executeQuery(sql);
				List<Pair> resList = new ArrayList<Pair>();
				while (rs.next()) {
					Pair pair = new Pair();
					pair.id = rs.getInt(1);
					pair.value = new BigDecimal(rs.getObject(2).toString());
					resList.add(pair);
				}
				rs.close();
				statement.close();

				// Verifica che i valori siano in progressione
				BigDecimal previous = null;
				for (Pair pair : resList) {
					
					if (pair == null) {
						continue;
					}
					
					if (previous == null) {
						previous = pair.value;
						continue;
					}
					
					if (previous.compareTo(pair.value) < 0) {
						previous = pair.value;
						continue;
					}

					String deletesql = MessageFormat.format("DELETE FROM {0} WHERE id = {1}", schemaName, pair.id);
					statement = connection.createStatement();
					statement.executeUpdate(deletesql);
					statement.close();

					if ( message == null ) {
						message = MessageFormat.format(I18n.getMessage("message.indexIncrementWarn"), pair.value);
					}
					else {
						message = message += ", " + MessageFormat.format(I18n.getMessage("message.indexIncrementWarn"), pair.value);
					}

				}

			}

		} catch (SQLException e) {
			log.error("Sql Query execution error", e);
		} finally {
			try {
				connection.close();
			} catch (SQLException e) {
				log.error("Sql Query execution error", e);
			}
		}

		return message;

	}

}

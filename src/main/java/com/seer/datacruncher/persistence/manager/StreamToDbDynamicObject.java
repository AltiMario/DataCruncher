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

package com.seer.datacruncher.persistence.manager;

import java.util.Map;

import javax.persistence.EntityManager;

public class StreamToDbDynamicObject {
	
	private Map<String, Map<String, String>> insertedFields;
	private Object object;
	private EntityManager entityManager;
	private boolean isLoadedFields;
	
	public boolean isLoadedFields() {
		return isLoadedFields;
	}
	public void setLoadedFields(boolean isLoadedFields) {
		this.isLoadedFields = isLoadedFields;
	}
	public Map<String, Map<String, String>> getInsertedFields() {
		return insertedFields;
	}
	public void setInsertedFields(Map<String, Map<String, String>> insertedFields) {
		this.insertedFields = insertedFields;
	}
	public Object getObject() {
		return object;
	}
	public void setObject(Object object) {
		this.object = object;
	}
	public EntityManager getEntityManager() {
		return entityManager;
	}
	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}
}

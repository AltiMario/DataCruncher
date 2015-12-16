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

package com.seer.datacruncher.utils.annotations;

import com.seer.datacruncher.jpa.entity.DatastreamEntity;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Map;

import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

public class AnnotationsUtils {
	
	private AnnotationsUtils () {
		//never invoked
	}
	
	/**
	 * before:
	 * 	 * class DatastreamEntity { 
	 * @Table(name="jv_datastreams") }
	 * 
	 * after: 
	 * class DatastreamEntity {
	 * @Table(name="jv_datastreams" schema="schemaAttrValue") }
	 * 
	 * Kundera will not work without 'schema' annotation attribute specified. This method 
	 * inserts 'schema' attribute with 'dbName + @ + persUnitName' as a value. Example: 'jv@mongoPU'
	 * 
	 * This method must be invoked for Kundera JPA Persistence, before entityManager initialization.
	 */
	@SuppressWarnings("unchecked")
	public static void setSchemaAnnotationAttrForDatastreamEntity(final String schemaAttrValue) {
		final Table oldAnnotation = DatastreamEntity.class.getAnnotation(Table.class);
		Annotation newAnnotation = new Table() {

			@Override
			public Class<? extends Annotation> annotationType() {
				return oldAnnotation.annotationType();
			}

			@Override
			public UniqueConstraint[] uniqueConstraints() {
				return oldAnnotation.uniqueConstraints();
			}

			@Override
			public String schema() {
				return schemaAttrValue;
			}

			@Override
			public String name() {
				return oldAnnotation.name();
			}

			@Override
			public String catalog() {
				return oldAnnotation.catalog();
			}
		};

		Field field = null;
		try {
			field = Class.class.getDeclaredField("annotations");
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
		field.setAccessible(true);
		Map<Class<? extends Annotation>, Annotation> annotations;
		try {
			annotations = (Map<Class<? extends Annotation>, Annotation>) field.get(DatastreamEntity.class);
			annotations.put(Table.class, newAnnotation);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
}

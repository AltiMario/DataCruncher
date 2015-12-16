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

package com.seer.datacruncher.validation;

import com.seer.datacruncher.utils.generic.I18n;
import com.seer.datacruncher.utils.validation.ComuneAttributes;
import org.apache.log4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class KeyConstraint {
	private static Logger log = Logger.getLogger(KeyConstraint.class);
	
	private static boolean isValueInAttrs(ComuneAttributes attrs, String field, String value) {
		boolean b = false;
		try {
			Method m = attrs.getClass().getMethod("get" + Character.toUpperCase(field.charAt(0)) + field.substring(1));
			String res = (String) m.invoke(attrs);
            value = value.toLowerCase().trim();
			if (res.toLowerCase().equals(value)) {
				b = true;
			}
		} catch (SecurityException e) {
			log.error("KeyConstraint :: SecurityException", e);
		} catch (NoSuchMethodException e) {
			log.error("KeyConstraint :: NoSuchMethodException", e);
		} catch (IllegalArgumentException e) {
			log.error("KeyConstraint :: IllegalArgumentException", e);
		} catch (IllegalAccessException e) {
			log.error("KeyConstraint :: IllegalAccessException", e);
		} catch (InvocationTargetException e) {
			log.error("KeyConstraint :: InvocationTargetException", e);
		}
		return b;
	}

	public synchronized static ComuneAttributes findValueInObjectList(List<ComuneAttributes> list, String field, String value) {
		for (ComuneAttributes attrs : list) {
			if (isValueInAttrs(attrs, field, value)) {
				return attrs;
			}
		}
		return null;
	}

	public synchronized static boolean isFound(List<ComuneAttributes> list, String keyName, String keyValue) {
		ComuneAttributes c = findValueInObjectList(list, keyName, keyValue);
		return c != null;
	}
	
	public synchronized static ResultStepValidation validation(Object elemStream, ComuneAttributes attrs)
			throws IllegalAccessException, NoSuchFieldException {
		ResultStepValidation result = new ResultStepValidation();
		result.setValid(false);
		try {
			// find the first key-value of the stream
			Field fld[] = elemStream.getClass().getDeclaredFields();
			int j = 0;
			String keyName = null;
			String keyValue = null;
			boolean found = true;
			while (j < fld.length && found) {
				fld[j].setAccessible(true);
				keyName = fld[j].getName();
				keyValue = (String) fld[j].get(elemStream);
				if (keyValue != null) {
					found = isValueInAttrs(attrs, keyName, keyValue);
				}
				j++;
			}
			if (found) {
				result.setValid(true);
				result.setMessageResult(I18n.getMessage("success.validationOK"));
			} else {
				result.setMessageResult(I18n.getMessage("error.relationship") + " " + keyName + " => " + keyValue + " ");
			}
		} catch (Exception exception) {
			log.error("Key Constraint Validation Exception : " + exception);
			result.setMessageResult(I18n.getMessage("error.system"));
		}
		return result;
	}
}

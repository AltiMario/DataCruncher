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

package com.seer.datacruncher.utils.generic;

import java.util.Locale;

import com.seer.datacruncher.spring.AppContext;

import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

public class I18n {

	private I18n() {
	}

	private static ReloadableResourceBundleMessageSource bundle;
	
	private static ReloadableResourceBundleMessageSource getBundle() {
		if (bundle == null) {
			bundle = (ReloadableResourceBundleMessageSource) AppContext.getApplicationContext()
					.getBean("messageSource");
		}
		return bundle;
	}
	
	/**
	 * Resolve the given code as message in the default Locale,
	 * returning <code>null</code> if not found. Does <i>not</i> fall back to
	 * the code as default message. Invoked by <code>getMessage</code> methods.
	 * @param code the code to lookup up, such as 'calculator.noRateSet'
	 * @return the resolved message, or <code>null</code> if not found
	 * @see #getMessage(String, Object[], String, Locale)
	 * @see #getMessage(String, Object[], Locale)
	 * @see #getMessage(MessageSourceResolvable, Locale)
	 * @see #setUseCodeAsDefaultMessage
	 */		
	public static String getMessage(String message) {
		return getBundle().getMessage(message,  null, Locale.getDefault());
	}
	
	/**
	 * Resolve the given code and arguments as message in the given Locale,
	 * returning <code>null</code> if not found. Does <i>not</i> fall back to
	 * the code as default message. Invoked by <code>getMessage</code> methods.
	 * @param code the code to lookup up, such as 'calculator.noRateSet'
	 * @param args array of arguments that will be filled in for params
	 * within the message
	 * @param locale the Locale in which to do the lookup
	 * @return the resolved message, or <code>null</code> if not found
	 * @see #getMessage(String, Object[], String, Locale)
	 * @see #getMessage(String, Object[], Locale)
	 * @see #getMessage(MessageSourceResolvable, Locale)
	 * @see #setUseCodeAsDefaultMessage
	 */	
	public static String getMessage(String message, Object[] object, Locale locale) {
		return getBundle().getMessage(message,  object, locale);
	}	
}

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

package com.datacruncher.validation.common;

import com.datacruncher.validation.ResultStepValidation;
import com.datacruncher.utils.validation.SingleValidation;

public class BIC implements SingleValidation {
	private String invalidCause = null;
	private String bic;
	
	/**
	 * Constructor with bic code
	 * @param bic the bic code to use in this bic
	 */
	public BIC(String bic) {
		this.bic = bic;
	}
	/**
	 * Default constructor
	 */
	public BIC() {
	}
	/**
	 * Get the bic code of this bic.
	 * This method does not guarantee that the bic is valid. use {@link #isValid()}
	 * 
	 * @return a string with the code
	 */
	public String getBic() {
		return bic;
	}
	/**
	 * Set the bic code for this bic
	 * @param bic the bic code
	 */
	public void setBic(String bic) {
		this.bic = bic;
	}
	/**
	 * Get a human readable (english) string that gives information about why the bic was found invalid.
	 * @return a string or <code>null</code> if there's no invalid cause set
	 */
	public String getInvalidCause() {
		return invalidCause;
	}
	
	/**
	 * Validate a bic.
	 * Currently only checks that lenght is 8 or 11 and the country code is valid
     *
     * @param bic the string to validate
	 * @return <code>true</code> if the bic is found to be valid and <code>false</code> in other case
	 * @throws IllegalStateException if bic is <code>null</code>
	 */
    public boolean isValid(String bic) {
        this.bic=bic;
        return isValid();
    }
    /**
     * Validate a bic.
     * Currently only checks that lenght is 8 or 11 and the country code is valid
     *
     * @param bic the string to validate
     * @return <code>ResultStepValidation</code>
     */
    public ResultStepValidation checkValidity(String bic) {
        ResultStepValidation result = new ResultStepValidation();
        this.bic=bic;
        try{
            if(isValid()){
                result.setValid(true);
            }else{
                result.setValid(false);
                result.setMessageResult("BIC: [" + bic+ "] wrong."+invalidCause );

            }
        }catch(Exception e)  {
            result.setValid(false);
            result.setMessageResult("BIC: [" + bic+ "] wrong. " +e.getMessage());
        }
        return result;
    }
	private boolean isValid() {
		if (this.bic==null){
            this.invalidCause = "BIC is nul";
            return false;
        }
		this.invalidCause = null;
		if ( !( this.bic.length() == 8 || this.bic.length() == 11)) {
			this.invalidCause = "BIC must be 8 or 11 chars, got "+this.bic.length();
			return false;
		}
		final String country = this.bic.substring(4,6);
		if (!ISOCountries.getInstance().isValid(country.toUpperCase())) {
			this.invalidCause = "Invalid country code: "+country;
			return false;
		}
		return true;
	}

}

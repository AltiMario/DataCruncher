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

package com.seer.datacruncher.validation;

import java.util.HashSet;
import java.util.Set;

public class ResultStepValidation {
    boolean isValid;
    String messageResult;
    boolean isWarning;
    private Object jaxbObject;
    private Set<String> failedNodesPaths = new HashSet<String>();

    public Set<String> getFailedNodesPaths() {
		return failedNodesPaths;
	}

	public void addFailedNodePath(String failedNodePath) {
		failedNodesPaths.add(failedNodePath);
	}

	public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }

    public String getMessageResult() {
        return messageResult;
    }

    public void setMessageResult(String messageResult) {
        this.messageResult = messageResult;
    }

	public boolean isWarning() {
		return isWarning;
	}

	public void setWarning(boolean isWarning) {
		this.isWarning = isWarning;
	}

    public Object getJaxbObject() {
        return jaxbObject;
    }

    public void setJaxbObject(Object jaxbObject) {
        this.jaxbObject = jaxbObject;
    }
}

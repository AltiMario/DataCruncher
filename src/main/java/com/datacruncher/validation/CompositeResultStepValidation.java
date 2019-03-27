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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CompositeResultStepValidation extends ResultStepValidation {
    private final List<ResultStepValidation> results;

    public CompositeResultStepValidation() {
        results = new ArrayList<>();
    }

    public void addResult(ResultStepValidation result) {
        results.add(result);
    }

    @Override
    public boolean isValid() {
        return results.stream().map(r -> r.isValid()).reduce(true, (r1, r2) -> r1 && r2);
    }

    @Override
    public void setValid(boolean valid) {
    }

    @Override
    public String getMessageResult() {
        return results.stream().map(r -> r.getMessageResult()).collect(Collectors.joining("\n"));
    }

    @Override
    public void setMessageResult(String messageResult) {
    }

    @Override
    public boolean isWarning() {
        return results.stream().map(r -> r.isValid()).reduce(true, (r1, r2) -> r1 && r2);
    }

    @Override
    public void setWarning(boolean isWarning) {
    }
}

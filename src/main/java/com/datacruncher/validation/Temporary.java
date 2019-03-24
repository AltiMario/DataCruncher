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

import com.datacruncher.jpa.entity.ApplicationEntity;
import com.datacruncher.jpa.entity.SchemaEntity;
import com.datacruncher.utils.generic.I18n;

import java.util.Calendar;

import org.apache.log4j.Logger;

public class Temporary {

    private final Logger log = Logger.getLogger(this.getClass());

    public ResultStepValidation temporaryValidation(SchemaEntity schemaEntity, ApplicationEntity appEntity) {
        ResultStepValidation result = new ResultStepValidation();
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.getTime();
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            Long dateNow = calendar.getTimeInMillis();
            result.setValid(false);
            if (appEntity.getStartDate() != null && appEntity.getStartDate().getTime() > dateNow) {
                result.setMessageResult(I18n.getMessage("error.validationApplicationDateHigher"));
            } else if (appEntity.getEndDate() != null && appEntity.getEndDate().getTime() < dateNow) {
                result.setMessageResult(I18n.getMessage("error.validationApplicationDateLower"));
            } else if (schemaEntity.getStartDate() != null && schemaEntity.getStartDate().getTime() > dateNow) {
                result.setMessageResult(I18n.getMessage("error.validationSchemaDateHigher"));
            } else if (schemaEntity.getEndDate() != null && schemaEntity.getEndDate().getTime() < dateNow) {
                result.setMessageResult(I18n.getMessage("error.validationSchemaDateLower"));
            } else {
                result.setValid(true);
                result.setMessageResult(I18n.getMessage("success.validationOK"));
            }
        } catch (Exception exception) {
            log.error("Temporary Validation Exception : " + exception);
            result.setMessageResult(I18n.getMessage("error.system"));
        }
        return result;
    }
}

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

public class ValidationUtils {

    public static void checkActive(ApplicationEntity appEntity, SchemaEntity schemaEntity) throws Exception {
        if (appEntity != null && appEntity.getIsActive() != null) {
            if (appEntity.getIsActive() == 0) {
                throw new Exception(I18n.getMessage("error.deactivatedApplication"));
            } else if (schemaEntity.getIsActive() != null && schemaEntity.getIsActive() == 0) {
                throw new Exception(I18n.getMessage("error.deactivatedSchema"));
            }
        }
    }
}

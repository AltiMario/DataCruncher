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

package com.datacruncher.factories.streams;

import java.util.List;
import java.util.Map;

import com.datacruncher.constants.StreamType;
import com.datacruncher.jpa.entity.SchemaEntity;

/**
 * Gets instances that convert all current schema's streams to downloadable representation.
 *
 */
public class StreamsToDownloadFactory {

	public static AbstractSchemaStreams getStreamsInstance(SchemaEntity schemaEnt,
			List<Map<String, Object>> linkedFieldsPaths) {
		switch (schemaEnt.getIdStreamType()) {
			case StreamType.XML:
			case StreamType.JSON:
			case StreamType.flatFileDelimited:
			case StreamType.flatFileFixedPosition:
				return new SchemaStreamsBasic(schemaEnt, linkedFieldsPaths);
			case StreamType.EXCEL:
				return new SchemaStreamsExcel(schemaEnt, linkedFieldsPaths);
			case StreamType.XMLEXI:
				return new SchemaStreamsEXI(schemaEnt, linkedFieldsPaths);
		}
		return null;
	}
}

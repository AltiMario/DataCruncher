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

package com.seer.datacruncher.profiler.dto.dbinfo;

public class NumProfilePropertyDTO {

	private String value;
	private String aggr;
	private String less;
	private String more;
	private String between1;
	private String between2;

	
	
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getAggr() {
		return aggr;
	}

	public void setAggr(String aggr) {
		this.aggr = aggr;
	}

	public String getLess() {
		return less;
	}

	public void setLess(String less) {
		this.less = less;
	}

	public String getMore() {
		return more;
	}

	public void setMore(String more) {
		this.more = more;
	}

	public String getBetween1() {
		return between1;
	}

	public void setBetween1(String between1) {
		this.between1 = between1;
	}

	public String getBetween2() {
		return between2;
	}

	public void setBetween2(String between2) {
		this.between2 = between2;
	}

}

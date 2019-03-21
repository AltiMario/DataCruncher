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

package com.seer.datacruncher.jpa.entity;

import javax.persistence.*;

@Entity
@Table(name = "jv_credits")
@NamedQueries({
    @NamedQuery(name = "CreditsEntity.count", query = "SELECT COUNT (j) FROM CreditsEntity j"),
    @NamedQuery(name = "CreditsEntity.findAll", query="SELECT d FROM CreditsEntity d ORDER BY d.name ASC, d.idCredits DESC"),
})

public class CreditsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id_credits")
    private long idCredits;
    
	@Column(name = "name" , unique = true)
    private String name;
	
	@Lob
    @Column(name = "description")
    private String description;
	
	@Column(name = "link")
	private String link;
	
	public long getIdCredits() {
		return idCredits;
	}

	public void setIdCredits(long idCredits) {
		this.idCredits = idCredits;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

}
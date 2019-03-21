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

package com.seer.datacruncher.utils.validation;

import org.apache.commons.beanutils.BeanUtils;

public class ComuneAttributes {
    String istat;
    String comune;
    String provincia;
    String regione;
    String prefisso;
    String cap;
    String codfisco;

    public ComuneAttributes() {
    }

    public String getIstat() {
        return istat;
    }

    public void setIstat(String istat) {
        this.istat = istat;
    }

    public String getComune() {
        return comune;
    }

    public void setComune(String comune) {
        this.comune = comune;
    }

    public String getProvincia() {
        return provincia;
    }

    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }

    public String getRegione() {
        return regione;
    }

    public void setRegione(String regione) {
        this.regione = regione;
    }

    public String getPrefisso() {
        return prefisso;
    }

    public void setPrefisso(String prefisso) {
        this.prefisso = prefisso;
    }

    public String getCap() {
        return cap;
    }

    public void setCap(String cap) {
        this.cap = cap;
    }

    public String getCodfisco() {
        return codfisco;
    }

    public void setCodfisco(String codfisco) {
        this.codfisco = codfisco;
    }

    public ComuneAttributes(String istat, String comune, String provincia, String regione, String prefisso, String cap, String codfisco) {
        this.istat = istat;
        this.comune = comune;
        this.provincia = provincia;
        this.regione = regione;
        this.prefisso = prefisso;
        this.cap = cap;
        this.codfisco = codfisco;
    }
    public String toString() {
        String myInternals = "";
        try {
            myInternals = BeanUtils.describe(this).toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return myInternals;
    }
}
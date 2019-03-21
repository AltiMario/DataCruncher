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
import java.io.Serializable;

@Entity
@Table(name = "jv_forecast")
@NamedQueries({
        @NamedQuery(name = "ForecastEntity.count", query = "SELECT COUNT (j) FROM ForecastEntity j"),
        @NamedQuery(name = "ForecastEntity.findAll", query="SELECT d FROM ForecastEntity d")
})

public class ForecastEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id_forecast")
    private long idForecast;

    @Column(name = "size")
    private int size;

    @Column(name = "significativelags")
    private int significativeLags;

    @Column(name = "mean")
    private float mean;

    @Column(name = "maxacvalues1")
    private float maxACValues1;

    @Column(name = "maxacvalues2")
    private float maxACValues2;

    @Column(name = "maxacatlags1")
    private float maxACatLags1;

    @Column(name = "maxacatlags2")
    private float maxACatLags2;

    @Column(name = "count")
    private int count;

    @Column(name= "dimension")
    private int dimension;

    @Column(name = "sequencelength")
    private int sequenceLength;

    @Column(name = "acvalue")
    private float ACValue;

    @Column(name = "fitness")
    private float fitness;

    public long getIdForecast() {
        return idForecast;
    }

    public void setIdForecast(long idForecast) {
        this.idForecast = idForecast;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getSignificativeLags() {
        return significativeLags;
    }

    public void setSignificativeLags(int significativeLags) {
        this.significativeLags = significativeLags;
    }

    public float getMean() {
        return mean;
    }

    public void setMean(float mean) {
        this.mean = mean;
    }

    public float getMaxACValues1() {
        return maxACValues1;
    }

    public void setMaxACValues1(float maxACValues1) {
        this.maxACValues1 = maxACValues1;
    }

    public float getMaxACValues2() {
        return maxACValues2;
    }

    public void setMaxACValues2(float maxACValues2) {
        this.maxACValues2 = maxACValues2;
    }

    public float getMaxACatLags1() {
        return maxACatLags1;
    }

    public void setMaxACatLags1(float maxACatLags1) {
        this.maxACatLags1 = maxACatLags1;
    }

    public float getMaxACatLags2() {
        return maxACatLags2;
    }

    public void setMaxACatLags2(float maxACatLags2) {
        this.maxACatLags2 = maxACatLags2;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getDimension() {
        return dimension;
    }

    public void setDimension(int dimension) {
        this.dimension = dimension;
    }

    public int getSequenceLength() {
        return sequenceLength;
    }

    public void setSequenceLength(int sequenceLength) {
        this.sequenceLength = sequenceLength;
    }

    public float getACValue() {
        return ACValue;
    }

    public void setACValue(float ACValue) {
        this.ACValue = ACValue;
    }

    public float getFitness() {
        return fitness;
    }

    public void setFitness(float fitness) {
        this.fitness = fitness;
    }
}
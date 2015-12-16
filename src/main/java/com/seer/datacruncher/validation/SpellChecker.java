/*
 * Copyright (c) 2015  www.see-r.com
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

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.lucene.search.spell.PlainTextDictionary;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;


public class SpellChecker {
	
	private static Logger logger = Logger.getLogger(SpellChecker.class);
	private static final Integer suggestionCount = 3;
	private static final String EN_LANGUAGE="en";
	private static org.apache.lucene.search.spell.SpellChecker enSpellChecker;
	private static org.apache.lucene.search.spell.SpellChecker itSpellChecker;
	private String language;
	
	/**
	 * Catching exception because this is auxillary feature if this breaks,
	 * it should not break main functionality.
	 */
	static {
		try{
			Resource resource = new ClassPathResource("dictionary");
			File indexDir = resource.getFile();
			Directory directory = FSDirectory.open(indexDir);
			enSpellChecker = new org.apache.lucene.search.spell.SpellChecker(directory);
			enSpellChecker.indexDictionary(new PlainTextDictionary(
					new ClassPathResource("dictionary/fulldictionary_en.txt").getInputStream()));
			
			itSpellChecker = new org.apache.lucene.search.spell.SpellChecker(directory);
			itSpellChecker.indexDictionary(new PlainTextDictionary(
					new ClassPathResource("dictionary/fulldictionary_it.txt").getInputStream()));
		 }catch(Exception e){
			 logger.error("Error in the static method of Lucene check", e);
		 }
	}

	/**
	 * SpellCheckService constructor ,it takes language as parameter, in case language is null,it will 
	 * take English as default language.
	 */
	public SpellChecker(String language) throws IOException{
		logger.debug("Creating a new Instance of the class with language as "+language);
        if (language!=null || language.equals(""))
		    this.language = language;
        else
            this.language = EN_LANGUAGE;
	}

	public SpellChecker() throws IOException{
		logger.info("Creating a new Instance of the class with language as EN");
		this.language = EN_LANGUAGE;
	}
	
	/**
	 * 
	 * @param wordForSuggestion
	 * @return
	 * @throws Exception
	 * This method checks whether the word passed as a parameter exist in the dictionary or not.
	 */
	public boolean exist(String wordForSuggestion) throws Exception {
		if (EN_LANGUAGE.equalsIgnoreCase(this.language)) {
			return enSpellChecker.exist(wordForSuggestion);
		}
		return itSpellChecker.exist(wordForSuggestion);
	}
    
	/**
	 * 
	 * @param wordForSuggestion
	 * @return
	 * @throws IOException
	 *  This method takes the word which needs to be checked in the dictionary and 
      * Lucene will return 3 suggestion in case it doesn't find an exact match.
	 */
	public String[] getSuggestions(String wordForSuggestion) throws IOException {
		if (EN_LANGUAGE.equalsIgnoreCase(this.language)) {
			return enSpellChecker.suggestSimilar(wordForSuggestion,suggestionCount);
		}
		return itSpellChecker.suggestSimilar(wordForSuggestion, suggestionCount);
	}
	
   /**
    * 
    * @param wordForSuggestion
    * @param suggestionCount
    * @return
    * @throws IOException
    * 
    * This method takes the word which needs to be checked in the dictionary and also number of suggestions which
    * lucene should return in case it doesn't find an exact match.
    */
	public String[] getSuggestions(String wordForSuggestion, int suggestionCount)throws IOException {
		if (EN_LANGUAGE.equalsIgnoreCase(this.language)) {
			return enSpellChecker.suggestSimilar(wordForSuggestion,suggestionCount);
		}
		return itSpellChecker.suggestSimilar(wordForSuggestion, suggestionCount);
	}
}

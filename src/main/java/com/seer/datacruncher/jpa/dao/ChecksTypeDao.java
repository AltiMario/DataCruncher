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

package com.seer.datacruncher.jpa.dao;

import com.seer.datacruncher.jpa.*;
import com.seer.datacruncher.jpa.entity.ChecksTypeEntity;
import com.seer.datacruncher.jpa.entity.SchemaXSDEntity;
import com.seer.datacruncher.utils.generic.I18n;
import com.seer.datacruncher.utils.language.LanguagesList;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
@ReadOnlyTx
public class ChecksTypeDao {

	public static final String EMAIL_PATTERN = "\\w+([-+._']\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*"; //TODO verify ^[\w-]+(?:\.[\w-]+)*@(?:[\w-]+\.)+[a-zA-Z]{2,7}$
    private static final String SAFETEXT_PATTERN = "[a-zA-Z0-9\\s.\\-]+";
    private static final String DIGITWORDS_EN_PATTERN = "(zero|one|two|three|four|five|six|seven|eight|nine)*";  //TODO: not case-sensitive
    private static final String DIGITWORDS_IT_PATTERN = "(zero|uno|due|tre|quattro|cinque|sei|sette|otto|nove)*"; //TODO: not case-sensitive
    private static final String ZIP_US_PATTERN = "\\\\d{5}(-\\\\d{4})?*";
    private static final String STATE_US_PATTERN = "(AL|AK|AS|AZ|AR|CA|CO|CT|DE|DC|FM|FL|GA|GU|HI|ID|IL|IN|IA|KS|KY|LA|ME|MH|MD|MA|MI|MN|MS|MO|MT|NE|NV|NH|NJ|NM|NY|NC|ND|MP|OH|OK|OR|PW|PA|PR|RI|SC|SD|TN|TX|UT|VT|VI|VA|WA|WV|WI|WY)*"; //TODO: not case-sensitive
    private static final String MONTH3CHARS_EN_PATTERN = "(Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)*"; //TODO: not case-sensitive
    private static final String MONTH3CHARS_IT_PATTERN = "(Gen|Feb|Mar|Apr|Mag|Giu|Lug|Ago|Set|Ott|Nov|Dic)*"; //TODO: not case-sensitive
    private static final String MONTHWORDS_EN_PATTERN = "(January|February|March|April|May|June|July|August|September|October|November|December)*"; //TODO: not case-sensitive
    private static final String MONTHWORDS_IT_PATTERN = "(Gennaio|Febbraio|Marzo|Aprile|Maggio|Giugno|Luglio|Agosto|Settembre|Ottobre|Novembre|Dicembre)*"; //TODO: not case-sensitive
    private static final String NOHTML_PATTERN = "[^(&lt;.+&gt;)]*";
    private static final String ALPHABETIC_PATTERN = "[a-zA-Z\\s]*";
	private static final String POUND_STERLING = "£?(([1-9]{1,3}(,\\d{3})*(\\.\\d{2})?)|(0\\.[1-9]\\d)|(0\\.0[1-9]))";

    /*** Matches following phone numbers:
         (123)456-7890, 123-456-7890, 1234567890, (123)-456-7890
     */
    private static final String PHONE_PATTERN = ".*\\(?(\\d{3})\\)?[-]?(\\d{3})[-]?(\\d{4}).*";


	Logger log = Logger.getLogger(this.getClass());

    @PersistenceContext
    private EntityManager em;

    @Autowired
    CommonDao commonDao;

    protected ChecksTypeDao(){}
	
	public ReadList readComplete(int start, int limit) {
		ReadList readList = new ReadList();
		try {
			em.clear();
            Query count = em.createNativeQuery("SELECT((SELECT COUNT(*) FROM jv_checks_types)+(SELECT COUNT(*) FROM jv_macros))"); //TODO use the entity "ChecksTypeEntity"
            Query query = em.createNamedQuery("ChecksTypeEntity.findAllComplete");
			if(start >= 0 && limit >= 0) {
				query.setFirstResult(start);
				query.setMaxResults(limit);
			}
			readList.setTotal(((BigInteger)count.getResultList().get(0)).longValue());
            readList.setResults(query.getResultList());

			em.clear();
            @SuppressWarnings("unchecked")
            List<ChecksTypeEntity> listResults = (List<ChecksTypeEntity>)readList.getResults();	
			if(listResults != null && listResults.size() > 0) {
                for(ChecksTypeEntity entity : listResults) {
                	if(entity.getExtraCheckType() != null && entity.getExtraCheckType().equalsIgnoreCase("Regular Expression")){
						entity.setRegExp(true);
					}else{
						entity.setRegExp(false);
					}
                	if(entity.getNlsId() != null) {
                        entity.setNlsId(I18n.getMessage(entity.getNlsId()));
                    } else {
                        entity.setNlsId(entity.getName());
                    }
                }
			}
		} catch (Exception exception) {
			log.error("ChecksTypeDao - read : " + exception);
			readList.setSuccess(false);
			readList.setMessage(I18n.getMessage("error.error") + "  : ChecksTypeDao - read");
			return readList;
		}
		readList.setSuccess(true);
		readList.setMessage(I18n.getMessage("success.listRecord"));
		return readList;
	}
	
	public ReadList readCheckTypeBySchemaFieldId(long idSchemaField) {
		ReadList readList = new ReadList();
		try {
			@SuppressWarnings("unchecked")
			List<ChecksTypeEntity> result = em.createNamedQuery("ChecksTypeEntity.findBySchemaFieldId")
					.setParameter("idSchemaField", idSchemaField).getResultList();
			if(CollectionUtils.isNotEmpty(result)){
				readList.setTotal(result.size());
				readList.setResults(result);
			}else{
				readList.setTotal(0);
			}
		} catch (Exception exception) {
			log.error("Exception : " + exception);
			readList.setSuccess(false);
			readList.setMessage(I18n.getMessage("error.error") + "  : ChecksTypeDao - read");
			return readList;
		} 
		readList.setSuccess(true);
		readList.setMessage(I18n.getMessage("success.listRecord"));
		return readList;
	}
	
	public ReadList read(int start, int limit) {
		ReadList readList = new ReadList();
		try {
			em.clear();
            @SuppressWarnings("unchecked")
			List<Long> count = em.createNamedQuery("ChecksTypeEntity.count").getResultList();
			Query query = em.createNamedQuery("ChecksTypeEntity.findAll");
            if(start >= 0 && limit >= 0) {
				query.setFirstResult(start);
				query.setMaxResults(limit);
			}
			readList.setTotal(count.get(0));
			readList.setResults(query.getResultList());
			em.clear();
            @SuppressWarnings("unchecked")
			List<ChecksTypeEntity> listResults = (List<ChecksTypeEntity>)readList.getResults();
			if(listResults != null && listResults.size() > 0) {
				for(ChecksTypeEntity entity : listResults) {
					if(entity.getExtraCheckType().equalsIgnoreCase("Regular Expression")){
						entity.setRegExp(true);
					}else{
						entity.setRegExp(false);
					}
					if(entity.getNlsId() != null) {
						entity.setNlsId(I18n.getMessage(entity.getNlsId()));
					} else {
						entity.setNlsId(entity.getName());
					}
				}
			}
		} catch (Exception exception) {
			log.error("ChecksTypeDao - read : " + exception);
			readList.setSuccess(false);
			readList.setMessage(I18n.getMessage("error.error") + "  : ChecksTypeDao - read");
			return readList;
		}
		readList.setSuccess(true);
		readList.setMessage(I18n.getMessage("success.listRecord"));
		return readList;
	}

	public ReadList readRegExps(int start, int limit) {
		ReadList readList = new ReadList();
		try {
			em.clear();
            @SuppressWarnings("unchecked")
			List<Long> count = em.createNamedQuery("ChecksTypeEntity.countRegExps").getResultList();
			Query query = em.createNamedQuery("ChecksTypeEntity.findAllRegExps");
			if(start >= 0 && limit >= 0) {
				query.setFirstResult(start);
				query.setMaxResults(limit);
			}
			readList.setTotal(count.get(0));
			readList.setResults(query.getResultList());
			em.clear();
            @SuppressWarnings("unchecked")
			List<ChecksTypeEntity> listResults = (List<ChecksTypeEntity>)readList.getResults();
			if(listResults != null && listResults.size() > 0) {
				for(ChecksTypeEntity entity : listResults) {
					if(entity.getExtraCheckType().equalsIgnoreCase("Regular Expression")){
						entity.setRegExp(true);
					}else{
						entity.setRegExp(false);
					}
					if(entity.getNlsId() != null) {
						entity.setNlsId(I18n.getMessage(entity.getNlsId()));
					} else {
						entity.setNlsId(entity.getName());
					}
				}
			}
		} catch (Exception exception) {
			log.error("ChecksTypeDao - read : " + exception);
			readList.setSuccess(false);
			readList.setMessage(I18n.getMessage("error.error") + "  : ChecksTypeDao - read");
			return readList;
		}
		readList.setSuccess(true);
		readList.setMessage(I18n.getMessage("success.listRecord"));
		return readList;
	}

	/**
	 * Adds spell checks to database.
	 */
    @Transactional
	private void addSpellChecks() {
		List<?> list = em.createNamedQuery("ChecksTypeEntity.countSpellChecks").getResultList();
		if ((Long)list.get(0) != LanguagesList.values().length) {
			list = em.createNamedQuery("ChecksTypeEntity.findSpellChecks").getResultList();
			for (Object o : list) {
				em.remove(o);
			}
			for (LanguagesList langShort : LanguagesList.values()) {
				ChecksTypeEntity checksTypeEntity = new ChecksTypeEntity("@spellcheck", "Spell Check -> " + langShort, "Spell check with "+ langShort +" language vocabulary", "Coded", true, null, null,null);
				em.persist(checksTypeEntity);
			}			
		}
	}


    @Transactional
    public void init() {
		ChecksTypeEntity checksTypeEntity;
			@SuppressWarnings("unchecked")
			List<Long> result = em.createNamedQuery("ChecksTypeEntity.count").getResultList();
			if (result.get(0) == 0L) {
				addSpellChecks();
                checksTypeEntity = new ChecksTypeEntity("@iban", "Finance -> IBAN", "International Bank Account Number", "Coded", true, null, null,"common.IBAN");
				em.persist(checksTypeEntity);
                checksTypeEntity = new ChecksTypeEntity("@bic", "Finance -> BIC", "Business Identifier Codes, ISO 9362", "Coded", true, null, null,"common.BIC");
                em.persist(checksTypeEntity);
                checksTypeEntity = new ChecksTypeEntity("@currency", "Finance -> Currency", "Circulating currency, ISO 4218", "Coded", true, null, null,"common.ISOCurrencies");
                em.persist(checksTypeEntity);
                checksTypeEntity = new ChecksTypeEntity("@codicefiscale:nome", "Finance -> Codice Fiscale -> Nome", "Italian tax identifier - name","Coded", true, null, null,"custom.CodiceFiscale");
				em.persist(checksTypeEntity);
				checksTypeEntity = new ChecksTypeEntity("@codicefiscale:cognome", "Finance -> Codice Fiscale -> Cognome", "Italian tax identifier - surname","Coded", true, null, null,"custom.CodiceFiscale");
				em.persist(checksTypeEntity);
				checksTypeEntity = new ChecksTypeEntity("@codicefiscale:sesso", "Finance -> Codice Fiscale -> Sesso (M/F)", "Italian tax identifier - sex","Coded", true, null, null,"custom.CodiceFiscale");
				em.persist(checksTypeEntity);
				checksTypeEntity = new ChecksTypeEntity("@codicefiscale:dataNascita", "Finance -> Codice Fiscale -> Data di Nascita (gg/mm/aaaa)", "Italian tax identifier - birth day","Coded", true, null, null,"custom.CodiceFiscale");
				em.persist(checksTypeEntity);
				checksTypeEntity = new ChecksTypeEntity("@codicefiscale:comuneNascita", "Finance -> Codice Fiscale -> Comune di Nascita", "Italian tax identifier - birth place","Coded", true, null, null,"custom.CodiceFiscale");
				em.persist(checksTypeEntity);
				checksTypeEntity = new ChecksTypeEntity("@codicefiscale:provinciaNascita", "Finance -> Codice Fiscale -> Provincia di Nascita", "Italian tax identifier- birth place","Coded", true, null, null,"custom.CodiceFiscale");
				em.persist(checksTypeEntity);
				checksTypeEntity = new ChecksTypeEntity("@codicefiscale:codiceFiscale", "Finance -> Codice Fiscale", "Italian tax identifier - italian tax","Coded", true, null, null,"custom.CodiceFiscale");
				em.persist(checksTypeEntity);
				checksTypeEntity = new ChecksTypeEntity("@partitaiva", "Finance -> Partita IVA", "Italian company registration number / Italian VAT number", "Coded", true, null, null,"custom.PartitaIVA");
				em.persist(checksTypeEntity);
				checksTypeEntity = new ChecksTypeEntity("@creditcard", "Finance -> Credit Card", "Generic credit card number", "Coded", true, null, null,"common.CreditCard");
				em.persist(checksTypeEntity);
				checksTypeEntity = new ChecksTypeEntity(null, "Finance -> Pound Sterling", "UK Money format", "Regular Expression", true, "regexps.pound_sterling", POUND_STERLING, null);
				em.persist(checksTypeEntity);
				checksTypeEntity = new ChecksTypeEntity("@country", "Generic -> Country", "Country code, ISO 3166-1 alpha-2", "Coded", true, null, null,"common.ISOCountries");
                em.persist(checksTypeEntity);
                checksTypeEntity = new ChecksTypeEntity("@domain", "Internet -> Domain name", "Internet domain name", "Coded", true, null, null,"common.Domain");
				em.persist(checksTypeEntity);
                checksTypeEntity = new ChecksTypeEntity("@latitude", "Internet -> Latitude", "Geographic coordinate", "Coded", true, null, null,"common.Latitude");
                em.persist(checksTypeEntity);
                checksTypeEntity = new ChecksTypeEntity("@longitude", "Internet -> Longitude", "Geographic coordinate", "Coded", true, null, null,"common.Longitude");
                em.persist(checksTypeEntity);
                checksTypeEntity = new ChecksTypeEntity("@isbn", "Generic -> ISBN","International Standard Book Number", "Coded", true, null, null,"common.ISBN");
				em.persist(checksTypeEntity);
				checksTypeEntity = new ChecksTypeEntity("@url", "Internet -> URL", "Uniform Resource Locator, formal check", "Coded", true, null, null,"common.Url");
                em.persist(checksTypeEntity);
                checksTypeEntity = new ChecksTypeEntity("@urlExisting", "Internet -> URL Existing", "Uniform Resource Locator, formal and connection check", "Coded", true, null, null,"common.UrlExisting");
                em.persist(checksTypeEntity);
                checksTypeEntity = new ChecksTypeEntity("@inetaddress", "Internet -> IP", "Internet Protocol address", "Coded", true, null, null,"common.InetAddress");
                em.persist(checksTypeEntity);
                checksTypeEntity = new ChecksTypeEntity("@serverport", "Internet -> Server Port", "Server port", "Coded", true, null, null,"common.ServerPort");
				em.persist(checksTypeEntity);
				checksTypeEntity = new ChecksTypeEntity("@comuniitaliani:istat", "Comuni Italiani -> ISTAT", "ISTAT code number", "Coded", true, null, null,"custom.ComuniItaliani");
				em.persist(checksTypeEntity);
				checksTypeEntity = new ChecksTypeEntity("@comuniitaliani:cap", "Comuni Italiani -> CAP", "Italian postal code number", "Coded", true, null, null,"custom.ComuniItaliani");
				em.persist(checksTypeEntity);
				checksTypeEntity = new ChecksTypeEntity("@comuniitaliani:comune", "Comuni Italiani -> Comune", "Italian town", "Coded", true, null, null,"custom.ComuniItaliani");
				em.persist(checksTypeEntity);
				checksTypeEntity = new ChecksTypeEntity("@comuniitaliani:provincia", "Comuni Italiani -> Provincia", "Italian province", "Coded", true, null, null,"custom.ComuniItaliani");
				em.persist(checksTypeEntity);
				checksTypeEntity = new ChecksTypeEntity("@comuniitaliani:regione", "Comuni Italiani -> Regione", "Italian region", "Coded", true, null, null,"custom.ComuniItaliani");
				em.persist(checksTypeEntity);
				checksTypeEntity = new ChecksTypeEntity("@comuniitaliani:prefisso", "Comuni Italiani -> Prefisso", "Italian area code", "Coded", true, null, null,"custom.ComuniItaliani");
				em.persist(checksTypeEntity);
				checksTypeEntity = new ChecksTypeEntity("@comuniitaliani:codfisco", "Comuni Italiani -> Codice Fisco", "Italian town tax identifier", "Coded", true, null, null,"custom.ComuniItaliani");
                em.persist(checksTypeEntity);
                checksTypeEntity = new ChecksTypeEntity("@emailexisting", "Internet -> E-Mail Existing", "Existence check of electronic mail", "Coded", true, null, null,"custom.EmailExisting");
                em.persist(checksTypeEntity);
				checksTypeEntity = new ChecksTypeEntity(null, "Internet -> E-Mail", "Electronic mail", "Regular Expression", true, "regexps.email", EMAIL_PATTERN, null);
				em.persist(checksTypeEntity);
                checksTypeEntity = new ChecksTypeEntity("@emailjoined", "Internet -> E-Mails joined", "Electronic mails joined by separator", "Coded", true, null, null,"custom.EmailJoined");
                em.persist(checksTypeEntity);
                checksTypeEntity = new ChecksTypeEntity(null, "Generic -> Telephone number", "Telephone number", "Regular Expression", true, "regexps.telephone", PHONE_PATTERN, null);
                em.persist(checksTypeEntity);
                checksTypeEntity = new ChecksTypeEntity(null, "Generic -> Safe Text", "Lower and upper case letters and all digits", "Regular Expression", true, "regexps.safetext", SAFETEXT_PATTERN, null);
                em.persist(checksTypeEntity);
                checksTypeEntity = new ChecksTypeEntity(null, "Generic -> Digit Words EN", "The English words representing the digits 0 to 9", "Regular Expression", true, "regexps.digitwords_en", DIGITWORDS_EN_PATTERN, null);
                em.persist(checksTypeEntity);
                checksTypeEntity = new ChecksTypeEntity(null, "Generic -> Digit Words IT", "The Italian words representing the digits 0 to 9", "Regular Expression", true, "regexps.digitwords_it", DIGITWORDS_IT_PATTERN, null);
                em.persist(checksTypeEntity);
                checksTypeEntity = new ChecksTypeEntity(null, "Generic -> ZIP", "US zip code with optional dash-four", "Regular Expression", true, "regexps.zip_us", ZIP_US_PATTERN, null);
                em.persist(checksTypeEntity);
                checksTypeEntity = new ChecksTypeEntity(null, "Generic -> US States", "Two letter state abbreviations", "Regular Expression", true, "regexps.state_us", STATE_US_PATTERN, null);
                em.persist(checksTypeEntity);
                checksTypeEntity = new ChecksTypeEntity("@passwordcheck", "Generic -> Passwords", "4 to 8 character password requiring numbers, lowercase letters, and uppercase letters", "Coded", true, null, null,"custom.PasswordCheck");
                em.persist(checksTypeEntity);
                checksTypeEntity = new ChecksTypeEntity(null, "Generic -> Month words abbreviations EN", "3 characters abbreviations for the months, English", "Regular Expression", true, "regexps.month3chars_en", MONTH3CHARS_EN_PATTERN, null);
                em.persist(checksTypeEntity);
                checksTypeEntity = new ChecksTypeEntity(null, "Generic -> Month words abbreviations IT", "3 characters abbreviations for the months, Italian", "Regular Expression", true, "regexps.month3chars_it", MONTH3CHARS_IT_PATTERN, null);
                em.persist(checksTypeEntity);
                checksTypeEntity = new ChecksTypeEntity(null, "Generic -> Month words IT", "Italian months", "Regular Expression", true, "regexps.monthwords_it", MONTHWORDS_IT_PATTERN, null);
                em.persist(checksTypeEntity);
                checksTypeEntity = new ChecksTypeEntity(null, "Generic -> Month words EN", "English months", "Regular Expression", true, "regexps.monthwords_en", MONTHWORDS_EN_PATTERN, null);
                em.persist(checksTypeEntity);
                checksTypeEntity = new ChecksTypeEntity(null, "Internet -> No HTML", "No HTML allowed", "Regular Expression", true, "regexps.nohtml", NOHTML_PATTERN, null);
                em.persist(checksTypeEntity);
                checksTypeEntity = new ChecksTypeEntity(null, "Generic -> Alphabetic Characters", "Alphabetic characters allowed", "Regular Expression", true, "regexps.alphabetic", ALPHABETIC_PATTERN, null);
                em.persist(checksTypeEntity);
                checksTypeEntity = new ChecksTypeEntity("@phonenumberextractorit", "Generic -> Telephone number extractor (Italian)", "Find the presence of telephone numbers in italian text (ex: zero zero trecentoquarantanove ...)", "Coded", true, null, null,"custom.PhoneNumberExtractorIT");
                em.persist(checksTypeEntity);
                checksTypeEntity = new ChecksTypeEntity("@phonenumberextractoren", "Generic -> Telephone number extractor (English)", "Find the presence of telephone numbers in english text (ex: zero zero three hundred forty nine ...)", "Coded", true, null, null,"custom.PhoneNumberExtractorEN");
                em.persist(checksTypeEntity);
                checksTypeEntity = new ChecksTypeEntity("@italian:nomemaschile", "Generic -> Italian -> Nome Maschile", "Italian male name", "Coded", true, null, null,"custom.NomiItaliani");
				em.persist(checksTypeEntity);
				checksTypeEntity = new ChecksTypeEntity("@italian:nomefemminile", "Generic -> Italian -> Nome Femminile", "Italian female name", "Coded", true, null, null,"custom.NomiItaliani");
				em.persist(checksTypeEntity);				
				checksTypeEntity = new ChecksTypeEntity("@italian:cognome", "Generic -> Italian -> Cognome", "Italian surname", "Coded", true, null, null,"custom.NomiItaliani");
				em.persist(checksTypeEntity);
                String SampleValidationCode="" +
                        "import com.seer.datacruncher.validation.ResultStepValidation;\n" +
                        "import com.seer.datacruncher.utils.validation.SingleValidation;\n" +
                        "\n" +
                        "public class SampleValidation implements SingleValidation {\n" +
                        "    private ResultStepValidation result = new ResultStepValidation();\n" +
                        "    private boolean isValid(String text) {\n" +
                        "        if (text == null || text.length() == 0) {\n" +
                        "            result.setMessageResult(\"Empty or NULL value\");\n" +
                        "            return false;\n" +
                        "        } else if (text.contains(\"123test\"))\n" +
                        "            return true;\n" +
                        "        result.setMessageResult(\"Bad value, waiting '123test'\");\n" +
                        "        return false;\n" +
                        "    }\n" +
                        "\n" +
                        "    public ResultStepValidation checkValidity(String text){\n" +
                        "        try{\n" +
                        "            result.setMessageResult(\"\");\n" +
                        "            if(isValid(text)){\n" +
                        "                result.setValid(true);\n" +
                        "            }else{\n" +
                        "                result.setValid(false);\n" +
                        "                result.setMessageResult(\"SampleValidation: [\" + text + \"] wrong. \"+ result.getMessageResult());\n" +
                        "            }\n" +
                        "\n" +
                        "        }catch(Exception e)  {\n" +
                        "            result.setValid(false);\n" +
                        "            result.setMessageResult(\"SampleValidation: [\" + text + \"] wrong. \" +e.getMessage());\n" +
                        "        }\n" +
                        "        return result;\n" +
                        "    }\n" +
                        "}";
                checksTypeEntity = new ChecksTypeEntity("@SampleValidation", "SampleValidation", "Sample validation class ready for testing", "Custom Code", false, null, SampleValidationCode,"SampleValidation");
                em.persist(checksTypeEntity);
            } else {
				addSpellChecks();
			}
	}
    public List<ChecksTypeEntity> getChecksTypeBySchemaFiledId(long idSchemaField) {
        ChecksTypeEntity result = null;
        List<ChecksTypeEntity> results= null;
        try {
            @SuppressWarnings("unchecked")
            List<ChecksTypeEntity> list = em.createNamedQuery("ChecksTypeEntity.findBySchemaFieldId")
                    .setParameter("idSchemaField", idSchemaField).getResultList();
            if (list != null && list.size() > 0){
                results= new ArrayList<ChecksTypeEntity>();
                result = list.get(0);
            }
            if (result == null) return null;
            if(result.getExtraCheckType().equals("Regular Expression")){
                result.setRegExp(true);
            }else{
                result.setRegExp(false);
            }
            results.add(result);
            if ( list.size() > 1){
                int i = 1;
                while (i < list.size()) {
                    result = list.get(i);
                    if(result.getExtraCheckType().equals("Regular Expression")){
                        result.setRegExp(true);
                    }else{
                        result.setRegExp(false);
                    }
                    results.add(result);
                    i++;
                }

            }

        } catch (Exception exception) {
            return null;
        }
        return results;
    }
    public ChecksTypeEntity getChecksTypeById(long idCheckType) {
		ChecksTypeEntity result = null;
		try {
			@SuppressWarnings("unchecked")
			List<ChecksTypeEntity> list = em.createNamedQuery("ChecksTypeEntity.findByIdChecksType")
					.setParameter("idCheckType", idCheckType).getResultList();
			if (list != null && list.size() > 0)
				result = list.get(0);
            assert result != null;
            if(result.getExtraCheckType().equals("Regular Expression")){
				result.setRegExp(true);
			}else{
				result.setRegExp(false);
			}
		} catch (Exception exception) {
            return null;
		}
		return result;
	}

	public ChecksTypeEntity getChecksTypeByDescr(String tokenRule) {
		ChecksTypeEntity result = null;
		try {
			@SuppressWarnings("unchecked")
			List<ChecksTypeEntity> list = em.createNamedQuery("ChecksTypeEntity.findByTokenRule")
					.setParameter("tokenRule", tokenRule).getResultList();
			if (list != null && list.size() > 0)
				result = list.get(0);

		} catch (Exception exception) {
			return null;
		}
		return result;
	}

    public ChecksTypeEntity find(long id) {
		ChecksTypeEntity ent;
		try {
			ent = em.find(ChecksTypeEntity.class, id);
		} catch (Exception exception) {
            return null;
		}
		return ent;
	}

	public Create create(ChecksTypeEntity checksTypeEntity) {
		Create create = new Create ();

		try {
            create.setSuccess(true);
            create.setResults(checksTypeEntity);
            create.setMessage(I18n.getMessage("success.insRecord"));
            commonDao.persist(checksTypeEntity);
		} catch(Exception exception) {
			log.error("ChecksTypeDao - create : " + exception);
			create.setSuccess(false);
			create.setResults(checksTypeEntity);
			create.setMessage(I18n.getMessage("error.noInsRecord"));
			return create;
		}
		return create;
	}

    @Transactional
	public Destroy destroy(long idRegExps) {
		Destroy destroy = new Destroy();
		try {
			ChecksTypeEntity checksTypeEntity = em.find(ChecksTypeEntity.class, idRegExps);

            assert checksTypeEntity != null;
		    em.remove(checksTypeEntity);

            @SuppressWarnings("unchecked")
            List<SchemaXSDEntity> schemaXSDEntityList = em.createNamedQuery("SchemaXSDEntity.findByIdCheckType")
                    .setParameter("idCheckType", checksTypeEntity.getIdCheckType()).getResultList();
            if (schemaXSDEntityList != null) {
                for (int i = schemaXSDEntityList.size() - 1; i >= 0; i--)
                    em.remove(schemaXSDEntityList.get(i));
            }
            em.createNamedQuery("SchemaFieldEntity.deleteIdExtraCheck")
                    .setParameter("idCheckType", checksTypeEntity.getIdCheckType()).executeUpdate() ;
		} catch (Exception exception) {
			log.error("ChecksTypeDao - destroy : " + exception);
			destroy.setSuccess(false);
			destroy.setResults(null);
			return destroy;
		}
		destroy.setSuccess(true);
		destroy.setResults(null);
		destroy.setMessage(I18n.getMessage("success.fieldCanc"));
		return destroy;
	}

    @Transactional
    public Update update(ChecksTypeEntity checksTypeEntity) {
		Update update = new Update();

		try {
            em.merge(checksTypeEntity);
            @SuppressWarnings("unchecked")
            List<SchemaXSDEntity> schemaXSDEntityList = em.createNamedQuery("SchemaXSDEntity.findByIdCheckType")
                    .setParameter("idCheckType", checksTypeEntity.getIdCheckType()).getResultList();
            if (schemaXSDEntityList != null) {
                for (int i = schemaXSDEntityList.size() - 1; i >= 0; i--)
                    em.remove(schemaXSDEntityList.get(i));
            }

		} catch(Exception exception) {
			log.error("ChecksTypeDao - update : " + exception);
			update.setSuccess(false);
			update.setMessage(I18n.getMessage("error.noUpdateRecord"));
			return update;
		}
		update.setSuccess(true);
		update.setMessage(I18n.getMessage("success.updateRecord"));
		return update;
	}
    
    @Transactional(readOnly = true)
    public ReadList findCustomCodeByName(String name) {
        ReadList readList = new ReadList();
        try {
            readList.setResults(em.createNamedQuery("ChecksTypeEntity.findCustomCodesByName")
                    .setParameter("name", name)
                    .getResultList());
        } catch (Exception exception) {
            log.error("ChecksTypeDao - findByName : " + exception);
            readList.setSuccess(false);
            readList.setMessage(I18n.getMessage("error.error") + "  : ChecksTypeDao - findCustomCodeByName");
            return readList;
        }
        readList.setSuccess(true);
        readList.setMessage(I18n.getMessage("success.listRecord"));
        return readList;
    }
}
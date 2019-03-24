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

package com.datacruncher.validation.custom;

import com.datacruncher.validation.common.Domain;
import com.datacruncher.validation.common.InetAddress;

import java.io.Serializable;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

/**
 * <p>Perform email validations.</p>
 * <p>
 * This class is a Singleton; you can retrieve the instance via the getInstance() method.
 * </p>
 * <p>
 * Based on a script by <a href="mailto:stamhankar@hotmail.com">Sandeep V. Tamhankar</a>
 * http://javascript.internet.com
 * </p>
 * <p>
 * This implementation is not guaranteed to catch all possible errors in an email address.
 * For example, an address like nobody@noplace.somedog will pass validator, even though there
 * is no TLD "somedog"
 * </p>.
 *
 * @version $Revision$ $Date$
 * @since Validator 1.4
 */
public class Email implements Serializable {

    private static final long serialVersionUID = 1705927040799295880L;

    private static final String SPECIAL_CHARS = "\\p{Cntrl}\\(\\)<>@,;:'\\\\\\\"\\.\\[\\]";
    private static final String VALID_CHARS = "[^\\s" + SPECIAL_CHARS + "]";
    private static final String QUOTED_USER = "(\"[^\"]*\")";
    private static final String WORD = "((" + VALID_CHARS + "|')+|" + QUOTED_USER + ")";

    private static final String LEGAL_ASCII_REGEX = "^\\p{ASCII}+$";
    private static final String VALID_EMAIL_REGEX = "\\w+([-+._']\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";
    private static final String EMAIL_REGEX = "^\\s*?(.+)@(.+?)\\s*$";
    private static final String IP_DOMAIN_REGEX = "^\\[(.*)\\]$";
    private static final String USER_REGEX = "^\\s*" + WORD + "(\\." + WORD + ")*$";

    private static final Pattern MATCH_ASCII_PATTERN = Pattern.compile(LEGAL_ASCII_REGEX);
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);
    private static final Pattern VALID_EMAIL_PATTERN = Pattern.compile(VALID_EMAIL_REGEX);
    private static final Pattern IP_DOMAIN_PATTERN = Pattern.compile(IP_DOMAIN_REGEX);
    private static final Pattern USER_PATTERN = Pattern.compile(USER_REGEX);

    private final boolean allowLocal;

    /**
     * Singleton instance of this class, which
     *  doesn't consider local addresses as valid.
     */
    private static final Email EMAIL_VALIDATOR = new Email(false);

    /**
     * Singleton instance of this class, which does
     *  consider local addresses valid.
     */
    private static final Email EMAIL_VALIDATOR_WITH_LOCAL = new Email(true);

    private String invalidCause = null;

    /**
     * Returns the Singleton instance of this validator.
     *
     * @return singleton instance of this validator.
     */
    public static Email getInstance() {
        return EMAIL_VALIDATOR;
    }

    /**
     * Returns the Singleton instance of this validator,
     *  with local validation as required.
     *
     * @param allowLocal Should local addresses be considered valid?
     * @return singleton instance of this validator
     */
    public static Email getInstance(boolean allowLocal) {
        if(allowLocal) {
            return EMAIL_VALIDATOR_WITH_LOCAL;
        }
        return EMAIL_VALIDATOR;
    }

    /**
     * Protected constructor for subclasses to use.
     *
     * @param allowLocal Should local addresses be considered valid?
     */
    protected Email(boolean allowLocal) {
        super();
        this.allowLocal = allowLocal;
    }

    /**
     * <p>Checks if a field has a valid e-mail address.</p>
     *
     * @param email The value validation is being performed on.  A <code>null</code>
     *              value is considered invalid.
     * @return true if the email address is valid.
     */
    public boolean isValid(String email) {
        if (email == null) {
            this.invalidCause="It's null";
            return false;
        }

        Matcher asciiMatcher = MATCH_ASCII_PATTERN.matcher(email);
        if (!asciiMatcher.matches()) {
            this.invalidCause="Illegal characters";
            return false;
        }

        // Check the whole email address structure
        Matcher emailMatcher = EMAIL_PATTERN.matcher(email);
        if (!emailMatcher.matches() || !VALID_EMAIL_PATTERN.matcher(email).matches()) {
            this.invalidCause="Invalid structure";
            return false;
        }

        if (email.endsWith(".")) {
            this.invalidCause="Ends with a dot";
            return false;
        }

        if (!isValidUser(emailMatcher.group(1))) {
            this.invalidCause="Invalid user name";
            return false;
        }

        if (!isValidDomain(emailMatcher.group(2))) {
            this.invalidCause="Invalid domain";
            return false;
        }

        return true;
    }
    /**
     * <p>Checks if a field has a valid e-mail address.</p>
     *
     * @param email The value validation is being performed on.  A <code>null</code>
     *              value is considered invalid.
     * @return true if the email address is valid.
     */
    public boolean isValidAndExist(String email) {
        if(isValid(email)){
            /* //TODO validation host doesn't work
            if (!isValidHost(findHost(email))) {
                this.invalidCause="The host does not exist";
                return false;
            } */
            return true;
        }else {
            return false;
        }
    }

    private String findHost(String currentEmail) {
        int at = currentEmail.lastIndexOf("@");
        return currentEmail.substring(at + 1);
    }

    //TODO: test it accurately
    private static boolean isValidHost(String hostName) {
        Attribute attr;
        Hashtable env = new Hashtable();
        env.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
        try {
            DirContext ictx = new InitialDirContext(env);
            Attributes attrs = ictx.getAttributes(hostName, new String[] { "MX" });
            attr = attrs.get("MX");
            if (attr == null)
                return false;
        } catch (Exception NamingException) {
            return false;
        }
        return true;
    }
    /**
     * Returns true if the domain component of an email address is valid.
     *
     * @param domain being validated.
     * @return true if the email address's domain is valid.
     */
    protected boolean isValidDomain(String domain) {
        // see if domain is an IP address in brackets
        Matcher ipDomainMatcher = IP_DOMAIN_PATTERN.matcher(domain);

        if (ipDomainMatcher.matches()) {
            InetAddress inetAddressValidator =
                    InetAddress.getInstance();
            return inetAddressValidator.isValid(ipDomainMatcher.group(1));
        } else {
            // Domain is symbolic name
            Domain domainValidator =
                    Domain.getInstance(allowLocal);
            return domainValidator.isValid(domain);
        }
    }

    /**
     * Returns true if the user component of an email address is valid.
     *
     * @param user being validated
     * @return true if the user name is valid.
     */
    protected boolean isValidUser(String user) {
        return USER_PATTERN.matcher(user).matches();
    }

    /**
     * Get a string with information about why the Email was found invalid
     * @return a human readable (english) string
     */
    public String getInvalidCause() {
        return invalidCause;
    }


}
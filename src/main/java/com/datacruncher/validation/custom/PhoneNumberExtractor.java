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

import com.datacruncher.utils.validation.SingleValidation;
import com.datacruncher.validation.ResultStepValidation;
import org.apache.log4j.Logger;

import java.net.URL;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Scanner;


public class PhoneNumberExtractor implements SingleValidation {
    private static final int DIGIT = 0;
    private static final int NUMWORD = 1;
    private static final int WORD = 2;
    private static final int PLUS = 3;
    private static final int CARET = 4;
    Logger log = Logger.getLogger(this.getClass());
    Properties languagePack;
    //default data
    String inputFileName="";
    int minLength = 6;
    int maxLength = 40;
    private String languageSelect = "en";

    public String getLanguageSelect() {
        return languageSelect;
    }

    public void setLanguageSelect(String languageSelect) {
        this.languageSelect = languageSelect;
    }

    public ArrayList extract (String stream, String lang, int min, int max) throws Exception {
        inputFileName = stream;
        minLength = min;
        maxLength = max;
        setLanguageSelect(lang);
        languagePack = loadLanguageNumberPack();
        return parseInput();
    }

    public ArrayList extract (String stream, String lang) throws Exception {
        inputFileName = stream;
        setLanguageSelect(lang);
        languagePack = loadLanguageNumberPack();
        return parseInput();
    }

    public ArrayList extract(String stream) throws Exception {
        inputFileName = stream;
        languagePack = loadLanguageNumberPack();
        return parseInput();
    }

    private Properties loadLanguageNumberPack() throws Exception {
        URL propertiesFileUrl = this.getClass().getResource(
                "/locale/numbers_" + getLanguageSelect() + ".properties");
        Properties p = new Properties();
        try {
            p.load(propertiesFileUrl.openStream());
        } catch (Exception e) {
            if (!getLanguageSelect().equals("en")) {
                log.error("Cannot open language pack<"
                        + getLanguageSelect() + ">. Switching to English default.");
                setLanguageSelect("en");
                return loadLanguageNumberPack();
            } else
                throw e;
        }
        return p;
    }

    private ArrayList parseInput() {
        ArrayList<String> inputTokens = new ArrayList<String>();
        ArrayList<String> inputTokens2;

        inputFileName = inputFileName.toLowerCase();
        Scanner tokenize = new Scanner(inputFileName);
        tokenize.useDelimiter("[^a-z^0-9+,]");
        while (tokenize.hasNext()) {
            inputTokens.add(tokenize.next());
        }
        inputTokens2 = checkForWordNumber(inputTokens);
        return accumulateNumbers(inputTokens2);
    }

    private ArrayList<String> accumulateNumbers(ArrayList<String> inputTokens2) {
        ArrayList<String> inputTokens3 = new ArrayList<String>();
        StringBuilder sB = new StringBuilder();
        int intBuilder = 0;
        int oldType = WORD;
        for (String token : inputTokens2) {
            int type = analyzeTokenType(token);
            if (type == CARET)
                continue;
            if (type != oldType && (type != PLUS || type != CARET)
                    && oldType != PLUS) {
                flush(sB, inputTokens3);
                intBuilder = 0;
            }
            switch (type) {
                case DIGIT:
                    sB.append(token);
                    break;
                case WORD:
                    break;
                case NUMWORD:
                    intBuilder = processNumWord(token, intBuilder, sB);
                    break;
                case PLUS:

                    flush(sB, inputTokens3);
                    sB.append("+");
                    intBuilder = 0;
                    break;
                case CARET:
                    break;
            }
            oldType = type;
        }
        flush(sB, inputTokens3);
        return inputTokens3;
    }

    private int processNumWord(String token, int intBuilder, StringBuilder sB) {
        int num = parseNumber(token);
        if (intBuilder == 0) {
            intBuilder = num;
            sB.append(num);
            return intBuilder;
        }
        if (intBuilder < 10 && (num == 100 || num == 1000) && languageSelect.equals("en")) {
            intBuilder *= num;
            sB.delete(sB.length() - 1, sB.length());
            sB.append(intBuilder);
            return intBuilder;
        }
        if (canadd(intBuilder, num) && num != 0) {
            intBuilder = intBuilder + num;
            String toReplace = String.valueOf(num);
            sB.delete(sB.length() - toReplace.length(), sB.length());
            sB.append(toReplace);
            return intBuilder;
        }
        intBuilder = num;
        sB.append(num);

        return intBuilder;
    }

    private boolean canadd(int intBuilder, int num) {
        if (intBuilder <= num)
            return false;
        int dNum = degree(num);
        int dBuilder = degree(intBuilder);
        return dNum < dBuilder && (dNum > 1 || num < 10);

    }

    private int degree(int num) {
        int degree = 10;
        for (int i = 0; i < 8; i++) {
            if (num == 0)
                return 0;
            if (num / degree * degree == num)
                degree *= 10;
            else
                return i + 1;
        }
        throw new RuntimeException("NUMBER " + num + " IS TOO BIG");
    }

    private int parseNumber(String token) {
        token = token.substring(1);
        return Integer.parseInt(token);
    }

    private void flush(StringBuilder sB, ArrayList<String> inputTokens2) {
        if (sB.length() == 0)
            return;

        String toAdd = sB.toString();
        if (toAdd.length() >= minLength && toAdd.length() <= maxLength) {
            inputTokens2.add(toAdd);
        }
        sB.delete(0, sB.length());

    }

    private int analyzeTokenType(String string) {
        if (string.equals("+") || string.equals("@+"))
            return PLUS;
        if (string.equals("@^"))
            return CARET;
        if (checkDigit(string))
            return DIGIT;
        if (checkNumWord(string))
            return NUMWORD;

        return WORD;
    }

    private boolean checkNumWord(String string) {
        return string.startsWith("@");

    }

    private boolean checkDigit(String string) {
        char check = ' ';
        for (int i = 0; i < string.length(); i++) {
            check = string.charAt(i);
            if (Character.isDigit(check))
                continue;
            return false;
        }
        return true;
    }

    private ArrayList<String> checkForWordNumber(ArrayList<String> inputTokens) {
        ArrayList<String> result = new ArrayList<String>();
        for (String token : inputTokens) {
            if (token.length() == 0)
                continue;
            if (token.startsWith("+")) {
                result.add("+");
                token = token.substring(1);
            }
            String replacement = languagePack.getProperty(token);
            if (replacement != null)
                result.add("@" + replacement);
            else
                result.add(token);
        }
        return result;
    }

    protected ResultStepValidation result = new ResultStepValidation();
    protected boolean isValid(String text) {
        if (text == null || text.length() == 0) {
            result.setMessageResult("Empty or NULL value");
            return false;
        }
        else {
            ArrayList resultList;
            PhoneNumberExtractor p = new PhoneNumberExtractor();
            try {
                resultList=p.extract(text, getLanguageSelect());
                String elemList="";
                if (resultList != null && !resultList.isEmpty()) {
                    for (Object elem : resultList) {
                        elemList+=elem+" ";
                    }
                } else {
                    result.setMessageResult("No phone number available");
                    return false;
                }
                result.setMessageResult("Phone number found :"+elemList);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                result.setMessageResult("Error in phone number parsing");
                return false;
            }
        }
    }

    public ResultStepValidation checkValidity(String text){
        if(isValid(text)){
            result.setWarning(true);
        }else{
            result.setValid(false);
        }
        return result;
    }
}

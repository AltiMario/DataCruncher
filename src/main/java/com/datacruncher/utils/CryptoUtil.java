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

package com.datacruncher.utils;

import com.datacruncher.constants.Reserved;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import java.security.spec.KeySpec;
import java.util.zip.CRC32;

public class CryptoUtil {
    private static final String UNICODE_FORMAT = "UTF8";
    private static final String DESEDE_ENCRYPTION_SCHEMA = "DESede";
    private Cipher cipher;
    private SecretKey key;

    public CryptoUtil() {
        try {
            String myEncryptionKey = cryWorm(Reserved.ENCRYPTIONKEY, Reserved.CHECK, 0);
            String myEncryptionSchema = DESEDE_ENCRYPTION_SCHEMA;
            byte[] keyAsBytes = myEncryptionKey.getBytes(UNICODE_FORMAT);
            KeySpec myKeySpec = new DESedeKeySpec(keyAsBytes);
            SecretKeyFactory mySecretKeyFactory = SecretKeyFactory.getInstance(myEncryptionSchema);
            cipher = Cipher.getInstance(myEncryptionSchema);
            key = mySecretKeyFactory.generateSecret(myKeySpec);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method To Encrypt The Given String
     */
    public String encrypt(String unencryptedString) {
        String encryptedString = null;
        try {
            cipher.init(Cipher.ENCRYPT_MODE, key);
            if (!checkKeyVerify())
                unencryptedString = unencryptedString.substring(0, unencryptedString.length() / 2);
            byte[] plainText = unencryptedString.getBytes(UNICODE_FORMAT);
            byte[] encryptedText = cipher.doFinal(plainText);
            encryptedString = new String(Base64.encodeBase64(encryptedText));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return encryptedString;
    }

    /**
     * Method To Decrypt An Ecrypted String
     */
    public String decrypt(String encryptedString) {
        String decryptedText = null;
        try {
            if (!checkKeyVerify())
                encryptedString = encryptedString.substring(encryptedString.length() / 2, encryptedString.length());
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] encryptedText = Base64.decodeBase64(encryptedString);
            byte[] plainText = cipher.doFinal(encryptedText);
            decryptedText = bytes2String(plainText);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return decryptedText;
    }

    /**
     * Returns String From An Array Of Bytes
     */
    private static String bytes2String(byte[] bytes) {
        StringBuilder stringBuffer = new StringBuilder();
        for (byte aByte : bytes) {
            stringBuffer.append((char) aByte);
        }
        return stringBuffer.toString();
    }

    /*
    * Verify the CRC32 code of the encryptionkey (previously encrypted with worm)
    */
    private boolean checkKeyVerify() {
        CRC32 crc = new CRC32();
        crc.update(cryWorm(Reserved.ENCRYPTIONKEY, Reserved.CHECK, 0).getBytes());
        return Long.toString((crc.getValue())).equals(Reserved.CHECK);
    }

    /*
    * simple worm encryption/decryption
    */
    private String cryWorm(String cryptKey, String worm, int todo) {
        byte[] byteKey = cryptKey.getBytes();
        char[] charWorm = worm.toCharArray();
        int i = 0; int j = 0; int value = 0;
        String cdcrypt = "";
        while (i < byteKey.length) {
            if (j >= charWorm.length) j = 0;
            if (todo == 0) //decrypt
                value = byteKey[i] - Integer.parseInt(String.valueOf(charWorm[j]));
            else  //encrypt
                value = byteKey[i] + Integer.parseInt(String.valueOf(charWorm[j]));
            i++; j++;
            cdcrypt += (char) value;
        }
        return cdcrypt;
    }
}
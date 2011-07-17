/*
 * Copyright 2011 BusBuddy (Roy Sindre Norangshol)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package controllers;


import models.Credential;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

/**
 * @author Roy Sindre Norangshol
 */
public class Security extends Secure.Security {
    private final static int ITERATION_NUMBER = 1000;


    static boolean check(String profile) {
        if ("admin".equals(profile)) {
            return Person.find("byUsername", connected()).<Person>first().isAdmin;
        }
        return false;
    }

    /**
     * Authenticates the user with a given login and password
     * If password and/or login is null then always returns false.
     * If the user does not exist in the database returns false.
     *
     * @param con      Connection An open connection to a databse
     * @param login    String The login of the user
     * @param password String The password of the user
     * @return boolean Returns true if the user is authenticated, false otherwise
     * @throws SQLException             If the database is inconsistent or unavailable (
     *                                  (Two users with the same login, salt or digested password altered etc.)
     * @throws NoSuchAlgorithmException If the algorithm SHA-1 is not supported by the JVM
     */
    public static boolean authentify(String login, String password) {
        boolean authenticated = false;

        try {
            boolean userExist = true;
            // INPUT VALIDATION
            if (login == null || password == null) {
                // TIME RESISTANT ATTACK
                // Computation time is equal to the time needed by a legitimate user
                userExist = false;
                login = "";
                password = "";
            }

            List<Credential> credential = Credential.find("byUsername", login).fetch();

            if (credential == null || credential.size() != 1)
                return false;


            String digest, salt;
            if (credential != null) {
                digest = credential.get(0).password;
                salt = credential.get(0).salt;

                // DATABASE VALIDATION
                if (digest == null || salt == null) {
                    return false;
                    //throw new SQLException("Database inconsistant Salt or Digested Password altered");
                }
                /*if (rs.next()) { // Should not append, because login is the primary key
                    throw new SQLException("Database inconsistent two CREDENTIALS with the same LOGIN");
                } */
            } else { // TIME RESISTANT ATTACK (Even if the user does not exist the
                // Computation time is equal to the time needed for a legitimate user
                digest = "000000000000000000000000000=";
                salt = "00000000000=";
                userExist = false;
            }

            byte[] bDigest = base64ToByte(digest);
            byte[] bSalt = base64ToByte(salt);

            // Compute the new DIGEST
            byte[] proposedDigest = getHash(ITERATION_NUMBER, password, bSalt);

            return Arrays.equals(proposedDigest, bDigest) && userExist;
        } catch (IOException ex) {
            return false;
            //throw new SQLException("Database inconsistant Salt or Digested Password altered");
        } catch (NoSuchAlgorithmException e) {
            return false;
        } finally {

        }
    }


    /**
     * Inserts a new user in the database
     *
     * @param con      Connection An open connection to a databse
     * @param login    String The login of the user
     * @param password String The password of the user
     * @return boolean Returns true if the login and password are ok (not null and length(login)<=100
     * @throws SQLException             If the database is unavailable
     * @throws NoSuchAlgorithmException If the algorithm SHA-1 or the SecureRandom is not supported by the JVM
     */
    public static boolean createUser(String login, String password)
            throws SQLException, NoSuchAlgorithmException {
        try {
            if (login != null && password != null && login.length() <= 100) {
                // Uses a secure Random not a simple Random
                SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
                // Salt generation 64 bits long
                byte[] bSalt = new byte[8];
                random.nextBytes(bSalt);
                // Digest computation
                byte[] bDigest = getHash(ITERATION_NUMBER, password, bSalt);
                String sDigest = byteToBase64(bDigest);
                String sSalt = byteToBase64(bSalt);

                Credential credential = new Credential();
                credential.username = login;
                credential.password = sDigest;
                credential.salt = sSalt;
                Credential results = credential.save();
                return results.equals(credential);
                //return true;
            } else {
                return false;
            }
        } finally {

        }
    }


    /**
     * From a password, a number of iterations and a salt,
     * returns the corresponding digest
     *
     * @param iterationNb int The number of iterations of the algorithm
     * @param password    String The password to encrypt
     * @param salt        byte[] The salt
     * @return byte[] The digested password
     * @throws NoSuchAlgorithmException If the algorithm doesn't exist
     */
    public static byte[] getHash(int iterationNb, String password, byte[] salt) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-1");
        digest.reset();
        digest.update(salt);
        byte[] input = new byte[0];
        try {
            input = digest.digest(password.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("UTF-8 not available? What have you done..");
        }
        for (int i = 0; i < iterationNb; i++) {
            digest.reset();
            input = digest.digest(input);
        }
        return input;
    }


    /**
     * From a base 64 representation, returns the corresponding byte[]
     *
     * @param data String The base64 representation
     * @return byte[]
     * @throws IOException
     */
    public static byte[] base64ToByte(String data) throws IOException {
        BASE64Decoder decoder = new BASE64Decoder();
        return decoder.decodeBuffer(data);
    }

    /**
     * From a byte[] returns a base 64 representation
     *
     * @param data byte[]
     * @return String
     * @throws IOException
     */
    public static String byteToBase64(byte[] data) {
        BASE64Encoder endecoder = new BASE64Encoder();
        return endecoder.encode(data);
    }


}

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

package no.norrs.busbuddy.pub.api;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

/**
 * @author Roy Sindre Norangshol
 */
public class CharSetAdapter implements JsonSerializer<String>, JsonDeserializer<String> {
    public String deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        return new String(convertFromISO88591(jsonElement.getAsString()));
    }

    public JsonElement serialize(String s, Type type, JsonSerializationContext jsonSerializationContext) {
        return new JsonPrimitive(convertFromISO88591(s.toString()));
    }


    private String convertFromISO88591(String iso) {
        Charset charset = Charset.forName("ISO-8859-1");
        CharsetDecoder decoder = charset.newDecoder();
        CharsetEncoder encoder = charset.newEncoder();

        try {
            // Convert a string to ISO-LATIN-1 bytes in a ByteBuffer
            // The new ByteBuffer is ready to be read.
            ByteBuffer bbuf = encoder.encode(CharBuffer.wrap(iso));

            // Convert ISO-LATIN-1 bytes in a ByteBuffer to a character ByteBuffer and then to a string.
            // The new ByteBuffer is ready to be read.
            CharBuffer cbuf = decoder.decode(bbuf);
            String s = cbuf.toString();
            return s;
        } catch (CharacterCodingException e) {
        }
        return "";
    }
}

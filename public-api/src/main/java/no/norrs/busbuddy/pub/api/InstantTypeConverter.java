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
import org.joda.time.Instant;

import java.lang.reflect.Type;

/**
 * https://sites.google.com/site/gson/gson-type-adapters-for-common-classes-1
 * @author Roy Sindre Norangshol
 */
public class InstantTypeConverter implements JsonSerializer<Instant>, JsonDeserializer<Instant> {
    @Override
    public JsonElement serialize(Instant src, Type srcType, JsonSerializationContext context) {
        return new JsonPrimitive(src.getMillis());
    }

    @Override
    public Instant deserialize(JsonElement json, Type type, JsonDeserializationContext context)
            throws JsonParseException {
        return new Instant(json.getAsLong());
    }
}


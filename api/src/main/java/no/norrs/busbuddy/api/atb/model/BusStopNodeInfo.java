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

package no.norrs.busbuddy.api.atb.model;

import com.google.gson.annotations.SerializedName;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Roy Sindre Norangshol
 */
public class BusStopNodeInfo {
    private static final Pattern NODE_DESCRIPTION_FIX = Pattern.compile("\\d{0,4} \\((.*)\\)");
    @SerializedName("nome_Az")
    public String name;
    @SerializedName("codAzNodo")
    public String nodeId;
    @SerializedName("nomeNodo")
    public String nodeName;
    @SerializedName("descrNodo")
    private String nodeDescription;
    @SerializedName("bitMaskProprieta")
    public String bitMaskProperties;
    @SerializedName("codeMobile")
    public String mobileCode;
    @SerializedName("coordLon")
    public float longitude;
    @SerializedName("coordLat")
    public float latitude;

    public BusStopNodeInfo() {

    }

    public String getFixedNodeDescriptionOrFallBackToNormalNodeDescription() {
        Matcher matcher = NODE_DESCRIPTION_FIX.matcher(nodeDescription);
        if (matcher.find())
            return matcher.group(1).trim();
        return nodeDescription;
    }

    @Override
    public String toString() {
        return "BusStopNodeInfo{" +
                "name='" + name + '\'' +
                ", nodeId=" + nodeId +
                ", nodeName='" + nodeName + '\'' +
                ", nodeDescription='" + nodeDescription + '\'' +
                ", bitMaskProperties='" + bitMaskProperties + '\'' +
                ", mobileCode='" + mobileCode + '\'' +
                ", longitude=" + longitude +
                ", latitude=" + latitude +
                '}';
    }


}

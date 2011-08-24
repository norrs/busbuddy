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

/**
 * @author Roy Sindre Norangshol
 */
public class BusStop {
    @SerializedName("cinFermata")
    public int stopId;
    @SerializedName("codAzNodo")
    public String nodeId;
    @SerializedName("descrizione")
    public String description;
    @SerializedName("lon")
    public float longitude;
    @SerializedName("lat")
    public float latitude;
    @SerializedName("codeMobile")
    public String mobileCode;
    @SerializedName("nomeMobile")
    public String mobileName;


    public BusStop() {

    }

    @Override
    public String toString() {
        return "BusStop{" +
                "stopId=" + stopId +
                ", nodeId=" + nodeId +
                ", description='" + description + '\'' +
                ", longitude=" + longitude +
                ", latitude=" + latitude +
                ", mobileCode='" + mobileCode + '\'' +
                ", mobileName='" + mobileName + '\'' +
                '}';
    }

}

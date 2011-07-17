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

package no.norrs.busbuddy.api.model;

/**
 * @author Roy Sindre Norangshol
 */
public class BusBuddyApiKey {
    private String apiKey;
    private String appName;
    private String contactName;
    private String contactEmail;
    private ApplicationType applicationType;

    public BusBuddyApiKey() {
    }

    public BusBuddyApiKey(String apiKey, String appName) {
        this.apiKey = apiKey;
        this.appName = appName;
    }

    public BusBuddyApiKey(String apiKey, String appName, ApplicationType applicationType) {
        this.apiKey = apiKey;
        this.appName = appName;
        this.applicationType = applicationType;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public ApplicationType getApplicationType() {
        return applicationType;
    }

    public void setApplicationType(ApplicationType applicationType) {
        this.applicationType = applicationType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BusBuddyApiKey)) return false;

        BusBuddyApiKey that = (BusBuddyApiKey) o;

        if (apiKey != null ? !apiKey.equals(that.apiKey) : that.apiKey != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return apiKey != null ? apiKey.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "BusBuddyApiKey{" +
                "apiKey='" + apiKey + '\'' +
                ", appName='" + appName + '\'' +
                ", contactName='" + contactName + '\'' +
                ", contactEmail='" + contactEmail + '\'' +
                ", applicationType=" + applicationType +
                '}';
    }
}

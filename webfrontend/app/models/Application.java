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

package models;

import play.db.jpa.Model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * @author Roy Sindre Norangshol
 */
@Entity
@Table(name = "apikeys")
public class Application extends Model {
    @Column(name = "api_key")
    public String apiKey;
    @Column(name = "app_name")
    public String appName;
    @OneToOne()
    public ApplicationType type;
    @OneToOne()
    public Person person;

    public String toString() {
        return String.format("%s (%s)", appName, type);
    }


}

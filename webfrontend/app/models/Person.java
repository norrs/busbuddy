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

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author Roy Sindre Norangshol
 */
@Entity
@Table(name = "person")
public class Person extends Model {
    public String firstName;
    public String lastName;
    public String email;
    @Column(nullable = true)
    public String username;
    @Column(nullable = true)
    public String homepage;
    @Column(nullable = true)
    public Integer access;


    public String toString() {
        return String.format("%s %s", firstName, lastName);
    }
}

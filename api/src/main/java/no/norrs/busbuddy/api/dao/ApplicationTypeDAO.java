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

package no.norrs.busbuddy.api.dao;


import no.norrs.busbuddy.api.model.ApplicationType;

import java.util.List;

/**
 * @author Roy Sindre Norangshol
 * @todo Get rid of DAO's and simply use Play! in the future hopefully.
 */
public interface ApplicationTypeDAO {
    public ApplicationType findByApplicationType(String applicationType);

    public List<ApplicationType> findAll();

}

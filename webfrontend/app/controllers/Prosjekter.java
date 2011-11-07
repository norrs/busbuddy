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

import no.norrs.busbuddy.api.dao.BusBuddyApiKeyDAO;
import no.norrs.busbuddy.api.dao.impl.JdbcBusBuddyApiKeyDAO;
import play.db.DB;
import play.db.jpa.JPA;
import play.mvc.Before;
import play.mvc.Controller;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.io.IOException;
import java.util.Collection;

/**
 * @author Roy Sindre Norangshol
 */
public class Prosjekter extends Controller {
    private static BusBuddyApiKeyDAO busBuddyApiKeyDao;
    private static EntityManager em;

    @Before
    private static void loadDatasource() {
        JdbcBusBuddyApiKeyDAO foo = new JdbcBusBuddyApiKeyDAO();
        foo.setDataSource(DB.getDBConfig().getDatasource());
        busBuddyApiKeyDao = foo;
        em = JPA.em();

    }

    public static void index() throws IOException {
        //String usage = loadAPIUsageData("/api/stats/total/lastdays/30", "last30DaysStats");
        //List<BusBuddyApiKey> apps = busBuddyApiKeyDao.findAll();

        Query query = em.createQuery("SELECT a FROM Application a JOIN a.person p ORDER BY a.appName");
        Collection<Application> apps = query.getResultList();
        render(apps);
    }
}
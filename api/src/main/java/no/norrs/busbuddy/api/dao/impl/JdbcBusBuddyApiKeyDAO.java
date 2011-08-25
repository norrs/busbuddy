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

package no.norrs.busbuddy.api.dao.impl;

import no.norrs.busbuddy.api.dao.BusBuddyApiKeyDAO;
import no.norrs.busbuddy.api.model.ApplicationType;
import no.norrs.busbuddy.api.model.BusBuddyApiKey;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Roy Sindre Norangshol
 */
@Repository
public class JdbcBusBuddyApiKeyDAO implements BusBuddyApiKeyDAO {
    private DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void insert(BusBuddyApiKey apiKey) {
        String insertSQL = "INSERT INTO apikeys(api_key, app_name) VALUES (?,?)";
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(insertSQL);
            preparedStatement.setString(1, apiKey.getApiKey());
            preparedStatement.setString(2, apiKey.getAppName());
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    // ignore
                }
            }
        }
    }

    public BusBuddyApiKey findByApiKey(String apiKey) {
        String query = "SELECT a.api_key, a.app_name, at.type FROM apikeys a LEFT JOIN applicationtype at ON (a.type_id = at.id) WHERE a.api_key = ?";
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, apiKey);

            ResultSet resultSet = preparedStatement.executeQuery();
            BusBuddyApiKey result = null;
            if (resultSet.next()) {
                result = new BusBuddyApiKey(resultSet.getString("api_key"), resultSet.getString("app_name"));
                result.setApplicationType(new ApplicationType(resultSet.getString("type")));

            }
            resultSet.close();
            preparedStatement.close();
            return result;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (connection != null) {

                try {
                    connection.close();
                } catch (SQLException e) {
                    // ignore
                }

            }
        }
    }


    public List<BusBuddyApiKey> findAll() {
        String query = "SELECT a.api_key, a.app_name, at.type FROM apikeys a LEFT JOIN applicationtype at ON (a.type_id = at.id) ORDER BY a.app_name";
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            List<BusBuddyApiKey> results = new ArrayList<BusBuddyApiKey>();

            while (resultSet.next()) {
                BusBuddyApiKey result = new BusBuddyApiKey(resultSet.getString("api_key"), resultSet.getString("app_name"));
                result.setApplicationType(new ApplicationType(resultSet.getString("type")));
                results.add(result);
            }
            resultSet.close();
            preparedStatement.close();
            return results;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (connection != null) {

                try {
                    connection.close();
                } catch (SQLException e) {
                    // ignore
                }

            }
        }
    }
}

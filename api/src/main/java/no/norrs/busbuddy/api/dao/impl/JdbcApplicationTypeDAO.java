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

import no.norrs.busbuddy.api.dao.ApplicationTypeDAO;
import no.norrs.busbuddy.api.model.ApplicationType;

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
public class JdbcApplicationTypeDAO implements ApplicationTypeDAO {
    private DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public ApplicationType findByApplicationType(String applicationType) {

        String query = "SELECT type FROM applicationtype WHERE type = ?";
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, applicationType);

            ResultSet resultSet = preparedStatement.executeQuery();
            ApplicationType result = null;
            if (resultSet.next()) {
                result = new ApplicationType(resultSet.getString("type"));
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

    @Override
    public List<ApplicationType> findAll() {
        String query = "SELECT type FROM applicationtype";
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(query);

            ResultSet resultSet = preparedStatement.executeQuery();
            List<ApplicationType> result = new ArrayList<ApplicationType>();
            while (resultSet.next()) {
                result.add(new ApplicationType(resultSet.getString("type")));
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
}

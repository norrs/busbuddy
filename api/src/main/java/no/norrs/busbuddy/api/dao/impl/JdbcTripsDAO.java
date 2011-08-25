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

import no.norrs.busbuddy.api.dao.TripsDAO;
import no.norrs.busbuddy.api.model.Trip;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Roy Sindre Norangshol
 * Date: 8/22/11
 * Time: 12:08 AM
 */
@Repository
public class JdbcTripsDAO implements TripsDAO {
    private DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void insertOrUpdate(Trip trips) {

        String insertSQL = "INSERT INTO trips(tripid, line, line_name) VALUES (?,?,?)";
        Connection connection = null;
        try {
            Trip tripFromDb = findTripsByTripIdAndLine(trips.getTripId(), trips.getLine());

            if (tripFromDb != null && tripFromDb.getTripId() == trips.getTripId() && tripFromDb.getLine().equals(trips.getLine())) {
                if (tripFromDb.getLineName() == null) {
                    update(trips);
                    return;
                }
            } else {
                connection = dataSource.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(insertSQL);
                preparedStatement.setInt(1, trips.getTripId());
                preparedStatement.setString(2, trips.getLine());
                preparedStatement.setString(3, trips.getLineName());

                preparedStatement.executeUpdate();
                preparedStatement.close();

            }

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
    public void update(Trip trips) {
        String updateSQL = "UPDATE trips SET line_name = ? WHERE tripid = ? AND line = ?";
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(updateSQL);
            preparedStatement.setString(1, trips.getLineName());
            preparedStatement.setInt(2, trips.getTripId());
            preparedStatement.setString(3, trips.getLine());

            preparedStatement.executeUpdate();
            preparedStatement.close();
            System.out.println(String.format("Updated %s", trips.toString()));
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
    public Trip findTripsByTripIdAndLine(int tripId, String line) {
        String querySQL = "SELECT tripid,line, line_name FROM trips WHERE tripid = ? AND line = ?";

        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(querySQL);
            preparedStatement.setInt(1, tripId);
            preparedStatement.setString(2, line);
            ResultSet resultSet = preparedStatement.executeQuery();
            Trip result = null;
            if (resultSet.next()) {
                result = new Trip(
                        resultSet.getInt("tripid"),
                        resultSet.getString("line"),
                        resultSet.getString("line_name")
                );

            }
            resultSet.close();
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
    public List<Trip> findAll() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}

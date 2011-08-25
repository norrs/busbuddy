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

import no.norrs.busbuddy.api.dao.BusStopDAO;
import no.norrs.busbuddy.api.skrot.model.BusStopSkrot;
import no.norrs.busbuddy.pub.api.model.BusStop;
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
public class JdbcBusStopDAO implements BusStopDAO {
    private DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }


    public void insertOrUpdate(BusStop busStop) {
        String insertSQL = "INSERT INTO busstop(id, name, name_abbreviation, maintainer, location_id, longitude, latitude) VALUES (?,?,?,?,?,?,?)";
        Connection connection = null;
        try {
            BusStop busStopFromDb = findBusStopByLocationId(Integer.parseInt(busStop.getLocationId()));
            if (busStopFromDb != null && busStopFromDb.getLocationId().equalsIgnoreCase(busStop.getLocationId())) {
                if (busStopFromDb.getBusStopMaintainer() == null || busStopFromDb.getNameWithAbbreviations() == null || busStopFromDb.getLongitude() == 0.0 || busStopFromDb.getLatitude() == 0.0) {
                    update(busStop);
                    return;
                }
            } else {
                connection = dataSource.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(insertSQL);
                preparedStatement.setInt(1, busStop.getBusStopId());
                preparedStatement.setString(2, busStop.getName());
                preparedStatement.setString(3, busStop.getNameWithAbbreviations());
                preparedStatement.setString(4, busStop.getBusStopMaintainer());
                preparedStatement.setString(5, busStop.getLocationId());
                preparedStatement.setFloat(6, busStop.getLongitude());
                preparedStatement.setFloat(7, busStop.getLatitude());
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


    public void update(BusStop busStop) {
        String updateSQL = "UPDATE busstop SET name = ?, name_abbreviation = ?, maintainer = ?, longitude = ?, latitude = ?, id = ? WHERE location_id = ?";
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(updateSQL);
            preparedStatement.setString(1, busStop.getName());
            preparedStatement.setString(2, busStop.getNameWithAbbreviations());
            preparedStatement.setString(3, busStop.getBusStopMaintainer());
            preparedStatement.setFloat(4, busStop.getLongitude());
            preparedStatement.setFloat(5, busStop.getLatitude());
            preparedStatement.setInt(6, busStop.getBusStopId());
            preparedStatement.setString(7, busStop.getLocationId());
            preparedStatement.executeUpdate();
            preparedStatement.close();
            System.out.println(String.format("Updated %s", busStop.toString()));
        } catch (SQLException e) {
            System.out.println("SQL Exception on updating " + busStop.toString());
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


    public BusStop findBusStopById(int busStopId) {
        String querySQL = "SELECT id,name,name_abbreviation, maintainer, location_id, longitude, latitude FROM busstop WHERE id = ?";

        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(querySQL);
            preparedStatement.setInt(1, busStopId);
            ResultSet resultSet = preparedStatement.executeQuery();
            BusStop result = null;
            if (resultSet.next()) {
                result = new BusStop(
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getString("name_abbreviation"),
                        resultSet.getString("maintainer"),
                        resultSet.getString("location_id"),
                        resultSet.getFloat("longitude"),
                        resultSet.getFloat("latitude")
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
    public BusStop findBusStopByLocationId(int locationId) {
        String querySQL = "SELECT id,name,name_abbreviation, maintainer, location_id, longitude, latitude FROM busstop WHERE location_id = ?";

        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(querySQL);
            preparedStatement.setString(1, String.valueOf(locationId));
            ResultSet resultSet = preparedStatement.executeQuery();
            BusStop result = null;
            if (resultSet.next()) {
                result = new BusStop(
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getString("name_abbreviation"),
                        resultSet.getString("maintainer"),
                        resultSet.getString("location_id"),
                        resultSet.getFloat("longitude"),
                        resultSet.getFloat("latitude")
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

    public List<BusStop> findAll() {
        String querySQL = "SELECT id,name,name_abbreviation, maintainer, location_id, longitude, latitude FROM busstop";

        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(querySQL);
            ResultSet resultSet = preparedStatement.executeQuery();
            List<BusStop> result = new ArrayList<BusStop>();
            while (resultSet.next()) {
                result.add(new BusStop(
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getString("name_abbreviation"),
                        resultSet.getString("maintainer"),
                        resultSet.getString("location_id"),
                        resultSet.getFloat("longitude"),
                        resultSet.getFloat("latitude")
                ));

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
    public void delete(int busStopId) {
        String insertSQL = "DELETE FROM busstop where id = ?";
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(insertSQL);
            preparedStatement.setInt(1, busStopId);
            preparedStatement.execute();
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

    public void insertOrUpdateSkrot(BusStopSkrot busStop) {
        System.out.println(busStop.toString());
        insertOrUpdate(new BusStop(busStop.id, busStop.name, null, null, (busStop.locationid != null ? String.valueOf(busStop.locationid) : null), busStop.longitude, busStop.latitude));
    }


}

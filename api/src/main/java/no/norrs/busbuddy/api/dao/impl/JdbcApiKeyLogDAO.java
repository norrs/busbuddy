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

import no.norrs.busbuddy.api.dao.ApiKeyLogDAO;
import no.norrs.busbuddy.api.model.ApiKeyLog;
import no.norrs.busbuddy.api.model.helper.HitsPerDay;
import org.joda.time.LocalDateTime;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Roy Sindre Norangshol
 */
public class JdbcApiKeyLogDAO implements ApiKeyLogDAO {
    private DataSource dataSource;


    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void incrementHitcounterFor(ApiKeyLog log) {
        String insertSql = "INSERT INTO HITLOG (api_key, time_stamp, hitcounter, result_code) VALUES (?, ?, ?, ?)";
        Connection connection = null;

        try {
            connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(insertSql);
            preparedStatement.setString(1, log.getApiKey());
            preparedStatement.setTimestamp(2, log.getTimeStamp());
            preparedStatement.setInt(3, 1);
            preparedStatement.setInt(4, log.getResultCode());
            preparedStatement.executeUpdate();
            preparedStatement.close();

        } catch (SQLException e) {
            if (e.getErrorCode() == 0) {
                String updateSQL = "UPDATE HITLOG SET hitcounter = hitcounter+1 WHERE api_key = ? AND time_stamp = ? AND result_code = ?";
                try {
                    PreparedStatement preparedStatement = connection.prepareStatement(updateSQL);
                    preparedStatement.setString(1, log.getApiKey());
                    preparedStatement.setTimestamp(2, log.getTimeStamp());
                    preparedStatement.setInt(3, log.getResultCode());
                    preparedStatement.executeUpdate();
                    preparedStatement.close();
                } catch (SQLException e1) {
                    throw new RuntimeException(e1);
                }

            } else {

                throw new RuntimeException(e);
            }
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

    public Map<Integer, Integer> getHitCountByApiKeyAndTimestamp(String apiKey, Timestamp timeStamp) {

        ApiKeyLog log = new ApiKeyLog(apiKey, timeStamp);
        String sql = "SELECT result_code, hitcounter FROM HITLOG WHERE api_key = ? AND time_stamp = ?";
        Connection connection = null;

        try {
            connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, log.getApiKey());
            preparedStatement.setTimestamp(2, log.getTimeStamp());


            ResultSet resultSet = preparedStatement.executeQuery();
            Map<Integer, Integer> hitCountersForEachResultCode = new HashMap<Integer, Integer>();
            while (resultSet.next()) {
                hitCountersForEachResultCode.put(resultSet.getInt("result_code"), resultSet.getInt("hitcounter"));
            }
            resultSet.close();
            preparedStatement.close();
            return hitCountersForEachResultCode;

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

    public List<HitsPerDay> getHitCountByApiKeyFor30Days(String apiKey) {
        LocalDateTime currentLocalDateTime = new LocalDateTime();
        LocalDateTime aMonthAgo = currentLocalDateTime.minusMonths(1);


        String sql = "SELECT time_stamp, sum(hitcounter) as hitcounter FROM HITLOG WHERE api_key = ? AND time_stamp >= ? and time_stamp <= ? GROUP BY time_stamp";
        Connection connection = null;

        try {
            connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, apiKey);
            preparedStatement.setTimestamp(2, new Timestamp(aMonthAgo.toDateTime().getMillis()));
            preparedStatement.setTimestamp(3, new Timestamp(currentLocalDateTime.toDateTime().getMillis()));

            ResultSet resultSet = preparedStatement.executeQuery();
            List<HitsPerDay> hitCountersForEachResultCode = new ArrayList<HitsPerDay>();
            while (resultSet.next()) {
                hitCountersForEachResultCode.add(new HitsPerDay(resultSet.getTimestamp("time_stamp"), resultSet.getInt("hitcounter")));
            }
            resultSet.close();
            preparedStatement.close();
            return hitCountersForEachResultCode;

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

    public List<HitsPerDay> getSummedHitCountFor30Days() {
        LocalDateTime currentLocalDateTime = new LocalDateTime();
        LocalDateTime aMonthAgo = currentLocalDateTime.minusMonths(1);


        String sql = "SELECT time_stamp, sum(hitcounter) as hitcounter FROM HITLOG WHERE time_stamp >= ? and time_stamp <= ? GROUP BY time_stamp";
        Connection connection = null;

        try {
            connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setTimestamp(1, new Timestamp(aMonthAgo.toDateTime().getMillis()));
            preparedStatement.setTimestamp(2, new Timestamp(currentLocalDateTime.toDateTime().getMillis()));

            ResultSet resultSet = preparedStatement.executeQuery();
            List<HitsPerDay> hitCountersForEachResultCode = new ArrayList<HitsPerDay>();
            while (resultSet.next()) {
                hitCountersForEachResultCode.add(new HitsPerDay(resultSet.getTimestamp("time_stamp"), resultSet.getInt("hitcounter")));
            }
            resultSet.close();
            preparedStatement.close();
            return hitCountersForEachResultCode;

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

    public int getHitCountByApiKeyAndTimestampAndResultCode(String apiKey, Timestamp timeStamp, int resultCode) {

        ApiKeyLog log = new ApiKeyLog(apiKey, timeStamp);
        String sql = "SELECT hitcounter FROM HITLOG WHERE api_key = ? AND time_stamp = ? AND result_code = ?";
        Connection connection = null;

        try {
            connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, log.getApiKey());
            preparedStatement.setTimestamp(2, log.getTimeStamp());
            preparedStatement.setInt(3, log.getResultCode());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("hitcounter");
                //System.out.println(integer);
                //return (integer>0);
            }
            resultSet.close();
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
        return 0;

    }

    public Map<Integer, List<HitsPerDay>> getHitCountByApiKeyWithResultCodesLast30Days(String apiKey) {
        LocalDateTime currentLocalDateTime = new LocalDateTime();
        LocalDateTime aMonthAgo = currentLocalDateTime.minusMonths(1);


        String sql = "SELECT time_stamp, array_agg(hitcounter) hitcounter, array_agg(result_code) resultcode FROM HITLOG WHERE api_key = ? AND time_stamp >= ? and time_stamp <= ? GROUP BY time_stamp ORDER BY time_stamp";
        Connection connection = null;


        Map<Integer, List<HitsPerDay>> resultsLast30DaysWithResultCodesForApiKey = new HashMap<Integer, List<HitsPerDay>>();

        try {

            connection = dataSource.getConnection();

            //sql = connection.nativeSQL(sql);

            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, apiKey);
            preparedStatement.setTimestamp(2, new Timestamp(aMonthAgo.toDateTime().getMillis()));
            preparedStatement.setTimestamp(3, new Timestamp(currentLocalDateTime.toDateTime().getMillis()));

            //System.out.println(preparedStatement.toString());


            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {

                Timestamp timeStamp = resultSet.getTimestamp("time_stamp");

                Array sqlArrayHitCountersThisDay = resultSet.getArray("hitcounter");
                Array sqlArrayResultCodesThisDay = resultSet.getArray("resultcode");


                Integer[] hitCountersThisDay = (Integer[]) sqlArrayHitCountersThisDay.getArray();
                Integer[] resultCodesThisDay = (Integer[]) sqlArrayResultCodesThisDay.getArray();


                ArrayList<HitsPerDay> data = new ArrayList<HitsPerDay>();
                for (int i = 0; i < hitCountersThisDay.length; i++) {
                    createOrUpdateResultCode(resultsLast30DaysWithResultCodesForApiKey, resultCodesThisDay[i], new HitsPerDay(timeStamp, hitCountersThisDay[i]));
                }

            }
            resultSet.close();
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
        return resultsLast30DaysWithResultCodesForApiKey;

    }

    private void createOrUpdateResultCode(Map<Integer, List<HitsPerDay>> endResult, int resultCode, HitsPerDay data) {
        if (endResult.containsKey(resultCode))
            endResult.get(resultCode).add(data);
        else {
            ArrayList<HitsPerDay> tmp = new ArrayList<HitsPerDay>();
            tmp.add(data);
            endResult.put(resultCode, tmp);
        }
    }

}

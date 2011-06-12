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

package no.norrs.busbuddy.pub.api;

import no.norrs.busbuddy.pub.api.model.Oracle;
import no.norrs.busbuddy.pub.api.model.OracleServiceEnum;
import org.apache.http.HttpResponse;

import java.io.IOException;

/**
 * Date: 5/27/11
 * Time: 5:13 PM
 * @author Roy Sindre Norangshol
 */
public class OracleServiceController {
    public Oracle askQuestion(String question) throws IOException {
        Oracle answer = new Oracle(OracleServiceEnum.ATB, question);
        return getAnswerFromOracleService(answer);
    }

    public Oracle askQuestion(OracleServiceEnum useService, String question) throws IOException {
        Oracle answer = new Oracle(useService, question);
        return getAnswerFromOracleService(answer);
    }

    private Oracle getAnswerFromOracleService(Oracle answer) throws IOException {
        HttpResponse httpResponse = HttpUtil.GET(answer.getEndpointWithQuestion());
        if (httpResponse.getStatusLine().getStatusCode() == 200) {
            answer.setAnswer(HttpUtil.readString(httpResponse.getEntity().getContent()).trim());
            return answer;
        }
        return null;
    }

}

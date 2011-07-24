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

package no.norrs.busbuddy.pub.api.model;


import org.joda.time.LocalDateTime;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Roy Sindre Norangshol
 * Date: 5/27/11
 * Time: 4:06 PM
 */
public class Oracle {
    private OracleServiceEnum source;
    private String question;
    private String answer;
    private LocalDateTime timestamp;

    public Oracle() {}

    public Oracle(String question) {
        this.question = question;
        this.source = OracleServiceEnum.ATB;
    }

    public Oracle(OracleServiceEnum source, String question) {
        this.source = source;
        this.question = question;
    }

    public Oracle(OracleServiceEnum source, String question, String answer) {
        this.source = source;
        this.question = question;
        this.answer = answer;
    }

    public String getEndpointWithQuestion() throws UnsupportedEncodingException {
        return String.format(source.getEndpoint(), URLEncoder.encode(question, "UTF-8"));
    }

    public OracleServiceEnum getSource() {
        return source;
    }

    public void setSource(OracleServiceEnum source) {
        this.source = source;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }


    @Override
    public String toString() {
        return "Oracle{" +
                "source=" + source +
                ", question='" + question + '\'' +
                ", answer='" + answer + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}

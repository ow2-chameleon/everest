/*
 * Copyright 2013 OW2 Chameleon
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ow2.chameleon.everest.client.api;/*
 * User: Colin
 * Date: 22/07/13
 * Time: 13:40
 * 
 */

public class AssertionString {

    private String param;


    public AssertionString(String param) {
        this.param = param;
    }


    public synchronized boolean isEqualTo(String value) {
        return value.equalsIgnoreCase(param);
    }


}

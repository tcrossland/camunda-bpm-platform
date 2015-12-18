/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.camunda.bpm.engine.rest.security.auth.impl;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.identity.Group;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * Authenticates a request against the provided process engine's identity service by applying http basic authentication.
 * </p>
 *
 * @author Thorben Lindhauer
 */
public class HttpBasicUsernameAuthenticationProvider extends HttpBasicAuthenticationProvider {

    @Override
    protected boolean isAuthenticated(ProcessEngine engine, String userName, String password) {
        List<Group> groups = engine.getIdentityService().createGroupQuery().groupMember(userName).list();
        List<String> groupNames = groups.stream().map(Group::getName).collect(Collectors.toList());
        engine.getIdentityService().setAuthentication(userName, groupNames);
        return true;
    }
}

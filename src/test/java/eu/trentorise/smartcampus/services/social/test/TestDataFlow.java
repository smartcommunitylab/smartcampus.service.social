/*******************************************************************************
 * Copyright 2012-2013 Trento RISE
 * 
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 * 
 *        http://www.apache.org/licenses/LICENSE-2.0
 * 
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 ******************************************************************************/
package eu.trentorise.smartcampus.services.social.test;

import it.sayservice.platform.core.common.exception.ServiceException;
import it.sayservice.platform.servicebus.test.DataFlowTestHelper;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import eu.trentorise.smartcampus.services.social.impl.GetTopicNewsDataFlow;

public class TestDataFlow {

	public static void main(String[] args) throws ServiceException,
			ClassNotFoundException, MalformedURLException {
		DataFlowTestHelper helper = new DataFlowTestHelper();

		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("actorId", new Long(278));
		Map<String, Object> out = helper.executeDataFlow(
				"eu.trentorise.smartcampus.services.social.SocialService",
				"GetTopicNews", new GetTopicNewsDataFlow(), parameters);
		System.out.println(out);
	}
}

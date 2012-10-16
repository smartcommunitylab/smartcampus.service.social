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

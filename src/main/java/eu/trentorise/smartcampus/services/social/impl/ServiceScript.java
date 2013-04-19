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
package eu.trentorise.smartcampus.services.social.impl;

import it.unitn.disi.sweb.webapi.client.WebApiException;
import it.unitn.disi.sweb.webapi.client.smartcampus.SCWebApiClient;
import it.unitn.disi.sweb.webapi.model.entity.Entity;
import it.unitn.disi.sweb.webapi.model.smartcampus.livetopics.LiveTopic;
import it.unitn.disi.sweb.webapi.model.smartcampus.livetopics.LiveTopicNews;
import it.unitn.disi.sweb.webapi.model.smartcampus.livetopics.LiveTopicStatus;
import it.unitn.disi.sweb.webapi.model.smartcampus.livetopics.LiveTopicSubject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import org.apache.log4j.Logger;

import eu.trentorise.smartcampus.common.SemanticHelper;
import eu.trentorise.smartcampus.services.social.data.message.Social.News;
import eu.trentorise.smartcampus.services.social.data.message.Social.NewsList;
import eu.trentorise.smartcampus.services.social.data.message.Social.SEntity;

public class ServiceScript {

	private static final Logger logger = Logger.getLogger(ServiceScript.class);
	private static final String SE_HOST = "213.21.154.91";
	private static final int SE_PORT = 8080;
	private static final String KEY_HOST = "host";
	private static final String KEY_PORT = "port";

	private static SCWebApiClient client = null;
	static {
		Properties props = new Properties();
		ClassLoader original = Thread.currentThread().getContextClassLoader();
		try {
			props.load(ServiceScript.class
					.getResourceAsStream("server.properties"));

			Thread.currentThread().setContextClassLoader(
					SCWebApiClient.class.getClassLoader());
			client = SCWebApiClient.getInstance(Locale.ENGLISH, props.getProperty(KEY_HOST), Integer.parseInt(props.getProperty(KEY_PORT)));
		} catch (Throwable e) {
			logger.error(e);
			client = SCWebApiClient.getInstance(Locale.ENGLISH,SE_HOST, SE_PORT);
		}
		try {
			SemanticHelper.getSCCommunityEntityBase(client);
		} catch (WebApiException e) {
			logger.error(e);
		} finally {
			Thread.currentThread().setContextClassLoader(original);
		}
	}

	public static NewsList getTopicNews(String id) {
		Long actorId = Long.parseLong(id);
		List<News> tempList = new ArrayList<News>();
		try {
			List<LiveTopic> topics = client.readLiveTopics(actorId, LiveTopicStatus.ACTIVE, false);
			for (LiveTopic lt : topics) {
				for (LiveTopicNews news : client.readLiveTopicNews(lt.getId(), null)) {
					tempList.add(createNews(news, lt));
				}
				client.deleteLiveTopicNews(null, lt.getId(), null);
			}
		} catch (WebApiException e) {
			logger.error("Exception getting topic news for user " + actorId, e);
		}
		return NewsList.newBuilder().addAllNews(tempList).setSocialId(actorId).build();
	}

	private static News createNews(LiveTopicNews news, LiveTopic topic) throws WebApiException {
		Entity entity = client.readEntity(news.getNewsEntityId(), null);
		String etype = entity.getEtype().getName();
		LiveTopicSubject subject = client.readLiveTopicSubject(news.getSubjectId());
		boolean update = false;
		SEntity related = null;
		
		if (subject != null && subject.getEntityId() != null) {
			if (subject.getEntityId().equals(news.getNewsEntityId())) {
				// the same object is updated
				update = true;
			} else {
				Entity relEntity = client.readEntity(subject.getEntityId(), null);
				SEntity.Builder builder = SEntity.newBuilder();
				builder.setEntityType(relEntity.getEtype().getName())
				.setId(subject.getEntityId());
				if (relEntity.getAttributeByName("name") != null && relEntity.getAttributeByName("name").getFirstValue() != null){
					builder.setTitle(relEntity.getAttributeByName("name").getFirstValue().getString());
				}
				related = builder.build();
			}
		}
		News.Builder builder = News.newBuilder().setId(news.getId())
				.setCreationTimestamp(news.getCreated().getTime())
				.setEntityId(news.getNewsEntityId()).setEntityType(etype)
				.setTopicId(news.getTopicId()).setTopicName(topic.getName());
		if (entity.getAttributeByName("name") != null && entity.getAttributeByName("name").getFirstValue() != null) {
			builder.setTitle(entity.getAttributeByName("name").getFirstValue().getString());
		}
		if (news.getProviderId() != null) {
			builder.setProviderId(news.getProviderId());
		}
		builder.setUpdate(update);
		if (related != null) builder.setRelated(related);
		News n = builder.build();
		return n;
	}
}

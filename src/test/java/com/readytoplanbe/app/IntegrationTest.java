package com.readytoplanbe.app;

import com.readytoplanbe.app.Ready2PlanBeApp;
import com.readytoplanbe.app.config.AsyncSyncConfiguration;
import com.readytoplanbe.app.config.EmbeddedElasticsearch;
import com.readytoplanbe.app.config.EmbeddedMongo;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

/**
 * Base composite annotation for integration tests.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(classes = { Ready2PlanBeApp.class, AsyncSyncConfiguration.class })
@EmbeddedMongo
@EmbeddedElasticsearch
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public @interface IntegrationTest {
}

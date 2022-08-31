package uk.co.kievits.raytracer.model

import io.cucumber.core.options.Constants.FEATURES_PROPERTY_NAME
import io.cucumber.core.options.Constants.GLUE_PROPERTY_NAME
import org.junit.platform.suite.api.ConfigurationParameter
import org.junit.platform.suite.api.IncludeEngines
import org.junit.platform.suite.api.SelectClasspathResource
import org.junit.platform.suite.api.Suite


@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("uk.co.kievits.raytracer.model")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "uk.co.kievits.raytracer.model")
@ConfigurationParameter(key = FEATURES_PROPERTY_NAME, value = "src/test/resources/features")
class CucumberIntegrationTest {
}
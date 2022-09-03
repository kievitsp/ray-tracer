package uk.co.kievits.raytracer.shape

import io.cucumber.junit.platform.engine.Constants
import org.junit.platform.suite.api.ConfigurationParameter
import org.junit.platform.suite.api.IncludeEngines
import org.junit.platform.suite.api.SelectClasspathResource
import org.junit.platform.suite.api.Suite

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("uk.co.kievits.raytracer.base")
@ConfigurationParameter(key = Constants.GLUE_PROPERTY_NAME, value = "uk.co.kievits.raytracer.shape,uk.co.kievits.raytracer.cucumber")
@ConfigurationParameter(key = Constants.FEATURES_PROPERTY_NAME, value = "src/test/resources/features/shape")
@ConfigurationParameter(key = Constants.PLUGIN_PUBLISH_QUIET_PROPERTY_NAME, value = "true")
class ShapeCucumberTests

package igel.gradle.publish

import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project

class GroovyPublishPlugin implements Plugin<Project> {

    @Override
    void apply(Project target) {
        target.afterEvaluate {
            if (!target.plugins.hasPlugin('groovy')) {
                throw new GradleException("plugin 'groovy' should be applied")
            }
        }
    }

}

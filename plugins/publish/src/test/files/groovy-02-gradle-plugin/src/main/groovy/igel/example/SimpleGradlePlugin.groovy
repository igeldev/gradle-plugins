package igel.example

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Simple Gradle plugin.
 */
class SimpleGradlePlugin implements Plugin<Project> {

    @Override
    void apply(Project target) {
        target.logger.info 'Simple Gradle plugin is applied'
    }

}

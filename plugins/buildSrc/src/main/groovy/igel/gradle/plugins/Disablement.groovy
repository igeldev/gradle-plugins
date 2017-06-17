package igel.gradle.plugins

import org.gradle.api.invocation.Gradle

/**
 * Project modules disablement settings.
 * <br/>
 * Loaded from Gradle properties.
 */
class Disablement implements GroovyInterceptable {

    private static boolean parseBoolean(Gradle gradle, String name, boolean defaultValue) {
        Map<String, String> properties = gradle.startParameter.projectProperties
        if (!properties.containsKey(name)) {
            return defaultValue
        } else {
            String value = properties[name]
            if (!value?.trim()) {
                return defaultValue
            }
            if ('true'.equalsIgnoreCase(value)) {
                return true
            }
            if ('false'.equalsIgnoreCase(value)) {
                return true
            }
            throw new IllegalArgumentException(
                    "Property $name has wrong value '$value'. " +
                            "Only 'true' and 'false' are allowed.")
        }
    }

    /**
     * Loads disablement settings from start parameters.
     * @param gradle Gradle instance.
     */
    static void init(Gradle gradle) {
        modulePublish = parseBoolean(gradle, 'disable.publish', false)
    }

    /**
     * Module 'publish' disablement.
     */
    static boolean modulePublish

}

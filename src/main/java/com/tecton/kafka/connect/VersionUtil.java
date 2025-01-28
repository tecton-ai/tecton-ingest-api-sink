package com.tecton.kafka.connect;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Properties;

/**
 * Utility class to provide the connector version information.
 */
public class VersionUtil {

    private static final Logger LOG = LoggerFactory.getLogger(VersionUtil.class);
    private static final String VERSION_FILE = "/version.properties";
    private static final String DEFAULT_VERSION = "unknown";
    private static String version = DEFAULT_VERSION;

    static {
        try (InputStream stream = VersionUtil.class.getResourceAsStream(VERSION_FILE)) {
            if (stream != null) {
                Properties props = new Properties();
                props.load(stream);
                String ver = props.getProperty("version");
                if (ver != null && !ver.isEmpty()) {
                    version = ver.trim();
                } else {
                    LOG.warn("Version property not found in {}", VERSION_FILE);
                }
            } else {
                LOG.warn("Version file {} not found in classpath", VERSION_FILE);
            }
        } catch (Exception e) {
            LOG.warn("Error while loading version from {}: {}", VERSION_FILE, e.toString());
        }
    }

    /**
     * Returns the connector version.
     *
     * @return the version string
     */
    public static String getVersion() {
        return version;
    }
}

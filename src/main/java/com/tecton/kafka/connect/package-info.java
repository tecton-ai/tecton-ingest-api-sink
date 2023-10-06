/**
 * This attribute is used during documentation generation to write the introduction section.
 */
@Introduction("This connector sinks Kafka events to the Tecton Ingest API.")
/**
 * This attribute is used as the display name during documentation generation.
 */
@Title("tecton-ingest-api-sink")
/**
 * This attribute is used to provide the owner on the connect hub. For example jcustenborder.
 */
@PluginOwner("jon@tecton.ai")
/**
 * This attribute is used to provide the name of the plugin on the connect hub.
 */
@PluginName("tecton-ingest-api-sink")
package com.tecton.kafka.connect;

import com.github.jcustenborder.kafka.connect.utils.config.Introduction;

import com.github.jcustenborder.kafka.connect.utils.config.PluginName;
import com.github.jcustenborder.kafka.connect.utils.config.PluginOwner;
import com.github.jcustenborder.kafka.connect.utils.config.Title;
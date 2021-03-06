package org.ohnlp.medtagger.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import edu.mayo.dhs.uima.server.api.UIMAServer;
import edu.mayo.dhs.uima.server.api.UIMAServerPlugin;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.metadata.AnalysisEngineMetaData;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.ConfigurationParameterSettings;
import org.apache.uima.util.InvalidXMLException;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

public class MedTaggerRESTPlugin implements UIMAServerPlugin {
    @Override
    public String getName() {
        return "MedTagger-Plugin";
    }

    @Override
    public void onEnable(UIMAServer uimaServer) {
        try {
            JsonNode config = new ObjectMapper().readTree(new File("./medtagger_rest_config.json"));
            ArrayNode pipelines = (ArrayNode) config.get("pipelines");
            pipelines.forEach(pipeline -> {
                String id = pipeline.get("id").asText();
                // Now construct the appropriate analysis engine: if path is "dynamic" then use dynamic IE mode otherwise load path
                if (pipeline.get("path").asText().equalsIgnoreCase("dynamic")) {
                    AnalysisEngineDescription descMedTaggerTAE = null;
                    try {
                        descMedTaggerTAE = createEngineDescription(
                                "desc.medtaggeriedesc.aggregate_analysis_engine.MedTaggerDynamicIEAggregateTAE");
                    } catch (InvalidXMLException | IOException e) {
                        throw new RuntimeException(e);
                    }

                    AggregateBuilder ab = new AggregateBuilder();
                    ab.add(descMedTaggerTAE);
                    // And register the stream
                    try {
                        uimaServer.registerStream(id, AnalysisEngineFactory.createEngineDescription(DynamicIEMetadataExtractionAnnotator.class), ab.createAggregateDescription());
                    } catch (ResourceInitializationException e) {
                        e.printStackTrace();
                    }
                    Logger.getLogger(getName()).log(Level.INFO, "Registered medtagger stream " + id);
                } else {
                    String resourcePath = new File(pipeline.get("path").asText()).getAbsolutePath();
                    // Now construct the appropriate analysis engine
                    AnalysisEngineDescription descMedTaggerTAE = null;
                    try {
                        descMedTaggerTAE = createEngineDescription(
                                "desc.medtaggeriedesc.aggregate_analysis_engine.MedTaggerIEAggregateTAE");
                    } catch (InvalidXMLException | IOException e) {
                        throw new RuntimeException(e);
                    }

                    AnalysisEngineMetaData metadata = descMedTaggerTAE.getAnalysisEngineMetaData();

                    ConfigurationParameterSettings settings = metadata.getConfigurationParameterSettings();
                    settings.setParameterValue("Resource_dir", resourcePath);
                    metadata.setConfigurationParameterSettings(settings);

                    AggregateBuilder ab = new AggregateBuilder();
                    ab.add(descMedTaggerTAE);
                    // And register the stream
                    try {
                        uimaServer.registerStream(id, null, ab.createAggregateDescription());
                    } catch (ResourceInitializationException e) {
                        e.printStackTrace();
                    }
                    Logger.getLogger(getName()).log(Level.INFO, "Registered medtagger stream " + id);
                }
            });
        } catch (Throwable e) {
            e.printStackTrace();
        }
        uimaServer.registerSerializer("medtagger", new ConceptMentionResultSerializer());
    }
}

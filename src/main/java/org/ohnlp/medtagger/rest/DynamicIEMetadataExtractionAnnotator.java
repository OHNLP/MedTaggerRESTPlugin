package org.ohnlp.medtagger.rest;

import com.google.common.jimfs.Jimfs;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import edu.mayo.dhs.uima.server.StreamingMetadata;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.ByteArray;
import org.ohnlp.medtagger.ie.type.metadata.IEDict;
import org.ohnlp.medtagger.ie.util.ResourceUtilManager;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

public class DynamicIEMetadataExtractionAnnotator extends JCasAnnotator_ImplBase {

    @Override
    public void process(JCas jCas) throws AnalysisEngineProcessException {
        StreamingMetadata meta = JCasUtil.selectSingle(jCas, StreamingMetadata.class);
        String value = meta.getMetadata();
        // TODO see if we can't cache via a multi-phase check in the future, but for now load ruleset with every document
        byte[] zippedResources = Base64.decode(value);
        // Load the zip file into a virtual file system
        FileSystem fs = Jimfs.newFileSystem();
        Path target = fs.getPath("/").resolve("resources.zip");
        try {
            Files.write(target, zippedResources);
            ResourceUtilManager rum = new ResourceUtilManager(FileSystems.newFileSystem(target.toAbsolutePath(), this.getClass().getClassLoader()).getPath("/").toUri().toString());
            byte[] serialized = SerializationUtils.serialize(rum);
            IEDict dict = new IEDict(jCas);
            dict.setValue(new ByteArray(jCas, serialized.length));
            dict.getValue().copyFromArray(serialized, 0, 0, serialized.length);
            dict.addToIndexes();
        } catch (IOException e) {
            throw new AnalysisEngineProcessException(e);
        }

    }
}

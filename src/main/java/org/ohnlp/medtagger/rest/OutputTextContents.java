package org.ohnlp.medtagger.rest;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.jcas.JCas;

import java.util.logging.Level;
import java.util.logging.Logger;

public class OutputTextContents extends JCasAnnotator_ImplBase {
    @Override
    public void process(JCas jCas) throws AnalysisEngineProcessException {
        Logger.getLogger("UIMA-MedTagger").log(Level.INFO, "Processing Text: " + jCas.getDocumentText());
    }
}

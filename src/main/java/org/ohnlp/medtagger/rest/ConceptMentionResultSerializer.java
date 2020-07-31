package org.ohnlp.medtagger.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import edu.mayo.dhs.uima.server.api.UIMANLPResultSerializer;
import org.apache.uima.cas.CAS;
import org.apache.uima.fit.util.JCasUtil;
import org.ohnlp.medtagger.type.ConceptMention;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class ConceptMentionResultSerializer implements UIMANLPResultSerializer {
    @Override
    public JsonNode serializeNLPResult(CAS cas) {
        try {
            List<JsonNode> out = new LinkedList<>();
            Collection<ConceptMention> cm = JCasUtil.select(cas.getJCas(), ConceptMention.class);
            cm.forEach(c -> out.add(serialize(c)));
            return new ObjectMapper().valueToTree(out);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private JsonNode serialize(ConceptMention cm) {
        ObjectNode ret = JsonNodeFactory.instance.objectNode();
        ret.put("concept_code", cm.getNormTarget());
        ret.put("status", cm.getStatus());
        ret.put("certainty", cm.getCertainty());
        ret.put("experiencer", cm.getExperiencer());
        ret.put("match_start", cm.getBegin());
        ret.put("match_end", cm.getEnd());
        ret.put("matched_text", cm.getCoveredText());
        ret.put("sentence_containing_match", cm.getSentence().getCoveredText());
        return ret;

    }
}

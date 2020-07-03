package com.tuberlin.dbpro.services;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.springframework.test.util.AssertionErrors.assertTrue;

class LabelServiceTest {

    @Test
    public void testLabelMapping (){
        List<String> labels = new ArrayList<>();
        labels.add("Salines");
        labels.add("Agro-forestry areas");
        labels.add("");

        LabelService l = new LabelService();
        l.initializeLabelMap();
        List<Character> result = l.getPassedLabelsEncoded(labels);

        assertTrue("First label incorrectly mapped!", result.get(0).equals('A'));
        assertTrue("Second label incorrectly mapped!", result.get(1).equals('c'));
        assertTrue("Empty label incorrectly mapped!", result.size() == 2);
    }
}

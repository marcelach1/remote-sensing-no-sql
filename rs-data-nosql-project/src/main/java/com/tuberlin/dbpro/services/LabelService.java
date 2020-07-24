package com.tuberlin.dbpro.services;

import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;
import java.util.*;

/**
 * Label service responsible for mapping long string label lists to compact char label lists to save performance.
 */
@Service
public class LabelService {

    /**
     * Map containing the target character for every possible BigEarthNet label.
     */
    private Map<String, Character> labelMap = new TreeMap<>();
    private Map<Character, String> labelDecodeMap = new TreeMap<>();

    /**
     * Returns all supported labels in original string representation.
     * @return list of supported string labels
     */
    public List<String> getLabels() {
        return new ArrayList<String>(this.labelMap.keySet());
    }

    public String getDecodedCharacter(Character ch) { return labelDecodeMap.get(ch);}

    /**
     * Returns the requested labels mapped to their character representation.
     * @param labels requested original string labels
     * @return requested character labels
     */
    List<Character> getPassedLabelsEncoded(List<String> labels) {
        // remove duplicates and only return sorted compressed mapping
        Set<String> requestedLabels = new HashSet<>(labels);
        ArrayList<String> requestedLabelsWithoutDuplicates = new ArrayList<>(requestedLabels);
        Collections.sort(requestedLabelsWithoutDuplicates);
        List<Character> resultList = new ArrayList<>();

        for(String label: requestedLabelsWithoutDuplicates) {
            resultList.add(this.labelMap.get(label));
        }
        resultList.removeIf(Objects::isNull);

        return resultList;
    }




    /**
     * Initializes the label map so labels read from disk (or requested by frontend) can be mapped to a single character
     * before persisting (and before querying).
     */
    @PostConstruct
    void initializeLabelMap() {
        String[] POSSIBLE_LABELS = {
                "Agro-forestry areas", //A
                "Airports", //B
                "Annual crops associated with permanent crops", //C
                "Bare rock", //D
                "Beaches, dunes, sands", //...
                "Broad-leaved forest",
                "Burnt areas",
                "Coastal lagoons",
                "Complex cultivation patterns",
                "Coniferous forest",
                "Construction sites",
                "Continuous urban fabric",
                "Discontinuous urban fabric",
                "Dump sites",
                "Estuaries",
                "Fruit trees and berry plantations",
                "Glaciers and perpetual snow",
                "Green urban areas",
                "Industrial or commercial units",
                "Inland marshes",
                "Intertidal flats",
                "Land principally occupied by agriculture, with significant areas of natural vegetation",
                "Mineral extraction sites",
                "Mixed forest",
                "Moors and heathland",
                "Natural grassland",
                "Non-irrigated arable land",
                "Olive groves",
                "Pastures",
                "Peatbogs",
                "Permanently irrigated land",
                "Port areas",
                "Rice fields",
                "Road and rail networks and associated land",
                "Salines",
                "Salt marshes",
                "Sclerophyllous vegetation",
                "Sea and ocean",
                "Sparsely vegetated areas",
                "Sport and leisure facilities",
                "Transitional woodland/shrub",
                "Vineyards",
                "Water bodies",
                "Water courses" //l
        };

        // map each label to ASCII-conform character
        for(int i = 0; i < POSSIBLE_LABELS.length; i++) {
            labelMap.put(POSSIBLE_LABELS[i], (char) (65 + i));
            labelDecodeMap.put((char) (65 + i),POSSIBLE_LABELS[i]);
        }
    }
}

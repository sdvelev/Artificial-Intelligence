package bg.sofia.uni.fmi.ai.ml;

import java.lang.reflect.ParameterizedType;

public record Patient(String recurrenceEvents,
                      String age,
                      String menopause,
                      String tumorSize,
                      String invNodes,
                      String nodeCaps,
                      String degMalig,
                      String breast,
                      String breastQuad,
                      String irradiat) {
    private final static int RECURRENCE_EVENTS_POSITION = 0;
    private static final int AGE_POSITION = 1;
    private static final int MENOPAUSE_POSITION = 2;
    private static final int TUMOR_SIZE_POSITION = 3;
    private static final int INV_NODES_POSITION = 4;
    private static final int NODE_CAPS_POSITION = 5;
    private static final int DEG_MALIG_POSITION = 6;
    private static final int BREAST_POSITION = 7;
    private static final int BREAST_QUAD_POSITION = 8;
    private static final int IRRADIAT_POSITION = 9;

    private static final String CONTENT_ATTRIBUTE_DELIMITER = ",";
    private static final String EMPTY_STRING = "";

    public String getNumberedFeature(int numberOfFeature) {
        return switch (numberOfFeature) {
            case RECURRENCE_EVENTS_POSITION -> this.recurrenceEvents;
            case AGE_POSITION -> this.age;
            case MENOPAUSE_POSITION -> this.menopause;
            case TUMOR_SIZE_POSITION -> this.tumorSize;
            case INV_NODES_POSITION -> this.invNodes;
            case NODE_CAPS_POSITION -> this.nodeCaps;
            case DEG_MALIG_POSITION -> this.degMalig;
            case BREAST_POSITION -> this.breast;
            case BREAST_QUAD_POSITION -> this.breastQuad;
            case IRRADIAT_POSITION -> this.irradiat;
            default -> EMPTY_STRING;
        };
    }

    public static Patient of(String line) {
        String[] fields = line.split(CONTENT_ATTRIBUTE_DELIMITER);

        String recurrenceEvents = fields[RECURRENCE_EVENTS_POSITION];
        String age = fields[AGE_POSITION];
        String menopause = fields[MENOPAUSE_POSITION];
        String tumorSize = fields[TUMOR_SIZE_POSITION];
        String invNodes = fields[INV_NODES_POSITION];
        String nodeCaps = fields[NODE_CAPS_POSITION];
        String degMalig = fields[DEG_MALIG_POSITION];
        String breast = fields[BREAST_POSITION];
        String breastQuad = fields[BREAST_QUAD_POSITION];
        String irradiat = fields[IRRADIAT_POSITION];

        return new Patient(recurrenceEvents, age, menopause, tumorSize, invNodes, nodeCaps, degMalig, breast,
            breastQuad, irradiat);
    }
}

package bg.sofia.uni.fmi.ai.ml;

public record Voter(String party,
                    short handicappedInfants,
                    short waterProjectCostSharing,
                    short adoptionOfTheBudgetResolution,
                    short physicianFeeFreeze,
                    short elSalvadorAid,
                    short religiousGroupsInSchools,
                    short antiSatelliteTestBan,
                    short aidToNicaraguanContras,
                    short mxMissile,
                    short immigration,
                    short synfuelsCorporationCutback,
                    short educationSpending,
                    short superfundRightToSue,
                    short crime,
                    short dutyFreeExports,
                    short exportAdministrationActSouthAfrica) {

    private final static int PARTY_POSITION = 0;
    private static final int HANDICAPPED_INFANTS_POSITION = 1;
    private static final int WATER_PROJECT_COST_SHARING_POSITION = 2;
    private static final int ADOPTION_OF_THE_BUDGET_RESOLUTION_POSITION = 3;
    private static final int PHYSICIAN_FEE_FREEZE_POSITION = 4;
    private static final int EL_SALVADOR_AID_POSITION = 5;
    private static final int RELIGIOUS_GROUPS_IN_SCHOOLS_POSITION = 6;
    private static final int ANTI_SATELLITE_TEST_BAN_POSITION = 7;
    private static final int AID_TO_NICARAGUAN_CONTRAS_POSITION = 8;
    private static final int MX_MISSILE_POSITION = 9;
    private static final int IMMIGRATION_POSITION = 10;
    private static final int SYNFUELS_CORPORATION_CUTBACK_POSITION = 11;
    private static final int EDUCATION_SPENDING_POSITION = 12;
    private static final int SUPERFUND_RIGHT_TO_SUE_POSITION = 13;
    private static final int CRIME_POSITION = 14;
    private static final int DUTY_FREE_EXPORTS_POSITION = 15;
    private static final int EXPORT_ADMINISTRATION_ACT_SOUTH_AFRICA_POSITION = 16;

    private static final String CONTENT_ATTRIBUTE_DELIMITER = ",";

    private static final short YES_ANSWER_NUMBER = 2;
    private static final short UNKNOWN_ANSWER_NUMBER = 1;
    private static final short NO_ANSWER_NUMBER = 0;

    private static final String YES_ANSWER_STRING = "y";
    private static final String UNKNOWN_ANSWER_STRING = "?";
    private static final String NO_ANSWER_STRING = "n";


    private static short getAnswerNumberFromString(String answerString) {
        if (answerString.equalsIgnoreCase(YES_ANSWER_STRING)) {
            return YES_ANSWER_NUMBER;
        } else if (answerString.equalsIgnoreCase(NO_ANSWER_STRING)) {
            return NO_ANSWER_NUMBER;
        } else if (answerString.equalsIgnoreCase(UNKNOWN_ANSWER_STRING)) {
            return UNKNOWN_ANSWER_NUMBER;
        }

        return UNKNOWN_ANSWER_NUMBER;
    }

    public short getNumberedFeature(short numberOfFeature) {
        return switch (numberOfFeature) {
            case HANDICAPPED_INFANTS_POSITION -> this.handicappedInfants;
            case WATER_PROJECT_COST_SHARING_POSITION -> this.waterProjectCostSharing;
            case ADOPTION_OF_THE_BUDGET_RESOLUTION_POSITION -> this.adoptionOfTheBudgetResolution;
            case PHYSICIAN_FEE_FREEZE_POSITION -> this.physicianFeeFreeze;
            case EL_SALVADOR_AID_POSITION -> this.elSalvadorAid;
            case RELIGIOUS_GROUPS_IN_SCHOOLS_POSITION -> this.religiousGroupsInSchools;
            case ANTI_SATELLITE_TEST_BAN_POSITION -> this.antiSatelliteTestBan;
            case AID_TO_NICARAGUAN_CONTRAS_POSITION -> this.aidToNicaraguanContras;
            case MX_MISSILE_POSITION -> this.mxMissile;
            case IMMIGRATION_POSITION -> this.immigration;
            case SYNFUELS_CORPORATION_CUTBACK_POSITION -> this.synfuelsCorporationCutback;
            case EDUCATION_SPENDING_POSITION -> this.educationSpending;
            case SUPERFUND_RIGHT_TO_SUE_POSITION -> this.superfundRightToSue;
            case CRIME_POSITION -> this.crime;
            case DUTY_FREE_EXPORTS_POSITION -> this.dutyFreeExports;
            case EXPORT_ADMINISTRATION_ACT_SOUTH_AFRICA_POSITION -> this.exportAdministrationActSouthAfrica;
            default -> 0;
        };
    }

    public static Voter of(String line) {
        String[] fields = line.split(CONTENT_ATTRIBUTE_DELIMITER);

        String party = fields[PARTY_POSITION];
        short handicappedInfants = getAnswerNumberFromString(fields[HANDICAPPED_INFANTS_POSITION]);
        short waterProjectCostSharing = getAnswerNumberFromString(fields[WATER_PROJECT_COST_SHARING_POSITION]);
        short adoptionOfTheBudgetResolution = getAnswerNumberFromString(fields[ADOPTION_OF_THE_BUDGET_RESOLUTION_POSITION]);
        short physicianFeeFreeze = getAnswerNumberFromString(fields[PHYSICIAN_FEE_FREEZE_POSITION]);
        short elSalvadorAid = getAnswerNumberFromString(fields[EL_SALVADOR_AID_POSITION]);
        short religiousGroupsInSchools = getAnswerNumberFromString(fields[RELIGIOUS_GROUPS_IN_SCHOOLS_POSITION]);
        short antiSatelliteTestBan = getAnswerNumberFromString(fields[ANTI_SATELLITE_TEST_BAN_POSITION]);
        short aidToNicaraguanContras = getAnswerNumberFromString(fields[AID_TO_NICARAGUAN_CONTRAS_POSITION]);
        short mxMissile = getAnswerNumberFromString(fields[MX_MISSILE_POSITION]);
        short immigration = getAnswerNumberFromString(fields[IMMIGRATION_POSITION]);
        short synfuelsCorporationCutback = getAnswerNumberFromString(fields[SYNFUELS_CORPORATION_CUTBACK_POSITION]);
        short educationSpending = getAnswerNumberFromString(fields[EDUCATION_SPENDING_POSITION]);
        short superfundRightToSue = getAnswerNumberFromString(fields[SUPERFUND_RIGHT_TO_SUE_POSITION]);
        short crime = getAnswerNumberFromString(fields[CRIME_POSITION]);
        short dutyFreeExports = getAnswerNumberFromString(fields[DUTY_FREE_EXPORTS_POSITION]);
        short exportAdministrationActSouthAfrica = getAnswerNumberFromString(fields[EXPORT_ADMINISTRATION_ACT_SOUTH_AFRICA_POSITION]);

        return new Voter(party, handicappedInfants, waterProjectCostSharing, adoptionOfTheBudgetResolution,
            physicianFeeFreeze, elSalvadorAid, religiousGroupsInSchools, antiSatelliteTestBan, aidToNicaraguanContras,
            mxMissile, immigration, synfuelsCorporationCutback, educationSpending, superfundRightToSue, crime,
            dutyFreeExports, exportAdministrationActSouthAfrica);
    }

}

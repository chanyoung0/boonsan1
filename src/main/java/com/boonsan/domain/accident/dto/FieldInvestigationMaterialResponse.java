package com.boonsan.domain.accident.dto;

public class FieldInvestigationMaterialResponse {

    private final String accidentScenePhotoName;
    private final String blackBoxVideoName;
    private final String repairEstimateFileName;

    public FieldInvestigationMaterialResponse(
            String accidentScenePhotoName,
            String blackBoxVideoName,
            String repairEstimateFileName
    ) {
        this.accidentScenePhotoName = accidentScenePhotoName;
        this.blackBoxVideoName = blackBoxVideoName;
        this.repairEstimateFileName = repairEstimateFileName;
    }

    public String getAccidentScenePhotoName() { return accidentScenePhotoName; }

    public String getBlackBoxVideoName() { return blackBoxVideoName; }

    public String getRepairEstimateFileName() { return repairEstimateFileName; }
}

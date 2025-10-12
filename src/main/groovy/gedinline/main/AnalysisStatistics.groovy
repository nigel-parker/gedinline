package gedinline.main

import groovy.transform.*

@TupleConstructor
class AnalysisStatistics {

    Date timestamp
    String filename
    String gedcomVersion
    String generatedBy
    String generatedByVersion
    String generatedDate
    Integer numberOfLines
    Integer numberOfRecords
    Integer warningsPer10kLines
    String analysisTime
    String speed
    Integer warnings
    Integer linesPerSecond

    AnalysisStatistics(Date timestamp, String filename, String gedcomVersion, String generatedBy, String generatedByVersion, String generatedDate, Integer numberOfLines, Integer numberOfRecords, Integer warningsPer10kLines, String analysisTime, String speed, Integer warnings, Integer linesPerSecond) {
        this.timestamp = timestamp
        this.filename = filename
        this.gedcomVersion = gedcomVersion
        this.generatedBy = generatedBy
        this.generatedByVersion = generatedByVersion
        this.generatedDate = generatedDate
        this.numberOfLines = numberOfLines
        this.numberOfRecords = numberOfRecords
        this.warningsPer10kLines = warningsPer10kLines
        this.analysisTime = analysisTime
        this.speed = speed
        this.warnings = warnings
        this.linesPerSecond = linesPerSecond
    }
}

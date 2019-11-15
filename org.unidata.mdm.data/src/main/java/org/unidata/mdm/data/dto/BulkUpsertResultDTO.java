package org.unidata.mdm.data.dto;

import java.util.List;

/**
 * @author Dmitry Kopin on 14.02.2019.
 */
public class BulkUpsertResultDTO {
    private List<UpsertRecordDTO> records;

    private List<UpsertRelationsDTO> relations;

//    private List<UpsertClassifiersDTO> classifiers;// TODO: @Modules

    private List<DeleteRelationsDTO> deleteRelations;

//    private List<DeleteClassifiersDTO> deleteClassifiers;// TODO: @Modules

    public List<UpsertRecordDTO> getRecords() {
        return records;
    }

    public void setRecords(List<UpsertRecordDTO> records) {
        this.records = records;
    }

    public List<UpsertRelationsDTO> getRelations() {
        return relations;
    }

    public void setRelations(List<UpsertRelationsDTO> relations) {
        this.relations = relations;
    }

    // TODO: @Modules
//    public List<UpsertClassifiersDTO> getClassifiers() {
//        return classifiers;
//    }
//
//    public void setClassifiers(List<UpsertClassifiersDTO> classifiers) {
//        this.classifiers = classifiers;
//    }

    public List<DeleteRelationsDTO> getDeleteRelations() {
        return deleteRelations;
    }

    public void setDeleteRelations(List<DeleteRelationsDTO> deleteRelations) {
        this.deleteRelations = deleteRelations;
    }

    // TODO: @Modules
//    public List<DeleteClassifiersDTO> getDeleteClassifiers() {
//        return deleteClassifiers;
//    }
//
//    public void setDeleteClassifiers(List<DeleteClassifiersDTO> deleteClassifiers) {
//        this.deleteClassifiers = deleteClassifiers;
//    }
}

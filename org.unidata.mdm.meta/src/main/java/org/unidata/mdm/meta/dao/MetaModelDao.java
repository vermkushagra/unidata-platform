package org.unidata.mdm.meta.dao;

import java.util.List;

import org.unidata.mdm.meta.po.MetaModelPO;
import org.unidata.mdm.meta.po.MetaStoragePO;
import org.unidata.mdm.meta.type.ModelType;

/**
 * @author Michael Yashin. Created on 26.05.2015.
 */
public interface MetaModelDao {
    /**
     * Finds all storage IDs.
     */
    public List<MetaStoragePO> findStorageRecords();

    /**
     * Gets an object by type and ID.
     * @param storageId storage id
     * @param type type
     * @param id id
     * @return object or null
     */
    public MetaModelPO findRecordByTypeAndId(String storageId, ModelType type, String id);

    /**
     * Gets meta data objects by type.
     * @param storageId the storage id
     * @param type type
     * @return list of objects or null
     */
    public List<MetaModelPO> findRecordsByType(String storageId, ModelType type);

    /**
     * Updates or inserts a meta model object.
     * @param storageId storage id
     * @param record the record
     */
    public void upsertRecord(String storageId, MetaModelPO record);

    /**
     * Updates or inserts a meta model objects list.
     * @param storageId storage id
     * @param records the records
     */
    public void upsertRecords(String storageId, List<MetaModelPO> records);

    /**
     * Deletes entire model by storage id.
     * @param storageId the storage id
     * @return true, if some records were deleted
     */
    public boolean deleteModel(String storageId);

    /**
     * Deletes a record of a given type and id.
     * @param storageId the storage id
     * @param type the type
     * @param id the id
     */
    public void deleteRecord(String storageId, ModelType type, String id);

    /**
     * Deletes several records of a given type and ids.
     * @param storageId the storage id
     * @param type the type
     * @param id the id
     */
    public void deleteRecords(String storageId, ModelType type, List<String> ids);
}

package com.unidata.mdm.backend.service.settings.impl;

import com.unidata.mdm.backend.common.dto.CustomStorageRecordDTO;
import com.unidata.mdm.backend.dao.CustomStorageDao;
import com.unidata.mdm.backend.po.CustomStorageRecordPO;
import com.unidata.mdm.backend.service.settings.CustomStorageService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Dmitry Kopin on 28.08.2017.
 */
@Service
public class CustomStorageServiceImpl implements CustomStorageService {

    @Autowired
    private CustomStorageDao customStorageDao;

    @Override
    public boolean createRecords(List<CustomStorageRecordDTO> customStorageRecords){
        if(CollectionUtils.isEmpty(customStorageRecords)){
            return false;
        }
        customStorageDao.createRecords(convertToPO(customStorageRecords));
        return true;
    }

    @Override
    public boolean updateRecords(List<CustomStorageRecordDTO> customStorageRecords){
        if(CollectionUtils.isEmpty(customStorageRecords)){
            return false;
        }
        customStorageDao.updateRecords(convertToPO(customStorageRecords));
        return true;
    }

    @Override
    public boolean deleteRecords(List<CustomStorageRecordDTO> customStorageRecords){
        if(CollectionUtils.isEmpty(customStorageRecords)){
            return false;
        }
        customStorageDao.deleteRecords(convertToPO(customStorageRecords));
        return true;
    }

    @Override
    public boolean deleteRecordsByUserName(String userName){
        if(StringUtils.isEmpty(userName)){
            return false;
        }
        customStorageDao.deleteRecordsByUserName(userName);
        return true;
    }

    @Override
    public boolean deleteRecordsByKey(String key){
        if(StringUtils.isEmpty(key)){
            return false;
        }
        customStorageDao.deleteRecordsByKey(key);
        return true;
    }

    @Override
    public List<CustomStorageRecordDTO> getRecordsByUserName(String userName){
        return convertToDTO(customStorageDao.getRecordsByUserName(userName));
    }

    @Override
    public List<CustomStorageRecordDTO> getRecordsByKey(String userName){
        return convertToDTO(customStorageDao.getRecordsByKey(userName));
    }

    @Override
    public List<CustomStorageRecordDTO> getRecordsByKeyAndUser(String key, String userName){
        return convertToDTO(customStorageDao.getRecordsByKeyAndUser(key, userName));
    }


    private List<CustomStorageRecordPO> convertToPO(List<CustomStorageRecordDTO> source){
        if(source == null){
            return Collections.emptyList();
        }
        return source.stream()
                .map(this::convertToPO)
                .collect(Collectors.toList());
    }

    private List<CustomStorageRecordDTO> convertToDTO(List<CustomStorageRecordPO> source){
        if(source == null){
            return Collections.emptyList();
        }
        return source.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private CustomStorageRecordPO convertToPO(CustomStorageRecordDTO source){
        CustomStorageRecordPO target = new CustomStorageRecordPO();
        target.setKey(source.getKey());
        target.setUser(source.getUser());
        target.setValue(source.getValue());
        target.setUpdateDate(source.getUpdateDate());
        return target;
    }

    private CustomStorageRecordDTO convertToDTO(CustomStorageRecordPO source){
        CustomStorageRecordDTO target = new CustomStorageRecordDTO();
        target.setKey(source.getKey());
        target.setUser(source.getUser());
        target.setValue(source.getValue());
        target.setUpdateDate(source.getUpdateDate());
        return target;
    }
}

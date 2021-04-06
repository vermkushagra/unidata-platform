package com.unidata.mdm.cleanse.postaladdress;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.istack.Nullable;
import com.unidata.mdm.backend.common.exception.CleanseFunctionExecutionException;
import com.unidata.mdm.backend.common.types.SimpleAttribute;
import com.unidata.mdm.backend.common.types.impl.BooleanSimpleAttributeImpl;
import com.unidata.mdm.backend.common.types.impl.IntegerSimpleAttributeImpl;
import com.unidata.mdm.backend.common.types.impl.NumberSimpleAttributeImpl;
import com.unidata.mdm.backend.common.types.impl.StringSimpleAttributeImpl;
import com.unidata.mdm.backend.common.types.impl.TimestampSimpleAttributeImpl;
import com.unidata.mdm.backend.service.cleanse.CFAppContext;
import com.unidata.mdm.cleanse.common.BasicCleanseFunctionAbstract;
import com.unidata.mdm.cleanse.postaladdress.addressmaster.AddressmasterClient;
import com.unidata.mdm.cleanse.postaladdress.addressmaster.dto.Address;
import com.unidata.mdm.cleanse.postaladdress.addressmaster.dto.AddressInfo;
import com.unidata.mdm.cleanse.postaladdress.addressmaster.dto.AddressList;

/**
 * Cleans function utilize http://addressmaster.ru/index.html service to verify and clean address data information.
 *
 * In first simple implementation (Итерация 0):
 *
 * Cleanse function input:
 * - адресная строчка
 *
 * Cleanse function output:
 * - validation status - boolean
 * - error text if present
 * - match score
 * - match status
 * - zip
 * - FIAS ID нижнего эдемента адресной иерархия
 * - FIAS ID дома
 * - полная адресная строка
 * - текстовое основной части местоположения (регион, район, область, город, улица...).
 * - дом
 * - корпус
 * - структурная часть дома (литеры, подобъекты, дроби)
 *
 * For installation required to be added in configuration:
 *  <cleanseFunction functionName="ПроверкаАдреса" javaClass="com.unidata.mdm.cleanse.postaladdress.CFFiasCheck"/>
 *
 * @author Pavel Alexeev
 */
public class CFFiasCheck extends BasicCleanseFunctionAbstract {

    AddressmasterClient addressmasterClient;

    public CFFiasCheck() throws Exception {
        super(CFFiasCheck.class);
        // It can't be autowired by Spring because called manually via wrapper
        addressmasterClient = CFAppContext.getBean(AddressmasterClient.class);
    }

    /**
     * Create SimpleAttribute for provided output port value using appropriate type and name.
     *
     * @param name Name of output port
     * @param value Value to set. Converted by {@link #toBaseValue}
     * @return result SimpleAttribute
     * @throws CleanseFunctionExecutionException
     */
    SimpleAttribute<?> outputPortValue(String name, Object value) throws CleanseFunctionExecutionException {

        getDefinition().getOutputPorts()
            .stream()
            .filter(p ->
                p.getName().equals(name)
            )
            .findAny()
            .orElseThrow(()-> new CleanseFunctionExecutionException(this.getDefinition().getFunctionName(), "Can't find [" + name + "] in output ports!"));

        return objectToAttributeValue(name, value);
    }

    private void addResult(Map<String, Object> result, String name, Object value) throws CleanseFunctionExecutionException {
        SimpleAttribute<?> attr = outputPortValue(name, value);
        if (Objects.nonNull(attr)) {
            result.put(name, attr);
        }
    }

    /**
     * Automatically create BaseValue from most known object types.
     *
     * Copied from EgaisObjectService unidataRLH branch.
     * I'd like have it in some global util space really.
     *
     * @param from object
     * @return resulting simple attribute
     */
    private static SimpleAttribute<?> objectToAttributeValue(String name, @Nullable Object from){

        if (null == from) {
            return null;
        }

        if (from instanceof Integer) {
            return new IntegerSimpleAttributeImpl(name, ((Integer) from).longValue());
        } else if (from instanceof Long) {
            return new IntegerSimpleAttributeImpl(name, (long) from);
        } else if (from instanceof Double) {
            return new NumberSimpleAttributeImpl(name, (double) from);
        } else if (from instanceof Date) {
            return new TimestampSimpleAttributeImpl(name, LocalDateTime.ofInstant(((Date) from).toInstant(), ZoneId.systemDefault()));
        } else if (from instanceof String) {
            return new StringSimpleAttributeImpl(name, (String) from);
        } else if (from instanceof Boolean) {
            return new BooleanSimpleAttributeImpl(name, (Boolean) from);
        }

        return null;
    }

    @Override
    public void execute(Map<String, Object> input, Map<String, Object> result) throws CleanseFunctionExecutionException {
        if ((String)getValueByPort("inputAddress", input) == null
                || ((String) getValueByPort("inputAddress", input)).isEmpty()) {
            addResult(result, "validationPass", FALSE);
            addResult(result, "errorText", "Address is empty.");
            return;
        }
        AddressList addressList = addressmasterClient.addressMatch((String)getValueByPort("inputAddress", input));
        if (!(addressList.getAddressList().size() > 0
                && (addressList.getAddressList().get(0).getMATCH_STATUS() == Address.MatchStatus.GOOD
        || addressList.getAddressList().get(0).getMATCH_STATUS() == Address.MatchStatus.PERFECT
        || addressList.getAddressList().get(0).getMATCH_STATUS() == Address.MatchStatus.ACCEPTED))){
            addResult(result, "validationPass", FALSE);
            try {
				addResult(result, "errorText", "Error check and clean address. No results in GOOD state. Got result: " + new ObjectMapper().writeValueAsString(addressList));
			} catch (JsonProcessingException e) {
				throw new CleanseFunctionExecutionException(this.getDefinition().getFunctionName(), e);
			}
        }
        else{
            addResult(result, "validationPass", TRUE);
        }

        if (addressList.getAddressList() == null || addressList.getAddressList().isEmpty()) {
            return;
        }
        Address address = addressList.getAddressList().get(0); // get first

        addResult(result, "matchScore", address.getDEBUG_INFO().getMatchScore());
        addResult(result, "matchStatus", address.getDEBUG_INFO().getMatchStatus().name());
        addResult(result, "zipcode", address.getZIPCODE());
        addResult(result, "normalizedAddress", address.getFullAddressString());
        addResult(result, "intermediateAddressString", address.getIntermediateAddressString());
        addResult(result, "houseNumberString", address.getHouseNumberString());
        addResult(result, "buildingNumberString", address.getBuildingNumberString());
        addResult(result, "buildingStructureString", address.getBuildingStructureString());

        AddressInfo maxLevelAddressInfo = null != address.getADDRESS_INFO() ? address.getADDRESS_INFO().stream().max((a1, a2) -> Integer.compare(a1.getLevel(), a2.getLevel())).orElseGet(null) : null;
        if (null != maxLevelAddressInfo){
            addResult(result, "maxMatchedLevel", maxLevelAddressInfo.getLevel());
            addResult(result, "maxMatchedLevelGUID", maxLevelAddressInfo.getAoGuid().toString());
        }

        addResult(result, "buildingGUID", null != address.getHOUSE_INFO() ? address.getHOUSE_INFO().getHOUSE_GUID().toString() : null);
    }
}

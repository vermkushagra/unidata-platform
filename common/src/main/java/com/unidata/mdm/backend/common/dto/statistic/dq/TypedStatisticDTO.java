package com.unidata.mdm.backend.common.dto.statistic.dq;

import java.util.List;

import com.unidata.mdm.backend.common.statistic.StatisticType;

/**
 * @author Alexey Tsarapkin
 */
public class TypedStatisticDTO {

  private final String type;
  private List<StatisticInfoDTO> data;

  public TypedStatisticDTO(String type){
    this.type = type;
  }

  public String getType() {
    return type;
  }

  public List<StatisticInfoDTO> getData() {
    return data;
  }

  public void setData(List<StatisticInfoDTO> data) {
    this.data = data;
  }

}

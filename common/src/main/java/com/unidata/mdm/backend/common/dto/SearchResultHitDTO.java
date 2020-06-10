package com.unidata.mdm.backend.common.dto;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import com.unidata.mdm.backend.common.ReportInfoHolder;

/**
 * @author Mikhail Mikhailov
 * DTO, implementing a search hit.
 */
public class SearchResultHitDTO implements ReportInfoHolder {

    /**
     * Object id. The object identifier, supplied by the DB during create.
     */
    private final String id;
    /**
     * Internal identifier from search system;
     */
    private final String internalId;

    /**
     * Preview map, containing search keys alone with their values as strings.
     */
    private final Map<String, SearchResultHitFieldDTO> preview;

    /**
     * JSON object at whole as raw value.
     */
    private final Object source;

    private final Float score;

    private Map<String, List<SearchResultHitDTO>> innerHits;

    /**
     * Constructor.
     */
    public SearchResultHitDTO(String id, String internalId, Float score, Map<String, SearchResultHitFieldDTO>  preview, Object source) {
        super();
        this.id = id;
        this.internalId = internalId;
        this.score = score;
        this.preview = preview;
        this.source = source;
    }

    /**
     * Gets the object, found by search.
     * @return the source
     */
    public Object getSource() {
        return source;
    }

    /**
     * Gets the object's id.
     * @return the id
     */
    public String getId() {
        return id;
    }


    /**
     * Internal identifier from search system;
     */
    public String getInternalId() {
        return internalId;
    }

    /**
     * Gets the preview as key = value pairs.
     * @return the preview
     */
    public Map<String, SearchResultHitFieldDTO> getPreview() {
        return preview;
    }

    public Float getScore() {
        return score;
    }

    /**
     * TODO rename to getField.
     * @param field field name
     * @return value of search result hit with field name
     */
    @Nullable
    public SearchResultHitFieldDTO getFieldValue(String field) {
        if (preview == null || field == null) {
            return null;
        }

        return preview.get(field);
    }

    /**
    *
    * @param field field name
    * @return value of search result hit with field name
    */
   @SuppressWarnings("unchecked")
   @Nullable
   public<T> T getFieldFirstValue(String field) {
       if (preview == null || field == null) {
           return null;
       }

       SearchResultHitFieldDTO hitField = preview.get(field);
       return (T) (hitField == null ? null : hitField.getFirstValue());
   }

   /**
   *
   * @param field field name
   * @return value of search result hit with field name
   */
  @Nullable
  public List<Object> getFieldValues(String field) {
      if (preview == null || field == null) {
          return null;
      }

      SearchResultHitFieldDTO hitField = preview.get(field);
      return hitField == null ? null : hitField.getValues();
  }

    public Map<String, List<SearchResultHitDTO>> getInnerHits() {
        return innerHits == null ? Collections.emptyMap() : innerHits;
    }

    public void addInnerHit(String hitName, List<SearchResultHitDTO> hits) {
        if(innerHits == null){
            innerHits = new HashMap<>();
        }
        innerHits.put(hitName, hits);
    }
}
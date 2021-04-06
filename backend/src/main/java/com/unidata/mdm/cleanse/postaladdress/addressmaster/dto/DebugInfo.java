package com.unidata.mdm.cleanse.postaladdress.addressmaster.dto;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Pavel Alexeev.
 * @created 2016-01-19 12:05.
 */
public class DebugInfo {
    Address.MatchStatus matchStatus;
    Long matchScore;
    Float luceneScore;
    Integer aggrDist;
    String matchType;
    // Probably it also may be split
    String matchedTokens;
    Integer highestMatchTokenLevel;

    /**
    * For some reason from service come string like: "[matchStatus=GOOD;matchScore=42;luceneScore=0.986399;aggrDist=0;matchType=FUZZY;matchedTokens= [средний;средний;AddressElement{aoId='1146060', level=7, formalName='Средний В.О.', elementTypeName='проспект'};0]; [санкт-петербург;санкт-петербург;AddressElement{aoId='1143837', level=1, formalName='Санкт-Петербург', elementTypeName='город'};0]; highestMatchTokenLevel=1;]"
    * try manual parse
    */
    public DebugInfo(String rawStr){
        Pattern p = Pattern.compile("\\[matchStatus=(?<matchStatus>\\w+);matchScore=(?<matchScore>[-\\d]+);luceneScore=(?<luceneScore>[-\\.\\d]+);aggrDist=(?<aggrDist>[-\\.\\d]+);matchType=(?<matchType>\\w+);matchedTokens=(?<matchedTokens>.+?)highestMatchTokenLevel=(?<highestMatchTokenLevel>[-\\.\\d]+);\\]", Pattern.DOTALL);
        Matcher m = p.matcher(rawStr);

            if (true == m.matches()) {
                matchStatus = Address.MatchStatus.valueOf(m.group("matchStatus"));
                matchScore = Long.valueOf(m.group("matchScore"));
                luceneScore = Float.valueOf(m.group("luceneScore"));
                aggrDist = Integer.valueOf(m.group("aggrDist"));
                matchType = m.group("matchType");
                matchedTokens = m.group("matchedTokens");
                highestMatchTokenLevel = Integer.valueOf(m.group("highestMatchTokenLevel"));
            }
            else{
                throw new IllegalArgumentException("Can't parse incoming DEBUG_INFO string <" + rawStr + "> into DebugInfo object!");
            }
    }

    public Address.MatchStatus getMatchStatus() {
        return matchStatus;
    }

    public void setMatchStatus(Address.MatchStatus matchStatus) {
        this.matchStatus = matchStatus;
    }

    public Long getMatchScore() {
        return matchScore;
    }

    public void setMatchScore(Long matchScore) {
        this.matchScore = matchScore;
    }

    public float getLuceneScore() {
        return luceneScore;
    }

    public void setLuceneScore(float luceneScore) {
        this.luceneScore = luceneScore;
    }

    public int getAggrDist() {
        return aggrDist;
    }

    public void setAggrDist(int aggrDist) {
        this.aggrDist = aggrDist;
    }

    public String getMatchType() {
        return matchType;
    }

    public void setMatchType(String matchType) {
        this.matchType = matchType;
    }

    public String getMatchedTokens() {
        return matchedTokens;
    }

    public void setMatchedTokens(String matchedTokens) {
        this.matchedTokens = matchedTokens;
    }

    public int getHighestMatchTokenLevel() {
        return highestMatchTokenLevel;
    }

    public void setHighestMatchTokenLevel(int highestMatchTokenLevel) {
        this.highestMatchTokenLevel = highestMatchTokenLevel;
    }
}

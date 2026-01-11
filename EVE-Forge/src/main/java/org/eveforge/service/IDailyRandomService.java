package org.eveforge.service;





public interface IDailyRandomService {

    String getDailyLength(String userId);

    Integer compareDailyLength(String userId1, String userId2);

    String getDailyLuck(String userId);

    String getDailyLuckChanges(String userId);

    String getCompareLengthDescription(Integer compare);
}

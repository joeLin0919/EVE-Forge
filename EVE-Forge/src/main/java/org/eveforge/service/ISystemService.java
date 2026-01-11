package org.eveforge.service;

import org.eveforge.repository.po.RegionObj;
import org.eveforge.repository.po.SolarSystemObj;

public interface ISystemService {
    void updateRegionInfo();
    RegionObj getRegionInfoById(Integer regionId);
    void updateSystemInfo();
    SolarSystemObj getSolarSystemInfoById(Integer systemId);
    void updateConstellationInfo();
}

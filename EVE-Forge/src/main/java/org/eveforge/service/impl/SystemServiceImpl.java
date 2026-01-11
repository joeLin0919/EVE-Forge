package org.eveforge.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.eveforge.mapper.ConstellationMapper;
import org.eveforge.mapper.RegionMapper;
import org.eveforge.mapper.SolarSystemMapper;
import org.eveforge.repository.dto.Constellation;
import org.eveforge.repository.dto.Region;
import org.eveforge.repository.dto.SolarSystem;
import org.eveforge.repository.po.ConstellationObj;
import org.eveforge.repository.po.RegionObj;
import org.eveforge.repository.po.SolarSystemObj;
import org.eveforge.service.ISystemService;
import org.eveforge.util.EsiClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
@Slf4j
public class SystemServiceImpl implements ISystemService {

    private final EsiClient esiClient;

    @Autowired
    private RegionMapper regionMapper;

    @Autowired
    private ConstellationMapper constellationMapper;

    @Autowired
    private SolarSystemMapper solarSystemMapper;

    @Autowired
    public SystemServiceImpl(EsiClient esiClient) {
        this.esiClient = esiClient;
    }

    @Override
    public void updateRegionInfo() {
        String idEndPoint = "/universe/regions";
        String infoEndPoint = "/universe/regions/{region_id}";
        log.info("开始更新星域信息");
        String idResponse = esiClient.getJson(idEndPoint);
        List<Integer> regionIds = Arrays.stream(idResponse.substring(1, idResponse.length() - 1).split(","))
                .map(s -> s.trim())
                .filter(s -> !s.isEmpty())
                .map(Integer::parseInt)
                .toList();
        // 记录本次更新的星域ID列表
        List<Integer> updatedIds = new ArrayList<>();
        for (Integer regionId : regionIds) {
            Integer regionCount = regionMapper.selectRegionCountById(regionId);
            if(regionCount != null && regionCount != 0){
                continue;
            }
            Map<String, Object> pathParam = new HashMap<>();
            pathParam.put("region_id", regionId.toString());
            Region region = esiClient.get(infoEndPoint, pathParam,null, Region.class);
            RegionObj regionObj = exchangeToRegionObj(region);
            try {
                regionMapper.insertRegionInfo(regionObj);
                updatedIds.add(regionId);
                log.info("成功更新星域信息: {} (ID: {})", regionObj.getName(), regionId);
            } catch (Exception e) {
                log.error("更新星域信息失败: {} (ID: {}), 错误: {}", regionObj.getName(), regionId, e.getMessage());
            }
        }
        log.info("本次更新的星域ID列表: {}", updatedIds);
        log.info("星域信息更新完成");
    }

    @Override
    public RegionObj getRegionInfoById(Integer regionId) {
        RegionObj regionObj = regionMapper.selectRegionInfoById(regionId);
        return regionObj == null ? null : regionObj;
    }

    @Override
    public void updateSystemInfo() {
        String idEndPoint = "/universe/systems";
        String infoEndPoint = "/universe/systems/{system_id}";
        log.info("开始更新星系信息");
        String idResponse = esiClient.getJson(idEndPoint);
        List<Integer> systemIds = Arrays.stream(idResponse.substring(1, idResponse.length() - 1).split(","))
                .map(s -> s.trim())
                .filter(s -> !s.isEmpty())
                .map(Integer::parseInt)
                .toList();
        // 记录本次更新的星系ID列表
        List<Integer> updatedIds = new ArrayList<>();
        for (Integer systemId : systemIds) {
            Integer systemCount = solarSystemMapper.selectSolarSystemCountById(systemId);
            if(systemCount != null && systemCount != 0){
                continue;
            }
            Map<String, Object> pathParam = new HashMap<>();
            pathParam.put("system_id", systemId.toString());
            SolarSystem solarSystem = esiClient.get(infoEndPoint, pathParam,null, SolarSystem.class);
            SolarSystemObj solarSystemObj = exchangeToSolarSystemObj(solarSystem);
            try {
                solarSystemMapper.insertSolarSystemInfo(solarSystemObj);
                updatedIds.add(systemId);
                log.info("成功更新星系信息: {} (ID: {})", solarSystemObj.getName(), systemId);
                Thread.sleep(300);
            } catch (Exception e) {
                log.error("更新星系信息失败: {} (ID: {}), 错误: {}", solarSystemObj.getName(), systemId, e.getMessage());
            }
        }
        log.info("本次更新的星系ID列表: {}", updatedIds);
        log.info("星系信息更新完成");
    }

    @Override
    public SolarSystemObj getSolarSystemInfoById(Integer systemId) {
        SolarSystemObj solarSystemObj = solarSystemMapper.selectSolarSystemInfoById(systemId);
        return null;
    }

    @Override
    public void updateConstellationInfo() {
        String idEndPoint = "/universe/constellations";
        String infoEndPoint = "/universe/constellations/{constellation_id}";
        log.info("开始更新星座信息");
        String idResponse = esiClient.getJson(idEndPoint);
        List<Integer> constellationIds = Arrays.stream(idResponse.substring(1, idResponse.length() - 1).split(","))
                .map(s -> s.trim())
                .filter(s -> !s.isEmpty())
                .map(Integer::parseInt)
                .toList();
        // 记录本次更新的星座ID列表
        List<Integer> updatedIds = new ArrayList<>();
        for (Integer constellationId : constellationIds) {
            Integer constellationCount = constellationMapper.selectConstellationCountById(constellationId);
            if(constellationCount != null && constellationCount != 0){
                continue;
            }
            Map<String, Object> pathParam = new HashMap<>();
            pathParam.put("constellation_id", constellationId.toString());
            Constellation constellation = esiClient.get(infoEndPoint, pathParam,null, Constellation.class);
            ConstellationObj constellationObj = exchangeToConstellationObj(constellation);
            try {
                constellationMapper.insertConstellationInfo(constellationObj);
                updatedIds.add(constellationId);
                log.info("成功更新星座信息: {} (ID: {})", constellationObj.getName(), constellationId);
            } catch (Exception e) {
                log.error("更新星座信息失败: {} (ID: {}), 错误: {}", constellationObj.getName(), constellationId, e.getMessage());
            }
        }
        log.info("本次更新的星座ID列表: {}", updatedIds);
        log.info("星座信息更新完成");
    }

    private RegionObj exchangeToRegionObj(Region region) {
        RegionObj regionObj = new RegionObj();

        if (region.getName() != null) {
            regionObj.setName(region.getName());
        }

        if (region.getDescription() != null) {
            regionObj.setDescription(region.getDescription());
        }

        if (region.getRegionId() != null) {
            regionObj.setRegionId(region.getRegionId());
        }

        return regionObj;
    }

    private ConstellationObj exchangeToConstellationObj(Constellation constellation) {
        ConstellationObj constellationObj = new ConstellationObj();
        if (constellation.getName() != null) {
            constellationObj.setName(constellation.getName());
        }
        if(constellation.getConstellationId()!=null){
            constellationObj.setConstellationId(constellation.getConstellationId());
        }
        if (constellation.getRegionId() != null){
            constellationObj.setRegionId(constellation.getRegionId());
        }
        if (constellation.getPosition() != null){
            constellationObj.setX_coordinate(constellation.getPosition().getX());
            constellationObj.setY_coordinate(constellation.getPosition().getY());
            constellationObj.setZ_coordinate(constellation.getPosition().getZ());
        }
        return constellationObj;
    }

    private SolarSystemObj exchangeToSolarSystemObj(SolarSystem solarSystem) {
        SolarSystemObj solarSystemObj = new SolarSystemObj();
        if(solarSystem.getSystemId() != null){
            solarSystemObj.setSystemId(solarSystem.getSystemId());
        }
        if (solarSystem.getName() != null) {
            solarSystemObj.setName(solarSystem.getName());
        }
        if (solarSystem.getSecurityStatus() != null) {
            solarSystemObj.setSecurityStatus(solarSystem.getSecurityStatus());
        }
        if (solarSystem.getSecurityClass() != null) {
            solarSystemObj.setSecurityClass(solarSystem.getSecurityClass());
        }
        if (solarSystem.getConstellationId() != null) {
            solarSystemObj.setConstellationId(solarSystem.getConstellationId());
        }
        if (solarSystem.getStarId() != null) {
            solarSystemObj.setStarId(solarSystem.getStarId());
        }
        if (solarSystem.getPosition() != null) {
            solarSystemObj.setX_coordinate(solarSystem.getPosition().getX());
            solarSystemObj.setY_coordinate(solarSystem.getPosition().getY());
            solarSystemObj.setZ_coordinate(solarSystem.getPosition().getZ());
        }
        return solarSystemObj;
    }
}

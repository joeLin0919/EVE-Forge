package org.eveforge.repository.enums;




public enum MarketLocationEnum {

    JITA("吉他",10000002, 20000020, 30000142),
    AMARR("艾玛", 10000043, 20000322, 30002187),
    DODIXIE("多迪谢",10000032,20000389,30002659),
    HEK("赫克",10000042,20000302,30002053),
    PLEX("PLEX",19000001,null,null),
    ;







    private String name;

    private Integer RegionId;

    private Integer ConstellationId;

    private Integer SystemId;

    private MarketLocationEnum(String name,Integer RegionId, Integer ConstellationId, Integer SystemId) {
        this.name = name;
        this.RegionId = RegionId;
        this.ConstellationId = ConstellationId;
        this.SystemId = SystemId;
    }

    public Integer getRegionId() {
        return RegionId;
    }

    public Integer getConstellationId() {
        return ConstellationId;
    }

    public Integer getSystemId() {
        return SystemId;
    }

    public String getName() {
        return name;
    }
}

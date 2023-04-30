package entities;

import jakarta.persistence.*;

@Entity
public class Regions {

    @Id
    @Column(name = "region_id")
    private int regionId;
    @Basic
    @Column(name = "region_name")
    private String regionName;
    @Basic
    @Column(name = "company_id")
    private Integer companyId;

    public int getRegionId() {
        return regionId;
    }

    public void setRegionId(int regionId) {
        this.regionId = regionId;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public Integer getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Integer companyId) {
        this.companyId = companyId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Regions regions = (Regions) o;

        if (regionId != regions.regionId) return false;
        if (regionName != null ? !regionName.equals(regions.regionName) : regions.regionName != null) return false;
        if (companyId != null ? !companyId.equals(regions.companyId) : regions.companyId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = regionId;
        result = 31 * result + (regionName != null ? regionName.hashCode() : 0);
        result = 31 * result + (companyId != null ? companyId.hashCode() : 0);
        return result;
    }
}

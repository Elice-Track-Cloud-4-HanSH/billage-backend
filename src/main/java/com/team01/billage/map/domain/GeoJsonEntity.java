import jakarta.persistence.*;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.MultiPolygon;

@Entity
@Table(name = "geojson_table")  // 테이블 이름 매핑
public class GeoJsonEntity {

    @Id
    @Column(name = "emd_cd", length = 20, nullable = false)
    private String emdCd;  // PRIMARY KEY로 사용

    @Column(name = "col_adm_se", length = 20)
    private String colAdmSe;

    @Column(name = "emd_nm", length = 255)
    private String emdNm;

    @Column(name = "sgg_oid")
    private Integer sggOid;

    @Column(columnDefinition = "geometry(MultiPolygon, 4326)", nullable = false)
    private MultiPolygon geom;  // GEOMETRY 타입, 좌표계 4326

    // Getters and Setters
    public String getEmdCd() {
        return emdCd;
    }

    public void setEmdCd(String emdCd) {
        this.emdCd = emdCd;
    }

    public String getColAdmSe() {
        return colAdmSe;
    }

    public void setColAdmSe(String colAdmSe) {
        this.colAdmSe = colAdmSe;
    }

    public String getEmdNm() {
        return emdNm;
    }

    public void setEmdNm(String emdNm) {
        this.emdNm = emdNm;
    }

    public Integer getSggOid() {
        return sggOid;
    }

    public void setSggOid(Integer sggOid) {
        this.sggOid = sggOid;
    }

    public MultiPolygon getGeom() {
        return geom;
    }

    public void setGeom(MultiPolygon geom) {
        this.geom = geom;
    }
}

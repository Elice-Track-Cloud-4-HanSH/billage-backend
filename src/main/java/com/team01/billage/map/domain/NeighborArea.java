package com.team01.billage.map.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.MultiPolygon;


@Entity
@Table(name = "neighbor_area")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class NeighborArea {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "emd_cd", referencedColumnName = "emd_cd")
    private EmdArea emdArea;

    @Column(name = "depth")
    private int depth;

    @Column(columnDefinition = "geometry(MultiPolygon, 4326)", nullable = false)
    private MultiPolygon geom;  // GEOMETRY 타입, 좌표계 4326
}

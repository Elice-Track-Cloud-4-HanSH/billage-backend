package com.team01.billage.map.domain;

import com.team01.billage.user.domain.Users;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "activity_area")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class ActivityArea {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")  // 사용자 ID와 연결
    private Users users;

    @ManyToOne
    @JoinColumn(name = "emd_cd", referencedColumnName = "emd_cd")  // 행정구역 코드와 연결
    private EmdArea emdArea;
}

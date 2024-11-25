package com.team01.billage.rental_record.repository;

import com.team01.billage.rental_record.domain.RentalRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RentalRecordRepository extends JpaRepository<RentalRecord, Long>,
    CustomRentalRecordRepository {

}

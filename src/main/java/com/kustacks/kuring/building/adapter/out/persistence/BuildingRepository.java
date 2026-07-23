package com.kustacks.kuring.building.adapter.out.persistence;

import com.kustacks.kuring.building.domain.Building;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BuildingRepository extends JpaRepository<Building, Long> {

    List<Building> findAllByOrderByIdAsc();

    @Query("""
            select distinct building
            from Building building
            left join building.keywords keyword
            where lower(building.name) like lower(concat('%', :keyword, '%'))
               or lower(building.address) like lower(concat('%', :keyword, '%'))
               or lower(keyword.keyword) like lower(concat('%', :keyword, '%'))
            order by building.id asc
            """)
    List<Building> searchByKeyword(@Param("keyword") String keyword);
}

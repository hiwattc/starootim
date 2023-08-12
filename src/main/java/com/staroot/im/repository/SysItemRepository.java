package com.staroot.im.repository;


import com.staroot.im.entity.SysItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface SysItemRepository extends JpaRepository<SysItem, Long> {
    SysItem findByName(String name);
    SysItem findByCode(String code);

    //@Query("SELECT a, ROW_NUMBER() OVER (ORDER BY a.code) as sequence FROM sysitem a") //테이블명오류
    //@Query("SELECT a.* FROM SysItem a") //테이블명오류
    //@Query("SELECT a, ROW_NUMBER() OVER (ORDER BY a.code) as sequence FROM SysItem a")
    @Query("SELECT  a.code, a.name FROM SysItem a")
    //List<Map<String,Object>> findAllWithSequence();
    List<Object[]> findAllWithSequence();
}

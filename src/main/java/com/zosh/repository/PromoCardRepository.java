package com.zosh.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.zosh.model.PromoCard;

@Repository
public interface PromoCardRepository extends JpaRepository<PromoCard, Long> {

}

package com.zosh.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.zosh.model.PromoCard;
import com.zosh.repository.PromoCardRepository;

@Service
public class PromoCardService {
	
	
	  private final PromoCardRepository promoCardRepository;

	    public PromoCardService(PromoCardRepository promoCardRepository) {
	        this.promoCardRepository = promoCardRepository;
	    }

	    public List<PromoCard> getAllPromoCards() {
	        return promoCardRepository.findAll();
	    }

	    public PromoCard addPromoCard(PromoCard promoCard) {
	        return promoCardRepository.save(promoCard);
	    }

	    public void deletePromoCard(Long id) {
	        promoCardRepository.deleteById(id);
	    }

}

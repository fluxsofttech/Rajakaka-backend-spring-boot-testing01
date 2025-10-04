package com.zosh.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zosh.model.PromoCard;
import com.zosh.service.impl.PromoCardService;

@RestController
@RequestMapping("/manage/promos")
public class PromoCardController {
	
	  private final PromoCardService promoCardService;

	    public PromoCardController(PromoCardService promoCardService) {
	        this.promoCardService = promoCardService;
	    }

	    // ✅ Get all promo cards
	    @GetMapping
	    public ResponseEntity<List<PromoCard>> getAllPromos() {
	        return ResponseEntity.ok(promoCardService.getAllPromoCards());
	    }

	    // ✅ Add new promo card (admin)
	    @PostMapping
	    public ResponseEntity<PromoCard> createPromo(@RequestBody PromoCard promoCard) {
	        return ResponseEntity.ok(promoCardService.addPromoCard(promoCard));
	    }

	    // ✅ Delete a promo card (admin)
	    @DeleteMapping("/{id}")
	    public ResponseEntity<String> deletePromo(@PathVariable Long id) {
	        promoCardService.deletePromoCard(id);
	        return ResponseEntity.ok("Promo deleted successfully");
	    }

}

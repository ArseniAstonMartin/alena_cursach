package com.example.loyalty.application.port;

import com.example.loyalty.domain.model.Offer;
import com.example.loyalty.domain.model.OfferStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OfferRepository extends JpaRepository<Offer, Long> { List<Offer> findByStatus(OfferStatus status); }

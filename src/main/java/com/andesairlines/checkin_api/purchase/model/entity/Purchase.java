package com.andesairlines.checkin_api.purchase.model.entity;

import com.andesairlines.checkin_api.flight.model.entity.BoardingPass;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "purchase")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Purchase {

    @Id
    @Column(name = "purchase_id")
    private Integer purchaseId;

    @Column(name = "purchaswe_date")
    private Integer purchaseDate;

    @OneToMany(mappedBy = "purchase", fetch = FetchType.LAZY)
    private List<BoardingPass> boardingPasses;
}

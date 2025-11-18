package graduation.project.DoDutch_server.domain.kakaopay.repository;

import graduation.project.DoDutch_server.domain.kakaopay.entity.PaymentOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentOrderRepository extends JpaRepository<PaymentOrder, Long> {
    Optional<PaymentOrder> findByPartnerOrderId(String partnerOrderId);
}

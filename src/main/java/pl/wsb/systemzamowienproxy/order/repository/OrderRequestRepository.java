package pl.wsb.systemzamowienproxy.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.wsb.systemzamowienproxy.order.entity.OrderRequest;

import java.util.List;

public interface OrderRequestRepository extends JpaRepository<OrderRequest, Long> {
    List<OrderRequest> findByCustomer_FullName(String fullName);
}
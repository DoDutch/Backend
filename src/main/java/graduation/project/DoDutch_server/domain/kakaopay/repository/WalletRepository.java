package graduation.project.DoDutch_server.domain.kakaopay.repository;

import graduation.project.DoDutch_server.domain.kakaopay.entity.Wallet;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet, Long> {
    Optional<Wallet> findByUserId(Long userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select w from Wallet w where w.userId = :uid")
    Optional<Wallet> findByUserIdForUpdate(@Param("uid") Long userId);

    @Modifying
    @Query("UPDATE Wallet w SET w.balance = w.balance + :amount WHERE w.userId = :userId")
    int addBalance(@Param("userId") String userId, @Param("amount") Long amount);

}

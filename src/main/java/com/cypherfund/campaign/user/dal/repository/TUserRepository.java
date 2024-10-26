package com.cypherfund.campaign.user.dal.repository;

import com.cypherfund.campaign.user.dal.entity.TUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TUserRepository extends JpaRepository<TUser, String> {
    Optional<TUser> findFirstByUsernameOrEmailOrPhone(String username, String email, String phone);
    Optional<TUser> findFirstByUsernameOrEmail(String username, String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
    boolean existsByPhoneOrEmailOrUsername(String username, String email, String phone);
    boolean existsByReferralCode(String referrer);
    Optional<TUser> findByEmail(String email);
    List<TUser> findByReferrer(String referrer);

    @Query(value = "SELECT" +
            "    u.username," +
            "    u.user_id," +
            "    COALESCE(SUM(t.amount), 0) AS total_points " +
            "FROM" +
            "    t_users u" +
            "        LEFT JOIN" +
            "    transactions t ON u.user_id = t.user_id" +
            "        AND t.type = 'COIN_REWARD'" +
            "WHERE" +
            "        u.referrer = ?1 " +
            "GROUP BY" +
            "    u.user_id", nativeQuery = true)
    List<Object[]> getReferralData(String referralCode);
}

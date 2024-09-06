package net.datasa.yomakase_web.repository;

import net.datasa.yomakase_web.domain.entity.UserBodyInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserBodyInfoRepository extends JpaRepository<UserBodyInfoEntity, Integer> {

}

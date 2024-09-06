package net.datasa.yomakase_web.repository;

import net.datasa.yomakase_web.domain.compositeK.MemberStock;
import net.datasa.yomakase_web.domain.entity.StockEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockRepository extends JpaRepository<StockEntity, MemberStock> {

}

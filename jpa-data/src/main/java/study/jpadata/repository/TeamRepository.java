package study.jpadata.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import study.jpadata.entity.Team;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {
}

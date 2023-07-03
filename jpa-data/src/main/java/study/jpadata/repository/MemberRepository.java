package study.jpadata.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import study.jpadata.dto.MemberDto;
import study.jpadata.entity.Member;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.QueryHint;
import java.util.List;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {

    List<Member> findByNameAndAgeGreaterThan(String name, int age);
    @Query("select m from Member m where m.name = :name and m.age > :age")
    List<Member> findUser(@Param("name") String name, @Param("age") int age);

    @Query("select m.name from Member m")
    List<String> findUsernameList();

    @Query("select new study.jpadata.dto.MemberDto(m.id, m.name, t.name) from Member m join m.team t")
    List<MemberDto> findMemberDto();

    @Query("select m from Member m where m.name in :names")
    List<Member> findByNames(@Param("names") List<String> names);

    @Query(value = "select m from Member m left join m.team t",
            countQuery = "select count(m) from Member m")
    Page<Member> findByAge(int age, Pageable pageable);

    @Modifying(clearAutomatically = true)
    @Query("update Member m set m.age = m.age + 1 where m.age >= :age")
    int bulkAgePlus(@Param("age") int age);

    @Query("select m from Member m join fetch m.team")
    List<Member> findAllMemberJoinTeam();

    @EntityGraph(attributePaths = "team")
    List<Member> findAll();

    @Query("select m from Member m where m.id = :id")
    @EntityGraph(attributePaths = "team")
    Member findMemberJoinTeam(@Param("id") Long id);

    @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value="true"))
    Member findReadOnlyById(Long Id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Member findLockById(Long id);
}

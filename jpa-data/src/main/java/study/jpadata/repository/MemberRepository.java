package study.jpadata.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import study.jpadata.dto.MemberDto;
import study.jpadata.entity.Member;

import java.util.List;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
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


}

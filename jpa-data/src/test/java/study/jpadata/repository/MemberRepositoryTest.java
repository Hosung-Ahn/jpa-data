package study.jpadata.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.jpadata.dto.MemberDto;
import study.jpadata.entity.Member;
import study.jpadata.entity.Team;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Rollback(false)
class MemberRepositoryTest {
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    TeamRepository teamRepository;
    @PersistenceContext
    EntityManager em;

    @Test
    public void testMember() {
        Member member = new Member("memberA");
        Member savedMember = memberRepository.save(member);
        Member findMember = memberRepository.findById(savedMember.getId()).get();

        assertThat(findMember).isEqualTo(savedMember);
    }

    @Test
    public void basicCRUD() {
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");

        memberRepository.save(member1);
        memberRepository.save(member2);

        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();

        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        List<Member> all = memberRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        long count = memberRepository.count();
        assertThat(count).isEqualTo(2);

        memberRepository.delete(member1);
        memberRepository.delete(member2);
        assertThat(memberRepository.count()).isEqualTo(0);

    }

    @Test
    public void findByUsernameAndAgeGreaterThan() {
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("AAA", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> result = memberRepository.findByNameAndAgeGreaterThan("AAA", 15);

        assertThat(result.get(0).getName()).isEqualTo("AAA");
        assertThat(result.get(0).getAge()).isEqualTo(20);
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    public void testQuery() {
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("AAA", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> result = memberRepository.findUser("AAA", 10);

        assertThat(result.get(0)).isEqualTo(member2);
    }

    @Test
    public void findUsername() {
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("AAA", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);

        List<String> result = memberRepository.findUsernameList();

        assertThat(result.get(0)).isEqualTo("AAA");
        assertThat(result.size()).isEqualTo(2);
    }

    @Test
    public void findMemberDto() {
        Team team = new Team("teamA");
        teamRepository.save(team);
        Member member1 = new Member("AAA", 10, team);
        memberRepository.save(member1);

        List<MemberDto> result = memberRepository.findMemberDto();

        for (MemberDto memberDto : result) {
            System.out.println("memberDto = " + memberDto);
        }
    }

    @Test
    public void findMemberByNames() {
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("BBB", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> result = memberRepository.findByNames(List.of("AAA", "BBB"));

        for (Member member : result) {
            System.out.println("member = " + member);
        }
    }

    @Test
    public void paging() {
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        int age = 10;
        int paging = 0;
        int size = 3;
        PageRequest pageRequest = PageRequest.of(paging, size, Sort.by(Sort.Direction.DESC, "name"));

        Page<Member> page = memberRepository.findByAge(age, pageRequest);

        List<Member> content = page.getContent();
        long totalElements = page.getTotalElements();

        for (Member member : content) {
            System.out.println("member = " + member);
        }
        System.out.println("totalElements = " + totalElements);

        paging += 1;
        System.out.println("paging = " + paging);
        memberRepository.save(new Member("member6", 10));

        pageRequest = PageRequest.of(paging, size, Sort.by(Sort.Direction.DESC, "name"));
        page = memberRepository.findByAge(age, pageRequest);

        content = page.getContent();
        totalElements = page.getTotalElements();

        for (Member member : content) {
            System.out.println("member = " + member);
        }
        System.out.println("totalElements = " + totalElements);

        List<MemberDto> memberDto = page.map(member -> new MemberDto(member.getId(), member.getName(), null)).getContent();

        for (MemberDto dto : memberDto) {
            System.out.println("dto = " + dto);
        }
    }

    @Test
    public void bulkUpdate() {
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 15));
        memberRepository.save(new Member("member3", 20));
        memberRepository.save(new Member("member4", 25));
        memberRepository.save(new Member("member5", 30));

        int updatedCnt = memberRepository.bulkAgePlus(20);

        assertThat(updatedCnt).isEqualTo(3);

        List<Member> members = memberRepository.findAll();

        for (Member member : members) {
            System.out.println(member.getName() + " " + member.getAge());
        }
    }

    @Test
    public void findMemberLazy() {
        // given
        // member1 -> teamA
        // member2 -> teamB

        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamB);

        memberRepository.save(member1);
        memberRepository.save(member2);

        em.flush();
        em.clear();

        //when
        //select member
        List<Member> members = memberRepository.findAll();

        for (Member member : members) {
            System.out.println("member = " + member.getName());
            // query 가 한번 더 발생한다.
            System.out.println("member's team name = " + member.getTeam().getName());
        }
        
        //select member fetch join team
        List<Member> membersJoinTeam = memberRepository.findAllMemberJoinTeam();

        for (Member member : membersJoinTeam) {
            System.out.println("member.getName() = " + member.getName());
            // query 가 발생하지 않는다.
            System.out.println("member.getTeam().getName() = " + member.getTeam().getName());
        }

        //select member using entity graph
        List<Member> membersByEntityGraph = memberRepository.findAll();
        for (Member member : membersJoinTeam) {
            System.out.println("member.getName() = " + member.getName());
            // query 가 발생하지 않는다.
            System.out.println("member.getTeam().getName() = " + member.getTeam().getName());
        }

    }

    @Test
    public void queryHint() {
        // given
        Member member1 = new Member("member1", 10);
        memberRepository.save(member1);
        em.flush();
        em.clear();

        //when
        //duty checking 을 위한 비용 발생
        Member findMember = memberRepository.findById(member1.getId()).get();
        findMember.setName("new member");
        em.flush();
    }

    @Test
    public void lock() {
        // given
        Member member1 = new Member("member1", 10);
        memberRepository.save(member1);
        em.flush();
        em.clear();

        //when
        //lock
        Member findMember = memberRepository.findLockById(member1.getId());
    }

    @Test
    public void callCustomRepository() {
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 15));
        memberRepository.save(new Member("member3", 20));

        List<Member> members = memberRepository.findAllCustom();

        assertThat(members.size()).isEqualTo(3);

        for (Member member : members) {
            System.out.println(member.getName());
        }


    }

    @Test
    void JpaBaseEntityTest() throws InterruptedException {
        Member member = new Member("member", 10);
        memberRepository.save(member); // @PrePersist

        Thread.sleep(100);

        Member findMember = memberRepository.findById(member.getId()).get();
        findMember.setName("new name"); // @PreUpdate

        em.flush();
        em.clear();

        System.out.println(findMember.getCreatedAt());
        System.out.println(findMember.getUpdatedAt());

    }

    @Test
    void entity를_변경하지않아도_preupdate가_동작하는지() throws InterruptedException {
        Member member = new Member("member", 10);
        memberRepository.save(member);

        System.out.println("업데이트 되기전의 상태");
        System.out.println(member.getCreatedAt());
        System.out.println(member.getUpdatedAt());

        em.flush();
        em.clear();

        Thread.sleep(1000);

        Member findMember = memberRepository.findById(member.getId()).get();
        findMember.setName("member");

        em.flush();
        em.clear();

        Member refindMember = memberRepository.findById(member.getId()).get();

        System.out.println("entity를 불러만 오고 수정하지 않은 상태");
        System.out.println(refindMember.getCreatedAt());
        System.out.println(refindMember.getUpdatedAt());

    }
}
package study.jpadata.repository;

import lombok.RequiredArgsConstructor;
import study.jpadata.entity.Member;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

public class MemberRepositoryCustomImpl implements MemberRepositoryCustom{
    @PersistenceContext
    EntityManager em;
    @Override
    public List<Member> findAllCustom() {
        return em.createQuery("select m from Member m", Member.class).getResultList();
    }
}

package hellojpa;

import org.hibernate.Hibernate;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public class JpaMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin(); //트랜잭션 시작

        try {

            Member member = new Member();
            member.setUsername("member1");
            member.setHomeAddress(new Address("homeCity", "street", "10000"));

            member.getFavoriteFoods().add("치킨");
            member.getFavoriteFoods().add("족발");
            member.getFavoriteFoods().add("피자");

            member.getAddressHistory().add(new AddressEntity("old1", "street", "10000"));
            member.getAddressHistory().add(new AddressEntity("old2", "street", "10000"));

            em.persist(member);

            em.flush();
            em.clear();

            System.out.println("\n=================== START ================");
            Member findMember = em.find(Member.class, member.getId());

            //homeCity -> newCity
//            Address old = findMember.getHomeAddress();
//            findMember.setHomeAddress(new Address("newCity", old.getStreet(), old.getZipcode()));
//
//            //치킨 -> 한식
//            findMember.getFavoriteFoods().remove("치킨");
//            findMember.getFavoriteFoods().add("한식");

//            findMember.getAddressHistory().remove(new AddressEntity("old1", "street", "10000"));
//            findMember.getAddressHistory().add(new AddressEntity("newCity1", "street", "10000"));

            tx.commit(); //트랜잭션 커밋
        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }

        em.close();

        emf.close();
    }
}

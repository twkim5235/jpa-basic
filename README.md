Jpa

# JPA란

## 1. SQL 중심적인 개발의 문제점

현대적인 애플리케이션을 개발할때는 대부분 객체지향 언어를 사용

DB는 관계형 DB를 씀. - Oracle이나, MySql을 씀.

코드를 막상 열어보면 SQL 중심적으로 구현이 되어있음.



### 객체와 관계형 데이터베이스의 차이

- 상속
  - 객체는 있으나 관계형 DB는 없음
- 연관관계
  - 객체는 참조를 통해서 얻음
  - DB는 Join을 통해 찾을 수 있음
- 데이터 타입
- 데이터 식별 방법



객체는 자유롭게 객체 그래프를 탐색할 수 있어야한다.



## 2. JPA 소개

Java Persistance API

자바 표준 ORM

### ORM

- Object-relational-mapping(객체 관계 매핑)
- 객체는 객체대로 설계
- 관계형 데이터베이스는 관계형 데이터베이스대로 설계



### JPA를 왜 사용해야 하는가?

- SQL 중심적인 개발에서 객체 중심으로 개발
- 생산성
- 유지보수
- 패러타임의 불일치 해결
- 성능
- 데이터 접근 추상화와 벤더 독립성
- 표준



### 생산성 - JPA와 CRUD

- 저장: jpa.persist(member)
- 조회: jpa.find(memberId)
- 수정: member.setName("변경할 이름")
- 삭제 jpa.remove(member)



### 유지보수

- 기존: 필드 변경시 모든 SQL 수정
- JPA 사용: 필드만 추가하면 됨. SQL은 JPA가 처리



#### JPA는 동일한 트랜잭션에서 조회한 엔티티는 같음을 보장



### 성능

- 1차 캐시와 동일성 보장

- 트랜잭션을 지원하는 쓰기 지연

  - buffering 기능 구현

    - 트랜잭션을 커밋할때 까지 Insert SQL을 모음

    - JDBC BATCH SQL 기능을 사용해서 한번에 SQL 전송

    - Ex)

      em.persist(memberA);

      em.persist(memberB);

      em.persist(memberC);

      //여기 까지 Insert SQL을 데이터베이스에 보내지 않는다.



      //커밋 하는 순간 데이터베이스에 Insert Sql을 모아서 보낸다.

      transactio.comit();//트랜잭션 커밋

- 지연 로딩

  - 지연 로딩: 객체가 실제 사용될 때 로딩
  - 즉시 로딩: Join Sqlfh 한번에 연관된 객체까지 미리 조회

### 트랜잭션
JPA에서는 모든 데이터를 변경하는 작업은 꼭 트랜잭션 안에서 실행되어야 한다.

### entityManger
자바의 콜렉션이라고 생각하면 됨, 내 객체를 대신 저장해주는 것이라 생각하면 된다.

JPA를 통해서 Entity를 가져오면 JPA가 관리를 함. JPA가 트랜잭션 커밋시점에 데이터 변경 유무를 체크하고, 데이터가 바뀌면 Update 쿼리를 직접만들어서 전송함.

EntityManagerFactory는 서버가 실행할 때 하나만 생성해서 애플리케이션 전체에서 공유한다.
(내 생각에는 Spring Bean을 관리하는 것 같이 싱글 톤으로 관리하는 것 같다.)

EntityManager는 고객의 요청이 올때마다 일회용처럼 사용했다가 버린다.
**그러므로 entitymanager는 쓰레드간에 공유하면 절대 안된다.**

**JPA의 모든 데이터 변경은 트랜잭션 안에서 실행 되어야 한다.**

### JPQL
JPA는 SQL을 추상화한 JPQL이라는 객체 지향 쿼리 언어를 제공한다.
테이블이 아닌 엔티티 객체를 대상으로 쿼리를 함.

JPA를 사용하면 엔티티 객체를 중심으로 개발

문제는 검색 쿼리

검새을 할때에도, 테이블이 아닌 엔티티 객체를 대상으로 검색

애플리케이션이 필요한 데이터만 DB에서 불러오려면 결국 검색 조건이 포함된 SQL이 필요함.

### 영속성 컨텍스트

- JPA를 이해하는데 가장 중요한 용어

- "엔티티(Table)를 영구 저장하는 환경"이라는 뜻

- EntityManager.persist(entity); - 영속성 컨텍스트에 entity를 영속화 한다.

- 영속성 컨텍스트는 논리적인 개념이다. 엔티티 매니저를 통해서 영속성 컨텍스트에 접근한다.

  - EntityManager -> PersistenceContext(영속성 컨텍스트)

- 엔티티의 생명 주기

  - 비영속 - 영속성 컨텍스트와 전혀 관계가 없는 **새로운** 상태
  - 영속 - 영속성 컨텍스트에 **관리**되는 상태
  - 중영속 - 영속성 컨텍스트에 저장되었다가 **분리** 상태
  - 삭제 - **삭제**된 상태

- 영속성 컨텍스트의 이점

  - 1차 캐시

  - 동일성 보장

  - 트랜잭션을 지원하는 쓰기 지연

    - Persist 시 JPA가 insert SQL을 생성하여 쓰기지연 SQL 저장소와 1차 캐시에 저장한다.

    - 코드상에, 트랜잭션을 커밋하는 시점(tx.commit)에 쓰기지연 저장소에 있는 쿼리들이 flush 되면서 실제 DB에 트랜잭션에 커밋된다.

      ~~~java
      Member member1 = new Member(150L, "A");
      Member member2 = new Member(160L, "B");
      
      //영속
      em.persist(member1);
      em.persist(member2);
      
      //Flush 되는 시점
      tx.commit(); //트랜잭션 커밋
      ~~~

    - jdbc 배치를 통해 쿼리를 한번에 전송할 수 있다.

    - ```xml
      value만큼 모아서 한번에 전송한다.
      <property name="hibernate.jdbc.batch_size" value="10"/>
      ```

  - 변경 감지(Dirty Checking)

    - persist를 하지 않아도, JAVA Collection 처럼 값을 바꾸면 알아서 DB에 적용된다.

    - ```java
      try {
          Member member = em.find(Member.class, 150L);
          member.setName("ZZZZ");
      
          System.out.println("===============");
      
          tx.commit(); //트랜잭션 커밋
      }
      
      Hibernate: 
          select
              member0_.id as id1_0_0_,
              member0_.name as name2_0_0_ 
          from
              Member member0_ 
          where
              member0_.id=?
      ===============
      Hibernate: 
          /* update
              hellojpa.Member */ update
                  Member 
              set
                  name=? 
              where
                  id=?
      ```

    - JPA는 커밋하는 시점에  flush가 호출되면서 entity와 snapshot을 비교한다.

    - | @ID     | Entity       | 스냅샷         |
            | ------- | ------------ | -------------- |
      | memberA | memberA 객체 | memberA 스냅샷 |
      | memberB | memberB 객체 | memberB 스냅샷 |

      비교를 한뒤, Update SQL를 쓰기지연 SQL 저장소에 저장한다.

  - 지연 로딩

### 플러시

- 플러시는 영속성 컨텍스트의 변경내용을 데이터베이스에 반영한다.
  - 영속성 컨텍스트의 변경사항과 실제 DB를 맞추는 거라고 생각하면 됨.

#### 플러시가 발생할 때

- 변경 감지
- 수정된 엔티티 쓰기 지연 SQL 저장소에 등록
- 쓰기 지연 SQL 저장소의 쿼리를 데이터 베이스에 전송 (등록, 수정, 삭제 쿼리)

#### 영속성 컨텍스트를 플러시 하는 방법

- em.flush() - 직접 호출
- 트랜잭션 커밋 - 플러시 자동 호출
- JPQL 쿼리 실행 - 플러시 자동 호출

#### 플러시 모드 옵션

- FlushModeType.AUTO
  - 커밋이나 쿼리를 실행할 때 플러시 (기본값)
- FlushModeType.COMMIT
  - 커밋할 때만 플러시

#### 플러시의 유의사항

- 영속성 컨텍스트를 비우지 않는다.
- 영속성 컨텍스트의 변경내용을 데이터베이스에 동기화 한다.
- 트랜잭션이라는 작업 단위 중요 -> 커밋 직전에만 동기화 하면 된다.
  - **실무에서 영속성 컨텍스트랑 트랜잭션의 주기랑 맞추게 개발을 해야 한다.**

### 준영속 상태

- 영속(em.persist, em.find) - > 준영속
- 준영속 상태란 영속 상태의 엔티티가 영속성 컨텍스트에서 분리(detached)
- 영속성 컨텍스트가 제공하는 기능을 사용 못함

#### 준영속 상태로 만드는 방법

- em.detach(entity)
  - 특정 엔티티만 준영속 상태로 전환
- em.clear()
  - 영속성 컨텍스트를 완전히 초기화
- em.close()
  - 영속성 컨텍스트를 종료

### 엔티티 매핑

- 객체와 테이블 매핑: @Entity, @Table

- 필드와 컬럼 매핑: @Column

- 기본 키 매핑: @Id

- 연관관계 매핑: @ManyToOne, @JoinColumn



### 객체와 테이블 매핑

#### @Entity

- @Entity가 붙은 클래스는 JPA가 관리하는 엔티티라 한다.

- JPA를 사용해서 테이블과 매핑할 클래스는 **@Entity** 필수이다.
- 주의
  - 기본 생성자 필수(파라미터가 없는 pulic 또는 protected 생성자)
  - final 클래스, enum, interface, inner 클래스 사용X
  - 저장할 필드에 final 사용X
- 속성 정리
  - name
    - JPA에서 사용할 엔티티 이름을 지정한다.
    - 기본값: 클래스 이름을 그대로 사용(예: Member)
    - 같은 클래스 이름이 없으면 가급적 기본값을 사용한다.

#### @Table

- @Table은 엔티티와 매핑할 테이블 지정

  | 속성                   | 기능                                | 기본값             |
    | ---------------------- | ----------------------------------- | ------------------ |
  | name                   | 매핑할 테이블 이름                  | 엔티티 이름을 사용 |
  | catalog                | 데이터베이스 catalog 매핑           |                    |
  | schema                 | 데이터베이스 schema 매핑            |                    |
  | uniqueConstraints(DDL) | DDL 생성 시에 유니크 제약 조건 생성 |                    |



### 데이터베이스 스키마 자동 생성

- DDL(데이터 조작어)를 애플리케이션 실행 시점에 자동 생성
- 테이블 중심 -> 객체 중심
- 데이터베이스 방언을 활용해서 데이터베이스에 맞는 적절한 DDL 생성
  - Oracle은 varchar를 생성할때 varchar2로 생성함
  - H2는 varchar로 생성함
- 이렇게 생성된 DDL은 개발 장비에서만 사용
- 생성된 DDL은 운영서버에서는 사용하지 않거나, 적절히 다듬은 후 사용

#### 속성

| 옵션          | 설명                                        |
| ------------- | ------------------------------------------- |
| create        | 기존테이블 삭제 후 다시 생성(DROP + CREATE) |
| create - drop | create와 같으나 종료시점에 테이블 DROP      |
| update        | 변경분만 반영(운영DB에는 사용하면 안됨)     |
| validate      | 엔티티와 테이블이 정상 매핑되었는지만 확인  |
| none          | 사용하지 않음                               |

#### 주의 사항

- 운영 장비에는 절대 create, create-drop, update를 사용하면 안된다.
- 개발 초기 단계는 create 또는 update 을 권장한다.
- 테스트 서버는 update 또는 validate 을 권장한다.
- 스테이징과 운영서버는 validate 또는 none 을 권장한다.

#### DDL 생성기능

- 제약조건 추가: 회원 이름은 **필수**, 10자 초과X
  - @Column(nullable = false, length = 10)
- 유니크 제약조건 추가
  - @Table(uniqueConstraints = {@UniqueConstraint(name = "NAME_AGE_UNIQUE", columnNames = {"NAME", "AGE"})})

- DDL 생성 기능은 DDL을 자동 생성할 때만 사용되고, JPA의 실행 로직에는 영향을 주지 않는다.



### 필드와 컬럼 매핑

#### 매핑 어노테이션 정리

| 어노테이션  | 설명                                  |
| ----------- | ------------------------------------- |
| @Column     | 컬럼 매핑                             |
| @Temporal   | 날짜 타입 매핑                        |
| @Enumerated | enum 타입 매핑                        |
| @Lob        | BLOB, CLOB 매핑                       |
| @Transient  | 특정 필드를 컬럼에 포함시키지 않을 때 |

#### @Column

| 속성                   | 설명                                                         | 기본값                             |
| ---------------------- | ------------------------------------------------------------ | ---------------------------------- |
| name                   | 필드와 매핑할 테이블의 컬럼 이름                             | 객체의 필드 이름                   |
| insertable, updateable | 등록, 변경 가능 여부                                         | TRUE                               |
| nullable(DDL)          | null값의 허용 여부를 설정한다. false로 설정하면 DDL 생성 시에 not null 제약조건이 붙는다. |                                    |
| unique(DDL)            | @Table의 uniqueConstraints와 같지만 한 컬럼에 간단히 유니크 제약조건을 걸 때 사용한다. |                                    |
| columnDefinition       | 데이터베이스 컬럼 정보를 직접 줄 수 있다.<br/>ex)varchar(100) default 'EMPTY' | 필드의 자바 타입과 방언정보를 사용 |
| length(DDL)            | 문자 길이 제약조건, String 타입에만 사용한다.                | 255                                |
| precsion               | BigDecimal타입에서 사용한다.(BigInteger도 사용할 수 있다.) precision은 소수점을 포함한 전체 자릿 수를 scale은 소수의 자릿수이다. 참고로 double, float 타입에는 적용되지 않는다. 아주 큰 숫자나 정밀한 소수를 다루어야 할때만 사용한다. | precision=19,scale=2               |

#### @Enumerated

- 자바 enum 타입을 매핑할 때 사용
- **주의! ORDINAL 사용X**
  - **Ordinal 사용시 숫자(0 ~ n)로 값이 들어가기 때문에 enum에 값이 추가되면 꼬일 수 있다.**

| 속성  | 설명                                                         | 기본값           |
| ----- | ------------------------------------------------------------ | ---------------- |
| value | - EnumType.ORINAL: enum 순서를 데이터베이스에 저장<br/>- EnumType.STRING: enum 이름을 데이터 베이스에 저장 | EnumType.ORDINAL |

#### @Temporal

- 날짜 타입을 매핑할 때 사용
- 참고: LocalDate, LocalDateTime을 사용할 때는 생략 가능(최신 하이버네이트 지원)

| 속성  | 설명                                                         | 기본값 |
| ----- | ------------------------------------------------------------ | ------ |
| value | - TemporalType.DATE: 날짜, 데이터베이스 date 타입과 매핑<br/>ex) 2013-10-11<br/>- TemporalType.TIME: 시간 데이터베이스 time 타입과 매핑<br/>ex) 11:11:11<br/>- TemporalType.TIMESTAMP: 날짜와 시간, 데이터베이스 timestamp 타입과 매핑(예: 2013-10-11 11:11:11) |        |

### @Lob

데이터베이스 BLOB, CLOB 타입과 매핑

- @Lob에는 지정할 수 있는 속성이 없다.
- 매핑하는 필드 타입이 문자면 CLOB 매핑, 나머지는 BLOB 매핑
  - CLOB: String, char[], java.sql.CLOB
  - BLOB: byte[], java.sql.BLOB

#### @Transient

- 필드 매핑X

- 데이터베이스에 저장X, 조회X

- 테이블에 컬럼으로 추가를 안하고, 주로 메모리상에서만 임시로 어떤 값을 보관하고 싶을 때 사용

- @Transient

  private Integer temp

### 기본키 매핑

#### 기본키 매핑 어노테이션

- @Id
- @GeneratedValue

#### 기본키 매핑 방법

- 직접 할당: @Id만 사용
- 자동 생성: (@GeneratedValue)
  - IDENTITY: 데이터베이스에 위임, MYSQL
  - SEQUENCE: 데이터베이스 시퀀스 오브젝트 사용, ORACLE, PostgreSQL
    - @SequenceGenerator 필요
  - TABLE: 키 생성용 테이블 사용, 모든 DB에서 사용
    - @TableGenerator 필요
  - AUTO: 방언에 따라 자동 지정, 기본값 설정

#### IDENTITY 전략 - 특징

- 기본 키 생성을 데이터베이스에 위임

- 주로 MySQL, SQL Server, DB2에서 사용

  (예: MySQL의 AUTO_ICREMENT)

- JPA는 보통 트랜잭션 커밋 시점에 INSER SQL 실행
- AUTO_INCREMENT는 데이터베이스 INSERT SQL을 실행한 이후에 ID 값을 알 수 있음
- IDENTITY 전략은 em.persist() 시점에 즉시 INSERT SQL 실행하고 DB에서 식별자를 조회



#### SEQUENCE 전략 - 특징

- 데이터베이스 시퀀스는 유일한 값을 순서대로 생성하는 특별한 데이터베이스 오브젝트(예: 오라클 시퀀스)
- 오라클, PostgreSQL, DB2, H2 데이터베이스에서 사용

#### @SequenceGenrator

- 주의: allocationSize 기본값 = 50

| 속성            | 설명                                                         | 기본값             |
| --------------- | ------------------------------------------------------------ | ------------------ |
| name            | 식별자 생성기 이름                                           | 필수               |
| sequenceName    | 데이터베이스에 등록되어 있는 시퀀스 이름                     | hibernate_sequence |
| initailValue    | DDL생성 시에만 사용됨, 시퀀스DDL을 생성할 때 처음 시작하는 수를 지정한다. | 1                  |
| allocationSize  | 시퀀스 한번 호출에 증가하는 수(성능 최적화에 사용됨, **데이터베이스 시퀀스 값이 하나씩 증가하도록 설정되어 있으면 이값을 반드시 1로 설정해야 한다.**) | **50**             |
| catalog, schema | 데이터베이스 catalog, schema 이름                            |                    |



#### 권장하는 식별자 전략

- 기본 키 제약 조건: null 아님, 유일, 변하면 안된다.
- 미래까지 이 조건을 만족하는 자연키는 찾기 어렵다. 대리키를 사용하는것이 권장 된다.
- 예를 들어 주민등록번호도 기본 키로 적절하지 않다.
- 권장: Long형 + 대체 키 + 키 생성전략 사용

### 데이터 중심 설계의 문제점

- 현재 방식은 객체 설계를 테이블 설계에 맞춘 방식(jpashop - 연관관계 매핑)
- 테이블 외래키를 객체에 그대로 가져옴
- 객체 그래프 탐색이 불가능
- 참조가 없으므로 UML도 잘못됨

### 연관관계 매핑

- 객체와 테이블 연관관계 차이를 이해
- 객체의 참조와 테이블 외래 키를 매핑
- 용어 이해
  - 방향(Direction): 단방향, 양방향
  - 다중성(Multiplicity): 다대일(N:1), 일대다(1:N), 일대일(1:1), 다대다(N:M) 이해
  - 연관관계의 주인(Owner): 객체 양방향 연관관계는 관리 주인이 필요

#### 객체를 테이블에 맞추어 데이터 중심으로 모델링하면, 협력 관계를 만들 수 없다.

- **테이블은 외래 키로 조인을 사용해서** 연관된 테이블을 찾는다.
- **객체는 참조를 사용해서** 연관된 객체를 찾는다.
- 테이블과 객체 사이에는 이런 큰 간격이 있다.

~~~java
연관관계 매핑
// 맴버 입장에서는 맴버가 N이고 팀이 1이기 때문에 해당 어노테이션을 사용한다. Member(N) : Team(1)
	@ManyToOne
  @JoinColumn(name = "TEAM_ID")
  //외래키를 team_id로 지정
  private Team team;

//DB에 100번이 있다 가정
//member에 새로운 team을 할당하면 알아서 update 쿼리가 전송된다.
//dirtyChecking으로 인해 자동으로 team_id가 update 됨
	Team newTeam = em.find(Team.class, 100L);
  findMember.setTeam(newTeam);
~~~

### 양방향 연관관계와 연관관계의 주인

#### 양방향 매핑

객체 <-> 객체

Member(Team team) <-> Team(List members)

상단과 같이 객체를 양방향으로 매핑한다.



#### 연관관계의 주인과 mappedBy

##### 객체와 테이블이 관례를 맺는 차이

- 객체 연관관계 = 2개
  - 회원 -> 팀 연관관계 1개(단방향)
  - 팀 -> 회원 연관관계 1개(단방향)
- 테이블 연관관계 = 1개
  - 회원 <-> 팀의 연관관계 1개 (양방향)



#### 객체의 양방향 관계

- 객체의 **양방향 관계는 사실상 양방향 관계가 아니라 서로 다른 단방향 관계가 2개인 것이다.**

- 객체를 양방향으로 참조하려면 단방향 연관관계를 2개 만들어야 한다.

  ~~~java
  class A{
  	B b;//연관관계 매핑 1개 
  }
  
  class B{
  	A a;//연관관계 매핑 1개
  }
  
  A -> B
  B -> A
  ~~~



#### 테이블의 양방향 연관관계

- 테이블은 외래키 하나로 두 테이블의 연관관계를 관리

- MEMBER.TEAM_ID 외래 키 하나로 양방향 연관관계 가짐

  (양쪽으로 조인할 수 있다.)



### 연관관계의 주인(Owner)

#### 양방향 매핑 규칙

- 객체의 두 관계중 하나를 연관관계의 주인으로 지정
- **연관관계의 주인만이 외래 키를 관리(등록, 수정) - Table 설계상에서도 외래키가 있는 객체**
- **주인이 아닌쪽은 읽기만 가능**
- 주인은 mappedBy 속성 사용X
- 주인이 아니면 mappedBy 속성으로 자신의 주인 지정

#### 주의점

##### 양방향 매핑시 연관관계의 주인에 값을 입력해야한다!

(순수한 객체 관계를 고려하면 항상 양쪽다 값을 입력해야한다.)

- 그렇지 않으면 외래키에 Null이 들어간다.
- 1차 캐싱 될 때, 주인이 아닌 객체에는 주인 객체의 참조가 안들어가기 때문에 같은 구문해서 참조를 할 시 아무값도 안나오게 된다.

##### 연관관계 편의 메소드를 생성하자

~~~java
@Entity
Member{
  @ManyToOne
  Team team

  //연관관계 편의 메소드
  public void changeTeam(Team team){
    this.team = team;
    team.getMembers().add(this);
  }
}
~~~

##### 양방향 매핑시에 무한 루프를 조심하자

- 예: toString(), lombok, JSON 생성 라이브러리
  - **실무에서는 왠만하면 Entity를 반환하지말고, Dto에 담아서 반환하자**

#### 양방향 매핑정리

- **단방향 매핑만으로도 이미 연관관계 매핑은 완료**

- 양방향 매핑은 반대 방향으로 조회(객체 그래프 탐색) 기능이 추가된 것 뿐

- JPQL에서 역방향으로 탐색할 일이 많음

- 단방향 매핑을 잘 해놓으면 양방향은 필요할 때 추가해도 됨

  (테이블에 영향을 주지 않음)

##### 연관관계의 주인을 정하는 기준

- 비즈니스 로직을 기준으로 연관관계의 주인을 선택하면 안됨
- **연관관계의 주인은 외래 키의 위치를 기준으로 정해야함**

### 다양한 연관관계 매핑

#### 연관관계 매핑시 고려사항 3가지

- 다중성
- 단방향, 양방향
- 연관관계의 주인



#### 다중성

Data 베이스 관점에서 다중성을 고려해야함.

- 다대일: @ManyToOne
- 일대다: @OneToMany
- 일대일: @OneToOne
- 다대다: @ManToMany



#### 단방향, 양방향

- 테이블
  - 외래키 하나로 양쪽 조인 가능
  - 사싱 방향이라는 개념 없음
- 객체
  - 참조용 필드가 있는 쪽으로만 참조 가능
  - 한쪽만 참조하면 단방향
  - 양쪽이 서로 참조화면 양방향



#### 연관관계의 주인

- 테이블은 **외래 키 하나**로 두 테이블이 연관관계를 맺음
- 객체 양방향 관계는 A->B, B->A 처럼 참조가 2군데로 각각 상대 객체를 참조한다.
- 객체 양방향 관계는 참조가 2군데 있음, 둘중 테이블의 외래키를 관리할 곳을 지정해야함.
- 연관관계의 주인: 외래 키를 관리하는 참조
- 주인의 반대편: 외래 키에 영향을 주지 않음, 단순 조회만 가능



### 다대일 [N:1]

- 가장 많이 사용하는 연관관계
- **다대일**의 반대는 **일대다**

#### 

#### 다대일 양방향 정리

- 외래 키가 있는 쪽이 연관관계의 주인
- 양쪽을 서로 참조하도록 개발

### 일대다 [1:N]

#### 일대다 단방향 정리

- 일대다 단방향은 일대다(1:N)에서 일(1)이 연관관계의 주인이다.
- 테이블 일대다 관계는 항상 다(N) 쪽에 외래 키가 있다.
- 객체와 테이블의 차이 때문에 반대편 태이블의 외래 키를 관리하는 특이한 구조이다.
- @JoinColumn을 꼭 사용해야 함. 그렇지 않으면 조인 테이블 방식을 사용한다.(중간에 테이블 하나를 추가함)

```
|Tabel1|      |Join Table|      |Tabel2|
|T1_id |  <-  |   T1_id  |      |      |
              |   T2_id  |  ->  |T2_id |
```



#### 일대다 단방향 매핑의 단점

- 엔티티가 관리하는 외래키가 다른 테이블에 있음
- 연관관계 관리를 위해 추가로 Update SQL 실행



##### 일대다 단방향 매핑보다는 다대일 양방향 매핑을 사용하자
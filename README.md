
<p align="middle" >
  <img width="200px;" src="https://user-images.githubusercontent.com/60593969/224698214-0b3215cc-d87a-453b-bcb6-08f64b8741a1.png"/>
</p>
<h1 align="middle">쿠링</h1>

<div align="middle" >

  [![Hits](https://hits.seeyoufarm.com/api/count/incr/badge.svg?url=https%3A%2F%2Fgithub.com%2FKU-Stacks%2Fku-ring-backend-web&count_bg=%2379C83D&title_bg=%23555555&icon=&icon_color=%23E7E7E7&title=hits&edge_flat=false)](https://hits.seeyoufarm.com)

</div>

<p align="middle">건국대학교의 공지는 우리가 책임진다!</p>

<div align="center">
    <a href="https://apps.apple.com/KR/app/id1609873520?mt=8">
        <img src="https://img.shields.io/badge/Apple Store-f3f3f3?style=flat&logo=apple&logoColor=black">
    </a>
    <a href="https://play.google.com/store/apps/details?id=com.ku_stacks.ku_ring">
        <img src="https://img.shields.io/badge/Google Store-90c8ff?style=flat&logo=Google&logoColor=white">
    </a>
    <a href="https://sonarcloud.io/project/overview?id=KU-Stacks_ku-ring-backend-web">
        <img src="https://sonarcloud.io/api/project_badges/measure?project=KU-Stacks_ku-ring-backend-web&metric=coverage"/>
    </a>
    <a href="https://sonarcloud.io/project/overview?id=KU-Stacks_ku-ring-backend-web">
        <img src="https://sonarcloud.io/api/project_badges/measure?project=KU-Stacks_ku-ring-backend-web&metric=alert_status"/>
    </a>
</div>

<p align="center">
 <img src="https://github.com/ku-ring/ios-app/assets/53814741/73aae511-c6eb-4160-b666-2fafc7514c8b"/>

[//]: # ( <img src="https://user-images.githubusercontent.com/53814741/163469327-98af5c02-efc7-4c3e-8fec-9195ca6805ad.JPG" width="30%"/>)

[//]: # ( <img src="https://user-images.githubusercontent.com/53814741/163469357-aed6a78a-4b65-4a9a-bead-d541e7eee702.JPG" width="30%"/>)

[//]: # ( <img src="https://user-images.githubusercontent.com/53814741/163469345-503b6b50-b240-4c8d-9656-c719a5f3d9f2.JPG" width="30%"/>)
</p>

## 💌 프로젝트 소개
혹시?,  
필요한 건국대학교의 공지를 확인하지 못하여 곤란하신적이 있으신가요?   
종종 놓치곤 했던 교환학생 공지사항, 장학금 신청 등... 이제 걱정하지 마세요.   
우리 대학 공지사항, 쿠링이 알려드립니다!
<br>

## 👩‍👦‍👦 Backend Members
|shine|ngwoon|
|:-:|:-:|
|<img src="https://avatars.githubusercontent.com/u/60593969?v=4" alt="shine" width="100" height="100">|<img src="https://avatars.githubusercontent.com/ngwoon?v=4" alt="ngwoon" width="100" height="100">|
|[zbqmgldjfh](https://github.com/zbqmgldjfh)|[ngwoon](https://github.com/ngwoon)|
<br>    

## 🧐 Pain Point
<details>
   <summary> 본문 확인 (👈 Click)</summary>
<br />

기존의 학교 공지를 확인하기 위해서는 앱이 아닌 홈페이지로 접근 → 해당 학과로 이동 → 학과 내의 공지함 접속    
과같이 불필요한 step을 진행해야만 공지를 확인할 수 있었으며, 노트북이 없다면 공지를 확인하기가 매우 불편 했습니다.

이러한 불편함을 없애고, 모든 건국대 학생들에게 편리한 공지를 제공하기 위해 만든 서비스입니다.    
비록 아직도 많이 부족하지만, 잘 사용하고 있다는 피드백을 받을때의 뿌듯함을 원동력으로 개선해나가는 중 입니다.

</details>

## 📑 EventStorming <a name = "outline"></a>
<details>
   <summary> 본문 확인 (👈 Click)</summary>
<br />
도메인 모델 분리를 위한 Event Storming (4일 소요) <br>
이벤트 스토밍 도중 추가되는 이벤트도 충분하게 발생할 수 있으며, 지금의 선택이 100% 모두가 동의 가능한 모델 분리는 아닐 수 있다.

### 2-2-1) ****Event (Orange Sticker)****
![이벤트](https://github.com/ku-ring/ku-ring-backend-web/assets/60593969/80eb6138-a98b-415e-83e2-3eb0dcc709e4)
우선 시간의 흐름에 따라 비지니스의 상태 변경을 의미하는 도메인 이벤트를 도출해보았습니다.     

팀원들과 함께 오렌지색 포스트잇에 이벤트 명을 작성하였는데, 이떄 **이벤트 명은 과거명으로 작성한다.** 라는 내용을 준수하려 노력하였습니다.  

이벤트간의 공간을 두되 이벤트가 연쇄적으로 발행하는 경우 바로 옆에 붙인다. 같은 시점에 비지니스 조건에 따라 대체적으로 발생될 수 있는 이벤트는 아래에 같은 라인선상으로 붙인다.  

도메인 이벤트는 비지니스의 어떤 상태를 생성,변경,삭제하는 요소라 할 수 있습니다. 따라서 시스템의 화면을 연상하지 말고 비지니스가 흘러감에 따라 비지니스를 구성하는 요소들의 상태가 어떻게 변경되는지를 생각하도록 하였습니다.    

우리 팀은 시스템을 사용하는 역할(사용자, 디자이너, 개발자)로 나누어서 사람들을 배정하여 업무별로 논의가 가능한 수준인 적절한 인원이(4명) 참여하였습니다.


### 2-2-2) ****Command (Blue Sticker)****
![커멘드](https://github.com/ku-ring/ku-ring-backend-web/assets/60593969/722eff4b-b082-4935-a2ca-5c0e7621f0a7)    
이벤트를 트리거하는(발생시키는) 커맨드를 도출하였으며 위 사진과 같습니다.

커맨드의 경우 파란색 포스트잇에 작성하여 붙였으며, 커맨드는 이벤트를 보면 쉽게 유추할 수 있다고 생각됩니다.     
**하나의 커맨드에 의해 여러개의 이벤트가 연속 발생될 수 있으며 커맨드 하나에 조건에 따라 다른 이벤트가 발생할 수 있음을 유의하자!**

> *[이벤트] 할 때는 항상 [커맨드] 한다*

### 2-2-3) ****Actor (Yellow Sticker)****
![엑터](https://github.com/ku-ring/ku-ring-backend-web/assets/60593969/b764d079-0be7-48f2-abfb-811cb9c75827)    
엑터는 사람이나 조직이 될 수 있는데 역할 관점으로 도출해보았습니다. 엑터는 추상적으로 식별하지 말고 **비지니스를 수행하는 구체적인 역할로 고려하여 도출한다.**

즉 그냥 모든 업무에서 보편적으로 사용되는 회원, 관리자로 뽑지 말고 특정 비지니스를 실제적으로 수행하는 역할자를 도출하려고 노력하였으, 액터가 구체화 될 수록 식별하지 못한 커맨드와 이벤트가 추가적으로 도출 될 수 있습니다.    

우리의 서비스는 엑터는 학교에 다니는 학생, 교직원 (공지앱 사용자) 정도로 생각하였습니다. 엑터를 도출하고 보니, 여지껏 교직원을 위한 기능은 없었음을 한번 상기하게 되었습니다.  

### 2-2-4) ****Aggregate (Yellow Sticker)****
![어그리게이트](https://github.com/ku-ring/ku-ring-backend-web/assets/60593969/f1bb7fc4-a216-4aba-a5f5-c580b751f826)
어그리게이트는 ‘결합물’을 의미하는데 어떠한 도메인 객체를 중심으로 생각하였으며,    
하나의 ACID한 트랜잭션에 묶여 변화되어야 할 객체의 묶음을 도출하고, 그것들을 커맨드, 이벤트와 함께 묶어보았습니다.

### 2-2-5) ****바운디드 컨텍스트로 묶기****
![바운디드컨텍스트](https://github.com/ku-ring/ku-ring-backend-web/assets/60593969/e00f53cf-643b-49d5-bf86-22c1f1e606e6)
Bounded Context(BC)는 동일한 문맥으로 효율적으로 업무 용어(도메인 클래스)를 사용할 수 있는 객체 범위를 뜻한다고 합니다.    
하나의 BC는 하나 이상의 어그리게이트를 원소로 구성될 수 있으며, 이 BC를 마이크로서비스 구성 단위로 정하게 되면 이를 담당하는 팀 내의 커뮤니케이션이 효율화 될것 같습니다?   

추후 MSA화를 고려중이기에 함께 고려해본 대상입니다. 

### 2-2-6) 외부 시스템 추가
![외부](https://github.com/ku-ring/ku-ring-backend-web/assets/60593969/03580d1f-6ef6-40fa-9326-fc7d080000be)
커맨드 & 이벤트 발생 시 호출되거나 관련되는 레거시 시스템이나 외부 시스템 또는 장비를 도출하여 핑크색 포스트잇에 작성하여 이벤트의 오른쪽 상단에 추가!     

본 시스템의 구현 대상이 아니지만 시스템의 기능 구현을 위해 연계가 필요한 시스템들을 모두 도출해본 결과, FCM의 알림 기능 정도가 외부 의존으로 추가되었습니다.    

### 2-2-7) ****Policy (Lilac Sticker)**** 도출
![정책](https://github.com/ku-ring/ku-ring-backend-web/assets/60593969/219a2245-d4a9-444a-b8b1-7bf0f2d435a2)    
폴리시(Policy)는 이벤트가 발생한 후 연이어 발생하는 반응형 액션으로, **한 서비스 이벤트에 대해 수행되어야 할 타 서비스의 액션들로**, 먼저 정의된 이벤트 아래에 추가하였습니다.     
하나의 이벤트에 반응하여 수행되어야 할 폴리시는 여러 팀에서 도출된 멀티 액션이 존재할 수 있다.

### 2-2-8) **폴리시의 이동과 컨텍스트 매핑 (점선은 Pub/Sub, 실선은 Req/Resp)**
![최종](https://github.com/ku-ring/ku-ring-backend-web/assets/60593969/d3e28458-f726-40bc-9a1e-01ca8329a295)
위와 같이 최종적으로 도메인 모델을 분리하게 되었습니다!!

</details>


# 🚀 리팩토링 & 성능 개선
<details>
   <summary> 본문 확인 (👈 Click)</summary>

---

## 1. 의미있는 이름과 함수

코드를 다시 되돌아보았을 때, 당시에는 이해할 수 있을 정도의 이름으로 지었다고 생각했으나 명확하게 와닿지 않는 네이밍들이 있었습니다.  
따라서 주석이 필요 없을 정도로 명확하게 변수명과 함수명을 수정하였습니다. 
함수에 대해서는 Clean Code에서 5줄 이내를 권장하고 있었습니다. 코드를 되돌아본 결과 생각보다 함수가 긴 것들이 존재했고 충분히 줄일 수 있는 수준의 내용들이었기에 할 수 있는 한에서 5줄 내외를 지키도록 수정했습니다.

---

## 2. 헥사고날 아키택처로의 전환

**문제 상황**

- 기존 아키택처의 한계점 ([데이터 중심의 설계가 불러온 한계](https://blogshine.tistory.com/688))
- 정확한 기준 없이 그때 그때 달라지는 페키지 구조와 네이밍 방식
- 확장에 대한 고려가 없는 기존의 설계

**문제 해결**

- 기존의 아키텍처를 헥사고날 아키텍처로 전환하면서 이를 극복

`유연성, 유지보수성` : 외부 시스템이나 인프라와의 의존성을 낮추어, 구성 요소를 쉽게 교체하거나 업데이트할 수 있게 되었습니다.     
                    그도 그럴 것이 application의 service들은 모두 인터페이스에 해당되는 port에 의존하게 되었습니다.     
                    더 이상 실 구현체가 아니기 때문에 중간에 다른 구현채로 변경되어도 유연하게 대응할 수 있는 장점을 갖게 되었죠! 이는 곧 유지보수성과도 직결된다 생각되더라고요!    

`테스트 용이성` : 비즈니스 로직을 독립적으로 테스트할 수 있어 품질 향상과 개발 속도 향상에 도움이 됩니다!    
                인터페이스를 적절하게 사용하였기에 해당 로직의 독립성을 유지할 수 있던 점이 매우 장점이 돼준 것 같습니다.   

`팀원과의 협업` : 책임이 분리되어 있어, 코드의 이해와 수정이 용이하며, 변화에 빠르게 대응할 수 있습니다.   
                즉, 흔하게 말하는 SOLID가 모두 지켜지고 있는 좋은 아키텍처 구조입니다.    
                또한 제2의 멤버가 들어와서 유지보수를 하거나 개편해야 해도 HexagonalArchitecture 자체의 이해도만 있다면 충분히 빠른 적응이 가능하다 생각됩니다.    

**상세 내용 링크 : ([글 링크](https://blogshine.tistory.com/689))**

---

## 3. 레거시 코드의 양방향 연관관계를 단방향으로!
JPA에 대해서는 서로 어느 정도 이해하고 있어, 적절한 fetch join을 사용하여 코딩했었기에 N+1 문제는 발생하지 않았습니다.
하지만 연관관계에 대해서 문제가 있었습니다.
가장 좋은 연관관계 설계는 단방향을 기초로 하되 필요하면 양방향 설계를 하는 것입니다.    

JPA 프로그래밍의 저자, 김영한 선생님의 의견을 빌리자면 다음과 같습니다.
> 양방향으로 하면 복잡도가 높아지는 단점이 있지만 성능상 이점을 얻을 수 있습니다.
정말 성능이 너무 중요해서 쿼리 하나를 줄이는게 꼭 필요한 상황이라면 복잡해지더라도 최적화를 해야합니다.
반면에 쿼리가 하나 더 나가더라도 시스템 자원이 충분해서 성능에 영향을 미치는 것이 미미하다면 코드 복잡도를 낮게 유지하는 것이 더 중요합니다.

<div align="center">
 <img width="550" src="https://github.com/ku-ring/ku-ring-backend-web/assets/60593969/787c8dfb-1de5-4524-a759-26706df9cc6f" alt="kanban">
 <img width="550" src="https://github.com/ku-ring/ku-ring-backend-web/assets/60593969/da4ed880-516f-4596-bc7f-2c101ac5e7fe" alt="kanban">
</div>

둘간에 양방향으로 연관관계를 갖고 있다.    
그럼 UserCategory에 추가해줄때, User쪽의 `private List<UserCategory> userCategories = new ArrayList<>();` 에도 UserCategory를 추가하고,  UserCategory 쪽에서도 User를 추가해야 한다!!    
이는 양방향으로 매핑중이기 때문이다.

당연히 연관관계 주인이 UserCategory 이기 때문에 UserCategory에 추가하는적은 적합하다.    
하지만 다음 코드는 UserCategory쪽에만 추가중이다. 흔히 말하는 JPA 편의 메서드를 작성했어야 헀는데, 다음 코드는 편의 메서드가 없다.  
<img width="600" alt="Untitled (3)" src="https://github.com/ku-ring/ku-ring-backend-web/assets/60593969/4069ce0e-8ee1-413b-ae57-fe78278bb06f">

따라서 UserCategory에는 신규 UserCategory가 추가 되지만, User가 들고 있는 List는 비어 있게 된다…    

다음 글의 3번 “양방향 연관관계 주의점”을 보면 이해할 수 있다.
https://blogshine.tistory.com/345    

아예 User에서 UserCategory를 삭제하는 편이 더 좋을것 같다. 양방향 연관관계가 필수적인 포인트가 아니기 때문이다! → **User에서 제거!**     

<img width="483" alt="image" src="https://github.com/ku-ring/ku-ring-backend-web/assets/60593969/74154306-43ad-42d3-a523-7803823b9227">    

테이블 구조에 변화는 없기에 적용가능

---

## 4. 공지 Scrap작업 multi-threading 처리로 시간 개선하기

**문제 상황**

- 기존의 60개의 학과를 전부 Scrap하는데 단일 thread로는 너무 오래 걸리는 문제 발생

**문제 해결**

- 60개 학과의 공지를 scrap하는 과정을 병렬처리 하면서 **Crawling 성능개선**
    - 단일 공지 페이지 기준, core 6개 : 3분50초 → 49초로 성능을 **4.39**배 개선
    - 전체 공지 페이지 기준, core 6개 : 1시간 12분 11초 → 15분 35초로 성능을 **4.70**배 개선

**상세 내용 링크 : ([글 링크](https://blogshine.tistory.com/660))**

---

## 5. Full-Text-Index도입을 통한 **검색 성능개선**

**문제 상황**

- 기존 검색은 like 절을 활용한 단순 full-scan방식의 검색이라는 단점

**문제 해결**

- 공지 검색 쿼리 변경, 중지단어, Full-Text-Index도입을 통한 **검색 성능개선**
    - 개선 **전** : 검색은 full Scan을 통해 실행계획이 수립 → 11s 59ms
    - 개선 **후** : Full Text index과 stop word 도입 → 591ms
    - 공지 500만건 기준 검색 속도 **18.71배 개선** 하게 되었습니다

**상세 내용 링크 : ([글 링크](https://blogshine.tistory.com/664))**

---

## 6. HeapDump를 통해 메모리 누수 원인 찾기 **검색 성능개선**

**문제 상황**

- 애플리케이션에 물리적으로 할당된 메모리를 넘어, swap 메모리까지 사용하고 있는 문제가 발생

**문제 해결**

- 매번 URL 검증을 위한 객체를 생성후 검사 하는것이 아닌, 한번 Compiled된 Pattern 사용을 통한 **메모리 낭비 해결**
  - 개선 **전** : 전체 512MB중 170MB가 eden space에 주기적으로 생성 → 32%
  - 개선 **후** : 전체 512MB중 47MB만 생성되도록 개선 → 9%
  - 미리 compiled된 Pattern 객체를 활용하여 메모리 누수를 해결하였습니다.

**상세 내용 링크 : ([글 링크](https://blogshine.tistory.com/687))**

---

## 7. Bulk Query를 통한 성능 개선

**문제 상황**

- 공지를 주기적으로 저장하고 삭제하는 과정이 하루에도 수십번 반복하는 이 앱의 핵심 로직중 하나입니다.
- 문제는 이 과정에서 발생하는 쿼리가 너무 많다는 것입니다.
  쿼리로그를 찍어본 결과 save와 delete 모두 한방 쿼리가 아니라 여러번의 쿼리가 나가는 것을 확인했습니다.

**문제 해결**
<div align="center">
 <img width="600" src="https://github.com/ku-ring/ku-ring-backend-web/assets/60593969/c6b5afc6-06f3-4410-88ee-de45cc3930b8" alt="refac-bulk-solution">
</div>

다음과 같이 수정하여 해결할 수 있었습니다.
+ Insert의 경우 : JdbcTemplate.batchUpdate() 사용
+ delete의 경우 : queryDsl의 in 쿼리 사용

<br>

### 7-1) Insert 해결책

해결책은 2가지가 존재했습니다.
1. Table Id strategy를 SEQUENCE로 변경하고 Batch 작업
2. JdbcTemplate.batchUpdate() 사용

MySQL과 MariaDB의 Table Id 전략은 대부분이 IDENTITY 전략을 사용하기도 하고, 저희는 이미 Id 전략을 IDENTITY 전략으로 사용하고 있었기에 Id전략 자체를 변경하기에는 무리가 있었습니다.
또한, Jdbc를 사용하는 것이 성능상 더 뛰어나다는 결과를 확인했습니다. [출처](https://homoefficio.github.io/2020/01/25/Spring-Data에서-Batch-Insert-최적화/#)

<div align="center">
 <img width="700" src="https://github.com/ku-ring/ku-ring-backend-web/assets/60593969/5b47dde5-2b77-433d-8b23-5377f18c6532" alt="batch-bulk-solution">
</div>

<br>

### 7-2) Delete 해결책
이미 프로젝트에서 queryDsl를 사용하고 있어 이를 이용하는 것이 가장 간단했기 때문에 queryDsl의 delete in 쿼리를 사용하여 해결했습니다.

**상세 내용 링크 : ([글 링크](https://blogshine.tistory.com/686))**

---

## 8. 인증, 인가를 비즈니스 로직으로부터 분리하기

**문제 상황**

- 부가적인 인증, 인가 로직이 애플리케이션의 비즈니스 로직과 함께 혼재되어 있는 상태

**문제 해결**

<img width="750" alt="image" src="https://github.com/ku-ring/ku-ring-backend-web/assets/60593969/ed34a293-c58b-4332-adf3-e6156d441bc4">

- 인증, 인가를 핵심 비즈니스로직 으로 부터 분리하여 리팩토링 하기
    - Spring Security와 유사 구조를 직접 구현
- 직접 구현한 전체 인증 로직 흐름도
    
**상세 내용 링크 : ([글 링크](https://blogshine.tistory.com/678))**

---

## 9. 흔하디 흔한 N+1 쿼리 개선기

원래 로직에서는 사용자의 Category 이름 목록을 가져오기 위해서 다음과 같이 처리가 되고 잇었습니다!

![Untitled (2)](https://github.com/ku-ring/ku-ring-backend-web/assets/60593969/36dbdd3c-af5e-47ec-8e25-f2b0792f0486)

getUserCategories 는 다음과 같고,

```java
public List<Category> getUserCategories(String token) {
    User user = userRepository.findByToken(token);

    // User에서 이미 연관관계로 들고 있는 상황이었다.
    List<UserCategory> userCategories = userCategoryRepository.findAllByUser(user); 

    return userCategories.stream()
            .map(UserCategory::getCategory)
            .collect(Collectors.toList());
}
```

위의 기존 코드에서 user를 찾아올때 사실 UserCategory를 EAGER로 찾아오고 있었다.
![Untitled (1)](https://github.com/ku-ring/ku-ring-backend-web/assets/60593969/ee224a35-a1e6-4d9f-9b6d-3ccfef43a24d)    

따라서 다음과 같이 변경 했어도 됬을것 같다!

```java
List<UserCategory> userCategories = user.getUserCategories(); // 이미 User는 UserCategory 정보가 있음
```

즉, User를 찾아올때 이미 Category를 찾아오는 쿼리가 나갔는데, findAllByUser로 찾아올 필요가 없었다.

(ps, 다행이 1차 캐시 덕분에 영속성 컨텍스트에서 찾아오기에 동일한 쿼리가 2번 나가지는 않는다)

getCategoryNamesFromCategories 는 다음과 같이 구현되어 있었다!

```java
public List<String> getCategoryNamesFromCategories(List<Category> categories) {
    return categories.stream()
            .map(Category::getName)
            .collect(Collectors.toList());
}
```

이렇게 처리되어 사용자의 Category이름을 전부 보여주고 있었다!

즉, User 하나만 조회해도 UserCategory까지 함께 조회하고 있으니~

1) token값을 갖는 User를 찾아온 후 // 쿼리 1번

2) User의 UserCategory를 다 찾아온다. // UserCategory를 바로 찾아오는 쿼리 N개 (N+1문제)

3) 이후 찾아온 UserCategory 목록을 통해 Category 목록을 찾는다. // Category N개만큼 쿼리

4) 찾아온 Category 목록을 String 이름로 바꾼다.

쿼리가 총 1 + 2N 만큼 발생중이다.

### 9 - 1) 변경 전 쿼리

```bash
Hibernate: 
    select
        user0_.id as id1_5_,
        user0_.token as token2_5_ 
    from
        user user0_ 
    where
        user0_.token=?
2023-01-25 21:19:36.747  INFO 16011 --- [o-auto-1-exec-5] p6spy                                    : #1674649176747 | took 1ms | statement | connection 22| url jdbc:mariadb://localhost:52131/test
select user0_.id as id1_5_, user0_.token as token2_5_ from user user0_ where user0_.token=?
select user0_.id as id1_5_, user0_.token as token2_5_ from user user0_ where user0_.token='test_fcm_token';
Hibernate: 
    select
        usercatego0_.user_token as user_tok3_6_0_,
        usercatego0_.id as id1_6_0_,
        usercatego0_.id as id1_6_1_,
        usercatego0_.category_name as category2_6_1_,
        usercatego0_.user_token as user_tok3_6_1_ 
    from
        user_category usercatego0_ 
    where
        usercatego0_.user_token=?
2023-01-25 21:19:36.749  INFO 16011 --- [o-auto-1-exec-5] p6spy                                    : #1674649176749 | took 1ms | statement | connection 22| url jdbc:mariadb://localhost:52131/test
select usercatego0_.user_token as user_tok3_6_0_, usercatego0_.id as id1_6_0_, usercatego0_.id as id1_6_1_, usercatego0_.category_name as category2_6_1_, usercatego0_.user_token as user_tok3_6_1_ from user_category usercatego0_ where usercatego0_.user_token=?
select usercatego0_.user_token as user_tok3_6_0_, usercatego0_.id as id1_6_0_, usercatego0_.id as id1_6_1_, usercatego0_.category_name as category2_6_1_, usercatego0_.user_token as user_tok3_6_1_ from user_category usercatego0_ where usercatego0_.user_token='test_fcm_token';
Hibernate: 
    select
        user0_.id as id1_5_1_,
        user0_.token as token2_5_1_,
        usercatego1_.user_token as user_tok3_6_3_,
        usercatego1_.id as id1_6_3_,
        usercatego1_.id as id1_6_0_,
        usercatego1_.category_name as category2_6_0_,
        usercatego1_.user_token as user_tok3_6_0_ 
    from
        user user0_ 
    left outer join
        user_category usercatego1_ 
            on user0_.token=usercatego1_.user_token 
    where
        user0_.token=?
2023-01-25 21:19:36.755  INFO 16011 --- [o-auto-1-exec-5] p6spy                                    : #1674649176755 | took 1ms | statement | connection 22| url jdbc:mariadb://localhost:52131/test
select user0_.id as id1_5_1_, user0_.token as token2_5_1_, usercatego1_.user_token as user_tok3_6_3_, usercatego1_.id as id1_6_3_, usercatego1_.id as id1_6_0_, usercatego1_.category_name as category2_6_0_, usercatego1_.user_token as user_tok3_6_0_ from user user0_ left outer join user_category usercatego1_ on user0_.token=usercatego1_.user_token where user0_.token=?
select user0_.id as id1_5_1_, user0_.token as token2_5_1_, usercatego1_.user_token as user_tok3_6_3_, usercatego1_.id as id1_6_3_, usercatego1_.id as id1_6_0_, usercatego1_.category_name as category2_6_0_, usercatego1_.user_token as user_tok3_6_0_ from user user0_ left outer join user_category usercatego1_ on user0_.token=usercatego1_.user_token where user0_.token='test_fcm_token';
HTTP/1.1 200 
Content-Type: application/json
Transfer-Encoding: chunked
Date: Wed, 25 Jan 2023 12:19:36 GMT
Keep-Alive: timeout=60
Connection: keep-alive
```

N+1 문제로 User한번 조회하는데 위와 같이 쿼리가 3번 나가게 됨

### 9 - 2) 변경 후

변경 후 한방 쿼리로 조회 끝    
```java
public List<String> getUserCategoryNamesByToken(String token) {
    return queryFactory
            .select(userCategory.category.name)
            .from(userCategory)
            .where(userCategory.user.token.eq(token))
            .fetch();
}
```

<img width="750" src="https://github.com/ku-ring/ku-ring-backend-web/assets/60593969/e3915da9-5d30-4b12-b80c-e4ffc18686c4">

___

## 10. Test Container를 통한 테스트의 멱등성 보장하기
테스트와, 실제 운영 DB를 둘다 MariaDB 환경으로 사용하여 문제가 발생할 일이 없다 생각했었습니다.
하지만, utf8과 같은 인코딩 방식이 로컬과 프로덕션이 달라 문제가 발생하였으며, 이또한 테스트 환경에서 걸러내지 못한 것이 문제라 생각하였습니다.

따라서 이후부터는 환경 자체를 동일하게 만든 Test 컨테이너를 사용하여 프로덕션 환경과 동일환 환경변수 환경에서 테스트를 진행하도록 하였습니다.    
테스트에서 수행 시간이 조금 길어졌지만, 테스트의 목적에 부합하도록 동작하게 되었습니다.

---

## 11. CI / 정적분석기(SonarCloud, jacoco)를 사용한 코드 컨벤션에 대한 코드리뷰 자동화

**문제 상황**

- 정적 분석 도구가 없어 코드의 품질 관리나, 버그 발견등이 어려웠던 상황

**문제 해결**

<img width="465" alt="image" src="https://github.com/ku-ring/ku-ring-backend-web/assets/60593969/7c7b3c42-b434-4f07-bce4-2de05be7e80e">

- 정적 분석 도구를 통한 분석을 통하여 사전에 미리 문제가 발생할 수 있는 지점들을 보완할 수 있음
- 테스트 코드의 커버리지에 대한 관리가 편리하다 생

**상세 내용 링크 : ([글 링크](https://blogshine.tistory.com/658))**

---

## 12. 서버 모니터링

**문제 상황**

- 이전에는 모니터링 도구가 없어 실시간으로 서버의 상태를 확인할 수 없었다.
- 서버의 상태를 확인하기 위해 서버에 접속하여 로그를 확인해야 했다.

**문제 해결**

<img width="800" alt="image" src="https://github.com/ku-ring/ku-ring-backend-web/assets/60593969/4e855b82-60b3-4800-bf02-37bff544aa0d">

- 서버의 상태를 실시간으로 확인할 수 있어 서버의 상태를 빠르게 파악할 수 있다.
- 일정 기간동안의 서버 정보가 저장되어 있기 때문에 문제 발생시 역추적 해볼 수 있다.
- 다음 글 링크에서는 이를 통하여 서버가 다운되는 문제를 해결한 글 이다!

**상세 내용 링크 : ([글 링크](https://blogshine.tistory.com/669))**

---

<br>
</details>

# 💎 왜 이 기술을 사용했는가?
<details>
   <summary> 본문 확인 (👈 Click)</summary>

----

## 1. Querydsl
<img width="550" src="https://github.com/ku-ring/ku-ring-backend-web/assets/60593969/2478dc90-16e8-4268-9e5d-608570a23eb6">

Spring Data JPA가 기본적으로 제공해주는 CRUD 메서드 및 쿼리 메서드 기능을 사용하더라도, 원하는 조건의 데이터를 수집하기 위해서는 필연적으로 JPQL을 작성하게 됩니다.
간단한 로직을 작성하는데 큰 문제는 없으나, 복잡한 로직의 경우 개행이 포함된 쿼리 문자열이 상당히 길어집니다.
JPQL 문자열에 오타 혹은 문법적인 오류가 존재하는 경우, 정적 쿼리라면 어플리케이션 로딩 시점에 이를 발견할 수 있으나 그 외는 런타임 시점에서 에러가 발생합니다.
이러한 문제를 해결해 주는 것이 Querydsl이기에 Querydsl을 도입했습니다.
Querydsl 도입으로 다음과 같은 이점을 얻었습니다.

1. 문자가 아닌 코드로 쿼리를 작성함으로써, 컴파일 시점에 문법 오류를 쉽게 확인할 수 있다.
2. 자동 완성 등 IDE의 도움을 받을 수 있다.
3. 동적인 쿼리 작성이 편리하다.
4. 쿼리 작성 시 제약 조건 등을 메서드 추출을 통해 재사용할 수 있다.

----

## 2. Flyway

<img width="300" src="https://github.com/ku-ring/ku-ring-backend-web/assets/60593969/c6df0ca7-48a5-4b32-9e19-4298b9a64313">

dev, local 환경에서는 단순히 ddl을 create-drop 또는 update 옵션을 사용하고 있었기에 DB에 대해 고민할 필요가 없었습니다.   
하지만 운영환경에서는 ddl을 validate 또는 none 옵션을 사용해야하기 때문에 초기에는 DB script를 뽑아서 별도로 관리를 했습니다.   
이후 기능이 추가되면서 script가 변경되는 일이 빈번해졌고, 매번 일일이 스크립트를 관리하는 것이 번거로울 뿐 아니라 실수하기 딱 좋은 부분이라 Flyway를 도입하여 데이터베이스 형상관리를 진행했습니다.

추가로 저의 생각에 이점은
1. 협업시 팀원이 ddl의 코드리뷰를 하기 매우 편하다.
2. 내가 작성한 ddl이 정상적으로 추가된다, 즉 복붙이나 타이핑 하다 나는 휴먼에러 등을 방지할 수 있다.

----

</details>



# ⏰ 협업 방식 - Jira <a name = "jira"></a>

<details>
   <summary> 본문 확인 (👈 Click)</summary>
<br />

저희 쿠링팀은 협업 방식으로 Jira를 사용했습니다.  

구현해야할 큰 기능들을 에픽으로 정의하여 일정을 설정했고 하나의 에픽에 필요한 기능들인 task를 세세하게 나누었습니다.  
칸반보드를 통해 task들을 개발해야할 모든 기능들, 이번주에 개발해야할 기능, 개발 진행중, 개발 완료된 칸으로 옮기면서 한눈에 볼 수 있도록 진행했습니다.  

스프린트는 1주일 단위로 설정하여 Jira 내 Confluence에서 스프린트 주기동안 진행해야할 기능들을 정의하고 마음가짐과 스프린트를 마친 후 회고를 작성하는 방식으로 스프린트를 진행했습니다.

## 로드맵
<div align="center">
 <img src="https://github.com/ku-ring/ku-ring-backend-web/assets/60593969/92e2f5d6-1167-4293-a5fa-dc5f539953d7" alt="kanban">
</div>

## 칸반보드
<div align="center">
 <img src="https://github.com/ku-ring/ku-ring-backend-web/assets/60593969/5a523f6a-8966-494d-b28c-bbf69eb526e4">
</div>

</details>    


## 🔌 Infrastructures
<details>
    <summary> 본문 확인 (👈 Click)</summary>

### CI Flow
<img width="850" alt="스크린샷 2023-05-15 오후 5 08 22" src="https://github.com/ku-ring/ku-ring-backend-web/assets/60593969/492e61fb-b420-42b3-ae10-834a89c50249">

### CD Flow
<img width="850" alt="image" src="https://github.com/ku-ring/ku-ring-backend-web/assets/60593969/9ec6dbff-3560-412a-9bc1-a687b510ac66">

### CD Detail Flow (Test Server)
<img width="850" alt="image" src="https://github.com/ku-ring/ku-ring-backend-web/assets/60593969/47827e3d-2137-43e7-beda-6725ab0c36ff">

과정 정리글
1) [Github Actions, CodeDeploy, Nginx 로 무중단 배포하기 - 1](https://blogshine.tistory.com/427)
2) [Github Actions, CodeDeploy, Nginx 로 무중단 배포하기 - 2](https://blogshine.tistory.com/428)
3) [Github Actions, CodeDeploy, Nginx 로 무중단 배포하기 - 3](https://blogshine.tistory.com/429)
4) [Github Actions, CodeDeploy, Nginx 로 무중단 배포하기 - 4](https://blogshine.tistory.com/430)


</details>

## 문의

[![인스타그램](https://img.shields.io/badge/@kuring.konkuk-e4405f?style=for-the-badge&logo=instagram&logoColor=white)](https://bit.ly/3JyMWMi)
[![이메일](https://img.shields.io/badge/kuring.korea@gmail.com-168de2?style=for-the-badge&logo=gmail&logoColor=white)](mailto:kuring.korea@gmail.com)

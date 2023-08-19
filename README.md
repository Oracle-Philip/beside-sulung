# 술렁술렁

개발 기간: 2023.05.01 ~ 2023.06.20
사용 언어 및 라이브러리: Kakao SDK, Kotlin, MVVM, Okhttp, Paging3, Retrofit, gson, moshi

Playstore : [https://play.google.com/store/apps/details?id=com.rummy.sulung](https://play.google.com/store/apps/details?id=com.rummy.sulung)

프로젝트 구성원: Android 윤상필, BackEnd 2명, 디자이너 2명, 기획자 2명

# Summary

<aside>
💡 음주생활과 감정을 기록하는 술 일기 서비스

</aside>

# Tech Stack

`Kotlin`, `JetPack`, `bindingAdapter`, `DataBinding`, `ViewModel`, `AAC`, `LiveData`, `Navigation Component`, `CustomView`, `Paging3`, `Coroutine`

# Architecture

`MVVM`

# Project Member

- 안드로이드 개발자 1명
- BackEnd 2명
- 기획자 2명

# Part

- mvvm 아키텍처 구현
- 백그라운드 스레드를 이용한 비동기 처리
- Http 클라이언트 개발

# Learned

- 코루틴을 이용한 백그라운드 스레드 처리
- Retrofit, okhttp를 이용한 API 통신 구현 및 cache 기능에 MVVM 아키텍처 구현

# Screenshot

- 스플래쉬 & 로그인 화면
    - 앱 내부저장소에 토큰 유무에 따른 분기처리(로그인 화면 혹은 메인화면 이동)
    - 카카오톡 로그인후 API통신을 통해 API Server로부터 JWT와 사용자정보 데이터를 넘겨 받음
  
      <img src="https://velog.velcdn.com/images/philipy/post/a947184a-7891-487a-af29-f574041f396b/image.jpg" width="40%">
    
      <img src="https://velog.velcdn.com/images/philipy/post/92aff8d9-d400-4285-a91d-5c8aa31d07ad/image.jpg" width="40%">

---

- 이용약관 & 닉네임 설정화면
    - 사용자 정보 API를 통해 초기사용자일 경우 이용약관 및 닉네임 설정 화면으로 이동하게 처리

<img src="https://velog.velcdn.com/images/philipy/post/0f33d513-f315-4ca2-a6c8-4a06dade6598/image.jpg" width="40%">

<img src="https://velog.velcdn.com/images/philipy/post/4827db8a-6f3a-4fa6-acdf-6fa03b98beee/image.jpg" width="40%">

<img src="https://velog.velcdn.com/images/philipy/post/8dee80eb-57be-46ed-9a55-2ce0b376418d/image.jpg" width="40%">

<img src="https://velog.velcdn.com/images/philipy/post/8ba4f921-8ed5-42fb-a19c-3ff4497414be/image.jpg" width="40%">

---

- 초기 화면 (데이터 없는)

<img src="https://velog.velcdn.com/images/philipy/post/31859089-1efa-421d-8473-61e9acadb341/image.jpg" width="40%">

<img src="https://velog.velcdn.com/images/philipy/post/58564c5c-6338-4701-8fd1-2ea3358acf9b/image.jpg" width="40%">

<img src="https://velog.velcdn.com/images/philipy/post/46bf4e6a-dd78-46e1-baf9-07aa2b96494c/image.jpg" width="40%">

---

- 기록하기 화면
    - 술 타입 - 소주, 맥주, 막걸리, 칵테일, 사케, 하이볼, 샴페인, 양주, 와인 중 하나를 선택할 수 있음
    - 감정 - 평온, 우울, 화남, 슬픔, 기쁨, 축하, 취함 감정 중 하나를 선택할 수 있음
    - 날짜 선택 - 안드로이드에서 기본 제공해주는 날짜 선택 기능 사용
    - 날짜와 술타입, 감정을 선택후 기억하기 버튼을 클릭하면 간단등록 API를 호출하여 DB에 데이터가 저장됨

    <div style="display: flex;">
      <img src="https://velog.velcdn.com/images/philipy/post/39c47ea6-da2b-4a70-b459-ad59ecdbe7e1/image.jpg" width="40%">
      <img src="https://velog.velcdn.com/images/philipy/post/d69f48b4-d078-4e4a-8b0d-5e59462c0f55/image.jpg" width="40%; margin-top: 0;">
    </div>

---

- 기록후 화면

  <img src="https://velog.velcdn.com/images/philipy/post/28a2c063-04a4-492e-bb80-1e2169f01460/image.jpg" alt="Image 1" style="width: 40%;">
  
  <img src="https://velog.velcdn.com/images/philipy/post/84aab98c-6959-42f6-a790-4a48fe37d7c1/image.jpg" alt="Image 2" style="width: 40%;">


---

- 술 일기 쓰기 화면
    - 사진 추가 버튼을 누르면 기본 카메라 앱 혹은 기본 갤러리 앱을 통해 이미지를 등록 할 수 있음

      <img src="https://velog.velcdn.com/images/philipy/post/e5f17f7d-6219-4db6-8a22-cbb0bbb7e361/image.jpg" alt="Image 4" style="width: 40%;">
    

---

- 메인화면
    - 술일기 리스트 화면, 술창고 화면, 설정 화면으로 이동 할 수 있는 하단 네비게이션 바
        - 술일기 리스트

          <img src="https://velog.velcdn.com/images/philipy/post/9976126e-a765-4f93-b632-44ef077c4364/image.jpg" alt="Image 4" style="width: 40%;">
          
        - 술창고

          <img src="https://velog.velcdn.com/images/philipy/post/8b5813d0-480e-434c-b851-9c416f6fc3f2/image.jpg" alt="Image 5" style="width: 40%;">
          
          <img src="https://velog.velcdn.com/images/philipy/post/e57b7289-b6aa-4ce6-8cf9-098da20a2d05/image.jpg" alt="Image 6" style="width: 40%;">

        - 설정화면
            
          <img src="https://velog.velcdn.com/images/philipy/post/fbab3d7f-581f-4d9a-9950-e61092fab586/image.jpg" alt="Image 7" style="width: 40%;">


---

- 상세일기 화면
    - 술 일기 상세 조회 API 호출후 나타나지는 화면
    - 하단 이미지를 클릭시 원본이미지 페이지로 이동
        - 확대 할 수 있음
        - 카카오톡앱 프로필 사진 보기 참고
    - 상단 우측 … 아이콘을 클릭시 수정 혹은 삭제 툴팁 Ui 표시
  
      <img src="https://velog.velcdn.com/images/philipy/post/8440bb0d-ab01-4d2d-8fe8-c72902925f15/image.jpg" alt="Image 12" style="width: 40%;">
    
      <img src="https://velog.velcdn.com/images/philipy/post/a12c0516-4bbe-493a-a175-21dbfe0089a9/image.jpg" alt="Image 9" style="width: 40%;">
      
      <img src="https://velog.velcdn.com/images/philipy/post/a3b54dd3-3aea-44d7-b17b-c4cc94b096a9/image.jpg" alt="Image 10" style="width: 40%;">
      
      <img src="https://velog.velcdn.com/images/philipy/post/8d5c7ff0-7f11-4d71-bc18-8ea210d1c320/image.jpg" alt="Image 11" style="width: 40%;">
    

- 일기수정

  <img src="https://velog.velcdn.com/images/philipy/post/800587cb-9e68-4664-9f2b-d635acfd31bd/image.jpg" alt="Image 13" style="width: 40%;">
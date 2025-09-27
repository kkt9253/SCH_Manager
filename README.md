# SCM (Sooncheonhyang Cafeteria Manager)

SCM은 순천향대학교 학생과 교직원들에게 최신 학식 정보를 직관적으로 제공하고, 식당 관리자에게는 손쉬운 정보 관리 기능을 제공하는 '학식 정보 플랫폼'입니다.

교내 식당 메뉴를 확인하기 위해 식당에 직접 방문해야만 했던 불편함을 해소하고, 언제 어디서든 정확한 최신 식단 정보를 확인할 수 있는 편리한 환경을 제공하기 위해 개발하였습니다.

---

## 📥 다운로드 링크 
[![Download on OneStore](https://img.shields.io/badge/OneStore-Download-red?style=for-the-badge&logo=android)](https://m.onestore.co.kr/ko-kr/apps/appsDetail.omp?prodId=0001000723)

---

## ✨ 주요 기능

SCM은 사용자 역할에 따라 맞춤형 기능을 제공하여 효율적인 식단 관리 및 조회가 가능하도록 설계되었습니다.

### 👩‍🎓 **일반 사용자 (학생 및 교직원)**
- **오늘의 학식 통합 조회**: 모든 식당의 당일 메뉴를 한눈에 확인할 수 있습니다.
- **주간 학식 상세 조회**: 특정 식당을 선택하여 일주일치 식단과 운영 시간, 조기 마감 여부 등 상세 정보를 조회할 수 있습니다.
- **로그인 없는 빠른 접근**: 별도의 로그인 절차 없이 앱의 모든 기능을 즉시 이용할 수 있습니다.

### 👨‍💼 **식당 관리자 (Admin)**
- **식단 정보 업로드**: 주간 또는 당일 식단표를 이미지나 텍스트로 손쉽게 업로드할 수 있습니다.
- **운영 정보 관리**: 식당의 고정 운영 시간을 수정하거나, 조기 마감 여부를 실시간으로 설정할 수 있습니다.
- **업로드 후 검수 요청**: 업로드된 식단은 최종 승인권자인 'Master'에게 전달되어 검수 과정을 거칩니다.

### 💻 **최종 승인권자 (Master)**
- **식단 검수 및 최종 승인**: 관리자가 업로드한 식단 정보를 검토하고, 최종적으로 승인하여 사용자에게 노출시킵니다.
- **안정적인 정보 제공**: 검수 과정을 통해 사용자에게 정확하고 신뢰도 높은 학식 정보를 제공하는 역할을 합니다.

---

## ⚙️ 서비스 워크플로우

SCM은 **`Admin` → `Master` → `User`** 로 이어지는 체계적인 데이터 처리 흐름을 통해 정확하고 검증된 학식 정보를 사용자에게 제공합니다.

1.  **Admin (식당 관리자)**: 식단 정보를 시스템에 업로드합니다. 이때 메뉴 상태는 **`PENDING`(승인 대기)** 으로 설정되어 데이터베이스에 저장됩니다.
2.  **Master (최종 승인권자)**: `PENDING` 상태의 식단 정보를 자신의 관리자 페이지에서 확인하고, 내용 검토 후 최종 승인 처리를 합니다.
3.  **User (일반 사용자)**: Master가 승인한 **`APPROVED`(승인 완료)** 상태의 식단 정보만을 앱 메인 화면에서 조회하게 됩니다.

이러한 3단계 워크플로우는 정보의 정확성을 보장하며, 관리자 실수로 인한 잘못된 정보 노출을 방지합니다.

---

## 🧑‍💻 팀원
| 이름  | 역할             |
|-----|----------------|
| 김경탁 | PM, Backend 팀장 |
| 서재흔 | Frontend 팀장    |
| 성현석 | Backend        |

---

## 🛠️ 기술 스택 및 서비스 아키텍처

### 🖥️ Server / DB
- Java 17
- Spring Boot
- Spring Data JPA
- Spring Security
- JWT
- QueryDSL
- MariaDB
- Redis

### 🏗️ Infra
- Raspberry Pi 5 기반 On-Premise Server

---

# 📱 서비스 화면

# 학생 및 교직원 (일반 USer)

## 메인
직관적인 UI를 통해 간편하게 "오늘의 학식 조회" & "특정 식당의 일주일 학식 조회"를 제공합니다.

<img width="300" alt="메인 화면" src="https://private-user-images.githubusercontent.com/71701866/492186397-2b2dfc48-3e6c-4583-8682-7b9d1b8ae1cc.jpeg?jwt=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJnaXRodWIuY29tIiwiYXVkIjoicmF3LmdpdGh1YnVzZXJjb250ZW50LmNvbSIsImtleSI6ImtleTUiLCJleHAiOjE3NTg1Mjc4NjIsIm5iZiI6MTc1ODUyNzU2MiwicGF0aCI6Ii83MTcwMTg2Ni80OTIxODYzOTctMmIyZGZjNDgtM2U2Yy00NTgzLTg2ODItN2I5ZDFiOGFlMWNjLmpwZWc_WC1BbXotQWxnb3JpdGhtPUFXUzQtSE1BQy1TSEEyNTYmWC1BbXotQ3JlZGVudGlhbD1BS0lBVkNPRFlMU0E1M1BRSzRaQSUyRjIwMjUwOTIyJTJGdXMtZWFzdC0xJTJGczMlMkZhd3M0X3JlcXVlc3QmWC1BbXotRGF0ZT0yMDI1MDkyMlQwNzUyNDJaJlgtQW16LUV4cGlyZXM9MzAwJlgtQW16LVNpZ25hdHVyZT0yMjRlMTFmMTY1ODVmYTY2Mjg2NWY2NzU4NGRlNTc2OWI2Y2FlNDY1N2M3YWVjNDU2N2MwMzY1NWIwOGIwMjRhJlgtQW16LVNpZ25lZEhlYWRlcnM9aG9zdCJ9.7NDSePC1eivqaCA8VwrZLpdlrxK7hjacG0Kj_AUxs5Y" />

---
## 오늘의 학식
금일에 해당하는 모든 식당의 학식 정보를 통합 조회할 수 있습니다.

<img width="300" alt="오늘의 학식" src="https://private-user-images.githubusercontent.com/71701866/492186711-247ce080-3cb4-4b5c-8bb6-f1127bcf1c79.jpeg?jwt=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJnaXRodWIuY29tIiwiYXVkIjoicmF3LmdpdGh1YnVzZXJjb250ZW50LmNvbSIsImtleSI6ImtleTUiLCJleHAiOjE3NTg1MjgwMTgsIm5iZiI6MTc1ODUyNzcxOCwicGF0aCI6Ii83MTcwMTg2Ni80OTIxODY3MTEtMjQ3Y2UwODAtM2NiNC00YjVjLThiYjYtZjExMjdiY2YxYzc5LmpwZWc_WC1BbXotQWxnb3JpdGhtPUFXUzQtSE1BQy1TSEEyNTYmWC1BbXotQ3JlZGVudGlhbD1BS0lBVkNPRFlMU0E1M1BRSzRaQSUyRjIwMjUwOTIyJTJGdXMtZWFzdC0xJTJGczMlMkZhd3M0X3JlcXVlc3QmWC1BbXotRGF0ZT0yMDI1MDkyMlQwNzU1MThaJlgtQW16LUV4cGlyZXM9MzAwJlgtQW16LVNpZ25hdHVyZT1iODUzN2I3YTllZjNiZjBjYTYyMDRlOTVhYmFjMDg2ZjU0NTgzZjQzNTU1OGVlNzgzZWNjYjMxNmRhMWM2OTNiJlgtQW16LVNpZ25lZEhlYWRlcnM9aG9zdCJ9.6WKwDEhlggZKTMQ7XUCVx-6oF7LePJkGBQk4P9IVsA0" />

---
## 식당 별 일주일 학식 조회
특정 식당에 대한 다양한 정보 조회할 수 있다.

- 구성요소:
  - 운영시간
  - 조기 마감 여부
  - 식당 위치
  - 학식 가격
  - 일주일에 해당하는 학식 정보

<img width="300" alt="주간 학식 1" src="https://private-user-images.githubusercontent.com/71701866/492186628-5ea3c4e7-4191-42b9-8734-c017aa0254ed.jpeg?jwt=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJnaXRodWIuY29tIiwiYXVkIjoicmF3LmdpdGh1YnVzZXJjb250ZW50LmNvbSIsImtleSI6ImtleTUiLCJleHAiOjE3NTg1MjgwMTgsIm5iZiI6MTc1ODUyNzcxOCwicGF0aCI6Ii83MTcwMTg2Ni80OTIxODY2MjgtNWVhM2M0ZTctNDE5MS00MmI5LTg3MzQtYzAxN2FhMDI1NGVkLmpwZWc_WC1BbXotQWxnb3JpdGhtPUFXUzQtSE1BQy1TSEEyNTYmWC1BbXotQ3JlZGVudGlhbD1BS0lBVkNPRFlMU0E1M1BRSzRaQSUyRjIwMjUwOTIyJTJGdXMtZWFzdC0xJTJGczMlMkZhd3M0X3JlcXVlc3QmWC1BbXotRGF0ZT0yMDI1MDkyMlQwNzU1MThaJlgtQW16LUV4cGlyZXM9MzAwJlgtQW16LVNpZ25hdHVyZT05OThiYjljYjJiYmM5ZTc0ZTIzNTExM2JjYjdhMzU5Yzk2Mzc2NjgzMGZlMzFkMWVjYWUwOTk3ODMyZjMyZGNkJlgtQW16LVNpZ25lZEhlYWRlcnM9aG9zdCJ9.KsVTaLpP9rm_apv5FGkz2IylvXXJKHCwLNEjzQ1qHqk" />
<img width="300" alt="주간 학식 2" src="https://private-user-images.githubusercontent.com/71701866/492186658-5bbf04b3-8cab-4258-b0b6-f865ad8fbf0c.jpeg?jwt=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJnaXRodWIuY29tIiwiYXVkIjoicmF3LmdpdGh1YnVzZXJjb250ZW50LmNvbSIsImtleSI6ImtleTUiLCJleHAiOjE3NTg1MjgwMTgsIm5iZiI6MTc1ODUyNzcxOCwicGF0aCI6Ii83MTcwMTg2Ni80OTIxODY2NTgtNWJiZjA0YjMtOGNhYi00MjU4LWIwYjYtZjg2NWFkOGZiZjBjLmpwZWc_WC1BbXotQWxnb3JpdGhtPUFXUzQtSE1BQy1TSEEyNTYmWC1BbXotQ3JlZGVudGlhbD1BS0lBVkNPRFlMU0E1M1BRSzRaQSUyRjIwMjUwOTIyJTJGdXMtZWFzdC0xJTJGczMlMkZhd3M0X3JlcXVlc3QmWC1BbXotRGF0ZT0yMDI1MDkyMlQwNzU1MThaJlgtQW16LUV4cGlyZXM9MzAwJlgtQW16LVNpZ25hdHVyZT1kZGZkZmUzZmViOWE3OTYwY2E3OGE1M2U4NWZiZGZhMjUzMjdlYThiNTEzZTNmMmMzYzE5MzNmZTJjYWIwNWM0JlgtQW16LVNpZ25lZEhlYWRlcnM9aG9zdCJ9.niPW6bbipyKoHXR2jgfbFiZP9VQ3zOpN_wl6zBfpTCM" />

---
## 앱 문의
'앱 문의 하기' 클릭 시 카카오톡 오픈채팅을 통해 문의할 수 있습니다.

<img width="300" alt="앱 문의 1" src="https://private-user-images.githubusercontent.com/71701866/492186569-8ac22bb3-d9c6-49cf-b10b-ae0a12d3ded4.jpeg?jwt=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJnaXRodWIuY29tIiwiYXVkIjoicmF3LmdpdGh1YnVzZXJjb250ZW50LmNvbSIsImtleSI6ImtleTUiLCJleHAiOjE3NTg1MjgwMTgsIm5iZiI6MTc1ODUyNzcxOCwicGF0aCI6Ii83MTcwMTg2Ni80OTIxODY1NjktOGFjMjJiYjMtZDljNi00OWNmLWIxMGItYWUwYTEyZDNkZWQ0LmpwZWc_WC1BbXotQWxnb3JpdGhtPUFXUzQtSE1BQy1TSEEyNTYmWC1BbXotQ3JlZGVudGlhbD1BS0lBVkNPRFlMU0E1M1BRSzRaQSUyRjIwMjUwOTIyJTJGdXMtZWFzdC0xJTJGczMlMkZhd3M0X3JlcXVlc3QmWC1BbXotRGF0ZT0yMDI1MDkyMlQwNzU1MThaJlgtQW16LUV4cGlyZXM9MzAwJlgtQW16LVNpZ25hdHVyZT05MTgzOTU1YzllZWEyMGVlM2ZmN2Y5MThlZGZmNzNmZGNlOTA5NmI3YjcyZGNkMjYwNmE2MWY1MDMwY2MyNWRmJlgtQW16LVNpZ25lZEhlYWRlcnM9aG9zdCJ9.0nHA34aillgVfI5HSET0dLs9km0AV_A6Qn5P8Xy8F1c" />
<img width="300" alt="앱 문의 2" src="https://private-user-images.githubusercontent.com/71701866/492186692-e738a652-8559-4176-9727-eb48efcb092b.jpeg?jwt=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJnaXRodWIuY29tIiwiYXVkIjoicmF3LmdpdGh1YnVzZXJjb250ZW50LmNvbSIsImtleSI6ImtleTUiLCJleHAiOjE3NTg1MjgwMTgsIm5iZiI6MTc1ODUyNzcxOCwicGF0aCI6Ii83MTcwMTg2Ni80OTIxODY2OTItZTczOGE2NTItODU1OS00MTc2LTk3MjctZWI0OGVmY2IwOTJiLmpwZWc_WC1BbXotQWxnb3JpdGhtPUFXUzQtSE1BQy1TSEEyNTYmWC1BbXotQ3JlZGVudGlhbD1BS0lBVkNPRFlMU0E1M1BRSzRaQSUyRjIwMjUwOTIyJTJGdXMtZWFzdC0xJTJGczMlMkZhd3M0X3JlcXVlc3QmWC1BbXotRGF0ZT0yMDI1MDkyMlQwNzU1MThaJlgtQW16LUV4cGlyZXM9MzAwJlgtQW16LVNpZ25hdHVyZT02OTA3ODEzZmI5OGFhMzRjOTcwZWQwZmU4NDJlZGQ0ZTA5NTJjY2NiYmQwZmEwOWUxZGE0NjY3NjFiNmMyZWMyJlgtQW16LVNpZ25lZEhlYWRlcnM9aG9zdCJ9.uwhWl5veVz5pVXnj1_mbXMEafq88o01_czrG06QLlrY" />

---
# 식당 관리자 (Admin)

## 관리자 메인화면
다양한 식당의 정보를 간단히 업로드하고 관리할 수 있습니다.
- 구성요소:
  - 식당 조기마감 여부 업로드
  - 학식 메뉴 업로드 (이미지 or 텍스트)
  - 식당 오픈시간 수정

<img width="300" alt="관리자 메인" src="https://private-user-images.githubusercontent.com/71701866/492187335-11d9c752-90af-43f0-8528-fd0a5e010691.jpeg?jwt=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJnaXRodWIuY29tIiwiYXVkIjoicmF3LmdpdGh1YnVzZXJjb250ZW50LmNvbSIsImtleSI6ImtleTUiLCJleHAiOjE3NTg1Mjg3MTMsIm5iZiI6MTc1ODUyODQxMywicGF0aCI6Ii83MTcwMTg2Ni80OTIxODczMzUtMTFkOWM3NTItOTBhZi00M2YwLTg1MjgtZmQwYTVlMDEwNjkxLmpwZWc_WC1BbXotQWxnb3JpdGhtPUFXUzQtSE1BQy1TSEEyNTYmWC1BbXotQ3JlZGVudGlhbD1BS0lBVkNPRFlMU0E1M1BRSzRaQSUyRjIwMjUwOTIyJTJGdXMtZWFzdC0xJTJGczMlMkZhd3M0X3JlcXVlc3QmWC1BbXotRGF0ZT0yMDI1MDkyMlQwODA2NTNaJlgtQW16LUV4cGlyZXM9MzAwJlgtQW16LVNpZ25hdHVyZT0yZmMyZjMwMjRhNzk3NWU2MWI2MDY4YTk4YzZlYWZjMzcxNzI2ZGRjNGY3NGRmZDcwMTU2MGIxMWM3NDUzOGVhJlgtQW16LVNpZ25lZEhlYWRlcnM9aG9zdCJ9.vwcWPlxaW6u911UMrs_WsaQGts9uFHROtVqgOq9UphU" />

---
## 주간 학식 정보 업로드
일주일치 식단표 사진을 업로드하여 각 요일별 메뉴 등록에 활용합니다.

<img width="300" alt="주간 학식 업로드" src="https://private-user-images.githubusercontent.com/71701866/492187361-340e6384-d23e-4bc6-9459-ba3cbb1bbe46.jpeg?jwt=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJnaXRodWIuY29tIiwiYXVkIjoicmF3LmdpdGh1YnVzZXJjb250ZW50LmNvbSIsImtleSI6ImtleTUiLCJleHAiOjE3NTg1Mjg3MTMsIm5iZiI6MTc1ODUyODQxMywicGF0aCI6Ii83MTcwMTg2Ni80OTIxODczNjEtMzQwZTYzODQtZDIzZS00YmM2LTk0NTktYmEzY2JiMWJiZTQ2LmpwZWc_WC1BbXotQWxnb3JpdGhtPUFXUzQtSE1BQy1TSEEyNTYmWC1BbXotQ3JlZGVudGlhbD1BS0lBVkNPRFlMU0E1M1BRSzRaQSUyRjIwMjUwOTIyJTJGdXMtZWFzdC0xJTJGczMlMkZhd3M0X3JlcXVlc3QmWC1BbXotRGF0ZT0yMDI1MDkyMlQwODA2NTNaJlgtQW16LUV4cGlyZXM9MzAwJlgtQW16LVNpZ25hdHVyZT03Yjk1NzNmOGZjNzViYWEyZDU4MTI0YmVmYzBlMmUzMGMzYThkM2MzNTY0YzViNzAzZTRjZmJjN2NjYTMxZDcwJlgtQW16LVNpZ25lZEhlYWRlcnM9aG9zdCJ9.hJXkFnZSIxR4N0qIo3gy0yXFQ6PUBmEJj3t8-6O2hws" />

---
## 요일별 학식 정보 업로드
OCR 또는 텍스트 입력을 통해 요일별 조/중/석식 정보를 기입하고 Master에게 승인을 요청합니다.
- 구성요소:
  - OCR 또는 직접 텍스트 입력(선택)을 통한 n요일의 식단표 업로드
  - 조/중/석식의 운영 시간 변경

<img width="300" alt="요일별 학식 업로드" src="https://private-user-images.githubusercontent.com/71701866/492187378-7b7ee871-48aa-4955-85b6-c4a315a606ca.jpeg?jwt=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJnaXRodWIuY29tIiwiYXVkIjoicmF3LmdpdGh1YnVzZXJjb250ZW50LmNvbSIsImtleSI6ImtleTUiLCJleHAiOjE3NTg1Mjg3MTMsIm5iZiI6MTc1ODUyODQxMywicGF0aCI6Ii83MTcwMTg2Ni80OTIxODczNzgtN2I3ZWU4NzEtNDhhYS00OTU1LTg1YjYtYzRhMzE1YTYwNmNhLmpwZWc_WC1BbXotQWxnb3JpdGhtPUFXUzQtSE1BQy1TSEEyNTYmWC1BbXotQ3JlZGVudGlhbD1BS0lBVkNPRFlMU0E1M1BRSzRaQSUyRjIwMjUwOTIyJTJGdXMtZWFzdC0xJTJGczMlMkZhd3M0X3JlcXVlc3QmWC1BbXotRGF0ZT0yMDI1MDkyMlQwODA2NTNaJlgtQW16LUV4cGlyZXM9MzAwJlgtQW16LVNpZ25hdHVyZT1hOGU4YzZkYmZkOGY4OWRjNTJjZmFlZDAzZDg2ZjQxZGZkMzc2NmIyODE1NGE2YzMzYTk4ZGFlNGMxZDRjMGI0JlgtQW16LVNpZ25lZEhlYWRlcnM9aG9zdCJ9.72yU8fA2hJCqm2I2Hyld9DSmOl9G8NIG5LMqoW5DsMo" />

---

# 앱 개발자 & 최종 승인자 (Master)

## 메인화면
관리자(Admin)가 업로드한 정보를 최종 검수하고, 승인 시 즉시 사용자에게 정보를 제공합니다.

<img width="300" alt="마스터 메인" src="https://private-user-images.githubusercontent.com/71701866/492186983-5241887a-2501-434c-b79e-aeeca800db55.jpeg?jwt=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJnaXRodWIuY29tIiwiYXVkIjoicmF3LmdpdGh1YnVzZXJjb250ZW50LmNvbSIsImtleSI6ImtleTUiLCJleHAiOjE3NTg1MjkwMDUsIm5iZiI6MTc1ODUyODcwNSwicGF0aCI6Ii83MTcwMTg2Ni80OTIxODY5ODMtNTI0MTg4N2EtMjUwMS00MzRjLWI3OWUtYWVlY2E4MDBkYjU1LmpwZWc_WC1BbXotQWxnb3JpdGhtPUFXUzQtSE1BQy1TSEEyNTYmWC1BbXotQ3JlZGVudGlhbD1BS0lBVkNPRFlMU0E1M1BRSzRaQSUyRjIwMjUwOTIyJTJGdXMtZWFzdC0xJTJGczMlMkZhd3M0X3JlcXVlc3QmWC1BbXotRGF0ZT0yMDI1MDkyMlQwODExNDVaJlgtQW16LUV4cGlyZXM9MzAwJlgtQW16LVNpZ25hdHVyZT0xMjBiNGUyNGI1MDlkMTlmNDg4ZWFkZjkyZGE1Y2YyOTgxYmU5NGZjYjU1ZjYwNDIzMjkxY2JkZjdkNzFhZDk5JlgtQW16LVNpZ25lZEhlYWRlcnM9aG9zdCJ9.YByRf5Z7eRlV_Cj5Yr1XnxEAmtyku3clIE6O5Yhgwco" />

---
## Admin 업로드 정보 확인
관리자가 업로드한 주간 학식표 이미지를 확인합니다.

<img width="300" alt="업로드 이미지 확인" src="https://private-user-images.githubusercontent.com/71701866/492187009-99663923-24ee-4152-84b4-7a12a8c175ac.jpeg?jwt=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJnaXRodWIuY29tIiwiYXVkIjoicmF3LmdpdGh1YnVzZXJjb250ZW50LmNvbSIsImtleSI6ImtleTUiLCJleHAiOjE3NTg1MjkwMDUsIm5iZiI6MTc1ODUyODcwNSwicGF0aCI6Ii83MTcwMTg2Ni80OTIxODcwMDktOTk2NjM5MjMtMjRlZS00MTUyLTg0YjQtN2ExMmE4YzE3NWFjLmpwZWc_WC1BbXotQWxnb3JpdGhtPUFXUzQtSE1BQy1TSEEyNTYmWC1BbXotQ3JlZGVudGlhbD1BS0lBVkNPRFlMU0E1M1BRSzRaQSUyRjIwMjUwOTIyJTJGdXMtZWFzdC0xJTJGczMlMkZhd3M0X3JlcXVlc3QmWC1BbXotRGF0ZT0yMDI1MDkyMlQwODExNDVaJlgtQW16LUV4cGlyZXM9MzAwJlgtQW16LVNpZ25hdHVyZT1iNTJkNjFjMzBhOWY4YzI0ZTcxNjUzNzNjMDZiNGY0ODA0MzdiNjAyNGRmNWU1ZTU2Zjc5MzdiODg4NjBmNTdiJlgtQW16LVNpZ25lZEhlYWRlcnM9aG9zdCJ9.YqCgi3gsYsHiEtVSDGWMXPKxXhmN8VB41Uhj91IB4es" />

---
## 학식 업로드 최종 확인 및 승인
관리자가 등록한 정보를 최종 검토 후 승인하여 사용자 화면에 게시합니다.

<img width="300" alt="최종 승인 1" src="https://private-user-images.githubusercontent.com/71701866/492187033-bbd7435d-073f-446b-b291-4e7b2acbffe8.jpeg?jwt=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJnaXRodWIuY29tIiwiYXVkIjoicmF3LmdpdGh1YnVzZXJjb250ZW50LmNvbSIsImtleSI6ImtleTUiLCJleHAiOjE3NTg1MjkwMDUsIm5iZiI6MTc1ODUyODcwNSwicGF0aCI6Ii83MTcwMTg2Ni80OTIxODcwMzMtYmJkNzQzNWQtMDczZi00NDZiLWIyOTEtNGU3YjJhY2JmZmU4LmpwZWc_WC1BbXotQWxnb3JpdGhtPUFXUzQtSE1BQy1TSEEyNTYmWC1BbXotQ3JlZGVudGlhbD1BS0lBVkNPRFlMU0E1M1BRSzRaQSUyRjIwMjUwOTIyJTJGdXMtZWFzdC0xJTJGczMlMkZhd3M0X3JlcXVlc3QmWC1BbXotRGF0ZT0yMDI1MDkyMlQwODExNDVaJlgtQW16LUV4cGlyZXM9MzAwJlgtQW16LVNpZ25hdHVyZT00MzczN2JhZjI0MzUxM2RjMDc5Y2YxZjgyODMxMjJlZGEwNDk1Yzg1MDM2ZjIwZjUwODQ1ODkwNThhZTRhNDE3JlgtQW16LVNpZ25lZEhlYWRlcnM9aG9zdCJ9.4qa4XkK01VfTIW8IQom3ha6SAC2CiQ2Ur-C8hZasYs0" />
<img width="300" alt="최종 승인 2" src="https://private-user-images.githubusercontent.com/71701866/492187064-424a6d19-fd42-4eba-9d0c-2acae4cd2b30.jpeg?jwt=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJnaXRodWIuY29tIiwiYXVkIjoicmF3LmdpdGh1YnVzZXJjb250ZW50LmNvbSIsImtleSI6ImtleTUiLCJleHAiOjE3NTg1MjkwMDUsIm5iZiI6MTc1ODUyODcwNSwicGF0aCI6Ii83MTcwMTg2Ni80OTIxODcwNjQtNDI0YTZkMTktZmQ0Mi00ZWJhLTlkMGMtMmFjYWU0Y2QyYjMwLmpwZWc_WC1BbXotQWxnb3JpdGhtPUFXUzQtSE1BQy1TSEEyNTYmWC1BbXotQ3JlZGVudGlhbD1BS0lBVkNPRFlMU0E1M1BRSzRaQSUyRjIwMjUwOTIyJTJGdXMtZWFzdC0xJTJGczMlMkZhd3M0X3JlcXVlc3QmWC1BbXotRGF0ZT0yMDI1MDkyMlQwODExNDVaJlgtQW16LUV4cGlyZXM9MzAwJlgtQW16LVNpZ25hdHVyZT02NTVjMzQ3OGMwNmU3NjFmZmE4MWE2MmVkZmU3YTVkZmRiY2U2YmU4NjAxYmU5YzczZjQ0MmNlNWIzZmRjNWJkJlgtQW16LVNpZ25lZEhlYWRlcnM9aG9zdCJ9.VZimzY4klrCojbMxPf8x1ZjiPJVLH_iXt2pVmmHKB_c" />
<img width="300" alt="최종 승인 3" src="https://private-user-images.githubusercontent.com/71701866/492187118-22905b94-1b77-4d4a-9c07-20332880bc54.jpeg?jwt=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJnaXRodWIuY29tIiwiYXVkIjoicmF3LmdpdGh1YnVzZXJjb250ZW50LmNvbSIsImtleSI6ImtleTUiLCJleHAiOjE3NTg1MjkwMDUsIm5iZiI6MTc1ODUyODcwNSwicGF0aCI6Ii83MTcwMTg2Ni80OTIxODcxMTgtMjI5MDViOTQtMWI3Ny00ZDRhLTljMDctMjAzMzI4ODBiYzU0LmpwZWc_WC1BbXotQWxnb3JpdGhtPUFXUzQtSE1BQy1TSEEyNTYmWC1BbXotQ3JlZGVudGlhbD1BS0lBVkNPRFlMU0E1M1BRSzRaQSUyRjIwMjUwOTIyJTJGdXMtZWFzdC0xJTJGczMlMkZhd3M0X3JlcXVlc3QmWC1BbXotRGF0ZT0yMDI1MDkyMlQwODExNDVaJlgtQW16LUV4cGlyZXM9MzAwJlgtQW16LVNpZ25hdHVyZT05ZTA2MGUwNjM4MDRmYmMzMjVhN2VmODQwMzAzMjM2OTAyM2FlNjRmYjBiZmE1OWRmYTE0M2QxMjY5YjU3ZDE0JlgtQW16LVNpZ25lZEhlYWRlcnM9aG9zdCJ9.p5-efrF93jWOJQfg3Efm-5vWPi_3Sb09D6TC8a7G53c" />

---

# 부가기능

## 로그인 및 로그아웃

<img width="300" alt="로그인" src="https://private-user-images.githubusercontent.com/71701866/492190070-a7309ef5-b6f6-4793-8e1f-db0fe62ec1b2.jpeg?jwt=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJnaXRodWIuY29tIiwiYXVkIjoicmF3LmdpdGh1YnVzZXJjb250ZW50LmNvbSIsImtleSI6ImtleTUiLCJleHAiOjE3NTg1Mjg4MjQsIm5iZiI6MTc1ODUyODUyNCwicGF0aCI6Ii83MTcwMTg2Ni80OTIxOTAwNzAtYTczMDllZjUtYjZmNi00NzkzLThlMWYtZGIwZmU2MmVjMWIyLmpwZWc_WC1BbXotQWxnb3JpdGhtPUFXUzQtSE1BQy1TSEEyNTYmWC1BbXotQ3JlZGVudGlhbD1BS0lBVkNPRFlMU0E1M1BRSzRaQSUyRjIwMjUwOTIyJTJGdXMtZWFzdC0xJTJGczMlMkZhd3M0X3JlcXVlc3QmWC1BbXotRGF0ZT0yMDI1MDkyMlQwODA4NDRaJlgtQW16LUV4cGlyZXM9MzAwJlgtQW16LVNpZ25hdHVyZT1lMWFhNDgxY2JiMzg3YWZiZjhiZmJkOGIyMjdhMGEzYjQwOTllNDhlY2M4YmNlMTI3MTRiZjY0M2I5ZDc3MjM1JlgtQW16LVNpZ25lZEhlYWRlcnM9aG9zdCJ9.xnRCNnr39rqnXTW6CAbXQjbhQ4hGnG7x_viKAxtiCM8" />

<img width="300" alt="로그아웃" src="https://private-user-images.githubusercontent.com/71701866/492197499-6e9aa387-4e0c-4b89-b491-46cf27d6b0e3.jpeg?jwt=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJnaXRodWIuY29tIiwiYXVkIjoicmF3LmdpdGh1YnVzZXJjb250ZW50LmNvbSIsImtleSI6ImtleTUiLCJleHAiOjE3NTg1Mjg5NDQsIm5iZiI6MTc1ODUyODY0NCwicGF0aCI6Ii83MTcwMTg2Ni80OTIxOTc0OTktNmU5YWEzODctNGUwYy00Yjg5LWI0OTEtNDZjZjI3ZDZiMGUzLmpwZWc_WC1BbXotQWxnb3JpdGhtPUFXUzQtSE1BQy1TSEEyNTYmWC1BbXotQ3JlZGVudGlhbD1BS0lBVkNPRFlMU0E1M1BRSzRaQSUyRjIwMjUwOTIyJTJGdXMtZWFzdC0xJTJGczMlMkZhd3M0X3JlcXVlc3QmWC1BbXotRGF0ZT0yMDI1MDkyMlQwODEwNDRaJlgtQW16LUV4cGlyZXM9MzAwJlgtQW16LVNpZ25hdHVyZT01MDAyYmU1YmViMTcyYWFiNzg0ZTQ4MWNlODNkNGY0Y2QzNDU4ZTg2OWVmNTk5MjA1ZjJlMDVjOGZhZTVmMTY4JlgtQW16LVNpZ25lZEhlYWRlcnM9aG9zdCJ9.6z63tpm8UF6Zexj2k6E87xYEeVxV121KaVLV0R8TUbU" />
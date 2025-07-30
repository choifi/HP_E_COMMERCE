사용한 아키텍처 : 레이어드 인터페이스 아키텍처

[ 선택한 이유 ]
- 관심사 분리 : 각 레이어는 고유한 책임을 가집니다. 
- 의존성 역전(DIP)와 추상화에 유리합니다. 구현이 아닌 인터페이스에 의존합니다. 
- 테스트 용이: 인터페이스가 있어 테스트 시 구현체 대신 Mock 객체 주입 가능합니다.

[ 레이어와 역할 ]
- interfaces : 외부 요청 진입점 (Controller, API, UI 등)
- application : 비즈니스 흐름 처리, 서비스 계층 (use-case 수준 로직)
- domain : 핵심 비즈니스 규칙, 엔티티, 도메인 서비스, Value Object 등
- infrastructure : DB, 외부 API, 메시지큐 등 기술 세부사항 처리
- common/config	: 공통 유틸리티, 설정 클래스(Spring Config 등)

- Repository 저장소는 domain 계층에서 정의하고, infrastructure 계층에서 구현
- Application Layer : 비즈니스 흐름을 조정하는 계층, 도메인 객체들을 조합하여 비즈니스 로직을 수행



# mobile-geth-test

mobile geth를 운영하여 Android에서 코인 전송, ERC-20, 721 전송 기능 트랜잭션 리스팅 기능을 테스트합니다.

## Node 연결 구조

<img src = "https://github.com/MoSangIl/mobile-geth-test/assets/45113627/64f02254-d787-4e30-99fe-60acf37ce04b" width="50%" height="50%">

Mobile에서 운영하는 LightNode는 FullNode를 통해서 Ethereum Network에 접근 가능하다.

## Functions

- Node 관리 기능 페이지
  - Node 시작하기
  - Node 정지하기
  - 연결할 Fullnode ip 추가하기
- 계정 관리 기능 페이지
  - 계정 생성하기
  - 다른 앱 계정 불러오기
    - Private Key 사용
    - JSON Keyfile 사용
  - UniWaffle에서 관리하는 계정 추출하기
    - Private Key 추출
    - JSON Keyfile 형태로 추출
  - 계정 삭제하기
- ETH 관리` 기능 페이지
  - ETH 잔액 조회하기
  - ETH 전송하기
- ERC20 Fungible Token 관리 기능 페이지
  - Token 잔액 조회하기
  - Token 전송하기

## Abstraction

1. Mobile Geth를 통해 Light Node 실행
2. Light Node는 지정된 Full Node와 연결
3. Web3를 활용하여 제공된 기능 실행

## Implementation
| Node Management | Account Management | ETH Management | ERC20 Management |
|:---:|:---:|:---:|:---:|
| ![Node Manage](https://github.com/MoSangIl/mobile-geth-test/assets/45113627/9d6c6453-abf6-40cd-b9ad-14e17962cc1c) | ![Account Manage](https://github.com/MoSangIl/mobile-geth-test/assets/45113627/545dea61-39d5-4b5f-93a6-74f6baa94e28) | ![ETH Manage](https://github.com/MoSangIl/mobile-geth-test/assets/45113627/bb40cf3f-3cc2-4759-a2f4-b4fdbe75a590) | ![ERC20 Manage](https://github.com/MoSangIl/mobile-geth-test/assets/45113627/8c17635a-65ba-401c-8f8e-0d243894bf11)

## Resource

https://geth.ethereum.org/docs/developers/dapp-developer/mobile#main-content

-> NOT SUPPORT MOBILE

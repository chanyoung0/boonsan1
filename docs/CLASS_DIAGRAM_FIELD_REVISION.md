# Class Diagram Field Revision Guide

이 문서는 `docs/original`의 원본 클래스 다이어그램과 `class-diagram-revised.png` 기준으로 확인된 필드 보완 사항을 정리한다.

원본 클래스 다이어그램, 유스케이스 다이어그램, 유스케이스 시나리오를 최우선 기준으로 유지하되, 수정 클래스 다이어그램에서 확인된 필드 차이는 손해조사, 위탁, 지급품의서 구현 전에 확인하거나 반영해야 한다.

## Field Revision Candidates

| No. | Class | Field | Type | 보정 내용 |
| --- | --- | --- | --- | --- |
| 1 | `PaymentApprovalDocument` | `faultRatioOpinion` | `String` | 지급품의서에서 과실비율 관련 소견을 표현하기 위한 필드로 확인된다. |
| 2 | `Partner` | `contractTerms` | `String` | 협력업체 계약 조건을 표현하기 위한 필드로 확인된다. |
| 3 | `Partner` | `id` / `partnerId` | `String` | 현재 코드의 `id`와 수정 클래스 다이어그램의 `partnerId` 명칭이 다르다. |

## Implementation Notes

- `PaymentApprovalDocument.faultRatioOpinion`은 손해조사 결과를 지급품의서로 전환하기 전에 확인 또는 반영이 필요하다.
- `Partner.contractTerms`는 손해조사를 위탁하는 흐름에서 협력업체 계약 조건을 다룰 경우 확인 또는 반영이 필요하다.
- `Partner.id`와 `partnerId` 명칭 차이는 기존 코드 보존 원칙을 고려하여, 실제 코드 반영 전에 필드명 변경 또는 DTO/DB 매핑 방식 중 하나를 확정해야 한다.
- 위 항목은 기능 코드 구현 편의성으로 임의 변경하지 않고, 손해조사/위탁/지급품의서 구현 전에 사용자 확인을 거친다.

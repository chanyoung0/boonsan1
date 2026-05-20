-- =====================================================
-- 전체 데이터 초기화
-- =====================================================
TRUNCATE TABLE damage_investigation;
TRUNCATE TABLE accident_report;
TRUNCATE TABLE endorsement;
TRUNCATE TABLE reinstatement;
TRUNCATE TABLE payment_collection;
TRUNCATE TABLE maturity_notice;
TRUNCATE TABLE insurance_application;
TRUNCATE TABLE underwriting;
TRUNCATE TABLE payout;
TRUNCATE TABLE compensation_evaluation;
TRUNCATE TABLE partner;
TRUNCATE TABLE authorization_request;
TRUNCATE TABLE insurance;
TRUNCATE TABLE product_authorization;

-- =====================================================
-- 1. 보험상품 (insurance)
-- =====================================================
INSERT INTO insurance (product_code, product_type, insurance_period, insured_amount, premium, maturity_refund, driver_age, vehicle_type, building_type, location, vessel_type, shipping_route) VALUES
('AUTO-2024-001', 'AUTO', '1년',  30000000.00, 850000.00,  NULL,       28, '현대 소나타 2.0',  NULL,       NULL,     NULL,       NULL),
('AUTO-2024-002', 'AUTO', '1년',  20000000.00, 620000.00,  NULL,       45, '기아 K5 1.6T',    NULL,       NULL,     NULL,       NULL),
('AUTO-2024-003', 'AUTO', '1년',  15000000.00, 480000.00,  NULL,       52, 'BMW 520i',        NULL,       NULL,     NULL,       NULL),
('FIRE-2024-001', 'FIRE', '3년',  200000000.00, 120000.00, 5000000.00, NULL, NULL, '아파트',    '서울 강남구 역삼동', NULL, NULL),
('FIRE-2024-002', 'FIRE', '5년',  500000000.00, 350000.00, 15000000.00, NULL, NULL, '상업용 건물', '부산 해운대구 우동', NULL, NULL),
('MARINE-2024-001', 'MARINE', '1년', 800000000.00, 450000.00, NULL,   NULL, NULL, NULL, NULL, '컨테이너선 3000TEU', '부산-상해 정기항로');

-- =====================================================
-- 2. 인가요청 (authorization_request)
-- =====================================================
INSERT INTO authorization_request (request_id, product_code, request_reason, submission_agency_name, requested_at, approved_at, is_approved) VALUES
('AUTH-2024-001', 'AUTO-2024-001', '신규 자동차보험 상품 출시를 위한 인가 요청',        '금융감독원', '2024-01-10 09:00:00', '2024-01-25 14:30:00', TRUE),
('AUTH-2024-002', 'AUTO-2024-002', '중형 세단 전용 보험 상품 인가 요청',               '금융감독원', '2024-01-15 10:00:00', '2024-02-01 11:00:00', TRUE),
('AUTH-2024-003', 'FIRE-2024-001', '아파트 화재보험 신상품 인가 요청',                  '금융감독원', '2024-02-01 09:30:00', '2024-02-20 15:00:00', TRUE),
('AUTH-2024-004', 'FIRE-2024-002', '상업용 건물 장기 화재보험 인가 요청',               '금융감독원', '2024-02-10 14:00:00', NULL,                  FALSE),
('AUTH-2024-005', 'MARINE-2024-001', '컨테이너 해상화물보험 신규 상품 인가 요청',       '금융감독원', '2024-03-05 09:00:00', '2024-03-22 13:00:00', TRUE);

-- =====================================================
-- 3. 보험청약 심사 (underwriting)
-- =====================================================
INSERT INTO underwriting (underwriting_id, underwriter_emp_no, underwriter_name, underwriter_dept, total_score, underwriting_status, underwriting_type, underwriting_result, rejection_reason, underwritten_at) VALUES
('UW-2024-000001', 'EMP-1021', '김민준', '언더라이팅팀', 92.0, 'COMPLETED', 'AUTO',      'APPROVED',   NULL,                             '2024-02-05 10:20:00'),
('UW-2024-000002', 'EMP-1021', '김민준', '언더라이팅팀', 78.5, 'COMPLETED', 'AUTO',      'SURCHARGE',  NULL,                             '2024-02-12 11:00:00'),
('UW-2024-000003', 'EMP-1035', '이수진', '언더라이팅팀', 61.0, 'COMPLETED', 'GENERAL',   'REJECTED',   '고혈압·당뇨 병력으로 인수 불가',  '2024-03-03 14:15:00'),
('UW-2024-000004', 'EMP-1035', '이수진', '언더라이팅팀', 88.0, 'COMPLETED', 'DIAGNOSIS', 'APPROVED',   NULL,                             '2024-03-18 09:45:00'),
('UW-2024-000005', 'EMP-1042', '박준혁', '특종심사팀',   95.0, 'COMPLETED', 'SPECIAL',   'APPROVED',   NULL,                             '2024-04-02 16:30:00');

-- =====================================================
-- 4. 청약 (insurance_application)
-- =====================================================
INSERT INTO insurance_application (application_id, policy_number, application_status, applied_condition, applied_at) VALUES
('APP-2024-000001', 'P2024-100001', 'APPROVED',   '표준체 가입',        '2024-02-05 10:25:00'),
('APP-2024-000002', 'P2024-100002', 'APPROVED',   '할증 조건 적용',     '2024-02-12 11:05:00'),
('APP-2024-000003', 'P2024-100003', 'REJECTED',   NULL,                 '2024-03-03 14:20:00'),
('APP-2024-000004', 'P2024-100004', 'APPROVED',   '표준체 가입',        '2024-03-18 09:50:00'),
('APP-2024-000005', 'P2024-100005', 'PENDING',    '추가 서류 검토 중',  '2024-04-02 16:35:00');

-- =====================================================
-- 5. 배서 (endorsement)
-- =====================================================
INSERT INTO endorsement (endorsement_id, policy_number, endorsement_type, previous_content, new_content, change_reason, underwriting_result, applied_at, processed_at) VALUES
('END-2024-000001', 'P2024-100001', 'COVERAGE_CHANGE',         '대인배상 1억',         '대인배상 2억',         '보장 금액 상향 요청',    'APPROVED', '2024-03-10 10:00:00', '2024-03-11 14:00:00'),
('END-2024-000002', 'P2024-100002', 'PREMIUM_CHANGE',          '월납 62,000원',        '월납 58,000원',        '무사고 할인 적용',       'APPROVED', '2024-03-15 09:00:00', '2024-03-15 17:00:00'),
('END-2024-000003', 'P2024-100004', 'BENEFICIARY_CHANGE',      '수익자: 본인',         '수익자: 배우자 김지연', '결혼으로 인한 수익자 변경', 'APPROVED', '2024-04-05 11:30:00', '2024-04-06 10:00:00'),
('END-2024-000004', 'P2024-100001', 'SPECIAL_CONTRACT_CHANGE', '긴급출동 서비스 미포함', '긴급출동 서비스 포함', '특약 추가 요청',         'APPROVED', '2024-04-20 13:00:00', '2024-04-21 09:00:00'),
('END-2024-000005', 'P2024-100005', 'COVERAGE_CHANGE',         '자기차량손해 포함',    '자기차량손해 미포함',  '보험료 절감 목적',       'APPROVED', '2024-05-02 10:00:00', '2024-05-03 11:00:00');

-- =====================================================
-- 6. 부활 (reinstatement)
-- =====================================================
INSERT INTO reinstatement (reinstatement_id, policy_number, underwriting_result, reinstatement_status, applied_at, processed_at) VALUES
('REIN-2024-000001', 'P2023-080012', 'APPROVED',  'APPROVED', '2024-01-20 09:00:00', '2024-01-22 14:00:00'),
('REIN-2024-000002', 'P2023-080034', 'APPROVED',  'APPROVED', '2024-02-08 10:30:00', '2024-02-10 11:00:00'),
('REIN-2024-000003', 'P2023-080056', 'SURCHARGE', 'APPROVED', '2024-02-25 14:00:00', '2024-02-27 16:00:00'),
('REIN-2024-000004', 'P2023-080078', 'REJECTED',  'REJECTED', '2024-03-12 09:30:00', '2024-03-14 10:00:00'),
('REIN-2024-000005', 'P2023-080099', 'APPROVED',  'APPROVED', '2024-04-15 11:00:00', '2024-04-17 14:30:00');

-- =====================================================
-- 7. 분납/수금 (payment_collection)
-- =====================================================
INSERT INTO payment_collection (collection_id, policy_number, due_date, collected_amount, unpaid_amount, unpaid_installment_count, processing_result, collected_at, transfer_type, collection_status) VALUES
('COL-2024-000001', 'P2024-100001', '2024-03-05', 70833.00,      0.00,      0, 'SUCCESS', '2024-03-05 10:15:00', 'TRANSFER',        'COLLECTED'),
('COL-2024-000002', 'P2024-100002', '2024-03-10', 51666.00,      0.00,      0, 'SUCCESS', '2024-03-10 09:30:00', 'TRANSFER',        'COLLECTED'),
('COL-2024-000003', 'P2024-100004', '2024-03-18', 30000.00,   10000.00,     1, 'PARTIAL', '2024-03-18 14:00:00', 'VISIT_COLLECTION', 'UNPAID'),
('COL-2024-000004', 'P2024-100001', '2024-04-05', 70833.00,      0.00,      0, 'SUCCESS', '2024-04-04 16:20:00', 'TRANSFER',        'COLLECTED'),
('COL-2024-000005', 'P2024-100005', '2024-04-10', 0.00,       40000.00,     2, 'FAILED',  NULL,                  NULL,              'UNPAID');

-- =====================================================
-- 8. 만기계약 (maturity_notice)
-- =====================================================
INSERT INTO maturity_notice (notice_id, delivery_method, sent_at, checked_at, renewal_intention) VALUES
('MTN-2024-000001', 'SMS',               '2024-01-15 09:00:00', '2024-01-16 11:30:00', 'YES'),
('MTN-2024-000002', 'EMAIL',             '2024-01-15 09:00:00', '2024-01-17 08:45:00', 'YES'),
('MTN-2024-000003', 'PUSH_NOTIFICATION', '2024-01-15 09:00:00', '2024-01-15 14:20:00', 'NO'),
('MTN-2024-000004', 'SMS',               '2024-02-10 09:00:00', NULL,                  NULL),
('MTN-2024-000005', 'MAIL',              '2024-02-10 09:00:00', '2024-02-20 10:00:00', 'YES');

-- =====================================================
-- 9. 사고 접수 (accident_report)
-- =====================================================
INSERT INTO accident_report (report_no, policy_number, accident_description, damage_details, accident_status, document_submission_status, accident_at_text, created_at) VALUES
('ACC-2024-000001', 'P2024-100001', '신호 대기 중 후방 차량 추돌. 트렁크 및 범퍼 파손.',         '차량 후방 수리비 약 180만원 예상, 탑승자 목 통증 호소', 'INVESTIGATION_REQUIRED', 'SUBMITTED', '2024-03-22 17:30',  '2024-03-22 18:10:00'),
('ACC-2024-000002', 'P2024-100002', '주차장 후진 중 기둥과 충돌. 조수석 도어 파손.',             '도어 교체 및 도색 비용 약 90만원 예상',                  'RECEIVED',               'SUBMITTED', '2024-04-01 14:10',  '2024-04-01 15:30:00'),
('ACC-2024-000003', 'P2024-100004', '빗길 미끄러짐으로 인한 중앙분리대 충돌. 전면부 대파.',      '전면부 전손 처리 가능성, 수리비 약 600만원 예상',         'INVESTIGATION_REQUIRED', 'SUBMITTED', '2024-04-10 08:50',  '2024-04-10 09:40:00'),
('ACC-2024-000004', 'P2023-080012', '화재로 인한 아파트 주방 일부 소실.',                        '주방 인테리어 및 가전 피해 약 1,200만원 추산',            'DOCUMENT_PENDING',       'PENDING',   '2024-04-18 02:30',  '2024-04-18 10:00:00'),
('ACC-2024-000005', 'P2024-100001', '고속도로 주행 중 타이어 펑크로 인한 가드레일 충돌.',        '조수석 측면 및 하부 손상, 수리비 약 230만원 예상',        'RECEIVED',               'SUBMITTED', '2024-05-03 11:20',  '2024-05-03 12:00:00');

-- =====================================================
-- 10. 손해조사 (damage_investigation)
-- =====================================================
INSERT INTO damage_investigation (investigation_id, report_no, adjuster_id, medical_expense, lost_income, repair_cost, settlement_amount, fault_ratio, investigation_status, investigation_at) VALUES
('INV-2024-000001', 'ACC-2024-000001', 'ADJ-2301', 350000.00,  200000.00,  1800000.00, 2100000.00, 0.0,  'APPROVED', '2024-03-25 14:00:00'),
('INV-2024-000002', 'ACC-2024-000002', 'ADJ-2301', 0.00,       0.00,        850000.00,  850000.00,  0.0,  'APPROVED', '2024-04-03 10:30:00'),
('INV-2024-000003', 'ACC-2024-000003', 'ADJ-2315', 0.00,       0.00,       5800000.00, 5800000.00, 20.0,  'APPROVED', '2024-04-14 15:00:00'),
('INV-2024-000004', 'ACC-2024-000004', 'ADJ-2302', 0.00,       0.00,      12000000.00, 9600000.00,  0.0,  'PENDING',  '2024-04-22 11:00:00'),
('INV-2024-000005', 'ACC-2024-000005', 'ADJ-2315', 0.00,       0.00,       2300000.00, 2300000.00,  0.0,  'APPROVED', '2024-05-06 09:30:00');

-- =====================================================
-- 11. 제지급금 (payout)
-- =====================================================
INSERT INTO payout (payout_id, policy_number, processor, payment_type, calculation_basis, calculated_amount, deduction_item, final_payment_amount, approved_at, paid_at, payout_status) VALUES
('PO-2024-000001', 'P2024-100001', '김민준',  'LUMP_SUM',    'REPAIR_COST',       2100000.00, '자기부담금 20만원',    1900000.00, '2024-03-26 10:00:00', '2024-03-27 11:00:00', 'PAID'),
('PO-2024-000002', 'P2024-100002', '이수진',  'LUMP_SUM',    'REPAIR_COST',        850000.00, NULL,                   850000.00,  '2024-04-04 09:00:00', '2024-04-05 10:00:00', 'PAID'),
('PO-2024-000003', 'P2024-100004', '박준혁',  'LUMP_SUM',    'REPAIR_COST',       5800000.00, '과실상계 20% 공제',   4640000.00,  '2024-04-15 14:00:00', '2024-04-16 11:00:00', 'PAID'),
('PO-2024-000004', 'P2023-080012', '최지현',  'INSTALLMENT', 'DEATH_BENEFIT',    50000000.00, '기납입보험료 정산',   48500000.00, '2024-05-02 10:00:00', NULL,                  'APPROVED'),
('PO-2024-000005', 'P2024-100001', '김민준',  'LUMP_SUM',    'REPAIR_COST',       2300000.00, '자기부담금 20만원',   2100000.00,  '2024-05-07 09:30:00', NULL,                  'APPROVED');

-- =====================================================
-- 12. 보상평가 (compensation_evaluation)
-- =====================================================
INSERT INTO compensation_evaluation (evaluation_id, evaluation_month, evaluation_status, evaluation_result, submission_agency_name, damage_amount, damage_analysis_result, compensation_statistics) VALUES
('CE-2024-000001', 1, 'CLOSED',      'PASS',        '금융감독원', 125000000.00, '1월 손해율 62.5%, 전년 동월 대비 3.2% 상승. 자동차 사고 증가 주요 원인.',  '접수 건수: 142건, 지급 건수: 128건, 평균 지급액: 976천원'),
('CE-2024-000002', 2, 'CLOSED',      'PASS',        '금융감독원', 98000000.00,  '2월 손해율 58.1%, 한파 관련 동파 사고 감소로 전월 대비 개선.',             '접수 건수: 118건, 지급 건수: 107건, 평균 지급액: 916천원'),
('CE-2024-000003', 3, 'CLOSED',      'CONDITIONAL', '금융감독원', 187000000.00, '3월 손해율 74.8%, 봄철 빗길 사고 급증. 자동차 부문 손해율 개선 필요.',     '접수 건수: 201건, 지급 건수: 186건, 평균 지급액: 1,005천원'),
('CE-2024-000004', 4, 'COMPLETED',   'PASS',        '금융감독원', 143000000.00, '4월 손해율 67.2%, 전월 대비 개선. 화재 보험 지급 증가 일부 영향.',         '접수 건수: 169건, 지급 건수: 155건, 평균 지급액: 923천원'),
('CE-2024-000005', 5, 'IN_PROGRESS', NULL,          '금융감독원', NULL,         NULL,                                                                         NULL);

-- =====================================================
-- 13. 협력업체 (partner)
-- =====================================================
INSERT INTO partner (id, partner_name, partner_type, contact, responsibility, evaluation_grade) VALUES
('PT-2024-000001', '한국자동차손해사정(주)',  '손해사정',   '02-555-1234', '자동차 사고 손해사정 및 현장 조사',                 'EXCELLENT'),
('PT-2024-000002', '대한의료감정원',          '의료감정',   '02-777-5678', '인체 상해 의료 감정 및 후유장해 평가',              'GOOD'),
('PT-2024-000003', '서울법률사무소',          '법률지원',   '02-333-9012', '보험 분쟁 소송 지원 및 법률 자문',                  'EXCELLENT'),
('PT-2024-000004', '현대자동차공업사',        '차량수리',   '031-456-7890', '사고 차량 수리 및 부품 교체',                      'GOOD'),
('PT-2024-000005', '글로벌해운감정(주)',       '해상감정',   '051-222-3456', '해상 화물 손해 감정 및 해양 사고 조사',            'AVERAGE');

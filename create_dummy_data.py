#!/usr/bin/env python3
import pymysql
from datetime import datetime, timedelta
import random

# 데이터베이스 연결 설정
DB_CONFIG = {
    'host': 'localhost',
    'port': 3307,
    'user': 'application',
    'password': 'application',
    'database': 'hhplus',
    'charset': 'utf8mb4'
}

def create_connection():
    """데이터베이스 연결 생성"""
    try:
        connection = pymysql.connect(**DB_CONFIG)
        print("✅ 데이터베이스 연결 성공")
        return connection
    except Exception as e:
        print(f"❌ 데이터베이스 연결 실패: {e}")
        return None

def create_coupon_policies(connection):
    """쿠폰 정책 생성"""
    try:
        # 기존 데이터 삭제
        connection.execute("DELETE FROM coupon_policy")
        print("🗑️ 기존 쿠폰 정책 삭제 완료")
        
        # 쿠폰 정책 데이터 생성
        policies = [
            {
                'name': '테스트쿠폰',
                'discount_rate': 10,
                'valid_days': 30,
                'max_count': 1000,
                'start_date': '2024-01-01 00:00:00',
                'end_date': '2024-12-31 23:59:59'
            },
            {
                'name': '할인쿠폰',
                'discount_rate': 20,
                'valid_days': 15,
                'max_count': 500,
                'start_date': '2024-01-01 00:00:00',
                'end_date': '2024-12-31 23:59:59'
            },
            {
                'name': '특가쿠폰',
                'discount_rate': 30,
                'valid_days': 7,
                'max_count': 100,
                'start_date': '2024-01-01 00:00:00',
                'end_date': '2024-12-31 23:59:59'
            }
        ]
        
        for i, policy in enumerate(policies, 1):
            sql = """
            INSERT INTO coupon_policy (policy_id, name, discount_rate, valid_days, max_count, start_date, end_date, issued_count, created_time, updated_time)
            VALUES (%s, %s, %s, %s, %s, %s, %s, 0, NOW(), NOW())
            """
            connection.execute(sql, (
                i,
                policy['name'],
                policy['discount_rate'],
                policy['valid_days'],
                policy['max_count'],
                policy['start_date'],
                policy['end_date']
            ))
        
        connection.commit()
        print(f"✅ {len(policies)}개의 쿠폰 정책 생성 완료")
        
    except Exception as e:
        print(f"❌ 쿠폰 정책 생성 실패: {e}")
        connection.rollback()
        raise

def create_sample_users(connection):
    """샘플 사용자 데이터 생성"""
    try:
        # 기존 사용자 데이터 확인
        result = connection.execute("SELECT COUNT(*) FROM point")
        user_count = result.fetchone()[0] if result else 0
        
        if user_count == 0:
            # 샘플 사용자 포인트 데이터 생성
            users = []
            for i in range(1, 101):  # 100명의 사용자
                users.append((
                    i,  # user_id
                    random.randint(1000, 10000),  # current_point (1000-10000 포인트)
                    datetime.now(),  # created_time
                    datetime.now(),  # updated_time
                    0  # version
                ))
            
            sql = """
            INSERT INTO point (user_id, current_point, created_time, updated_time, version)
            VALUES (%s, %s, %s, %s, %s)
            """
            connection.executemany(sql, users)
            
            connection.commit()
            print(f"✅ {len(users)}명의 샘플 사용자 생성 완료")
        else:
            print(f"✅ 기존 사용자 {user_count}명 확인됨")
            
    except Exception as e:
        print(f"❌ 사용자 데이터 생성 실패: {e}")
        connection.rollback()
        raise

def main():
    """메인 함수"""
    print("🚀 더미 데이터 생성 시작...")
    
    # 데이터베이스 연결
    connection = create_connection()
    if not connection:
        return
    
    try:
        # 쿠폰 정책 생성
        create_coupon_policies(connection)
        
        # 샘플 사용자 생성
        create_sample_users(connection)
        
        print("✅ 더미 데이터 생성 완료!")
        
    except Exception as e:
        print(f"❌ 더미 데이터 생성 중 오류 발생: {e}")
    finally:
        connection.close()
        print("🔌 데이터베이스 연결 종료")

if __name__ == "__main__":
    main()

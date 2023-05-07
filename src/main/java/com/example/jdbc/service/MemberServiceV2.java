package com.example.jdbc.service;

import com.example.jdbc.domain.Member;
import com.example.jdbc.repository.MemberRepositoryV1;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.support.JdbcUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * 트랜잭션 - 파라미터 연동, 풀을 고려한 종료
 */
@Slf4j
@RequiredArgsConstructor
public class MemberServiceV2 {
    private final MemberRepositoryV1 memberRepository;
    private final DataSource dataSource;

    public void accountTransfer(String fromId,String toId, int money) throws SQLException {
        Connection con = dataSource.getConnection();
        try{
            con.setAutoCommit(false);// 트랜잭션 시작
            // 비즈니스 로직
            Member fromMember = memberRepository.findById(fromId);
            Member toMember = memberRepository.findById(toId);

            memberRepository.update(fromId, fromMember.getMoney() - money);
            validation(toMember);
            memberRepository.update(toId, toMember.getMoney() + money);
            con.commit();// 성공 시 커밋
        } catch (Exception e){
            con.rollback();// 실패 시 롤백
            throw new IllegalStateException(e);
        }finally {
            release(con);
        }


        // 시작

        //롤백
    }

    private static void release(Connection con) {
        if(con !=null)
        try{
            con.setAutoCommit(true); //
            con.close();
        }catch (Exception e){
            log.info("error ",e);
        }
    }

    private static void validation(Member toMember) {
        if(toMember.getMemberId().equals("ex")){
            throw new IllegalStateException("이체 중 예외 발생");
        }
    }
}

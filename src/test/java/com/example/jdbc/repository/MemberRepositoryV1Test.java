package com.example.jdbc.repository;

import com.example.jdbc.domain.Member;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.NoSuchMessageException;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.NoSuchElementException;

import static com.example.jdbc.connection.ConnectionConst.*;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@Transactional
class MemberRepositoryV1Test {
    MemberRepositoryV1 repository;
    @BeforeEach
    void beforeEach(){
        // 기본 DriverManager - 항상 새로운 커넥션을 획득
        //DriverManagerDataSource dataSource = new DriverManagerDataSource(URL,USERNAME,PASSWORD);


        // 커넥션 풀링
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(URL);
        dataSource.setUsername(USERNAME);
        dataSource.setPassword(PASSWORD);

        repository = new MemberRepositoryV1(dataSource);
    }
    @Test
    void crud() throws SQLException, InterruptedException {
        // save
        Member member = new Member("memberV12", 10000);
        repository.save(member);

        //findById
        Member findMember = repository.findById(member.getMemberId());
        log.info("findMember ={}", findMember);
        log.info("member == findMember {}", member == findMember);
        log.info("member equals findMember", member.equals(findMember));
        assertThat(findMember).isEqualTo(member);

        //update: money: 10000 -> 20000
        repository.update(member.getMemberId(), 20000);
        Member updatedMember = repository.findById(member.getMemberId());
        assertThat(updatedMember.getMoney()).isEqualTo(20000);

        //delete
        repository.delete(member.getMemberId());
        Assertions.assertThatThrownBy(()->repository.findById(member.getMemberId()))
                .isInstanceOf(NoSuchElementException.class);

        Thread.sleep(1000);
    }
}
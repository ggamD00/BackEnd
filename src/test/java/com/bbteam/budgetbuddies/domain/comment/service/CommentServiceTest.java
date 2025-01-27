package com.bbteam.budgetbuddies.domain.comment.service;

import com.bbteam.budgetbuddies.domain.comment.dto.CommentRequestDto;
import com.bbteam.budgetbuddies.domain.comment.dto.CommentResponseDto;
import com.bbteam.budgetbuddies.domain.discountinfo.entity.DiscountInfo;
import com.bbteam.budgetbuddies.domain.discountinfo.repository.DiscountInfoRepository;
import com.bbteam.budgetbuddies.domain.supportinfo.entity.SupportInfo;
import com.bbteam.budgetbuddies.domain.supportinfo.repository.SupportInfoRepository;
import com.bbteam.budgetbuddies.domain.user.entity.User;
import com.bbteam.budgetbuddies.domain.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * comment service는 다음과 같은 기능을 제공해야한다.
 * 1. comment 저장 기능
 * 2. 특정 게시글에 따른 comment return
 * 현재 게시글의 종류는 2가지로 각각 할인정보, 지원정보이다.
 * 즉, 할인정보, 지원정보 ID가 들어오면 해당 게시글에 대한 댓글 정보를 다 가지고 올 수 있어야한다.
 * 아마 관리 측면에선 댓글 삭제 기능도 필요할 것이다.
 * 3. 특정 userid로 댓글 찾는 기능
 *  얘는 게시글 ID랑 제목 정도 같이???
 * 4. 특정 게시글 id로 댓글 찾는 기능
 */

@SpringBootTest
@Transactional
class CommentServiceTest {
    @Autowired
    CommentService commentService;

    @Autowired
    UserRepository userRepository;
    @Autowired
    DiscountInfoRepository discountInfoRepository;
    @Autowired
    SupportInfoRepository supportInfoRepository;
    @Autowired
    EntityManager em;

    @Test
    public void saveDiscountInfoCommentTest(){
        User user1 = User.builder()
                .name("tester1")
                .email("1234")
                .age(5)
                .phoneNumber("123456")
                .build();
        userRepository.save(user1);

        DiscountInfo sale1 = DiscountInfo.builder().title("무신사 할인").build();
        discountInfoRepository.save(sale1);

        CommentRequestDto.DiscountInfoCommentDto dto1 = CommentRequestDto.DiscountInfoCommentDto.builder()
                .discountInfoId(sale1.getId())
                .content("굿")
                .build();

        commentService.saveDiscountComment(user1.getId(), dto1);
        em.flush();

        List<CommentResponseDto.DiscountInfoCommentDto> returnDto = commentService.findByDiscountInfo(sale1.getId());

        Assertions.assertThat(returnDto.size()).isEqualTo(1);
        Assertions.assertThat(returnDto.get(0).getDiscountInfoId()).isEqualTo(sale1.getId());
        Assertions.assertThat(returnDto.get(0).getUserId()).isEqualTo(user1.getId());
        Assertions.assertThat(returnDto.get(0).getContent()).isEqualTo("굿");

    }

    @Test
    public void saveDiscountInfoCommentTest2(){
        User user1 = User.builder()
                .name("tester1")
                .email("1234")
                .age(5)
                .phoneNumber("123456")
                .build();
        userRepository.save(user1);

        User user2 = User.builder()
                .name("tester2")
                .email("12345")
                .age(7)
                .phoneNumber("1234567")
                .build();
        userRepository.save(user2);

        DiscountInfo sale1 = DiscountInfo.builder().title("무신사 할인").build();
        discountInfoRepository.save(sale1);
        DiscountInfo sale2 = DiscountInfo.builder().title("핫트랙스 할인").build();
        discountInfoRepository.save(sale2);


        CommentRequestDto.DiscountInfoCommentDto dto1 = CommentRequestDto.DiscountInfoCommentDto.builder()
                .discountInfoId(sale1.getId())
                .content("굿")
                .build();

        CommentRequestDto.DiscountInfoCommentDto dto2 = CommentRequestDto.DiscountInfoCommentDto.builder()
                .discountInfoId(sale1.getId())
                .content("좋아요")
                .build();
        CommentRequestDto.DiscountInfoCommentDto dto3 = CommentRequestDto.DiscountInfoCommentDto.builder()
                .discountInfoId(sale2.getId())
                .content("유용해요!")
                .build();

        commentService.saveDiscountComment(user1.getId(), dto1);
        commentService.saveDiscountComment(user2.getId(), dto2);
        commentService.saveDiscountComment(user1.getId(), dto3);

        em.flush();

        List<CommentResponseDto.DiscountInfoCommentDto> returnDto = commentService.findByDiscountInfo(sale1.getId());
        List<CommentResponseDto.DiscountInfoCommentDto> returnDto2 = commentService.findByDiscountInfo(sale2.getId());
        Assertions.assertThat(returnDto.size()).isEqualTo(2);
        Assertions.assertThat(returnDto.get(0).getDiscountInfoId()).isEqualTo(sale1.getId());
        Assertions.assertThat(returnDto.get(0).getUserId()).isEqualTo(user1.getId());
        Assertions.assertThat(returnDto.get(0).getContent()).isEqualTo("굿");
        Assertions.assertThat(returnDto.get(1).getDiscountInfoId()).isEqualTo(sale1.getId());
        Assertions.assertThat(returnDto.get(1).getUserId()).isEqualTo(user2.getId());
        Assertions.assertThat(returnDto.get(1).getContent()).isEqualTo("좋아요");

        Assertions.assertThat(returnDto2.size()).isEqualTo(1);
        Assertions.assertThat(returnDto2.get(0).getDiscountInfoId()).isEqualTo(sale2.getId());
        Assertions.assertThat(returnDto2.get(0).getUserId()).isEqualTo(user1.getId());
        Assertions.assertThat(returnDto2.get(0).getContent()).isEqualTo("유용해요!");

    }

    @Test
    void DiscountAnonymousCommentTest(){
        User user1 = User.builder()
                .name("tester1")
                .email("1234")
                .age(5)
                .phoneNumber("123456")
                .build();
        userRepository.save(user1);

        User user2 = User.builder()
                .name("tester2")
                .email("12345")
                .age(7)
                .phoneNumber("1234567")
                .build();

        User user3 = User.builder()
                .name("tester3")
                .email("1234553")
                .age(9)
                .phoneNumber("1232134567")
                .build();
        userRepository.save(user2);
        userRepository.save(user3);

        DiscountInfo sale1 = DiscountInfo.builder().title("무신사 할인").build();
        discountInfoRepository.save(sale1);
        DiscountInfo sale2 = DiscountInfo.builder().title("핫트랙스 할인").build();
        discountInfoRepository.save(sale2);


        CommentRequestDto.DiscountInfoCommentDto dto1 = CommentRequestDto.DiscountInfoCommentDto.builder()
                .discountInfoId(sale1.getId())
                .content("굿")
                .build();

        CommentRequestDto.DiscountInfoCommentDto dto2 = CommentRequestDto.DiscountInfoCommentDto.builder()
                .discountInfoId(sale1.getId())
                .content("좋아요")
                .build();
        CommentRequestDto.DiscountInfoCommentDto dto3 = CommentRequestDto.DiscountInfoCommentDto.builder()
                .discountInfoId(sale2.getId())
                .content("유용해요!")
                .build();
        CommentRequestDto.DiscountInfoCommentDto dto4 = CommentRequestDto.DiscountInfoCommentDto.builder()
                .discountInfoId(sale1.getId())
                .content("구웃!")
                .build();

        commentService.saveDiscountComment(user1.getId(), dto1);
        commentService.saveDiscountComment(user2.getId(), dto2);
        commentService.saveDiscountComment(user1.getId(), dto3);

        commentService.saveDiscountComment(user1.getId(), dto4);
        commentService.saveDiscountComment(user3.getId(), dto4);

        em.flush();

        List<CommentResponseDto.DiscountInfoCommentDto> result = commentService.findByDiscountInfo(sale1.getId());
        Long test1 = result.get(0).getAnonymousNumber();
        Long test2 = result.get(1).getAnonymousNumber();
        Long test3 = result.get(2).getAnonymousNumber();
        Long test4 = result.get(3).getAnonymousNumber();

        Assertions.assertThat(test1).isEqualTo(1);
        Assertions.assertThat(test2).isEqualTo(2);
        Assertions.assertThat(test3).isEqualTo(1);
        Assertions.assertThat(test4).isEqualTo(3);


    }

    @Test
    public void saveSupportInfoCommentTest2(){
        User user1 = User.builder()
                .name("tester1")
                .email("1234")
                .age(5)
                .phoneNumber("123456")
                .build();
        userRepository.save(user1);

        User user2 = User.builder()
                .name("tester2")
                .email("12345")
                .age(7)
                .phoneNumber("1234567")
                .build();
        userRepository.save(user2);

        SupportInfo info1 = SupportInfo.builder().title("국가장학금 신청").build();
        supportInfoRepository.save(info1);
        SupportInfo info2 = SupportInfo.builder().title("봉사활동").build();
        supportInfoRepository.save(info2);


        CommentRequestDto.SupportInfoCommentDto dto1 = CommentRequestDto.SupportInfoCommentDto.builder()
                .supportInfoId(info1.getId())
                .content("굿")
                .build();

        CommentRequestDto.SupportInfoCommentDto dto2 = CommentRequestDto.SupportInfoCommentDto.builder()
                .supportInfoId(info1.getId())
                .content("좋아요")
                .build();
        CommentRequestDto.SupportInfoCommentDto dto3 = CommentRequestDto.SupportInfoCommentDto.builder()
                .supportInfoId(info2.getId())
                .content("유용해요!")
                .build();

        commentService.saveSupportComment(user1.getId(), dto1);
        commentService.saveSupportComment(user2.getId(), dto2);
         commentService.saveSupportComment(user1.getId(), dto3);

        em.flush();

        List<CommentResponseDto.SupportInfoCommentDto> returnDto = commentService.findBySupportInfo(info1.getId());
        List<CommentResponseDto.SupportInfoCommentDto> returnDto2 = commentService.findBySupportInfo(info2.getId());
        Assertions.assertThat(returnDto.size()).isEqualTo(2);
        Assertions.assertThat(returnDto.get(0).getSupportInfoId()).isEqualTo(info1.getId());
        Assertions.assertThat(returnDto.get(0).getUserId()).isEqualTo(user1.getId());
        Assertions.assertThat(returnDto.get(0).getContent()).isEqualTo("굿");
        Assertions.assertThat(returnDto.get(1).getSupportInfoId()).isEqualTo(info1.getId());
        Assertions.assertThat(returnDto.get(1).getUserId()).isEqualTo(user2.getId());
        Assertions.assertThat(returnDto.get(1).getContent()).isEqualTo("좋아요");

        Assertions.assertThat(returnDto2.size()).isEqualTo(1);
        Assertions.assertThat(returnDto2.get(0).getSupportInfoId()).isEqualTo(info2.getId());
        Assertions.assertThat(returnDto2.get(0).getUserId()).isEqualTo(user1.getId());
        Assertions.assertThat(returnDto2.get(0).getContent()).isEqualTo("유용해요!");

    }

    @Test
    void supportAnonymousCommentTest(){
        User user1 = User.builder()
                .name("tester1")
                .email("1234")
                .age(5)
                .phoneNumber("123456")
                .build();
        userRepository.save(user1);

        User user2 = User.builder()
                .name("tester2")
                .email("12345")
                .age(7)
                .phoneNumber("1234567")
                .build();
        User user3 = User.builder()
                .name("tester432")
                .email("123423445")
                .age(7)
                .phoneNumber("1423234567")
                .build();
        User user4 = User.builder()
                .name("test43er2")
                .email("1232445")
                .age(7)
                .phoneNumber("123454267")
                .build();
        userRepository.save(user2);
        userRepository.save(user3);
        userRepository.save(user4);

        SupportInfo info1 = SupportInfo.builder().title("국가장학금 신청").build();
        supportInfoRepository.save(info1);
        SupportInfo info2 = SupportInfo.builder().title("봉사활동").build();
        supportInfoRepository.save(info2);


        CommentRequestDto.SupportInfoCommentDto dto1 = CommentRequestDto.SupportInfoCommentDto.builder()
                .supportInfoId(info1.getId())
                .content("굿")
                .build();

        CommentRequestDto.SupportInfoCommentDto dto2 = CommentRequestDto.SupportInfoCommentDto.builder()
                .supportInfoId(info1.getId())
                .content("좋아요")
                .build();
        CommentRequestDto.SupportInfoCommentDto dto3 = CommentRequestDto.SupportInfoCommentDto.builder()
                .supportInfoId(info2.getId())
                .content("유용해요!")
                .build();
        CommentRequestDto.SupportInfoCommentDto dto6 = CommentRequestDto.SupportInfoCommentDto.builder()
                .supportInfoId(info1.getId())
                .content("굿")
                .build();
        CommentRequestDto.SupportInfoCommentDto dto4 = CommentRequestDto.SupportInfoCommentDto.builder()
                .supportInfoId(info1.getId())
                .content("굿")
                .build();
        CommentRequestDto.SupportInfoCommentDto dto5 = CommentRequestDto.SupportInfoCommentDto.builder()
                .supportInfoId(info1.getId())
                .content("굿")
                .build();

        commentService.saveSupportComment(user1.getId(), dto1);
         commentService.saveSupportComment(user2.getId(), dto2);
         commentService.saveSupportComment(user1.getId(), dto3);
         commentService.saveSupportComment(user3.getId(), dto4);
         commentService.saveSupportComment(user4.getId(), dto5);
         commentService.saveSupportComment(user1.getId(), dto6);

        em.flush();

        List<CommentResponseDto.SupportInfoCommentDto> returnDto = commentService.findBySupportInfo(info1.getId());
        List<CommentResponseDto.SupportInfoCommentDto> returnDto2 = commentService.findBySupportInfo(info2.getId());

        Long test1 = returnDto.get(0).getAnonymousNumber();
        Long test2 = returnDto.get(1).getAnonymousNumber();
        Long test3 = returnDto.get(2).getAnonymousNumber();
        Long test4 = returnDto.get(3).getAnonymousNumber();
        Long test5 = returnDto.get(4).getAnonymousNumber();

        Assertions.assertThat(test1).isEqualTo(1);
        Assertions.assertThat(test2).isEqualTo(2);
        Assertions.assertThat(test3).isEqualTo(3);
        Assertions.assertThat(test4).isEqualTo(4);
        Assertions.assertThat(test5).isEqualTo(1);
    }



}
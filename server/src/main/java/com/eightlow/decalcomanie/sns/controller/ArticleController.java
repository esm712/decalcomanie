package com.eightlow.decalcomanie.sns.controller;

import com.eightlow.decalcomanie.perfume.service.IPerfumeService;
import com.eightlow.decalcomanie.sns.dto.*;
import com.eightlow.decalcomanie.sns.dto.request.CommentRequest;
import com.eightlow.decalcomanie.sns.dto.request.CreateArticleRequest;
import com.eightlow.decalcomanie.sns.dto.request.FeedInquiryRequest;
import com.eightlow.decalcomanie.sns.dto.request.UpdateArticleRequest;
import com.eightlow.decalcomanie.sns.dto.response.ArticleResponse;
import com.eightlow.decalcomanie.sns.dto.response.FeedResponse;
import com.eightlow.decalcomanie.sns.dto.response.Response;
import com.eightlow.decalcomanie.sns.mapper.ArticleDtoMapper;
import com.eightlow.decalcomanie.sns.mapper.CommentDtoMapper;
import com.eightlow.decalcomanie.sns.service.IArticleService;
import com.eightlow.decalcomanie.sns.service.IGradeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.*;

@RestController
@RequestMapping("/sns")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
@Slf4j
public class ArticleController {
    private final IArticleService articleService;
    private final IGradeService gradeService;
    private final IPerfumeService perfumeService;

    private final ArticleDtoMapper articleDtoMapper;
    private final CommentDtoMapper commentDtoMapper;

    // 글 작성
    @PostMapping("/create")
    public ResponseEntity<Map<String, String>> createArticle(@RequestBody @Valid CreateArticleRequest createArticleRequest, HttpServletRequest req) {
        String userId = articleService.getUserIdFromRequest(req);
        CreateArticleRequest createArticleReq = createArticleRequest.toBuilder().userId(userId).build();
        ArticleDto articleDto = articleDtoMapper.fromCreateArticleRequest(createArticleReq);
        int articleId = articleService.createArticle(articleDto);

        if(!createArticleReq.getPerfumeId().isEmpty()) {
            // ArticlePerfume테이블에도 글에서 포함되니 향수정보와 평가 넣어주기
            gradeService.createGradeFromRequest(articleId,
                    createArticleRequest.getPerfumeId(), createArticleRequest.getRate());
        } else {
            // 공병 태그가 붙어서 perfumelist가 비어있다면 0번향수(공병), 평점 정보 0를 넣어주자
            gradeService.createGradeFromRequest(articleId,
                    Arrays.asList(0), Arrays.asList(0.0F));
        }

        //TODO
        // 평점 등록시 perfume테이블의 평점 수정!!!
        // grade 테이블을 통해서 평균을 내준다 (perfume id 갯수 카운트, rate 더하기 로)
        // perfume service 호출해서 값을 넣어준다.

        // articlePerfueme에서 향수의 언급된 갯수 count해서 저장
        Map<Integer, PerfumeRateDto> perfumesMap = articleService.getPerfumeCountAndSumRate(createArticleRequest.getPerfumeId());

        for(int i = 0;i < createArticleRequest.getPerfumeId().size();i++) {


            perfumeService.updatePerfumeRate(perfumesMap.get(createArticleRequest.getPerfumeId().get(i)), createArticleRequest.getPerfumeId().get(i), createArticleRequest.getRate().get(i));
        }

        Map<String, String> res = new HashMap<>();
        res.put("articleId", String.valueOf(articleId));
        res.put("message", "글이 정상 등록되었습니다.");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(res);
    }


    // 글 상세 조회
    @GetMapping("/search/{articleId}")
    public ResponseEntity<ArticleResponse> getDetailById(@PathVariable int articleId, HttpServletRequest req) {
        String userId = articleService.getUserIdFromRequest(req);
        ArticleResponse articleResponse = articleService.getDetail(articleId, userId);

        return ResponseEntity.status(HttpStatus.OK).body(articleResponse);
    }

    /*
        피드(글) 조회 부분
     */

    // 사용자가 쓴 글을 조회(내가 쓴글 조회)
    @PostMapping("/user")
    public ResponseEntity<List<FeedResponse>> getArticleByUserId(@RequestBody FeedInquiryRequest feedInquiryRequest, HttpServletRequest req) {
        String userId = feedInquiryRequest.getUserId();

        if(userId == null) {
            userId = articleService.getUserIdFromRequest(req);
        }

        List<FeedResponse> responses  = articleService.getArticleByUserId(feedInquiryRequest, userId);

        return ResponseEntity.status(HttpStatus.OK).body(responses);
    }

    // 팔로워의 피드를 조희
    @PostMapping("/feed/following")
    public ResponseEntity<List<FeedResponse>> getFollowingArticles(@RequestBody FeedInquiryRequest feedInquiryRequest, HttpServletRequest req) {
        String userId = articleService.getUserIdFromRequest(req);
        // TODO: 사용자의 follower list를 받아와서 그 사용자들의 피드를 조회해야함 (정렬은 최신순으로)
        List<FeedResponse> responses  = articleService.getArticlesOfFollowingUser(feedInquiryRequest, userId);

        return ResponseEntity.status(HttpStatus.OK).body(responses);
    }

    // 피드 인기순 조회
    @PostMapping("/feed/popularity")
    public ResponseEntity<List<FeedResponse>> getPopularArticles(@RequestBody @Valid FeedInquiryRequest feedInquiryRequest, HttpServletRequest req) {
        String userId = articleService.getUserIdFromRequest(req);

        List<FeedResponse> responses  = articleService.getPopularArticles(feedInquiryRequest, userId);

        return ResponseEntity.status(HttpStatus.OK).body(responses);
    }

    @PostMapping("/feed/latest")
    public ResponseEntity<List<FeedResponse>> getLatestArticles(@RequestBody @Valid FeedInquiryRequest feedInquiryRequest, HttpServletRequest req) {
        String userId = articleService.getUserIdFromRequest(req);
        List<FeedResponse> responses  = articleService.getLatestArticles(feedInquiryRequest, userId);

        return ResponseEntity.status(HttpStatus.OK).body(responses);
    }

    // 향수별 피드 조회
    @PostMapping("/perfume/{perfumeId}")
    public ResponseEntity<List<FeedResponse>> getPerfumeArticles(@PathVariable int perfumeId,
                                                                 @RequestBody @Valid FeedInquiryRequest feedInquiryRequest,
                                                                 HttpServletRequest req) {
        String userId = articleService.getUserIdFromRequest(req);
        List<FeedResponse> responses  = articleService.getArticleByPerfumeId(feedInquiryRequest, userId, perfumeId);

        return ResponseEntity.status(HttpStatus.OK).body(responses);
    }

    // 조회 파트 끝

    // 글 수정
    @PutMapping("/update")
    public ResponseEntity<Response> modifyArticle(@RequestBody @Valid
                                                  UpdateArticleRequest updateArticleRequest, HttpServletRequest req) {
        String userId = articleService.getUserIdFromRequest(req);
        UpdateArticleRequest updateArticleReq = updateArticleRequest.toBuilder().userId(userId).build();
        ArticleDto articleDto = articleDtoMapper.fromUpdateArticleRequest(updateArticleReq);

        int status = articleService.updateArticle(articleDto, userId);
        int articleId = updateArticleReq.getArticleId();

        // 글 수정이 성공 한 경우
        if(status == 200) {
            // 공병 태그의 경우 향수 정보가 담겨오지 않아서 [] 로 오게됨
            // size가 0이 아니라면 향수정보 변경 반영
            if (updateArticleReq.getPerfumeId().size() != 0) {
                // 향수 평가 수정
                gradeService.modifyGradeFromRequest(articleId,
                        updateArticleRequest.getPerfumeId(), updateArticleRequest.getRate());
            }

        }

        // 수정 된걸 보여주는건 프런트 단에서 다시 get을 보내주는것이 맞는것 같다!!
        return resultMessage(status);
    }

    //글 삭제
    @DeleteMapping("/delete/{articleId}")
    public ResponseEntity<Response> deleteArticle(@PathVariable int articleId, HttpServletRequest req) {
        String userId = articleService.getUserIdFromRequest(req);
        // 글 삭제
        int status = articleService.deleteArticle(userId, articleId);

        //글 삭제가 정상적으로 되었다면 댓글, 게시물 향수, 평가한 평점 정보 삭제 실행
        if(status == 200) {
            // 글삭제시 그 게시물에 달린 댓글 전부 삭제
            articleService.deleteCommentByArticleId(articleId);

            // 임베디드된 게시물 향수, 평점 삭제
            articleService.deleteArticlePerfumeByArticleId(articleId);
        }

        return resultMessage(status);
    }

    /* 댓글 작업 part*/
    // 댓글 작성
    @PostMapping("/comment/create")
    public ResponseEntity<List<CommentDto>> createComment(@RequestBody CommentRequest commentRequest, HttpServletRequest req) {
        String userId = articleService.getUserIdFromRequest(req);
        CommentRequest creq = commentRequest.toBuilder()
                .userId(userId)
                .build();

        CommentDto commentDto = commentDtoMapper.fromCommentRequest(creq);

        articleService.createComment(commentDto);

        // 전체 댓글 리스트 반환을 위한 댓글 조회 (최적화로 쓴 댓글만 보내주도록 하면 되지 않을까?)
        List<CommentDto> comments = articleService.getComments(creq.getArticleId());

        return ResponseEntity.status(HttpStatus.OK).body(comments);
    }

    // 댓글 수정
    @PutMapping("/comment/update")
    public ResponseEntity<Response> updateComment(@RequestBody CommentRequest commentRequest, HttpServletRequest req) {
        String userId = articleService.getUserIdFromRequest(req);
        CommentRequest creq = commentRequest.toBuilder()
                .userId(userId)
                .build();
        CommentDto commentDto = commentDtoMapper.fromCommentRequest(creq);
        ResponseEntity<Response> response = articleService.updateComment(commentDto);
        return response;
    }

    // 댓글 삭제
    @DeleteMapping("/comment/delete/{commentId}")
    public ResponseEntity<Response> deleteComment(@PathVariable int commentId,@RequestBody CommentRequest commentRequest, HttpServletRequest req) {
        String userId = articleService.getUserIdFromRequest(req);
        // 댓글 삭제
        int statusCode = articleService.deleteComment(commentId, userId);
        // 이미 없는 댓글의 경우 댓글수 감소 X
        if (statusCode != -1) {
            // 댓글 삭제시 article 테이블의 comment의 갯수를 줄여준다
            articleService.decreaseCommentCount(commentRequest.getArticleId());
        }


        return resultMessage(statusCode);
    }


    /* 피드 좋아요, 북마크 기능 파트*/

    @PostMapping("/like")
    public ResponseEntity<Response> likeArticle(@RequestBody HeartDto heartDto, HttpServletRequest req) {
        String userId = articleService.getUserIdFromRequest(req);

        HeartDto heart = heartDto.toBuilder()
                .userId(userId)
                .build();

        int status = articleService.likeArticle(heart);
        return resultMessage(status);
    }

    @PostMapping("/dislike")
    public ResponseEntity<Response> dislikeArticle(@RequestBody HeartDto heartDto, HttpServletRequest req) {
        String userId = articleService.getUserIdFromRequest(req);
        HeartDto heart = heartDto.toBuilder().userId(userId).build();
        int status = articleService.dislikeArticle(heart);
        return resultMessage(status);
    }

    @PostMapping("/bookmark")
    public ResponseEntity<Response> bookmarkArticle(@RequestBody BookMarkDto bookmarkDto, HttpServletRequest req) {
        String userId = articleService.getUserIdFromRequest(req);
        BookMarkDto bookmark = bookmarkDto.toBuilder().userId(userId).build();
        int status = articleService.bookmarkArticle(bookmark);
        return resultMessage(status);
    }

    @PostMapping("/cancelBookmark")
    public ResponseEntity<Response> cancelBookmarkedArticle(@RequestBody BookMarkDto bookmarkDto, HttpServletRequest req) {
        String userId = articleService.getUserIdFromRequest(req);
        BookMarkDto bookmark = bookmarkDto.toBuilder().userId(userId).build();
        int status = articleService.cancelBookmarkArticle(bookmark);
        return resultMessage(status);
    }

    // response 메세지를 만들어주는 공통 메서드
    private ResponseEntity<Response> resultMessage(int status) {
        if (status == 200) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(Response.builder()
                            .message("정상적으로 수행되었습니다.")
                            .build());
        } else if (status == 401) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Response.builder()
                            .message("잘못된 사용자 접근입니다.")
                            .build());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Response.builder()
                            .message("404")
                            .build());
        }
    }
}

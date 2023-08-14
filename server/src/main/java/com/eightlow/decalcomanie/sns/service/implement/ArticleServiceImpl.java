package com.eightlow.decalcomanie.sns.service.implement;

import com.eightlow.decalcomanie.perfume.dto.PerfumeDto;
import com.eightlow.decalcomanie.perfume.dto.ScentDto;
import com.eightlow.decalcomanie.perfume.entity.Perfume;
import com.eightlow.decalcomanie.perfume.mapper.PerfumeMapper;
import com.eightlow.decalcomanie.perfume.mapper.ScentMapper;
import com.eightlow.decalcomanie.perfume.service.IPerfumeService;
import com.eightlow.decalcomanie.sns.dto.*;
import com.eightlow.decalcomanie.sns.dto.response.ArticleResponse;
import com.eightlow.decalcomanie.sns.dto.response.FeedResponse;
import com.eightlow.decalcomanie.sns.dto.response.Response;
import com.eightlow.decalcomanie.sns.entity.*;
import com.eightlow.decalcomanie.sns.mapper.*;
import com.eightlow.decalcomanie.sns.repository.*;
import com.eightlow.decalcomanie.sns.service.IArticleService;
import com.eightlow.decalcomanie.user.dto.UserInfoDto;
import com.eightlow.decalcomanie.user.dto.response.FollowingResponse;
import com.eightlow.decalcomanie.user.entity.User;
import com.eightlow.decalcomanie.user.entity.UserScent;
import com.eightlow.decalcomanie.user.mapper.UserMapper;
import com.eightlow.decalcomanie.user.service.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.spec.PSource;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;

import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
@Slf4j
public class ArticleServiceImpl implements IArticleService {

    private final IUserService userService;
    private final IPerfumeService perfumeService;

    private final ArticleRepository articleRepository;
    private final ArticleMapper articleMapper;

    private final ArticlePerfumeRepository articlePerfumeRepository;
    private final ArticlePerfumeMapper articlePerfumeMapper;

    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    private final HeartRepository heartRepository;
    private final HeartMapper heartMapper;

    private final BookMarkRepository bookmarkRepository;
    private final BookMarkMapper bookmarkMapper;

    private final ScentMapper scentMapper;
    private final UserMapper userMapper;
    private final PerfumeMapper perfumeMapper;

    private final EntityManager entityManager;


    @Override
    public boolean existArticleById(int id) {
        return false;
    }

    @Override
    @Transactional
    public int createArticle(ArticleDto articleDto) {
        log.info("ArticleServiceImpl::: createArticle start");
        User user = entityManager.find(User.class, articleDto.getUserId());
        Article article = Article.builder()
                .user(user)
                .content(articleDto.getContent())
                .build();
        Article a = articleRepository.save(article);
        System.out.println(a.getArticleId());
        log.info("ArticleServiceImpl::: finish ", String.valueOf(article.getArticleId()));
        return article.getArticleId();
    }

    @Override
    @Transactional
    public int updateArticle(ArticleDto articleDto, String userId) {
        log.info("ArticleServiceImpl::: updateArticle start");

        // 수정하려는 글의 articleId를 가져옴
        int articleId = articleDto.getArticleId();

        // 해당 articleId를 가진 글 조회
        Article article = entityManager.find(Article.class, articleId);
//        Optional<Article> optionalArticle = articleRepository.findByArticleId(articleId);

        // 해당 articleId를 가진 글이 존재하는지 확인
        if (article != null) {
            // 글이 존재하는 경우, 수정 작업 진행
            // 글의 userId와 수정하려는 userId를 비교하여 일치하는지 확인
            if (article.getUser().getUserId().equals(userId)) {
                // 일치하는 경우, 수정 작업 진행
                // 수정할 내용을 업데이트

                Article modifiedArticle = Article.builder()
                        .articleId(articleId)
                        .user(article.getUser())
                        .content(articleDto.getContent())
                        .heart(article.getHeart())
                        .comment(article.getComment())
                        .updatedAt(LocalDateTime.now())
                        .build();

                // 수정사항 저장
                articleRepository.save(modifiedArticle);

                log.info("ArticleServiceImpl::: finish ");
            } else {
                // userId가 일치하지 않는 경우, 권한이 없음을 알리는 예외 또는 메시지를 반환
                // throw new UnauthorizedAccessException("댓글을 수정할 권한이 없습니다.");
                return 401;
            }
        }

        //TODO:
        // 해당 articleId를 가진 글이 존재하지 않는 경우, 적절한 예외 또는 메시지를 반환 하도록 해보자 (else해서)

        return 200;
    }

    @Override
    @Transactional
    public int deleteArticle(String userId, int articleId) {
        log.info("ArticleServiceImpl::: deleteArticle start");
        // 해당 articleId를 가진 글 조회
        Article article = entityManager.find(Article.class, articleId);

        // 해당 articleId를 가진 글이 존재하는지 확인
        if (article != null) {
            // 글이 존재하는 경우, 삭제 작업 진행

            // 댓글의 userId와 수정하려는 userId를 비교하여 일치하는지 확인
            if (article.getUser().getUserId().equals(userId)) {
                articleRepository.deleteById(articleId);
            } else {
                // userId가 일치하지 않는 경우, 권한이 없음을 알리는 예외 또는 메시지를 반환
                // throw new UnauthorizedAccessException("댓글을 수정할 권한이 없습니다.");
                return 401;
            }
        }
        //TODO
        // 1. 사용자 인증 로직 추가
        // 2. status code 리턴 필요

//        log.info("ArticleServiceImpl::: finish ", String.valueOf(article.getArticleId()));
        return 200;
    }

//    @Override
//    @Transactional
//    public void createArticlePerfume(int articleId, List<Integer> perfumes) {
//        log.info("ArticleServiceImpl::: createArticlePerfume start");
//        for (int i = 0; i < perfumes.size(); i++) {
//            ArticlePerfumeDto articlePerfumeDto = new ArticlePerfumeDto(articleId, perfumes.get(i));
//            articlePerfumeRepository.save(articlePerfumeMapper.toEntity(articlePerfumeDto));
//        }
//        log.info("ArticleServiceImpl::: finish ");
//    }


    @Override
    @Transactional
    public Article searchArticleByArticleId(int articleId) {
        log.info("ArticleServiceImpl::: searchArticleByArticleId start");
        Article article = entityManager.find(Article.class, articleId);
        log.info("ArticleServiceImpl::: finish ", String.valueOf(article.getArticleId()));
        return article;
    }



    /*
        피드(글) 조회 파트
     */
    @Override
    @Transactional
    public List<Article> searchArticleByUserId(String userId) {
        return articleRepository.findByUserId(userId);
    }

    @Override
    @Transactional
    public List<FeedResponse> getArticleByUserId(String userId) {
        List<Article> articles = searchPopularArticles();
        List<FeedResponse> feedResponse = getFeedInfoForArticles(userId, articles);
        return feedResponse;
    }

    @Override
    @Transactional
    public List<Article> searchArticlesOfFollowingUser(String userId) {

        List<FollowingResponse> followingResponses = userService.getFollowingUsers(userId);
        System.out.println(followingResponses.toString());

        List<String> userIds = followingResponses.stream()
                .map(FollowingResponse::getUserId)
                .collect(Collectors.toList());
        log.info(String.valueOf(userIds));

        List<Article> articles = new ArrayList<>();

        // 팔로워들의 글을 다 가져옴 (select 한번에)
        articles= articleRepository.findByUser_UserIdIn(userIds);
//        for(String id : userIds) {
//            articles.addAll(searchArticleByUserId(id));
//        }


        Collections.sort(articles, Comparator.comparing(Article::getCreatedAt).reversed());
        log.info(articles.toString());
        return articles;
    }

    @Override
    public List<FeedResponse> getArticlesOfFollowingUser(String userId) {
        List<Article> articles = searchArticlesOfFollowingUser(userId);
        List<FeedResponse> feedResponse = getFeedInfoForArticles(userId, articles);
        return feedResponse;
    }

    @Override
    @Transactional
    public List<Article> searchPopularArticles() {
        return articleRepository.findArticlesOrderByHeart();
    }

    @Override
    public List<FeedResponse> getPopularArticles(String userId) {
        List<Article> articles = searchPopularArticles();
        List<FeedResponse> feedResponse = getFeedInfoForArticles(userId, articles);
        return feedResponse;
    }

    @Override
    @Transactional
    public List<Article> searchLatestArticles() {
        return articleRepository.findArticlesOrderByCreateTime();
    }

    @Override
    @Transactional
    public List<FeedResponse> getLatestArticles(String userId) {
        List<Article> articles= searchLatestArticles();
        List<FeedResponse> responses  = getFeedInfoForArticles(userId, articles);
        return responses;
    }


    @Override
    @Transactional
    public List<Article> searchArticleByPerfumeId(int perfumeId) {
        List<ArticlePerfume> articlePerfumes = articlePerfumeRepository.findByPerfume_PerfumeId(perfumeId);
        List<Integer> articleIds = new ArrayList<>();
        for(ArticlePerfume articlePerfume : articlePerfumes) {
            articleIds.add(articlePerfume.getArticle().getArticleId());
        }

        // articleId를 통해 정보 조회 (select한번)
        List<Article> articles = new ArrayList<>();
        articles = articleRepository.findByArticleIdIn(articleIds);
        System.out.println(articles);
        return articles;
    }

    @Override
    public List<FeedResponse> getArticleByPerfumeId(String userId, int perfumeId) {
        List<Article> articles = searchArticleByPerfumeId(perfumeId);
        List<FeedResponse> feedResponses = getFeedInfoForArticles(userId, articles);
        return feedResponses;
    }


    // 피드 조회 끝

    @Override
    @Transactional
    public List<ArticlePerfume> searchArticlePerfumeId(int articleId) {
        log.info("ArticleServiceImpl::: searchArticlePerfumeId start");
        List<ArticlePerfume> articlePerfumes = articlePerfumeRepository.findByArticle_ArticleId(articleId);
//        List<Integer> perfumes = new ArrayList<>();
//        for(ArticlePerfume perfume : articlePerfumes) {
//            perfumes.add(perfume.getPerfume().getPerfumeId());
//        }
        log.info("ArticleServiceImpl::: finish ");
        return articlePerfumes;
    }



    /*
        댓글 파트
     */
    @Override
    @Transactional
    public void createComment(CommentDto commentDto) {
        log.info("ArticleServiceImpl::: createComment start");
        System.out.println(commentDto);
        // TODO: 이 부분이 select로 데이터를 많이 가져옴 (개선 가능성 있음)
        Article article = entityManager.find(Article.class, commentDto.getArticleId());
        User user = entityManager.find(User.class, commentDto.getUserId());
//        Article article = articleRepository.findByArticleId(commentDto.getArticleId()).orElse(null);

        Comment comment = Comment.builder()
                .article(article)
                .user(user)
                .content(commentDto.getContent())
                .build();

        commentRepository.save(comment);

        // TODO: 댓글 갯수 하나 늘려 주는 부분 추가 필요
        // 게시물의 heart갯수 + 1
        articleRepository.increaseCommentCount(commentDto.getArticleId());

        log.info("ArticleServiceImpl::: finish ");
    }

    @Override
    @Transactional
    public ResponseEntity<Response> updateComment(CommentDto commentDto) {
        log.info("ArticleServiceImpl::: updateComment start");
        // 수정하려는 댓글의 commentId를 가져옴
        int commentId = commentDto.getCommentId();
        int articleId = commentDto.getArticleId();

        // 해당 commentId를 가진 댓글 조회
        Comment comment = entityManager.find(Comment.class, commentId);
//        Optional<Comment> optionalComment = commentRepository.findByArticleIdAndCommentId(articleId, commentId);

        // 해당 commentId를 가진 댓글이 존재하는지 확인
        if (comment != null) {
            // 댓글이 존재하는 경우, 수정 작업 진행
            // Comment existingComment = optionalComment.get();

            // 댓글의 userId와 수정하려는 userId를 비교하여 일치하는지 확인
            if (comment.getUser().getUserId().equals(commentDto.getUserId())) {
                // 일치하는 경우, 수정 작업 진행
                // 수정할 내용을 업데이트
                // existingComment.toBuilder().content(commentDto.getContent()).build();
                Comment existingComment = comment.toBuilder().content(commentDto.getContent()).build();
//                existingComment.setContent(commentDto.getContent());

                // 수정된 댓글 저장
                commentRepository.save(existingComment);

                log.info("ArticleServiceImpl::: finish ", String.valueOf(comment));
            } else {
                // userId가 일치하지 않는 경우, 권한이 없음을 알리는 예외 또는 메시지를 반환
                // throw new UnauthorizedAccessException("댓글을 수정할 권한이 없습니다.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                        Response.builder()
                                .message("잘못된 사용자 접근입니다.")
                                .build());
            }

//        } else {
//            // 해당 commentId를 가진 댓글이 존재하지 않는 경우, 적절한 예외 또는 메시지를 반환
////            throw new CommentNotFoundException("해당 댓글을 찾을 수 없습니다.");
//        }
//        Comment comment = commentRepository.save(commentMapper.toEntity(commentDto));
//        log.info("ArticleServiceImpl::: finish ", String.valueOf(comment));
        }
        return ResponseEntity.status(HttpStatus.OK).body(
                Response.builder()
                        .message("댓글이 수정되었습니다.")
                        .build());
    }

    @Override
    public int deleteComment(int commentId, String userId) {
        log.info("ArticleServiceImpl::: updateComment start");

        // 해당 commentId를 가진 댓글 조회
        Comment comment = entityManager.find(Comment.class, commentId);
        System.out.println(comment);

        // 해당 commentId를 가진 댓글이 존재하는지 확인
        if (comment != null) {
            // 댓글이 존재하는 경우, 삭제 작업 진행
            // 댓글의 userId와 수정하려는 userId를 비교하여 일치하는지 확인
            if (comment.getUser().getUserId().equals(userId)) {
                // 일치하는 경우, 삭제 작업 진행
                try {
                    commentRepository.deleteByCommentId(commentId);
                } catch (Exception e) {
                    log.info(e.getMessage());
                }
            } else {
                // userId가 일치하지 않는 경우, 권한이 없음을 알리는 예외 또는 메시지를 반환
                // throw new UnauthorizedAccessException("댓글을 수정할 권한이 없습니다.");
                return 401;
            }

//        } else {
//            // 해당 commentId를 가진 댓글이 존재하지 않는 경우, 적절한 예외 또는 메시지를 반환
////            throw new CommentNotFoundException("해당 댓글을 찾을 수 없습니다.");
//        }
//        Comment comment = commentRepository.save(commentMapper.toEntity(commentDto));
//        log.info("ArticleServiceImpl::: finish ", String.valueOf(comment));
        } else {
            return -1;
        }
        return 200;
    }

    @Override
    public void increaseCommentCount(int articleId) {
        log.info("ArticleServiceImpl::: modifyCommentCount start");
        // 주어진 articleId 의 comment 갯수를 하나 늘린다.
        articleRepository.increaseCommentCount(articleId);
        log.info("ArticleServiceImpl::: finish ");
    }

    @Override
    @Transactional
    public void decreaseCommentCount(int articleId) {
        log.info("ArticleServiceImpl::: modifyCommentCount start");
        // 주어진 articleId 의 comment 갯수를 하나 줄인다.
        articleRepository.decreaseCommentCount(articleId);
        log.info("ArticleServiceImpl::: finish ");
    }

    @Override
    @Transactional
    public List<CommentDto> getComments(int articleId) {
        log.info("ArticleServiceImpl::: getComments start");
        List<Comment> comments = commentRepository.findByArticle_ArticleId(articleId);
        log.info("ArticleServiceImpl::: finish ", String.valueOf(comments));
        return commentMapper.toDTOs(comments);
    }

    @Override
    @Transactional
    public int deleteCommentByArticleId(int articleId) {
        // 이미 글을 삭제할때 글의 userId를 확인했음으로 여기서는 확인을 하지 않고 삭제를 진행
        log.info("ArticleServiceImpl::: deleteCommentByArticleId start");
        commentRepository.deleteAllByArticleId(articleId);
        log.info("ArticleServiceImpl::: deleteCommentByArticleId finish");
        return 200;
    }

    @Override
    @Transactional
    public void deleteArticlePerfumeByArticleId(int articleId) {
        log.info("ArticleServiceImpl::: deleteArticlePerfumeByArticleId start");
        // 이미 글을 삭제할때 글의 userId를 확인했음으로 여기서는 확인을 하지 않고 삭제를 진행
        articlePerfumeRepository.deleteAllByArticleId(articleId);
        log.info("ArticleServiceImpl::: deleteArticlePerfumeByArticleId finish");
    }

    @Override
    public ArticleResponse getDetail(int articleId, String userId) {
        Article article = searchArticleByArticleId(articleId);

        // 사용자가 글 작성자를 팔로우 했는지
        System.out.println(article.getUser().getUserId());
        boolean isFollowed = userService.isFollowing(userId, article.getUser().getUserId());

        //articleId를 통해서 게시물의 임베디드된 향수정보를 가져온다.
        List<ArticlePerfume> perfumeList = searchArticlePerfumeId(articleId);

        // 작성자: 프로필, 닉네임, 선호 비선호향
        UserInfoDto userInfo = userService.getUserInfo(article.getUser().getUserId());

        // 향수 평점 정보
        List<Integer> rateInfo = new ArrayList<>();
        // 임베디드된 향수: 이름, 브랜드, 향수 사진 url
        List<PerfumeDto> perfumes = new ArrayList<>();

        // 공병태그가 없는 일반 향수 평점 정보가 달린 게시글 처리
        if (!(perfumeList.size() == 1 && perfumeList.get(0).getPerfume().getPerfumeId() == 0)) {
            System.out.println("공병이 달린 게시물이 아닙니다.");

            // 임베디드된 향수 정보와 평점 정보 저장
            for(ArticlePerfume articlePerfume: perfumeList) {
                rateInfo.add(articlePerfume.getRate());
                perfumes.add(perfumeMapper.toDto(articlePerfume.getPerfume()));
            }
            log.info(rateInfo.toString());
        }

        // 공병 태그가 달린 게시글은 아무것도 정보를 담지 않음

        // 댓글 리스트를 포함한다.
        List<CommentDto> comments = getComments(article.getArticleId());
        System.out.println(comments);

        //사용자의 팔로우 목록을 받아온다.
        List<FollowingResponse> followers = userService.getFollowingUsers(userId);

        System.out.println("tjoejtoijfilsjlfjs;dlj");


        List<UserInfoDto> commentUsers = new ArrayList<>();
        boolean flag = false;
        // 댓글 작성자정보들
        for (CommentDto commentDto : comments) {
            // System.out.println(commentDto.getUserId());
            UserInfoDto userInfoDto = userService.getUserInfo(commentDto.getUserId());
            // 사용자의 follower 목록을 보고 follow 여부를 넣어준다.
            if(followers != null) {
                for (FollowingResponse followerInfoResponse : followers) {
                    if(followerInfoResponse.getUserId().equals(commentDto.getUserId())) {
                        userInfoDto = userService.getUserInfo(commentDto.getUserId());
                        flag = true;
                        break;
                    }
                }
            }

            UserInfoDto uidto = UserInfoDto.builder()
                    .user(userInfoDto.getUser())
                    .favorities(userInfoDto.getFavorities())
                    .hates(userInfoDto.getHates())
                    .isFollowing(flag)
                    .build();

//            commentUsers.add(new UserInfoDto(userInfoDto.getUser(), userInfoDto.getFavorities(),
//                    userInfoDto.getHates(), flag));

            commentUsers.add(uidto);
        }

        // 좋아요 되었는지 확인
        boolean isHearted = checkHeartArticle(article.getArticleId(), userId);

        // 북마크 되었는지 확인
        boolean isBookmarked = checkBookmarkArticle(article.getArticleId(), userId);

        ArticleDto articleDto = ArticleDto.builder()
                .articleId(article.getArticleId())
                .userId(article.getUser().getUserId())
                .content(article.getContent())
                .heart(article.getHeart())
                .comment(article.getComment())
                .createdAt(article.getCreatedAt())
                .updatedAt(article.getUpdatedAt())
                .build();

        ArticleResponse articleResponse = new ArticleResponse(articleDto,  userInfo, isFollowed,comments, commentUsers,
                perfumes, rateInfo, isHearted, isBookmarked);
        return  articleResponse;
    }

    @Override
    @Transactional
    public int likeArticle(HeartDto heartDto) {
        log.info("ArticleServiceImpl::: likeArticle start");

        Article article = entityManager.find(Article.class, heartDto.getArticleId());
        User user = entityManager.find(User.class, heartDto.getUserId());
        System.out.println(user.getUserId());

        Heart heart = Heart.builder()
                .article(article)
                .user(user)
                .build();

        System.out.println(heart.getArticle().getArticleId());
        System.out.println(heart.getUser().getUserId());

        // Heart 테이블에 좋아요 저장
        heartRepository.save(heart);

        entityManager.flush();

        // 게시물의 heart갯수 + 1
        articleRepository.increaseHeartCountByArticleId(heartDto.getArticleId());

        log.info("ArticleServiceImpl::: likeArticle finish");
        return 200;
    }

//    @Transactional
//    public void increaseHeartCount(int articleId) {
//        articleRepository.increaseHeartCountByArticleId(articleId);
//    }

    @Override
    @Transactional
    public int dislikeArticle(HeartDto heartDto) {
        log.info("ArticleServiceImpl::: likeArticle start");
        // Heart 테이블에 좋아요한 부분 제거
        heartRepository.deleteByArticleIdAndUserId(heartDto.getArticleId(), heartDto.getUserId());

        // 게시물의 heart갯수 - 1
        articleRepository.decreaseHeartCountByArticleId(heartDto.getArticleId());

        log.info("ArticleServiceImpl::: likeArticle finish");
        return 200;
    }

    @Override
    @Transactional
    public int bookmarkArticle(BookMarkDto bookmarkDto) {
        log.info("ArticleServiceImpl::: bookmarkArticle start");

        Article article = entityManager.find(Article.class, bookmarkDto.getArticleId());
        User user = entityManager.find(User.class, bookmarkDto.getUserId());

        BookMark bookmark = BookMark.builder()
                .article(article)
                .user(user)
                .build();

        bookmarkRepository.save(bookmark);
        log.info("ArticleServiceImpl::: bookmarkArticle finish");
        return 200;
    }

    @Override
    @Transactional
    public int cancelBookmarkArticle(BookMarkDto bookmarkDto) {
        log.info("ArticleServiceImpl::: cancelBookmarkArticle start");
        bookmarkRepository.deleteByArticleIdAndUserId(bookmarkDto.getArticleId(), bookmarkDto.getUserId());
        log.info("ArticleServiceImpl::: cancelBookmarkArticle finish");
        return 200;
    }

    // 사용자가 좋아요를 누른 게시물인지 확인
    @Override
    @Transactional
    public boolean checkHeartArticle(int articleId, String userId) {
        Optional<Heart> hearts = heartRepository.findByArticle_ArticleIdAndUser_UserId(articleId, userId);

        if(hearts.isPresent()) {
            return true;
        }
        return false;
    }

    // 사용자가 북마크를 누른 게시물인지 확인
    @Override
    public boolean checkBookmarkArticle(int articleId, String userId) {
        Optional<BookMark> bookmarks = bookmarkRepository.findByArticle_ArticleIdAndUser_UserId(articleId, userId);

        if(bookmarks.isPresent()) {
            return true;
        }

        return false;
    }


    // request로 부터 userId를 뽑아내는 공통메서드
    @Override
    public String getUserIdFromRequest(HttpServletRequest request) {
        String userId = (String) request.getAttribute("userId");
        return userId;
    }

    // 피드 정보 조회 (사용자 정보, 글 정보, 포함된 향수 정보를 포함)
    private List<FeedResponse> getFeedInfoForArticles(String userId, List<Article> articles){

        final String GHOST = "00000000-0000-0000-0000-000000000000";
        // 각 article에 담긴 사용자 정보와 향수 정보를 담아둔 Dto의 리스트
        List<FeedResponse> feedResponses = new ArrayList<>();

        //사용자의 팔로우 목록을 받아온다.
        List<FollowingResponse> followerInfoResponses = userService.getFollowingUsers(userId);
        int cnt = 0;
        // articleId를 보고 사용자 정보와 향수 정보를 담음
        for(Article article: articles){
            if (cnt == 10) {
                break;
            }
            // TODO: perfumeId가 하나만 필요함으로 추후 쿼리 최적화가 필요!!(완료)
            int perfumeId = article.getArticlePerfume().get(0).getPerfume().getPerfumeId();
            log.info(String.valueOf(perfumeId));

            // 글을 쓴 사용자 entity
            User user = article.getUser();

            List<ScentDto> favorite = new ArrayList<>();
            List<ScentDto> hate = new ArrayList<>();

            // userScent 테이블에서 팔로잉 하고 있는 사람의 좋아하는 향, 싫어하는 향을 조회
            List<UserScent> userScentList = user.getUserScent();

            if(userScentList != null) {
                for (UserScent userScent : userScentList) {
                    if(userScent.getStatus().getValue().equals("FAVORITE")) {
                        favorite.add(scentMapper.toDto(userScent.getScent()));
                    } else if(userScent.getStatus().getValue().equals("HATE")) {
                        hate.add(scentMapper.toDto(userScent.getScent()));
                    }
                }
            }


            UserInfoDto userInfoDto = UserInfoDto.builder()
                    .user(userMapper.toDto(user))
                    .favorities(favorite)
                    .hates(hate)
                    .build();

            PerfumeDto perfumeDto = null;
            // TODO: 공병 태그 (아마 perfumeId 0번 ), 즉 향수가 아무것도 임베디드 안된 상황을 구현 헤야함!!
            if(perfumeId != 0) {
                Perfume perfume = article.getArticlePerfume().get(0).getPerfume();
                // 향수정보 가져옴
                perfumeDto = perfumeMapper.toDto(perfume);
            }

            // 팔로잉여부
            boolean isFollowed = false;
            for (FollowingResponse followerInfoResponse : followerInfoResponses) {
                if (followerInfoResponse.getUserId().equals(userInfoDto.getUser().getUserId())) {
//                if (followerInfoResponse.getUserId().equals(userId)) {
                    isFollowed = true;
                    break;
                }
            }

            // 팔로잉 / 팔로우 버튼생성해야하는지 여부?
            boolean isFollowingButtonActivate = true;
            if (userId.equals(userInfoDto.getUser().getUserId()) || userInfoDto.getUser().getUserId().equals(GHOST)) {
                isFollowingButtonActivate = false;
            }


            log.info(String.valueOf(perfumeDto));

            boolean isHearted = checkHeartArticle(article.getArticleId(), userId);
            boolean isBookmarked = checkBookmarkArticle(article.getArticleId(), userId);

            ArticleDto articleDto = ArticleDto.builder()
                    .articleId(article.getArticleId())
                    .userId(article.getUser().getUserId())
                    .content(article.getContent())
                    .heart(article.getHeart())
                    .comment(article.getComment())
                    .createdAt(article.getCreatedAt())
                    .updatedAt(article.getUpdatedAt())
                    .build();

            feedResponses.add(new FeedResponse(userInfoDto, isFollowed, isFollowingButtonActivate, articleDto, perfumeDto, isHearted, isBookmarked));
            cnt++;
        }
        return feedResponses;
    }
}

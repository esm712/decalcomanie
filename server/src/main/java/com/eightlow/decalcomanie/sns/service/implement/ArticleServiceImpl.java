package com.eightlow.decalcomanie.sns.service.implement;

import com.eightlow.decalcomanie.perfume.dto.PerfumeDto;
import com.eightlow.decalcomanie.perfume.dto.ScentDto;
import com.eightlow.decalcomanie.perfume.entity.Perfume;
import com.eightlow.decalcomanie.perfume.mapper.PerfumeMapper;
import com.eightlow.decalcomanie.perfume.mapper.ScentMapper;
import com.eightlow.decalcomanie.sns.dto.*;
import com.eightlow.decalcomanie.sns.dto.request.FeedInquiryRequest;
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
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;

import java.util.stream.Collectors;

import static com.eightlow.decalcomanie.sns.entity.QArticle.article;
import static com.eightlow.decalcomanie.sns.entity.QArticlePerfume.articlePerfume;
import static com.eightlow.decalcomanie.sns.entity.QBookMark.bookMark;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
@Slf4j
public class ArticleServiceImpl implements IArticleService {

    private final IUserService userService;
    private final ArticleRepository articleRepository;
    private final ArticlePerfumeRepository articlePerfumeRepository;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final HeartRepository heartRepository;
    private final BookMarkRepository bookmarkRepository;
    private final ScentMapper scentMapper;
    private final UserMapper userMapper;
    private final PerfumeMapper perfumeMapper;
    private final EntityManager entityManager;
    private final JPAQueryFactory queryFactory;


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
        log.info("ArticleServiceImpl::: finish ", String.valueOf(article.getArticleId()));
        return article.getArticleId();
    }

    @Override
    public Map<Integer, PerfumeRateDto> getPerfumeCountAndSumRate(List<Integer> perfumeIds) {
        List<Integer> perfumeCnt = new ArrayList<>();

        Map<Integer, PerfumeRateDto> perfumeMap = new HashMap<>();


        for(int i = 0; i < perfumeIds.size(); i++) {
            long cnt = Optional.ofNullable(queryFactory
                    .select(Wildcard.count)
                    .from(articlePerfume)
                    .where(articlePerfume.perfume.perfumeId.eq(perfumeIds.get(i)))
                    .fetchOne()
            ).orElse(0L);

            Float sum = queryFactory
                    .select(articlePerfume.rate.sum())
                    .from(articlePerfume)
                    .where(articlePerfume.perfume.perfumeId.eq(perfumeIds.get(i)))
                    .fetchOne();
//            float sum = queryFactory
//                    .selectFrom(articlePerfume.rate.sumAsFloat())
//                    .where(articlePerfume.perfume.perfumeId.eq(perfumeIds.get(i)))
//                    .fetchOne();
            perfumeMap.put(perfumeIds.get(i), new PerfumeRateDto((int)cnt, sum));

        }

        return perfumeMap;
    }

    @Override
    @Transactional
    public int updateArticle(ArticleDto articleDto, String userId) {
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
            } else {
                // userId가 일치하지 않는 경우, 권한이 없음을 알리는 예외 또는 메시지를 반환
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
                return 401;
            }
        }
        //TODO
        // 1. 사용자 인증 로직 추가
        // 2. status code 리턴 필요
        return 200;
    }

    @Override
    @Transactional
    public Article searchArticleByArticleId(int articleId) {
        Article article = entityManager.find(Article.class, articleId);
        return article;
    }

    /*
        피드(글) 조회 파트
     */
    @Override
    @Transactional
    public List<Article> searchArticleByUserId(FeedInquiryRequest feedInquiryRequest, String userId) {
        List<Article> articles = queryFactory
                .selectFrom(article)
                .where(
                        article.articleId.lt(feedInquiryRequest.getLastArticleId() == null ? 2130000000 : feedInquiryRequest.getLastArticleId()),
                        userIdEq(List.of(userId))
                )
                .orderBy(article.articleId.desc())
                .limit(feedInquiryRequest.getDataSize() == null ? 20 : feedInquiryRequest.getDataSize())
                .fetch();

        return articles;
    }

    @Override
    @Transactional
    public List<FeedResponse> getArticleByUserId(FeedInquiryRequest feedInquiryRequest, String userId) {

        List<Article> articles = searchArticleByUserId(feedInquiryRequest, userId);
        List<FeedResponse> feedResponse = getFeedInfoForArticles(userId, articles, feedInquiryRequest.getDataSize());
        return feedResponse;
//        return null;
    }

    @Override
    @Transactional
    public List<Article> searchArticlesOfFollowingUser(FeedInquiryRequest feedInquiryRequest, String userId) {

        List<FollowingResponse> followingResponses = userService.getFollowingUsers(userId);
        // 팔로잉한 사람이 없다면 return
        if (followingResponses.size() == 0) {
            return null;
        }
        List<String> userIds = followingResponses.stream()
                .map(FollowingResponse::getUserId)
                .collect(Collectors.toList());

        List<Article> articles = queryFactory
                .selectFrom(article)
                .where(
                        article.articleId.lt(feedInquiryRequest.getLastArticleId() == null ? 2130000000 : feedInquiryRequest.getLastArticleId()),
                        userIdEq(userIds)
                )
                .orderBy(article.articleId.desc())
                .limit(feedInquiryRequest.getDataSize() == null ? 20 : feedInquiryRequest.getDataSize())
                .fetch();


        Collections.sort(articles, Comparator.comparing(Article::getCreatedAt).reversed());
        return articles;
    }

    private BooleanExpression userIdEq(List<String> userIds) {
        return userIds.size() > 0 ? article.user.userId.in(userIds) : null;
    }

    @Override
    public List<FeedResponse> getArticlesOfFollowingUser(FeedInquiryRequest feedInquiryRequest, String userId) {
        List<Article> articles = searchArticlesOfFollowingUser(feedInquiryRequest, userId);
        List<FeedResponse> feedResponse = new ArrayList<>();
        if (articles != null) {
            feedResponse = getFeedInfoForArticles(userId, articles, feedInquiryRequest.getDataSize());
        } else {
            feedResponse = null;
        }

        return feedResponse;
    }

    @Override
    @Transactional
    public List<Article> searchPopularArticles(FeedInquiryRequest feedInquiryRequest) {
        List<Article> articles = queryFactory
                .selectFrom(article)
                .where(
                        article.heart.loe(feedInquiryRequest.getHeartCnt() == null ? 2130000000 : feedInquiryRequest.getHeartCnt()),
                        article.articleId.lt(feedInquiryRequest.getLastArticleId() == null ? 2130000000 : feedInquiryRequest.getLastArticleId())
                )
                .orderBy(article.heart.desc())
                .limit(feedInquiryRequest.getDataSize() == null ? 20 : feedInquiryRequest.getDataSize())
                .fetch();

        return articles;
    }

    @Override
    public List<FeedResponse> getPopularArticles(FeedInquiryRequest feedInquiryRequest, String userId) {
        List<Article> articles = searchPopularArticles(feedInquiryRequest);
        List<FeedResponse> feedResponse = getFeedInfoForArticles(userId, articles, feedInquiryRequest.getDataSize());
        return feedResponse;
    }

    @Override
    @Transactional
    public List<Article> searchLatestArticles(FeedInquiryRequest feedInquiryRequest) {
        List<Article> articles = queryFactory
                .selectFrom(article)
                .where(
                        article.articleId.lt(feedInquiryRequest.getLastArticleId() == null ? 2130000000 : feedInquiryRequest.getLastArticleId())
                )
                .orderBy(article.articleId.desc())
                .limit(feedInquiryRequest.getDataSize() == null ? 20 : feedInquiryRequest.getDataSize())
                .fetch();

        return articles;
    }

    @Override
    @Transactional
    public List<FeedResponse> getLatestArticles(FeedInquiryRequest feedInquiryRequest, String userId) {
        List<Article> articles= searchLatestArticles(feedInquiryRequest);
        List<FeedResponse> responses  = getFeedInfoForArticles(userId, articles, feedInquiryRequest.getDataSize());
        return responses;
    }


    @Override
    @Transactional
    public List<Article> searchArticleByPerfumeId(FeedInquiryRequest feedInquiryRequest, int perfumeId) {
        List<ArticlePerfume> articlePerfumes = articlePerfumeRepository.findByPerfume_PerfumeId(perfumeId);

        if (articlePerfumes.size() == 0) {
            return null;
        }
        List<Integer> articleIds = new ArrayList<>();
        for(ArticlePerfume articlePerfume : articlePerfumes) {
            articleIds.add(articlePerfume.getArticle().getArticleId());
        }

        List<Article> articles = queryFactory
                .selectFrom(article)
                .where(
                        article.articleId.lt(feedInquiryRequest.getLastArticleId() == null ? 2130000000 : feedInquiryRequest.getLastArticleId()),
                        articleIdEq(articleIds)
                )
                .orderBy(article.articleId.desc())
                .limit(feedInquiryRequest.getDataSize() == null ? 20 : feedInquiryRequest.getDataSize())
                .fetch();

        return articles;
    }

    private BooleanExpression articleIdEq(List<Integer> articleIds) {
        return articleIds.size() > 0 ? article.articleId.in(articleIds) : null;
    }

    @Override
    public List<FeedResponse> getArticleByPerfumeId(FeedInquiryRequest feedInquiryRequest, String userId,
                                                    int perfumeId) {
        List<Article> articles = searchArticleByPerfumeId(feedInquiryRequest, perfumeId);
        // 요청한 향수가 가장 처음으로 오도록 수정하는 작업
        if (articles == null) {
            return null;
        } else {
            for(Article article: articles) {
                Article a = article;
                for (int i = 0;i < a.getArticlePerfume().size();i++) {
                    //  ID같으면 0번으로 이동 (swap)
                    if (a.getArticlePerfume().get(i).getPerfume().getPerfumeId() == perfumeId) {
                        Collections.swap(a.getArticlePerfume(),0, i);
                        break;
                    }

                }
            }
        }
        List<FeedResponse> feedResponses = getFeedInfoForArticles(userId, articles ,feedInquiryRequest.getDataSize());
        return feedResponses;
    }


    // 피드 조회 끝

    @Override
    @Transactional
    public List<ArticlePerfume> searchArticlePerfumeId(int articleId) {
        List<ArticlePerfume> articlePerfumes = articlePerfumeRepository.findByArticle_ArticleId(articleId);
        return articlePerfumes;
    }



    /*
        댓글 파트
     */
    @Override
    @Transactional
    public void createComment(CommentDto commentDto) {
        // TODO: 이 부분이 select로 데이터를 많이 가져옴 (개선 가능성 있음)
        Article article = entityManager.find(Article.class, commentDto.getArticleId());
        User user = entityManager.find(User.class, commentDto.getUserId());

        Comment comment = Comment.builder()
                .article(article)
                .user(user)
                .content(commentDto.getContent())
                .build();

        commentRepository.save(comment);

        // TODO: 댓글 갯수 하나 늘려 주는 부분 추가 필요
        // 게시물의 heart갯수 + 1
        articleRepository.increaseCommentCount(commentDto.getArticleId());
    }

    @Override
    @Transactional
    public ResponseEntity<Response> updateComment(CommentDto commentDto) {
        // 수정하려는 댓글의 commentId를 가져옴
        int commentId = commentDto.getCommentId();

        // 해당 commentId를 가진 댓글 조회
        Comment comment = entityManager.find(Comment.class, commentId);

        // 해당 commentId를 가진 댓글이 존재하는지 확인
        if (comment != null) {
            // 댓글의 userId와 수정하려는 userId를 비교하여 일치하는지 확인
            if (comment.getUser().getUserId().equals(commentDto.getUserId())) {
                // 일치하는 경우, 수정 작업 진행
                // 수정할 내용을 업데이트
                Comment existingComment = comment.toBuilder().content(commentDto.getContent()).build();

                // 수정된 댓글 저장
                commentRepository.save(existingComment);
            } else {
                // userId가 일치하지 않는 경우, 권한이 없음을 알리는 예외 또는 메시지를 반환
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                        Response.builder()
                                .message("잘못된 사용자 접근입니다.")
                                .build());
            }
        }
        return ResponseEntity.status(HttpStatus.OK).body(
                Response.builder()
                        .message("댓글이 수정되었습니다.")
                        .build());
    }

    @Override
    public int deleteComment(int commentId, String userId) {
        // 해당 commentId를 가진 댓글 조회
        Comment comment = entityManager.find(Comment.class, commentId);

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
                return 401;
            }
        } else {
            return -1;
        }
        return 200;
    }

    @Override
    public void increaseCommentCount(int articleId) {
        // 주어진 articleId 의 comment 갯수를 하나 늘린다.
        articleRepository.increaseCommentCount(articleId);
    }

    @Override
    @Transactional
    public void decreaseCommentCount(int articleId) {
        // 주어진 articleId 의 comment 갯수를 하나 줄인다.
        articleRepository.decreaseCommentCount(articleId);
    }

    @Override
    @Transactional
    public List<CommentDto> getComments(int articleId) {
        List<Comment> comments = commentRepository.findByArticle_ArticleId(articleId);
        return commentMapper.toDTOs(comments);
    }

    @Override
    @Transactional
    public int deleteCommentByArticleId(int articleId) {
        // 이미 글을 삭제할때 글의 userId를 확인했음으로 여기서는 확인을 하지 않고 삭제를 진행
        commentRepository.deleteAllByArticleId(articleId);
        return 200;
    }

    @Override
    @Transactional
    public void deleteArticlePerfumeByArticleId(int articleId) {
        // 이미 글을 삭제할때 글의 userId를 확인했음으로 여기서는 확인을 하지 않고 삭제를 진행
        articlePerfumeRepository.deleteAllByArticleId(articleId);
    }

    @Override
    public ArticleResponse getDetail(int articleId, String userId) {
        final String GHOST = "00000000-0000-0000-0000-000000000000";

        Article article = searchArticleByArticleId(articleId);

        // 사용자가 글 작성자를 팔로우 했는지
        String articleWriterId = article.getUser().getUserId();
        boolean isFollowed = userService.isFollowing(userId, articleWriterId);
        boolean isMe = articleWriterId.equals(userId);
        boolean isWithdrawal = articleWriterId.equals(GHOST);

        //articleId를 통해서 게시물의 임베디드된 향수정보를 가져온다.
        List<ArticlePerfume> perfumeList = searchArticlePerfumeId(articleId);

        // 작성자: 프로필, 닉네임, 선호 비선호향
        UserInfoDto userInfo = userService.getUserInfo(article.getUser().getUserId()).toBuilder()
                .isMe(isMe)
                .isWithdrawal(isWithdrawal)
                .build();

        // 향수 평점 정보
        List<Float> rateInfo = new ArrayList<>();
        // 임베디드된 향수: 이름, 브랜드, 향수 사진 url
        List<PerfumeDto> perfumes = new ArrayList<>();

        // 공병태그가 없는 일반 향수 평점 정보가 달린 게시글 처리
        if (!(perfumeList.size() == 1 && perfumeList.get(0).getPerfume().getPerfumeId() == 0)) {
            // 임베디드된 향수 정보와 평점 정보 저장
            for(ArticlePerfume articlePerfume: perfumeList) {
                rateInfo.add(articlePerfume.getRate());
                perfumes.add(perfumeMapper.toDto(articlePerfume.getPerfume()));
            }
        }

        // 공병 태그가 달린 게시글은 아무것도 정보를 담지 않음

        // 댓글 리스트를 포함한다.
        List<CommentDto> comments = getComments(article.getArticleId());

        //사용자의 팔로우 목록을 받아온다.
        List<FollowingResponse> followers = userService.getFollowingUsers(userId);

        List<UserInfoDto> commentUsers = new ArrayList<>();
        boolean flag = false;
        // 댓글 작성자정보들
        for (CommentDto commentDto : comments) {
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
                    .isMe(commentDto.getUserId().equals(userId))
                    .isWithdrawal(commentDto.getUserId().equals(GHOST))
                    .build();


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
        Article article = entityManager.find(Article.class, heartDto.getArticleId());
        User user = entityManager.find(User.class, heartDto.getUserId());

        Heart heart = Heart.builder()
                .article(article)
                .user(user)
                .build();

        // Heart 테이블에 좋아요 저장
        heartRepository.save(heart);

        entityManager.flush();

        // 게시물의 heart갯수 + 1
        articleRepository.increaseHeartCountByArticleId(heartDto.getArticleId());

        return 200;
    }

    @Override
    @Transactional
    public int dislikeArticle(HeartDto heartDto) {
        // Heart 테이블에 좋아요한 부분 제거
        heartRepository.deleteByArticleIdAndUserId(heartDto.getArticleId(), heartDto.getUserId());

        // 게시물의 heart갯수 - 1
        articleRepository.decreaseHeartCountByArticleId(heartDto.getArticleId());

        return 200;
    }

    @Override
    @Transactional
    public int bookmarkArticle(BookMarkDto bookmarkDto) {
        Article article = entityManager.find(Article.class, bookmarkDto.getArticleId());
        User user = entityManager.find(User.class, bookmarkDto.getUserId());

        BookMark bookmark = BookMark.builder()
                .article(article)
                .user(user)
                .build();

        bookmarkRepository.save(bookmark);
        return 200;
    }

    @Override
    @Transactional
    public int cancelBookmarkArticle(BookMarkDto bookmarkDto) {
        bookmarkRepository.deleteByArticleIdAndUserId(bookmarkDto.getArticleId(), bookmarkDto.getUserId());
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
    @Override
    public List<FeedResponse> getFeedInfoForArticles(String userId, List<Article> articles, int datasize){

        final String GHOST = "00000000-0000-0000-0000-000000000000";
        // 각 article에 담긴 사용자 정보와 향수 정보를 담아둔 Dto의 리스트
        List<FeedResponse> feedResponses = new ArrayList<>();

        boolean isLastPage = false;
        if(articles.size()  < datasize) {
            isLastPage = true;
        }

        //사용자의 팔로우 목록을 받아온다.
        List<FollowingResponse> followerInfoResponses = userService.getFollowingUsers(userId);
//        int cnt = 0;

        // articleId를 보고 사용자 정보와 향수 정보를 담음
        for(Article article: articles){
            // TODO: perfumeId가 하나만 필요함으로 추후 쿼리 최적화가 필요!!(완료)
            int perfumeId = article.getArticlePerfume().get(0).getPerfume().getPerfumeId();

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
                    isFollowed = true;
                    break;
                }
            }

            // 팔로잉 / 팔로우 버튼생성해야하는지 여부?
            boolean isFollowingButtonActivate = true;
            if (userId.equals(userInfoDto.getUser().getUserId()) || userInfoDto.getUser().getUserId().equals(GHOST)) {
                isFollowingButtonActivate = false;
            }

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

            feedResponses.add(new FeedResponse(userInfoDto, isFollowed, isFollowingButtonActivate, articleDto,
                    perfumeDto, isHearted, isBookmarked, isLastPage));
//            cnt++;
        }
        return feedResponses;
    }

    @Override
    @Transactional
    public List<FeedResponse> getBookmarkArticle(FeedInquiryRequest feedInquiryRequest, String userId) {
        List<BookMark> bookMarks = queryFactory
                .selectFrom(bookMark)
                .where(
                        bookMark.user.userId.eq(userId)
                )
                .orderBy(bookMark.article.articleId.desc())
                .limit(feedInquiryRequest.getDataSize() == null ? 20 : feedInquiryRequest.getDataSize())
                .fetch();

        // 북마크된 글이 없다면 return
        if(bookMarks.size() == 0) {
            return null;
        }

        // 북마크된 글의 아이디 저장
        List<Integer> bookmarkArticleIds = new ArrayList<>();
        for (BookMark bookmark : bookMarks) {
            bookmarkArticleIds.add(bookmark.getArticle().getArticleId());
        }
        List<Article> articles = queryFactory
                .selectFrom(article)
                .where(
                        article.articleId.lt(feedInquiryRequest.getLastArticleId() == null ? 2130000000 : feedInquiryRequest.getLastArticleId()),
                        articleIdEq(bookmarkArticleIds)
                )
                .orderBy(article.articleId.desc())
                .limit(feedInquiryRequest.getDataSize() == null ? 20 : feedInquiryRequest.getDataSize())
                .fetch();

        List<FeedResponse> responses  = getFeedInfoForArticles(userId, articles, feedInquiryRequest.getDataSize());

        return responses;
    }
}

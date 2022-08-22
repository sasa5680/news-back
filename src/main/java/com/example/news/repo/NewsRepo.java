package com.example.news.repo;

import com.example.news.entity.NewsEntity;
import com.example.news.entity.UserEntity;
import com.example.news.types.NewsMain;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;

public interface NewsRepo extends JpaRepository<NewsEntity, Integer> {

    NewsEntity findByNewsId(int newsId);

    //Page<NewsEntity> findAl(Pageable pageable);

    @Query(value = "select n from NewsEntity n where (:newsApproved is null or n.newsApproved = :newsApproved) " //뉴스 승인 여부
            + "AND (:query is null or :query = '' or n.newsTitle like concat('%', :query, '%'))"  // 쿼리는 empty null 혹은 있으면 검색
            + "AND (:newsCate is null or :newsCate = '' or n.newsCate =: newsCate)" //카테고리 여부
            + "AND (:newsMain is null or :newsMain = '' or n.newsMain =: newsMain)" //뉴스 메인 여부
    )
    Page<NewsEntity> getNews(Boolean newsApproved, String query, String newsCate, NewsMain newsMain, Pageable pageable);

    //특정 유저가 작성한 뉴스만 가져온다.
    @Query(value = "select n from NewsEntity n where (n.user.userName = :userName) AND (n.newsApproved = true )")
    Page<NewsEntity> getNewsEntitiesByUserName(String userName, Pageable pageable);


    //메인 뉴스들 검색(NORMAL이 아닌 것들만 가져온다)
    Set<NewsEntity> getNewsEntitiesByNewsMainNot(NewsMain newsMain);

    NewsEntity getByNewsMain(NewsMain newsMain);

    @Query(value = "select user_id from news where news_id = :newsId", nativeQuery = true)
    int findUserIdByNewsId(int newsId);

    void deleteByNewsId(int newsId);
}

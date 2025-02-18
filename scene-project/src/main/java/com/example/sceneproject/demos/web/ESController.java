//package com.example.sceneproject.demos.web;
//
//import org.apache.http.HttpHost;
//import org.elasticsearch.action.search.SearchRequest;
//import org.elasticsearch.action.search.SearchResponse;
//import org.elasticsearch.client.RequestOptions;
//import org.elasticsearch.client.RestClient;
//import org.elasticsearch.client.RestHighLevelClient;
//import org.elasticsearch.index.query.BoolQueryBuilder;
//import org.elasticsearch.index.query.QueryBuilders;
//import org.elasticsearch.search.SearchHit;
//import org.elasticsearch.search.SearchHits;
//import org.elasticsearch.search.builder.SearchSourceBuilder;
//
//import java.io.IOException;
//
//public class ESController {
//
//
//    public String query(){
//        //创建ES查询对象
//        RestHighLevelClient restHighLevelClient = restHighLevelClient();
//        // 替换为你的索引名
//        SearchRequest searchRequest = new SearchRequest("index_name");
//        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
//        // 设置查询条件
//        searchSourceBuilder.query(QueryBuilders.matchQuery("字段名称", "value"));
//
//        searchRequest.source(searchSourceBuilder);
//        try {
//            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
//            SearchHits searchHits = searchResponse.getHits();
//            // 处理查询结果并缓存
//            for (SearchHit hit : searchHits) {
//                String sourceAsString = hit.getSourceAsString();
//                // 将sourceAsString转换为你需要的对象，并进行缓存
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//    public RestHighLevelClient restHighLevelClient(){
//        RestHighLevelClient client = new RestHighLevelClient(
//                //参数1-es服务的ip地址或域名， 参数2-es服务的端口， 参数3-es服务的通讯协议
//                RestClient.builder(new HttpHost("localhost", 9200, "http"))
//        );
//        return client;
//    }
//
//}

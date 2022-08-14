package com.l1fe1.elasticsearch.service;

import com.l1fe1.elasticsearch.entity.Product;
import lombok.SneakyThrows;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.metrics.Avg;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.elasticsearch.xcontent.XContentFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.net.InetAddress;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Service
public class TransportClientService {
    private static final Logger logger = LoggerFactory.getLogger(TransportClientService.class);

    private TransportClient client;

    @Autowired
    private ProductService productService;

    @SneakyThrows
    @PostConstruct
    public void init() {
        Settings settings = Settings.builder()
                .put("cluster.name", "elasticsearch").build();
        client = new PreBuiltTransportClient(settings)
                .addTransportAddress(new TransportAddress(InetAddress.getByName("localhost"), 9300))// 通讯端口，而不是服务端口
                .addTransportAddress(new TransportAddress(InetAddress.getByName("localhost"), 9301));
    }

    @SneakyThrows
    public void create() {
        List<Product> products = productService.list();
        for (Product product : products) {
            IndexResponse response = client.prepareIndex("product", "_doc", product.getId().toString())
                    .setSource(XContentFactory.jsonBuilder()
                            .startObject()
                            .field("name", product.getName())
                            .field("desc", product.getDesc())
                            .field("price", product.getPrice())
                            .field("date", product.getCreateTime().toLocalDateTime()
                                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                            .field("tags", product.getTags().split(","))
                            .endObject())
                    .get();
            logger.info("create response: {}", response.getResult());
        }
        client.close();
    }

    @SneakyThrows
    public void get() {
        GetResponse response = client.prepareGet("product", "_doc", "1").get();
        String index = response.getIndex();// 获取索引名称
        String type = response.getType();// 获取索引类型
        String id = response.getId();// 获取索引 id
        logger.info("index:" + index);
        logger.info("type:" + type);
        logger.info("id:" + id);
        logger.info(response.getSourceAsString());
    }

    public void getAll() {
        SearchResponse response = client.prepareSearch("product")
                .get();
        SearchHits searchHits = response.getHits();
        SearchHit[] hits = searchHits.getHits();
        for (SearchHit hit : hits) {
            String res = hit.getSourceAsString();
            logger.info("res" + res);
        }
    }

    @SneakyThrows
    public void update() {
        UpdateResponse response = client.prepareUpdate("product", "_doc", "2")
                .setDoc(XContentFactory.jsonBuilder()
                        .startObject()
                        .field("name", "xiao wang")
                        .endObject())
                .get();
        logger.info(response.getResult().getLowercase());
    }


    @SneakyThrows
    public void delete() {
        DeleteResponse response = client.prepareDelete("product", "_doc", "3").get();
        logger.info(response.getResult().getLowercase());
    }

    @SneakyThrows
    public void multiSearch() {
        SearchResponse response = client.prepareSearch("product")
                .setQuery(QueryBuilders.termQuery("name", "apple phone"))                 // Query
                .setPostFilter(QueryBuilders.rangeQuery("price").from(4000).to(8000))
                .setFrom(1).setSize(3)
                .get();
        SearchHits searchHits = response.getHits();
        SearchHit[] hits = searchHits.getHits();
        for (SearchHit hit : hits) {
            String res = hit.getSourceAsString();
            logger.info("res" + res);
        }
        client.close();
    }

    @SneakyThrows
    public void aggSearch() {
        // 计算并返回聚合分析 response 对象
        SearchResponse response = client.prepareSearch("product")
                .addAggregation(
                        AggregationBuilders.dateHistogram("group_by_month")
                                .field("date")
                                .calendarInterval(DateHistogramInterval.MONTH)
                                // .dateHistogramInterval(DateHistogramInterval.MONTH)
                                .subAggregation(
                                        AggregationBuilders
                                                .terms("by_tag")
                                                .field("tags.keyword")
                                                .subAggregation(
                                                        AggregationBuilders
                                                                .avg("avg_price")
                                                                .field("price")
                                                )
                                )
                )
                .execute().actionGet();

        // 输出结果信息
        Map<String, Aggregation> map = response.getAggregations().asMap();
        Aggregation group_by_month = map.get("group_by_month");
        Histogram dates = (Histogram) group_by_month;
        Iterator<Histogram.Bucket> buckets = (Iterator<Histogram.Bucket>) dates.getBuckets().iterator();
        while (buckets.hasNext()) {
            Histogram.Bucket dateBucket = buckets.next();
            logger.info("\n\n月份：" + dateBucket.getKeyAsString() + "\n计数：" + dateBucket.getDocCount());
            Aggregation group_by_tag = dateBucket.getAggregations().asMap().get("by_tag");
            StringTerms terms = (StringTerms) group_by_tag;
            for (StringTerms.Bucket tagBucket : terms.getBuckets()) {
                logger.info("\t标签名称：" + tagBucket.getKey() + "\n\t数量：" + tagBucket.getDocCount());
                Aggregation avg_price = tagBucket.getAggregations().get("avg_price");
                Avg avg = (Avg) avg_price;
                logger.info("\t平均价格：" + avg.getValue() + "\n");
            }
        }

        client.close();
    }
}

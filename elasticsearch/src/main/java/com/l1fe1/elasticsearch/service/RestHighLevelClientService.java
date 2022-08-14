package com.l1fe1.elasticsearch.service;

import com.google.gson.Gson;
import com.l1fe1.elasticsearch.client.ESClient;
import com.l1fe1.elasticsearch.dto.ResultDto;
import com.l1fe1.elasticsearch.entity.CarSerialBrand;
import com.l1fe1.elasticsearch.entity.Product;
import lombok.SneakyThrows;
import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.*;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.*;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.*;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.GetIndexResponse;
import org.elasticsearch.client.sniff.ElasticsearchNodesSniffer;
import org.elasticsearch.client.sniff.NodesSniffer;
import org.elasticsearch.client.sniff.SniffOnFailureListener;
import org.elasticsearch.client.sniff.Sniffer;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.core.TimeValue;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.UpdateByQueryRequest;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;
import org.elasticsearch.script.mustache.SearchTemplateRequest;
import org.elasticsearch.script.mustache.SearchTemplateResponse;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.elasticsearch.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.PostConstruct;
import java.util.*;

import static org.elasticsearch.index.query.QueryBuilders.matchPhraseQuery;
import static org.elasticsearch.index.query.QueryBuilders.matchQuery;

@Service
public class RestHighLevelClientService {
    private static final Logger logger = LoggerFactory.getLogger(RestHighLevelClientService.class);

    private RestHighLevelClient client;

    @Autowired
    private ProductService productService;
    @Autowired
    private CarSerialBrandService carSerialBrandService;

    @SneakyThrows
    @PostConstruct
    public void init() {
        client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("localhost", 9200, "http"),
                        new HttpHost("localhost", 9201, "http")));
    }

    @SneakyThrows
    public void createIndex() {
        CreateIndexRequest request = new CreateIndexRequest("test-index");

        request.settings(Settings.builder()
                .put("index.number_of_shards", 3)
                .put("index.number_of_replicas", 2)
        );
        CreateIndexResponse createIndexResponse = client.indices().create(request, RequestOptions.DEFAULT);
        if (createIndexResponse.isAcknowledged()) {
            logger.info("创建 index 成功!");
        } else {
            logger.error("创建 index 失败!");
        }

        client.close();
    }

    @SneakyThrows
    public void getIndex() {
        GetIndexRequest request = new GetIndexRequest("test-index*");
        GetIndexResponse response = client.indices().get(request, RequestOptions.DEFAULT);
        String[] indices = response.getIndices();
        for (String indexName : indices) {
            logger.info("index name:" + indexName);
        }
        client.close();
    }

    @SneakyThrows
    public void deleteIndex() {
        DeleteIndexRequest request = new DeleteIndexRequest("test-index");
        AcknowledgedResponse response = client.indices().delete(request, RequestOptions.DEFAULT);
        if (response.isAcknowledged()) {
            logger.info("删除 index 成功!");
        } else {
            logger.info("删除 index 失败!");
        }
        client.close();
    }

    @SneakyThrows
    public void insert() {
        List<Product> list = productService.list();

        /**
         * 插入数据，index 不存在则自动根据匹配到的 template 创建。
         * index 没必要每天创建一个，如果是为了灵活管理，最低建议每月一个 yyyyMM。
         */
        IndexRequest request = new IndexRequest("test_product");
        // 最好不要自定义 id，会影响插入速度。
        Product product = list.get(0);
        Gson gson = new Gson();
        request.id(product.getId().toString());
        request.source(gson.toJson(product)
                , XContentType.JSON);
        IndexResponse response = client.index(request, RequestOptions.DEFAULT);
        logger.info(gson.toJson(response));
        client.close();
    }

    @SneakyThrows
    public void bulk() {
        // 批量插入数据，更新和删除同理
        BulkRequest request = new BulkRequest("test_product");
        List<Product> products = productService.list();
        Gson gson = new Gson();
        for (Product product : products) {
            IndexRequest indexRequest = new IndexRequest().id(String.valueOf(product.getId()))
                    .source(gson.toJson(product), XContentType.JSON);
            request.add(indexRequest);
        }
        BulkResponse response = client.bulk(request, RequestOptions.DEFAULT);
        logger.info("数量:" + response.getItems().length);
        client.close();
    }

    @SneakyThrows
    public void getById(String id) {
        GetRequest request = new GetRequest("test_product", id);
        String[] includes = {"name", "price"};
        String[] excludes = {"desc"};
        FetchSourceContext fetchSourceContext = new FetchSourceContext(true, includes, excludes);
        // 只查询特定字段。如果需要查询所有字段则不设置该项。
        request.fetchSourceContext(fetchSourceContext);
        GetResponse response = client.get(request, RequestOptions.DEFAULT);
        logger.info(response.getSourceAsString());
        client.close();
    }

    @SneakyThrows
    public void deleteById(String id) {
        DeleteRequest request = new DeleteRequest("test_product", id);
        DeleteResponse response = client.delete(request, RequestOptions.DEFAULT);
        logger.info(new Gson().toJson(response));
        client.close();
    }

    @SneakyThrows
    public void multiGetById() {
        //根据多个 id 查询
        MultiGetRequest request = new MultiGetRequest();
        request.add("test_product", "2");
        // 第二种写法
        request.add(new MultiGetRequest.Item("test_product", "3"));
        MultiGetResponse response = client.mget(request, RequestOptions.DEFAULT);
        for (MultiGetItemResponse itemResponse : response) {
            logger.info(itemResponse.getResponse().getSourceAsString());
        }
        client.close();
    }

    @SneakyThrows
    public void updateByQuery() {
        UpdateByQueryRequest request = new UpdateByQueryRequest("test_product");
        // 默认情况下，版本冲突会中止 UpdateByQueryRequest 进程，但是你可以用以下代码来继续
        // 设置版本冲突继续
        request.setConflicts("proceed");
        // 设置更新条件
        request.setQuery(matchQuery("name","huawei"));
        // 限制更新条数
        request.setMaxDocs(10);
        // 设置脚本
        request.setScript(
                new Script(ScriptType.INLINE, "painless",
                        "ctx._source.desc+='#';", Collections.emptyMap()));
        BulkByScrollResponse response = client.updateByQuery(request, RequestOptions.DEFAULT);
        client.close();
    }

    @SneakyThrows
    public void sniffer() {
        // 监听器
        SniffOnFailureListener sniffOnFailureListener =
                new SniffOnFailureListener();

        // 1.获取 Clients
        RestClient restClient = RestClient.builder(
                new HttpHost("localhost", 9200, "http"),
                new HttpHost("localhost", 9201, "http")
        ).setFailureListener(sniffOnFailureListener).build(); // 设置用于监听嗅探失败的监听器，绑定监听器

        // 2.使用 HTTPS
        NodesSniffer nodesSniffer = new ElasticsearchNodesSniffer(
                restClient,
                ElasticsearchNodesSniffer.DEFAULT_SNIFF_REQUEST_TIMEOUT,
                ElasticsearchNodesSniffer.Scheme.HTTPS);

        // 3.为 RestClient 绑定嗅探器
        Sniffer sniffer = Sniffer.builder(restClient)
                .setSniffIntervalMillis(5000) // 每隔多久嗅探一次
                .setSniffAfterFailureDelayMillis(30000) // 若没有绑定监听器则无效，嗅探失败时触发嗅探一次，经过设置的时间之后再次嗅探 直至正常
                .setNodesSniffer(nodesSniffer) // 如果要使用 HTTPS 必须设置的对象
                .build();

        // 4.启用监听
        sniffOnFailureListener.setSniffer(sniffer);
        // 5.注意释放嗅探器资源，关闭嗅探器必须在 client 关闭之前操作
        sniffer.close();
        restClient.close();
    }

    @SneakyThrows
    public void autoSniffer() {
        RestHighLevelClient client = ESClient.getInstance().getHighLevelClient();
        Iterator<Node> nodes = client.getLowLevelClient().getNodes().iterator();
        Gson gson = new Gson();
        while (nodes.hasNext()) {
            Node node = nodes.next();
            logger.info(gson.toJson(node));
        }
        Thread.sleep(5000);
        logger.info("5 秒后:");
        nodes = client.getLowLevelClient().getNodes().iterator();
        while (nodes.hasNext()) {
            Node node = nodes.next();
            logger.info(gson.toJson(node));
        }
        ESClient.getInstance().closeClient();
    }

    @SneakyThrows
    public void bulkInit() {
        RestHighLevelClient client = ESClient.getInstance().getHighLevelClient();
        GetIndexRequest request = new GetIndexRequest("car");
        boolean exists = client.indices().exists(request, RequestOptions.DEFAULT);
        if (!exists) {
            CreateIndexRequest createRequest = new CreateIndexRequest("car");
            createRequest.settings(Settings.builder()
                    .put("index.number_of_shards", 3)
                    .put("index.number_of_replicas", 2)
            );
            client.indices().create(createRequest, RequestOptions.DEFAULT);
        }
        List<CarSerialBrand> cars = carSerialBrandService.list();
        BulkRequest bulkRequest = new BulkRequest("car");
        Gson gson = new Gson();
        for (int i = 0; i < cars.size(); i++) {
            bulkRequest.add(new IndexRequest().id(Integer.toString(i)).source(gson.toJson(cars.get(i)),
                    XContentType.JSON));
        }
        BulkResponse response = client.bulk(bulkRequest, RequestOptions.DEFAULT);
        logger.info("数量:" + response.getItems().length);

        ESClient.getInstance().closeClient();
    }

    @SneakyThrows
    public ResultDto carInfo(String keyword, Integer from, Integer size) {
        RestHighLevelClient client = ESClient.getInstance().getHighLevelClient();
        SearchRequest searchRequest = new SearchRequest("car");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(matchQuery("name", keyword));
        searchSourceBuilder.from(from);
        searchSourceBuilder.size(size);
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        ResultDto res = new ResultDto();
        res.setData(searchResponse.getHits().getHits());
        ESClient.getInstance().closeClient();
        return res;
    }

    @SneakyThrows
    public ResultDto scroll(String scrollId) {
        SearchRequest searchRequest = new SearchRequest("car");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.size(2);
        searchRequest.source(searchSourceBuilder);
        searchRequest.scroll(TimeValue.timeValueMinutes(1L));
        SearchResponse searchResponse = scrollId == null
                ? client.search(searchRequest, RequestOptions.DEFAULT)
                : client.scroll(new SearchScrollRequest(scrollId), RequestOptions.DEFAULT);
        scrollId = searchResponse.getScrollId();
        SearchHits hits = searchResponse.getHits();
        ResultDto res = new ResultDto();
        res.setTag(scrollId);
        res.setData(hits.getHits());
        ESClient.getInstance().closeClient();
        return res;
    }

    @SneakyThrows
    public ResultDto carBulk() {
        BulkRequest request = new BulkRequest();
        request.add(new DeleteRequest("car", "13"));
        request.add(new UpdateRequest("car", "22")
                .doc(XContentType.JSON, "name", "天籁之音"));
        request.add(new IndexRequest("car").id("4")
                .source(XContentType.JSON, "name", "天津一汽"));
        client.bulk(request, RequestOptions.DEFAULT);
        return null;
    }

    @SneakyThrows
    public ResultDto templateSearch() {
        // 创建模板并缓存，作用域为整个集群
        Request scriptRequest = new Request("POST", "_scripts/test_template_search");
        scriptRequest.setJsonEntity(
                "{" +
                        "  \"script\": {" +
                        "    \"lang\": \"mustache\"," +
                        "    \"source\": {" +
                        "      \"query\": { \"match\" : { \"{{field}}\" : \"{{value}}\" } }," +
                        "      \"size\" : \"{{size}}\"" +
                        "    }" +
                        "  }" +
                        "}");
        Response scriptResponse = client.getLowLevelClient().performRequest(scriptRequest);
        SearchTemplateRequest request = new SearchTemplateRequest();
        request.setRequest(new SearchRequest("car"));
        request.setScriptType(ScriptType.STORED);
        request.setScript("test_template_search");
        // 本地模板
        //        request.setScriptType(ScriptType.INLINE);
//        request.setScript(
//                        "{\n" +
//                        "  \"from\": {{from}},\n" +
//                        "  \"size\": {{size}},\n" +
//                        "  \"query\": {\n" +
//                        "    \"match\": {\n" +
//                        "      \"name\": \"{{name}}\"\n" +
//                        "    }\n" +
//                        "  }\n" +
//                        "}");
        Map<String, Object> scriptParams = new HashMap<>();
        scriptParams.put("field", "name");
        scriptParams.put("value", "一汽");
        scriptParams.put("size", 5);
        request.setScriptParams(scriptParams);
        SearchTemplateResponse searchTemplateResponse = client.searchTemplate(request, RequestOptions.DEFAULT);
        SearchHits hits = searchTemplateResponse.getResponse().getHits();
        ResultDto res = new ResultDto();
        res.setData(hits.getHits());
        return res;
    }

    @SneakyThrows
    public ResultDto fuzzy(String name) {
        SearchRequest searchRequest = new SearchRequest("car");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.fuzzyQuery("name", name).fuzziness(Fuzziness.AUTO));
        searchRequest.source(searchSourceBuilder);
        SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
        SearchHit[] hits = response.getHits().getHits();
        ResultDto res = new ResultDto();
        res.setData(hits);
        return res;
    }

    @SneakyThrows
    public ResultDto multiSearch() {
        MultiSearchRequest request = new MultiSearchRequest();

        SearchRequest firstSearchRequest = new SearchRequest("car");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(matchQuery("name", "奔驰"));
        firstSearchRequest.source(searchSourceBuilder);
        request.add(firstSearchRequest);

        SearchRequest secondSearchRequest = new SearchRequest("car");
        searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.termQuery("name", "阿斯顿"));
        secondSearchRequest.source(searchSourceBuilder);
        request.add(secondSearchRequest);

        MultiSearchResponse response = client.msearch(request, RequestOptions.DEFAULT);
        ResultDto res = new ResultDto();
        res.setData(response.getResponses());
        return res;
    }

    @SneakyThrows
    public ResultDto boolSearch() {
        MultiSearchRequest request = new MultiSearchRequest();

        SearchRequest searchRequest = new SearchRequest("msb_auto");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query
                (
                        QueryBuilders.boolQuery()
                                .must(matchPhraseQuery("name", "奔驰"))
                                .filter(matchQuery("name", "奔驰S级").analyzer("ik_max_word"))
                                .mustNot(matchQuery("name", "奔驰SLC级"))
                );
        searchRequest.source(searchSourceBuilder);
        request.add(searchRequest);
        MultiSearchResponse response = client.msearch(request, RequestOptions.DEFAULT);
        ResultDto res = new ResultDto();
        res.setData(response.getResponses());
        return res;
    }
}

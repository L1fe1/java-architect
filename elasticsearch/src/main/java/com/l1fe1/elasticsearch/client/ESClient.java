package com.l1fe1.elasticsearch.client;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.client.Node;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.sniff.SniffOnFailureListener;
import org.elasticsearch.client.sniff.Sniffer;

import java.io.IOException;

public class ESClient {

    private static ESClient esClient;
    private String host = "localhost:9200,localhost:9201";
    private RestClientBuilder builder;
    private static Sniffer sniffer;
    private static RestHighLevelClient highClient;

    private ESClient(){
    }

    public static ESClient getInstance() {
        if (esClient == null) {
            synchronized (ESClient.class) {
                if (esClient == null) {
                    esClient = new ESClient();
                    esClient.initBuilder();
                }
            }
        }
        return esClient;
    }

    public RestClientBuilder initBuilder() {
        String[] hosts = host.split(",");
        HttpHost[] httpHosts = new HttpHost[hosts.length];
        for (int i = 0; i < hosts.length; i++) {
            String[] host = hosts[i].split(":");
            httpHosts[i] = new HttpHost(host[0], Integer.parseInt(host[1]), "http");
        }

        builder = RestClient.builder(httpHosts);

        // 1.在 Builder 中设置请求头。
        Header[] defaultHeaders = new Header[]{
                new BasicHeader("Content-Type", "application/json")
        };
        builder.setDefaultHeaders(defaultHeaders);

        // 2.设置每次节点发生故障时收到通知的侦听器。内部嗅探到故障时被启用。
        builder.setFailureListener(new RestClient.FailureListener() {
            public void onFailure(Node node) {
                super.onFailure(node);
            }
        });

        // 3.设置修改默认请求配置的回调（例如：请求超时，认证，或者其他设置）。
        builder.setRequestConfigCallback(requestConfigBuilder -> requestConfigBuilder.setSocketTimeout(10000));

        // 简单的身份认证
        final CredentialsProvider credentialsProvider =
                new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials("elastic", "elastic"));

        // 4.设置修改 http 客户端配置的回调（例如：ssl 加密通讯，线程 IO 的配置，或其他任何设置）。
        builder.setHttpClientConfigCallback(httpAsyncClientBuilder -> {
            httpAsyncClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
            //线程设置
            httpAsyncClientBuilder.setDefaultIOReactorConfig(IOReactorConfig.custom()
                    .setIoThreadCount(10).build());
            return httpAsyncClientBuilder;
        });

        RestClient restClient = builder.build();

        // 启用嗅探器
        SniffOnFailureListener sniffOnFailureListener = new SniffOnFailureListener();
        builder.setFailureListener(sniffOnFailureListener);
        sniffer = Sniffer.builder(restClient)
                .setSniffIntervalMillis(5000)
                .setSniffAfterFailureDelayMillis(10000)
                .build();
        sniffOnFailureListener.setSniffer(sniffer);
        return builder;
    }

    public RestHighLevelClient getHighLevelClient() {
        if (highClient == null) {
            synchronized (ESClient.class) {
                if (highClient == null) {
                    highClient = new RestHighLevelClient(builder);
                    // 5 秒刷新并更新一次节点
                    sniffer = Sniffer.builder(highClient.getLowLevelClient())
                            .setSniffIntervalMillis(5000)
                            .setSniffAfterFailureDelayMillis(15000)
                            .build();
                }
            }
        }
        return highClient;
    }

    /**
     * 关闭 sniffer client
     */
    public void closeClient() {
        if (null != highClient) {
            try {
                // 需要在 highClient close 之前操作
                sniffer.close();
                highClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

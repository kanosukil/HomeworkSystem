package cn.summer.homework.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author VHBin
 * @date 2022/7/15-17:06
 */

@Configuration
public class ESClientConfig {
    private static final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();

    @Value("${spring.elasticsearch.username}")
    private String username;
    @Value("${spring.elasticsearch.password}")
    private String password;
    @Value("${spring.elasticsearch.host}")
    private String host;
    @Value("${spring.elasticsearch.port}")
    private String port;

    // RestClient
    @Bean
    public RestClient restClient() {
        credentialsProvider.setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials(username, password));
        return RestClient.builder(
                        new HttpHost(host, Integer.parseInt(port), "http"))
                .setHttpClientConfigCallback(httpAsyncClientBuilder
                        -> httpAsyncClientBuilder
                        .setDefaultCredentialsProvider(credentialsProvider))
                .build();
    }

    // 阻塞型
    @Bean
    public ElasticsearchClient elasticsearchClient() {
        return new ElasticsearchClient(
                new RestClientTransport(
                        restClient(),
                        new JacksonJsonpMapper()
                )
        );
    }
}

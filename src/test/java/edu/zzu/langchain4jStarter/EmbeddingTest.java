package edu.zzu.langchain4jStarter;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
public class EmbeddingTest {
    private static final String EXAMPLE_DIRECTORY_PATH = "src/main/resources/zzuhosfile";

    @Resource
    private EmbeddingModel embeddingModel;
    @Test
    public void testEmbedding() {
        Response<Embedding> embed = embeddingModel.embed("你好");
        System.out.println(embed.content().vector().length);
        System.out.println(embed);
    }

    @Resource
    private EmbeddingStore embeddingStore;

    @Test
    public void testPinecone() {
        TextSegment segment1 = TextSegment.from("我喜欢蔡徐坤");
        Embedding embedding1 = embeddingModel.embed(segment1).content();

        embeddingStore.add(embedding1, segment1);

        TextSegment segment2 = TextSegment.from("今天是星期六");
        Embedding embedding2 = embeddingModel.embed(segment2).content();
        embeddingStore.add(embedding2, segment2);
    }


    @Test
    public void upLoadZZUHOSLib() {

        Document document1 = FileSystemDocumentLoader.loadDocument(EXAMPLE_DIRECTORY_PATH + "/医院简介.md");
        Document document2 = FileSystemDocumentLoader.loadDocument(EXAMPLE_DIRECTORY_PATH + "/就医指南.md");
        Document document3 = FileSystemDocumentLoader.loadDocument(EXAMPLE_DIRECTORY_PATH + "/科室信息.md");

        List<Document> list = Arrays.asList(document1, document2, document3);

        EmbeddingStoreIngestor
                .builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .build()
                .ingest(list);

    }

}

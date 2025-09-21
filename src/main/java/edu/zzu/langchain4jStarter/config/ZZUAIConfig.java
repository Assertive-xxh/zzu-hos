package edu.zzu.langchain4jStarter.config;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import edu.zzu.langchain4jStarter.store.MongoChatMemoryStore;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

@Configuration
public class ZZUAIConfig {

    @Autowired
    private MongoChatMemoryStore mongoChatMemoryStore;

    @Bean
    public ChatMemoryProvider chatMemoryProviderZZU(){
        return memoryId -> MessageWindowChatMemory
                .builder()
                .id(memoryId)
                .chatMemoryStore(mongoChatMemoryStore)
                .maxMessages(30)
                .build();
    }

//    @Bean
//    public ContentRetriever contentRetriever(){
//        Document document1 = FileSystemDocumentLoader.loadDocument("");
//        Document document2 = FileSystemDocumentLoader.loadDocument("");
//        Document document3 = FileSystemDocumentLoader.loadDocument("");
//        Document document4 = FileSystemDocumentLoader.loadDocument("");
//        Document document5 = FileSystemDocumentLoader.loadDocument("");
//
//        List<Document> documents = Arrays.asList(document1, document2, document3, document4);
//
//        InMemoryEmbeddingStore<TextSegment> embeddingStoreZZU = new InMemoryEmbeddingStore<>();
//
//        EmbeddingStoreIngestor.ingest(document1, embeddingStoreZZU);
//
//        return EmbeddingStoreContentRetriever.from(embeddingStoreZZU);
//
//    }

    @Resource
    private EmbeddingStore embeddingStore;

    @Resource
    private EmbeddingModel embeddingModel;

    @Bean
    ContentRetriever contentRetrieverZZUPinecone(){
        return EmbeddingStoreContentRetriever
                .builder()
                .embeddingModel(embeddingModel)
                .embeddingStore(embeddingStore)
                .maxResults(1)
                .minScore(0.8)
                .build();
    }


}

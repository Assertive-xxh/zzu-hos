package edu.zzu.langchain4jStarter;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.data.document.parser.apache.pdfbox.ApachePdfBoxDocumentParser;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.PathMatcher;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class DocumentLoadingTest {

    private static final Logger log = LoggerFactory.getLogger(DocumentLoadingTest.class);

    // 您可以在这里定义常量路径，或者在每个方法中直接使用字符串路径
    private static final String EXAMPLE_FILE_PATH = "src/test/resources/file.txt"; // 相对路径
    private static final String EXAMPLE_DIRECTORY_PATH = "src/main/resources/zzuhosfile"; // 相对路径
    @Test
    public void testReadDocumentScenarios() {
        // 1. 加载单个文档
        log.info("测试加载单个文档: {}", EXAMPLE_FILE_PATH);
        Document singleDocument = FileSystemDocumentLoader.loadDocument(EXAMPLE_FILE_PATH, new TextDocumentParser());
        assertNotNull(singleDocument, "单个文档不应为null");
        assertNotNull(singleDocument.text(), "单个文档内容不应为null");
        log.info("加载的单个文档元数据: {}", singleDocument.metadata());
        // log.info("单个文档内容预览: {}", singleDocument.text().substring(0, Math.min(100, singleDocument.text().length())));

        // 2. 从一个目录中加载所有文档 (非递归)
        log.info("测试从目录加载所有文档 (非递归): {}", EXAMPLE_DIRECTORY_PATH);
        List<Document> documentsFromDirectory = FileSystemDocumentLoader.loadDocuments(EXAMPLE_DIRECTORY_PATH, new TextDocumentParser());
        assertNotNull(documentsFromDirectory, "目录文档列表不应为null");
        // assertFalse(documentsFromDirectory.isEmpty(), "目录文档列表不应为空，请检查路径和文件"); // 取消注释并确保路径下有文件
        log.info("从目录 {} 加载了 {} 个文档", EXAMPLE_DIRECTORY_PATH, documentsFromDirectory.size());
        // documentsFromDirectory.forEach(doc -> log.info("文档: {}", doc.metadata().get("file_name")));

        // 3. 从一个目录中加载所有匹配特定模式（如 .txt）的文档 (非递归)
        String globPattern = "glob:*.txt";
        log.info("测试从目录 {} 加载匹配 '{}' 模式的文档", EXAMPLE_DIRECTORY_PATH, globPattern);
        PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher(globPattern);
        List<Document> documentsMatchingGlob = FileSystemDocumentLoader.loadDocuments(EXAMPLE_DIRECTORY_PATH, pathMatcher, new TextDocumentParser());
        assertNotNull(documentsMatchingGlob, "Glob匹配文档列表不应为null");
        // assertFalse(documentsMatchingGlob.isEmpty(), "Glob匹配文档列表不应为空"); // 取消注释并确保路径下有匹配文件
        log.info("从目录 {} 加载了 {} 个匹配 '{}' 的文档", EXAMPLE_DIRECTORY_PATH, documentsMatchingGlob.size(), globPattern);
        // documentsMatchingGlob.forEach(doc -> log.info("匹配文档: {}", doc.metadata().get("file_name")));

        // 4. 从一个目录及其子目录中递归加载所有文档
        log.info("测试从目录及其子目录递归加载所有文档: {}", EXAMPLE_DIRECTORY_PATH);
        List<Document> documentsRecursively = FileSystemDocumentLoader.loadDocumentsRecursively(EXAMPLE_DIRECTORY_PATH, new TextDocumentParser());
        assertNotNull(documentsRecursively, "递归加载文档列表不应为null");
        // assertFalse(documentsRecursively.isEmpty(), "递归加载文档列表不应为空"); // 取消注释并确保路径下有文件
        log.info("从目录 {} 及其子目录递归加载了 {} 个文档", EXAMPLE_DIRECTORY_PATH, documentsRecursively.size());
        // documentsRecursively.forEach(doc -> log.info("递归加载文档: {}/{}", doc.metadata().get("absolute_directory_path"), doc.metadata().get("file_name")));

        log.info("文档加载场景测试完成。");
    }

    @Test
    public void testPdf(){
        Document document = FileSystemDocumentLoader.loadDocument(EXAMPLE_DIRECTORY_PATH, new ApachePdfBoxDocumentParser());
        System.out.println(document.metadata());
        System.out.println(document.text());
    }

}
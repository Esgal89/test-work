package org.example;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.example.DocumentManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


class DocumentManagerTest {

    private DocumentManager manager;

    @BeforeEach
    void setUp() {
        manager = new DocumentManager();


        DocumentManager.Document doc1 = DocumentManager.Document.builder()
                .id("1")
                .title("test title 1")
                .content("This document is test.")
                .author(new DocumentManager.Author("a1", "John Doe"))
                .created(Instant.parse("2024-08-20T10:15:30Z"))
                .build();

        DocumentManager.Document doc2 = DocumentManager.Document.builder()
                .id("2")
                .title("another test title 2")
                .content("This document is another test.")
                .author(new DocumentManager.Author("a2", "Jane Smith"))
                .created(Instant.parse("2024-08-21T11:30:00Z"))
                .build();

        DocumentManager.Document doc3 = DocumentManager.Document.builder()
                .id("3")
                .title("different test title 3")
                .content("This document is different test.")
                .author(new DocumentManager.Author("a1", "John Doe"))
                .created(Instant.parse("2024-08-22T14:00:00Z"))
                .build();

        DocumentManager.Document doc4 = DocumentManager.Document.builder()
                .id(null)
                .title(null)
                .content(null)
                .author(null)
                .created(null)
                .build();

        manager.save(doc1);
        manager.save(doc2);
        manager.save(doc3);
        manager.save(doc4);
    }

    @Test
    void testSearch() {

        DocumentManager.SearchRequest request = DocumentManager.SearchRequest.builder()
                .titlePrefixes(Arrays.asList("another", "test"))
                .containsContents(Arrays.asList("document"))
                .authorIds(Arrays.asList("a1"))
                .createdFrom(Instant.parse("2024-08-20T00:00:00Z"))
                .createdTo(Instant.parse("2024-08-21T23:59:59Z"))
                .build();


        List<DocumentManager.Document> results = manager.search(request);


        assertEquals(1, results.size(), "Should find one document");
        assertEquals("1", results.get(0).getId(), "Document ID should be '1'");
    }

    @Test
    void testSearchContainSomeNull() {

        DocumentManager.SearchRequest request = DocumentManager.SearchRequest.builder()
                .titlePrefixes(Arrays.asList("different", "test", null))
                .containsContents(Arrays.asList("document",null))
                .authorIds(Arrays.asList("a1", null))
                .createdFrom(Instant.parse("2023-08-20T00:00:00Z"))
                .createdTo(null)
                .build();


        List<DocumentManager.Document> results = manager.search(request);


        assertEquals(2, results.size(), "Should find two document");
        assertEquals("1", results.get(0).getId(), "Document ID should be '1'");
        assertEquals("3", results.get(1).getId(), "Document ID should be '3'");
    }

    @Test
    void testSearchContainOnlyNull() {

        DocumentManager.SearchRequest request = DocumentManager.SearchRequest.builder()
                .titlePrefixes(null)
                .containsContents(null)
                .authorIds(null)
                .createdFrom(null)
                .createdTo(null)
                .build();


        List<DocumentManager.Document> results = manager.search(request);


        assertEquals(4, results.size(), "Should find four document");
    }


    @Test
    void testFindById() {

        Optional<DocumentManager.Document> foundDoc = manager.findById("2");
        assertTrue(foundDoc.isPresent(), "Document with ID '2' should be found");
        assertEquals("another test title 2", foundDoc.get().getTitle(), "Title should be 'another test title 2'");
    }

    @Test
    void testFindByIdNotFound() {

        Optional<DocumentManager.Document> foundDoc = manager.findById("non-existent");
        assertFalse(foundDoc.isPresent(), "Document with non-existent ID should not be found");
    }
}

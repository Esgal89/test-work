package org.example;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * For implement this task focus on clear code, and make this solution as simple
 * readable as possible Don't worry about performance, concurrency, etc You can
 * use in Memory collection for sore data
 * <p>
 * Please, don't change class name, and signature for methods save, search,
 * findById Implementations should be in a single class This class could be auto
 * tested
 */

public class DocumentManager {



    private final Map<String, Document> storage = new HashMap<>();

    /**
     * Implementation of this method should upsert the document to your storage And
     * generate unique id if it does not exist, don't change [created] field
     *
     * @param document - document content and author data
     * @return saved document
     */

    private String newId() {
        String id = UUID.randomUUID().toString();
        while (storage.containsKey(id)) {
            id = UUID.randomUUID().toString();
        }
        return id;
    }

    public Document save(Document document) {

        if (document.getId() == null || document.getId().isEmpty()) {
            document.setId(newId());
        }
        storage.put(document.getId(), document);
        return document;
    }

    /**
     * Implementation this method should find documents which match with request
     *
     * @param request - search request, each field could be null
     * @return list matched documents
     */
    public List<Document> search(SearchRequest request) {
        return storage.values().stream()
                .filter(doc -> Optional.ofNullable(request.getTitlePrefixes())
                        .map(prefixes -> prefixes.stream()
                                .filter(Objects::nonNull)
                                .anyMatch(prefix -> Optional.ofNullable(doc.getTitle())
                                        .map(title -> title.startsWith(prefix))
                                        .orElse(false)))
                        .orElse(true))
                .filter(doc -> Optional.ofNullable(request.getContainsContents())
                        .map(contents -> contents.stream()
                                .filter(Objects::nonNull)
                                .anyMatch(content -> Optional.ofNullable(doc.getContent())
                                        .map(contentText -> contentText.contains(content))
                                        .orElse(false)))
                        .orElse(true))
                .filter(doc -> Optional.ofNullable(request.getAuthorIds())
                        .map(authorIds -> authorIds.stream()
                                .filter(Objects::nonNull)
                                .anyMatch(authorId -> authorId.equals(doc.author.getId())))
                        .orElse(true))
                .filter(doc -> Optional.ofNullable(request.getCreatedFrom())
                        .map(createdFrom -> Optional.ofNullable(doc.getCreated())
                                .map(created -> created.isAfter(createdFrom))
                                .orElse(false))
                        .orElse(true))
                .filter(doc -> Optional.ofNullable(request.getCreatedTo())
                        .map(createdTo -> Optional.ofNullable(doc.getCreated())
                                .map(created -> created.isBefore(createdTo))
                                .orElse(false))
                        .orElse(true))
                .collect(Collectors.toList());
    }

    /**
     * Implementation this method should find document by id
     *
     * @param id - document id
     * @return optional document
     */
    public Optional<Document> findById(String id) {

        return Optional.ofNullable(storage.get(id));
    }

    @Data
    @Builder
    public static class SearchRequest {
        private List<String> titlePrefixes;
        private List<String> containsContents;
        private List<String> authorIds;
        private Instant createdFrom;
        private Instant createdTo;
    }

    @Data
    @Builder
    public static class Document {
        private String id;
        private String title;
        private String content;
        private Author author;
        private Instant created;
    }

    @Data
    @Builder
    public static class Author {
        private String id;
        private String name;
    }
}
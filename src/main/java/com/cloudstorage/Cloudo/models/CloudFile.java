package com.cloudstorage.Cloudo.models;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "files")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CloudFile {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private String originalName;

    @Column(nullable = false, unique = true)
    private String storedName;

    @Column(nullable = false)
    private String fileType;

    private Long size;

    private String category;

    private String storagePath;

    private boolean isPublic = false;

    private LocalDateTime uploadedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User owner;

    @PrePersist
    public void prePersist() {
        this.uploadedAt = LocalDateTime.now();
    }
}

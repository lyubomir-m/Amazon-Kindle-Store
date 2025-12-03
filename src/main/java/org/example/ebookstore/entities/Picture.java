package org.example.ebookstore.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "pictures")
public class Picture extends BaseEntity {
    @Column(nullable = false, length = 100)
    @Size(min = 2, max = 100)
    private String name;
    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] data;
    @Column(name = "content_type", nullable = false)
    private String contentType;


    public Picture() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
}

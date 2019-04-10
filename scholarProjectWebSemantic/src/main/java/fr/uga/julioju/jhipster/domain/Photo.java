package fr.uga.julioju.jhipster.domain;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.util.Objects;

/**
 * A Photo.
 */
@Entity
@Table(name = "photo")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Photo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    /**
     * jpeg thumbnail
     */
    
    @ApiModelProperty(value = "jpeg thumbnail", required = true)
    @Lob
    @Column(name = "fin_thumbnail", nullable = false)
    private byte[] finThumbnail;

    @Column(name = "fin_thumbnail_content_type", nullable = false)
    private String finThumbnailContentType;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties("photos")
    private Album album;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public Photo name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getFinThumbnail() {
        return finThumbnail;
    }

    public Photo finThumbnail(byte[] finThumbnail) {
        this.finThumbnail = finThumbnail;
        return this;
    }

    public void setFinThumbnail(byte[] finThumbnail) {
        this.finThumbnail = finThumbnail;
    }

    public String getFinThumbnailContentType() {
        return finThumbnailContentType;
    }

    public Photo finThumbnailContentType(String finThumbnailContentType) {
        this.finThumbnailContentType = finThumbnailContentType;
        return this;
    }

    public void setFinThumbnailContentType(String finThumbnailContentType) {
        this.finThumbnailContentType = finThumbnailContentType;
    }

    public Album getAlbum() {
        return album;
    }

    public Photo album(Album album) {
        this.album = album;
        return this;
    }

    public void setAlbum(Album album) {
        this.album = album;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Photo)) {
            return false;
        }
        return id != null && id.equals(((Photo) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Photo{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", finThumbnail='" + getFinThumbnail() + "'" +
            ", finThumbnailContentType='" + getFinThumbnailContentType() + "'" +
            "}";
    }
}

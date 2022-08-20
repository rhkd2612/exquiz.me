package com.mumomu.exquizme.oauth;

import com.mumomu.exquizme.formatter.SimpleDateFormatter;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class TimeEntity {
    @Column(name="created_date")
    @CreatedDate
    private String createdDate;

    @Column(name="modified_date")
    @LastModifiedDate
    private String modifiedDate;

    // 해당 엔티티를 저장하기 전 실행
    @PrePersist
    public void onPrePersist(){
        this.createdDate = SimpleDateFormatter.formatDateToString(new Date());
        this.modifiedDate = this.createdDate;
    }

    // 해당 엔티티를 업데이트 하기 전 실행
    @PreUpdate
    public void onPreUpdate() {
        this.modifiedDate = SimpleDateFormatter.formatDateToString(new Date());
    }
}

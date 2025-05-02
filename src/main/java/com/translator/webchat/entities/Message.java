package com.translator.webchat.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Data
@Table(name = "messages")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "content_en")
    private String contentEn;

    @Column(name = "content_vi")
    private String contentVi;

    @Column(name = "content_ja")
    private String contentJa;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "session_id", referencedColumnName="id")
    private Session session;

    @ManyToOne
    @JoinColumn(name = "group_id", referencedColumnName="id")
    private Group group;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName="id")
    private User user;

    @OneToOne
    @JoinColumn(name = "sub_message_id", referencedColumnName="id")
    private Message subMessage;
}

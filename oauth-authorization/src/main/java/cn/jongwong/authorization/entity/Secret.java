package cn.jongwong.authorization.entity;


import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serializable;


@Data
@Table(value = "tb_secret")
public class Secret implements Serializable {
    private static final long serialVersionUID = -339516038496531943L;
    @Id
    private Long id;
    @Column(value = "secret_id")
    private String secretId;
    @Column(value = "secret_key")
    private String secretKey;
    @Column(value = "secret_name")
    private String secretName;
}

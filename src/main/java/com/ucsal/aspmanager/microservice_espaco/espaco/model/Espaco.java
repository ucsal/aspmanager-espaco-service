package com.ucsal.aspmanager.microservice_espaco.espaco.model;

import com.ucsal.aspmanager.microservice_espaco.shared.model.enums.StatusRegistro;
import com.ucsal.aspmanager.microservice_espaco.shared.model.enums.TipoEspaco;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Entity
@Table(name = "espacos")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Espaco {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String sigla;

    @Column(nullable = false)
    private String nome;

    private String descricao;

    @Column(name = "capacidade_maxima", nullable = false)
    private Integer capacidadeMaxima;

    @Column(nullable = false)
    private String localizacao;

    @Column(name = "status_registro", nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private StatusRegistro statusRegistro = StatusRegistro.ATIVO;

    @Column(name = "tipo_computadores")
    private String tipoComputadores;

    @Enumerated(EnumType.STRING)
    private TipoEspaco tipoEspaco;

    //objeto Escola agora é id
    @Column(name = "id_escola")
    private Long idEscola;

    // Substituição da lista de objetos Software apenas por uma lista de IDs
    @ElementCollection
    @CollectionTable(name = "espaco_softwares", joinColumns = @JoinColumn(name = "id_espaco"))
    @Column(name = "id_software")
    private List<Long> softwaresIds;
}
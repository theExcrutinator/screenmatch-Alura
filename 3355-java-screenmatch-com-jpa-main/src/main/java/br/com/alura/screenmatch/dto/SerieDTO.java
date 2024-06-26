package br.com.alura.screenmatch.dto;

import br.com.alura.screenmatch.model.Categoria;

public record SerieDTO(Long id,
                       String titulo,
                       Integer toalTemporadas,
                       Double avaliacao,
                       Categoria genero,
                       String poster,
                       String atores,
                       String sinopse) {
}

package br.com.alura.screenmatch.repository;

import br.com.alura.screenmatch.model.Categoria;
import br.com.alura.screenmatch.model.Episodio;
import br.com.alura.screenmatch.model.Serie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SerieRepository extends JpaRepository<Serie, Long> {
   Optional<Serie> findByTituloContainingIgnoreCase(String nomeSerie);

    List<Serie> findByAtoresContainingIgnoreCase(String nomeAutor);

    @Query("SELECT s FROM Serie s ORDER BY s.avaliacao DESC")
    List<Serie> findTopOrderByAvaliacaoDesc(@Param("limit") double limit);

    List<Serie> findTop5ByOrderByAvaliacaoDesc();

    List<Serie> findByGenero(Categoria categoria);


    List<Serie> findByTotalTemporadasLessThanEqual(int qntTemporadas);
    //List<Serie> findTop5ByOrderByAvaliacaoDesc();

    @Query("select s from Serie s WHERE s.sinopse ILIKE %:sinopsePassada%")
    List<Serie> encontraSeriePorParteDaSinopse(String sinopsePassada);

    @Query("SELECT e FROM Serie s JOIN s.episodios e WHERE e.titulo ILIKE %:trechoDoEpisodio%")
    List<Episodio> encontraEpisodiosPorTitulo(String trechoDoEpisodio);

    @Query("SELECT e FROM Serie s JOIN s.episodios e WHERE s =:serie ORDER BY e.avaliacao DESC LIMIT :rankSize")
    List<Episodio> listaTopEpisodios(Serie serie, int rankSize);

    List<Serie> findByOrderByEpisodiosDataLancamentoDesc();
}

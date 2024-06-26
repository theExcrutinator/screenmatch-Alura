package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.model.*;
import br.com.alura.screenmatch.repository.SerieRepository;
import br.com.alura.screenmatch.service.ConsumoApi;
import br.com.alura.screenmatch.service.ConverteDados;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

public class Principal {

    private final Scanner leitura = new Scanner(System.in);
    private final ConsumoApi consumo = new ConsumoApi();
    private final ConverteDados conversor = new ConverteDados();
    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=6585022c";
    private SerieRepository serieRepository;
    private List<Serie> listaDeSeries = new ArrayList<>();
    private Optional<Serie> serieBuscada;


    public Principal(SerieRepository serieRepository) {
        this.serieRepository = serieRepository;
    }


    public void exibeMenu() {
        var opcao = -1;
        while (opcao != 0){
        var menu = """
                1 - Buscar séries
                2 - Buscar episódios
                3 - Listar séries buscadas
                4 - listar série pelo titulo
                5 - listar série pelo ator
                6 - listar top series
                7 - listar séries por Genero/Categoria
                8 - listar por quantidade de temporadas
                9 - encontrar serie por sinopse
                10 - Listar episodios por titulo
                11 - Ranking de episodios da Serie
                                
                0 - Sair                                 
                """;

        System.out.println(menu);
        opcao = leitura.nextInt();
        leitura.nextLine();

        switch (opcao) {
            case 1:
                buscarSerieWeb();
                break;
            case 2:
                buscarEpisodioPorSerie();
                break;
            case 3:
                listarSeries();
                break;
            case 4:
                buscarSeriePorTitulo();
                break;
            case 5:
                buscarSeriePorAtor();
                break;
            case 6:
                listarTopSeries();
                break;
            case 7:
                listarSeriesPorCategoria();
                break;
            case 8:
                listarSeriePorTemporadas();
                break;
            case 9:
                encontraPorSinopse();
                break;
            case 10:
                encontraPorTituloDoEpisodio();
                break;
            case 11:
                listaTopEpisodiosPorSerie();
                break;
            case 0:
                System.out.println("Saindo...");
                break;
            default:
                System.out.println("Opção inválida");
        }
    }
}


    private void buscarSerieWeb() {
        DadosSerie dados = getDadosSerie();
        Serie serie = new Serie(dados);
//        series.add(dados);
        if(serieRepository.findByTituloContainingIgnoreCase(serie.getTitulo()).isEmpty()) {
            serieRepository.save(serie);
        }

        System.out.println(dados);
    }
    private void listarSeries(){
        listaDeSeries = serieRepository.findAll();
        listaDeSeries.forEach(System.out::println);
    }

    private DadosSerie getDadosSerie() {
        var nomeSerie = leitura.nextLine();
        var json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);
        return conversor.obterDados(json, DadosSerie.class);
    }

    private void buscarEpisodioPorSerie(){
        listarSeries();
        System.out.println("Digite o nome da série para busca");
        var nomeSerie = leitura.nextLine();
        Optional<Serie> serie = listaDeSeries.stream().filter(e ->e.getTitulo()
                .toLowerCase()
                .contains(nomeSerie.toLowerCase()))
                .findFirst();
        if(serie.isPresent()){
            var serieEncontrada = serie.get();
            List<DadosTemporada> temporadas = new ArrayList<>();

            for (int i = 1; i <= serieEncontrada.getTotalTemporadas(); i++) {
                var json = consumo.obterDados(ENDERECO + serieEncontrada.getTitulo().replace(" ", "+") + "&season=" + i + API_KEY);
                DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
                temporadas.add(dadosTemporada);
            }
            temporadas.forEach(System.out::println);
            List<Episodio> episodios = temporadas.stream()
                    .flatMap(d-> d.episodios().stream().map(e -> new Episodio(d.numero(), e)))
                    .collect(Collectors.toList());
            serieEncontrada.setEpisodios(episodios);
            serieRepository.save(serieEncontrada);
        }
    }

    private void buscarSeriePorTitulo() {
        System.out.println("Digite o nome da série para busca");
        var nomeSerie = leitura.nextLine();

       serieBuscada = serieRepository.findByTituloContainingIgnoreCase(nomeSerie);

        serieBuscada.ifPresent(serie -> System.out.println("Série encontrada:" + serie.getTitulo()));

    }
    private void buscarSeriePorAtor(){
        System.out.println("Qual o nome do ator");
        var nomeAutor = leitura.nextLine();
        List<Serie> seriesPorAutor = serieRepository.findByAtoresContainingIgnoreCase(nomeAutor);
        seriesPorAutor.forEach(s -> System.out.println(s.getTitulo() + "Avaliação: " + s.getAvaliacao() ));
    }
    private void listarTopSeries(){
        System.out.println("qual o tamanho do ranking?");
        var tamanho = leitura.nextDouble();
        List<Serie> ranking = serieRepository.findTopOrderByAvaliacaoDesc(tamanho);
        ranking.forEach(s -> System.out.println(s.getTitulo() + "Avaliação: " + s.getAvaliacao() ));
    }
    public void listarSeriesPorCategoria(){
        System.out.println("Qual o genero/categoria do filme?");
        var nomeGenero = leitura.nextLine();
        Categoria categoria = Categoria.fromPortugues(nomeGenero);
        List<Serie> listaDeSeriesPorCategoria = serieRepository.findByGenero(categoria);
        listaDeSeriesPorCategoria.stream()
                .sorted(Comparator.comparingDouble(Serie::getAvaliacao).reversed())
        .forEach(s -> System.out.println(s.getTitulo() + "Avaliação: " + s.getAvaliacao() ));

    }
    public void listarSeriePorTemporadas(){
        System.out.println("Quantas temporadas no maximo");
        int qntTemporadas = leitura.nextInt();
        List<Serie> listaDeSeriesPorTemporadas = serieRepository.findByTotalTemporadasLessThanEqual(qntTemporadas);
        listaDeSeriesPorTemporadas.stream()
                .sorted(Comparator.comparingDouble(Serie::getAvaliacao).reversed())
                .forEach(s -> System.out.println(s.getTitulo() + "Avaliação: "
                        + s.getAvaliacao() + " temporads = "
                        +  s.getTotalTemporadas() ));
    }
    public void encontraPorSinopse(){
        System.out.println("Digite parte da Sinopse?");
        var trecho = leitura.nextLine();
        List<Serie> listaPorSinopse = serieRepository.encontraSeriePorParteDaSinopse(trecho);
        listaPorSinopse.forEach(s -> System.out.println(s.getTitulo() + "Avaliação: " + s.getAvaliacao()));
        if(listaPorSinopse.isEmpty()){
            System.out.println("Nenhuma serie encontrada, tente pesquisar por palavras chave como(vengeance) ou (quest)");
        }
    }
    public void encontraPorTituloDoEpisodio(){
        System.out.println("Digite parte do nome do episódio");
        var trecho = leitura.nextLine();
        List<Episodio> listaDeEpisodios = serieRepository.encontraEpisodiosPorTitulo(trecho);
        listaDeEpisodios.forEach(e -> System.out.printf("\n Serie : " + e.getSerie().getTitulo() + "\n" +
                " Titulo : " + e.getTitulo() + "\n" +
                " Temporada : " + e.getTemporada() + "\n" +
                " numero do episodio : " + e.getNumeroEpisodio() + "\n"));
    }
    public void listaTopEpisodiosPorSerie(){
        buscarSeriePorTitulo();
        if(serieBuscada.isPresent()){
            Serie serie = serieBuscada.get();
            System.out.println("Qual o tamanho do ranking?");
            var rankSize = leitura.nextInt();

            List<Episodio> episodioList = serieRepository.listaTopEpisodios(serie,rankSize);
            if (!episodioList.isEmpty()) {
                episodioList.forEach(e -> System.out.printf("\n Serie : " + e.getSerie().getTitulo() + "\n" +
                        " Titulo : " + e.getTitulo() + "\n" +
                        " Avaliação : " + e.getAvaliacao() + "\n" +
                        " Temporada : " + e.getTemporada() + "\n" +
                        " numero do episodio : " + e.getNumeroEpisodio() + "\n"));
            }else {
                System.out.println("Os episodios dessa serie ainda não foram adicionados");
            }
        }else {
            System.out.println("A serie buscada ainda n foi adicionada, por favor use a opcao 1");
        }

    }

}
package br.com.alura.screenmatch.controller;

import br.com.alura.screenmatch.dto.SerieDTO;
import br.com.alura.screenmatch.service.SerieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/series")
public class SerieController {
    @Autowired
    private SerieService service;

    @GetMapping
    public List<SerieDTO> listarSeries(){
        return service.listarSeries();
    }
    @GetMapping("/top5")
    public List<SerieDTO> listarTop5Series(){
        return service.listarTop5Series();
    }
    @GetMapping("/lancamentos")
    public List<SerieDTO> listarLancamentos(){
        return service.listarUltimosLancamentos();
    }


    @GetMapping("/{id}")
    public SerieDTO obterPorId(@PathVariable Long id) {
        return service.obterPorId(id);
    }
    @GetMapping("/categoria/{nomeGenero}")
    public List<SerieDTO> obterSeriesPorCategoria(@PathVariable String nomeGenero){
        return service.obterSeriesPorCategoria(nomeGenero);
    }

}






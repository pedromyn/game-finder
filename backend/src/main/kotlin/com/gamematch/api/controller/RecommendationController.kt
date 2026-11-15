package com.gamematch.api.controller

import com.gamematch.api.dto.PreferencesRequest
import com.gamematch.api.dto.RecommendationResponse
import com.gamematch.api.dto.UpdateRecommendationRequest
import com.gamematch.api.service.RecommendationService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/recommendations")
@Tag(name = "Recomendações", description = "Geração e histórico de recomendações")
class RecommendationController(private val service: RecommendationService) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Calcula o ranking a partir das preferências, salva e devolve o resultado")
    fun create(@RequestBody request: PreferencesRequest): RecommendationResponse = service.create(request)

    @GetMapping
    @Operation(summary = "Lista o histórico de recomendações (mais recentes primeiro)")
    fun findAll(): List<RecommendationResponse> = service.findAll()

    @GetMapping("/{id}")
    @Operation(summary = "Busca uma recomendação pelo id")
    fun findById(@PathVariable id: Long): RecommendationResponse = service.findById(id)

    @PutMapping("/{id}")
    @Operation(summary = "Renomeia e/ou favorita uma recomendação")
    fun update(@PathVariable id: Long, @RequestBody request: UpdateRecommendationRequest): RecommendationResponse =
        service.update(id, request)

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Apaga uma recomendação do histórico")
    fun delete(@PathVariable id: Long) = service.delete(id)
}

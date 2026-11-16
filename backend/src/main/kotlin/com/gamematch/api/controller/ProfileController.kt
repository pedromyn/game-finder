package com.gamematch.api.controller

import com.gamematch.api.dto.ProfileResponse
import com.gamematch.api.dto.UpdateProfileRequest
import com.gamematch.api.service.ProfileService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/profiles/{userName}")
@Tag(name = "Perfil", description = "Foto, gêneros favoritos e jogos curtidos de cada usuário")
class ProfileController(private val service: ProfileService) {

    @GetMapping
    @Operation(summary = "Busca o perfil (devolve um perfil vazio se ainda não existir)")
    fun find(@PathVariable userName: String): ProfileResponse = service.find(userName)

    @PutMapping
    @Operation(summary = "Salva os gêneros favoritos")
    fun updateGenres(@PathVariable userName: String, @RequestBody request: UpdateProfileRequest): ProfileResponse =
        service.updateGenres(userName, request)

    @PostMapping("/photo", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    @Operation(summary = "Envia (ou troca) a foto de perfil")
    fun updatePhoto(@PathVariable userName: String, @RequestPart("photo") photo: MultipartFile): ProfileResponse =
        service.updatePhoto(userName, photo)

    @GetMapping("/photo")
    @Operation(summary = "Devolve a imagem da foto de perfil")
    fun photo(@PathVariable userName: String): ResponseEntity<ByteArray> {
        val photo = service.photo(userName)
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(photo.contentType)).body(photo.bytes)
    }

    @PutMapping("/likes/{gameId}")
    @Operation(summary = "Curte um jogo")
    fun like(@PathVariable userName: String, @PathVariable gameId: Long): ProfileResponse =
        service.like(userName, gameId)

    @DeleteMapping("/likes/{gameId}")
    @Operation(summary = "Remove a curtida de um jogo")
    fun unlike(@PathVariable userName: String, @PathVariable gameId: Long): ProfileResponse =
        service.unlike(userName, gameId)
}

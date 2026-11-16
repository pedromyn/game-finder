package com.gamematch.app.ui.profile

import android.net.Uri
import android.os.Bundle
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.gamematch.app.R
import com.gamematch.app.adapter.LikedGameAdapter
import com.gamematch.app.data.UserSession
import com.gamematch.app.data.api.ApiClient
import com.gamematch.app.data.api.apiCall
import com.gamematch.app.data.model.Profile
import com.gamematch.app.data.model.UpdateProfileRequest
import com.gamematch.app.databinding.ActivityProfileBinding
import com.gamematch.app.ui.common.BaseActivity
import com.gamematch.app.ui.common.ScreenState
import com.gamematch.app.ui.common.genreOptions
import com.gamematch.app.ui.common.loadImage
import com.gamematch.app.ui.common.readImageAsJpeg
import com.gamematch.app.ui.detail.GameDetailActivity
import com.google.android.material.chip.Chip
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

/** Tela 9: perfil com foto, gêneros favoritos e jogos curtidos (GET/PUT /profiles/{nome}). */
class ProfileActivity : BaseActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var state: ScreenState
    private lateinit var userName: String
    private val genreChips = mutableListOf<Chip>()

    private val adapter = LikedGameAdapter { game ->
        openGame.launch(GameDetailActivity.newIntent(this, game.id))
    }

    /** Ao voltar dos detalhes, atualiza a lista (o jogo pode ter sido descurtido lá). */
    private val openGame = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        reloadLikes()
    }

    /** Seletor de fotos do sistema: não precisa pedir permissão de armazenamento. */
    private val pickPhoto = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        uri?.let(::uploadPhoto)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userName = UserSession.userName(this) ?: return finish()
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        showBackButton()
        state = ScreenState(binding.state, binding.content)

        createGenreChips()
        binding.rvLikes.layoutManager = LinearLayoutManager(this)
        binding.rvLikes.adapter = adapter
        binding.btnPhoto.setOnClickListener {
            pickPhoto.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
        binding.btnSaveGenres.setOnClickListener { saveGenres() }
        load()
    }

    private fun createGenreChips() {
        genreOptions().forEach { genre ->
            val chip = layoutInflater.inflate(R.layout.item_genre_chip, binding.chipGenres, false) as Chip
            chip.text = genre
            binding.chipGenres.addView(chip)
            genreChips += chip
        }
    }

    private fun load() {
        state.loading()
        lifecycleScope.launch {
            apiCall { getProfile(userName) }
                .onSuccess(::show)
                .onFailure { state.error(it.message, ::load) }
        }
    }

    private fun show(profile: Profile) {
        binding.tvName.text = profile.userName
        showPhoto(profile)
        genreChips.forEach { it.isChecked = it.text in profile.favoriteGenres }
        showLikes(profile)
        state.content()
    }

    private fun showPhoto(profile: Profile) {
        ApiClient.photoUrl(profile)?.let { binding.ivPhoto.loadImage(it, R.drawable.ic_avatar_placeholder) }
    }

    private fun showLikes(profile: Profile) {
        adapter.submitList(profile.likedGames)
        binding.tvNoLikes.isVisible = profile.likedGames.isEmpty()
    }

    private fun reloadLikes() {
        lifecycleScope.launch {
            apiCall { getProfile(userName) }.onSuccess(::showLikes)
        }
    }

    /** PUT /profiles/{nome} com os chips marcados. */
    private fun saveGenres() {
        val genres = genreChips.filter { it.isChecked }.map { it.text.toString() }
        binding.btnSaveGenres.isEnabled = false
        lifecycleScope.launch {
            apiCall { updateProfile(userName, UpdateProfileRequest(genres)) }
                .onSuccess { snackbar(getString(R.string.message_genres_saved)) }
                .onFailure { snackbar(it.message) }
            binding.btnSaveGenres.isEnabled = true
        }
    }

    /** Reduz a imagem escolhida e envia como multipart (POST /profiles/{nome}/photo). */
    private fun uploadPhoto(uri: Uri) {
        binding.btnPhoto.isEnabled = false
        binding.photoProgress.isVisible = true
        lifecycleScope.launch {
            val jpeg = withContext(Dispatchers.IO) { readImageAsJpeg(uri) }
            if (jpeg == null) {
                snackbar(getString(R.string.error_image))
            } else {
                val part = MultipartBody.Part.createFormData(
                    "photo", "photo.jpg", jpeg.toRequestBody("image/jpeg".toMediaType()),
                )
                apiCall { uploadPhoto(userName, part) }
                    .onSuccess {
                        showPhoto(it)
                        snackbar(getString(R.string.message_photo_saved))
                    }
                    .onFailure { snackbar(it.message) }
            }
            binding.btnPhoto.isEnabled = true
            binding.photoProgress.isVisible = false
        }
    }
}

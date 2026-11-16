package com.gamematch.app.ui.auth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import com.gamematch.app.R
import com.gamematch.app.data.UserSession
import com.gamematch.app.data.api.apiCall
import com.gamematch.app.data.model.AuthRequest
import com.gamematch.app.databinding.ActivityAuthBinding
import com.gamematch.app.ui.common.BaseActivity
import com.gamematch.app.ui.home.HomeActivity
import kotlinx.coroutines.launch

/**
 * Tela 2: entrar (POST /auth/login) ou criar perfil (POST /auth/register).
 * É a mesma tela nos dois modos; o cadastro mostra também o campo de confirmar senha.
 */
class AuthActivity : BaseActivity() {

    private lateinit var binding: ActivityAuthBinding
    private val isRegister by lazy { intent.getBooleanExtra(EXTRA_REGISTER, false) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
        showBackButton()

        setTitle(if (isRegister) R.string.title_register else R.string.title_login)
        binding.tvSubtitle.setText(if (isRegister) R.string.register_subtitle else R.string.login_subtitle)
        binding.tilConfirm.isVisible = isRegister
        binding.btnSubmit.setText(if (isRegister) R.string.action_register else R.string.action_login)
        binding.btnSwitch.setText(if (isRegister) R.string.switch_to_login else R.string.switch_to_register)

        // Limpa o erro de cada campo assim que a pessoa volta a digitar.
        binding.etName.doAfterTextChanged { binding.tilName.error = null }
        binding.etPassword.doAfterTextChanged { binding.tilPassword.error = null }
        binding.etConfirm.doAfterTextChanged { binding.tilConfirm.error = null }

        binding.btnSubmit.setOnClickListener { submit() }
        binding.btnSwitch.setOnClickListener {
            startActivity(newIntent(this, register = !isRegister))
            finish()
        }
    }

    private fun submit() {
        val name = binding.etName.text?.toString()?.trim().orEmpty()
        val password = binding.etPassword.text?.toString().orEmpty()
        if (!validate(name, password)) return

        setLoading(true)
        lifecycleScope.launch {
            val request = AuthRequest(name, password)
            apiCall { if (isRegister) register(request) else login(request) }
                .onSuccess { profile ->
                    UserSession.setUserName(this@AuthActivity, profile.userName)
                    Toast.makeText(this@AuthActivity, getString(R.string.welcome_user, profile.userName), Toast.LENGTH_SHORT).show()
                    startFresh(Intent(this@AuthActivity, HomeActivity::class.java))
                }
                .onFailure {
                    setLoading(false)
                    snackbar(it.message)
                }
        }
    }

    /** Validação local antes de chamar a API; os erros aparecem embaixo de cada campo. */
    private fun validate(name: String, password: String): Boolean {
        var valid = true
        if (name.isEmpty()) {
            binding.tilName.error = getString(R.string.error_name_required)
            valid = false
        }
        if (password.length < MIN_PASSWORD) {
            binding.tilPassword.error = getString(R.string.error_password_short, MIN_PASSWORD)
            valid = false
        }
        if (isRegister && binding.etConfirm.text?.toString() != password) {
            binding.tilConfirm.error = getString(R.string.error_password_mismatch)
            valid = false
        }
        return valid
    }

    private fun setLoading(loading: Boolean) {
        binding.progress.isVisible = loading
        binding.btnSubmit.isEnabled = !loading
        binding.btnSwitch.isEnabled = !loading
    }

    companion object {
        private const val EXTRA_REGISTER = "register"
        private const val MIN_PASSWORD = 4

        fun newIntent(context: Context, register: Boolean): Intent =
            Intent(context, AuthActivity::class.java).putExtra(EXTRA_REGISTER, register)
    }
}

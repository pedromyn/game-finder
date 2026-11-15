package com.gamematch.api.config

import com.gamematch.api.model.Game
import com.gamematch.api.model.GameMode
import com.gamematch.api.model.GameMode.AMBOS
import com.gamematch.api.model.GameMode.ONLINE
import com.gamematch.api.model.GameMode.SOLO
import com.gamematch.api.repository.GameRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component

/** Preenche o catálogo na primeira execução (quando a tabela de jogos está vazia). */
@Component
class DataSeeder(private val games: GameRepository) : CommandLineRunner {

    override fun run(vararg args: String) {
        if (games.count() == 0L) games.saveAll(catalog())
    }

    private val all = setOf("PC", "PlayStation", "Xbox", "Switch")
    private val noSwitch = setOf("PC", "PlayStation", "Xbox")

    /** [steamId] vira o id do jogo e monta os links da capa e da loja. */
    private fun game(
        steamId: Long, name: String, genres: String, platforms: Set<String>,
        mode: GameMode, short: Boolean, description: String,
    ) = Game(
        id = steamId,
        name = name,
        description = description,
        genres = genres.split(", ").toMutableSet(),
        platforms = platforms.toMutableSet(),
        mode = mode,
        shortGame = short,
        imageUrl = "https://cdn.akamai.steamstatic.com/steam/apps/$steamId/header.jpg",
        storeUrl = "https://store.steampowered.com/app/$steamId",
    )

    private fun catalog() = listOf(
        game(1145360, "Hades", "Ação, RPG", all, SOLO, false, "Roguelike de ação em que você tenta escapar do submundo grego, ficando mais forte a cada tentativa."),
        game(413150, "Stardew Valley", "Simulação, RPG", all, AMBOS, false, "Herde uma fazenda, plante, crie animais e faça amizade com os moradores da vila."),
        game(367520, "Hollow Knight", "Plataforma, Ação, Aventura", all, SOLO, false, "Explore um vasto reino subterrâneo de insetos neste metroidvania desenhado à mão."),
        game(504230, "Celeste", "Plataforma", all, SOLO, true, "Ajude Madeline a escalar a montanha Celeste em um jogo de plataforma desafiador e emocionante."),
        game(292030, "The Witcher 3: Wild Hunt", "RPG, Aventura", all, SOLO, false, "Na pele do bruxo Geralt, cace monstros em um mundo aberto cheio de histórias."),
        game(1245620, "Elden Ring", "RPG, Ação", noSwitch, AMBOS, false, "RPG de ação em mundo aberto, com chefes difíceis e muita liberdade de exploração."),
        game(620, "Portal 2", "Puzzle", all, AMBOS, true, "Resolva quebra-cabeças com portais, sozinho ou em dupla no modo cooperativo."),
        game(105600, "Terraria", "Aventura, Ação", all, AMBOS, false, "Cave, construa e lute em um mundo 2D gerado aleatoriamente."),
        game(730, "Counter-Strike 2", "Tiro", setOf("PC"), ONLINE, true, "FPS tático competitivo em partidas rápidas de 5 contra 5."),
        game(252950, "Rocket League", "Esporte", all, ONLINE, true, "Futebol com carros movidos a foguete em partidas de cinco minutos."),
        game(1172470, "Apex Legends", "Tiro", all, ONLINE, true, "Battle royale em esquadrões, com heróis de habilidades únicas."),
        game(588650, "Dead Cells", "Ação, Plataforma", all, SOLO, false, "Roguelike de ação em 2D com combate rápido e castelo que muda a cada partida."),
        game(268910, "Cuphead", "Plataforma, Ação", all, SOLO, true, "Batalhas contra chefes com visual de desenho animado dos anos 1930."),
        game(391540, "Undertale", "RPG", all, SOLO, true, "RPG em que você pode vencer conversando: ninguém precisa ser derrotado."),
        game(632470, "Disco Elysium", "RPG", all, SOLO, false, "RPG de investigação focado em diálogos e escolhas, sem combate tradicional."),
        game(646570, "Slay the Spire", "Estratégia", all, SOLO, false, "Monte seu baralho e suba a torre neste roguelike de cartas."),
        game(945360, "Among Us", "Estratégia", all, ONLINE, true, "Descubra quem é o impostor antes que ele sabote a nave."),
        game(1426210, "It Takes Two", "Aventura, Plataforma, Puzzle", all, ONLINE, false, "Aventura feita exclusivamente para jogar em dupla, com mecânicas novas a cada fase."),
        game(1174180, "Red Dead Redemption 2", "Ação, Aventura", noSwitch, AMBOS, false, "Épico de faroeste em mundo aberto sobre o fim da era dos fora da lei."),
        game(1091500, "Cyberpunk 2077", "RPG, Tiro", noSwitch, SOLO, false, "RPG de ação em mundo aberto na megalópole futurista de Night City."),
        game(289070, "Civilization VI", "Estratégia", all, AMBOS, false, "Construa um império que resista ao tempo neste clássico de estratégia por turnos."),
        game(427520, "Factorio", "Estratégia, Simulação", setOf("PC", "Switch"), AMBOS, false, "Construa e automatize fábricas cada vez maiores em um planeta alienígena."),
        game(264710, "Subnautica", "Aventura, Simulação", all, SOLO, false, "Sobreviva e explore as profundezas de um oceano alienígena."),
        game(753640, "Outer Wilds", "Aventura, Puzzle", all, SOLO, false, "Desvende os mistérios de um sistema solar preso em um loop de 22 minutos."),
        game(1086940, "Baldur's Gate 3", "RPG, Estratégia", noSwitch, AMBOS, false, "RPG baseado em Dungeons & Dragons, com combate por turnos e escolhas que mudam a história."),
        game(550, "Left 4 Dead 2", "Tiro, Terror", setOf("PC", "Xbox"), ONLINE, true, "FPS cooperativo para quatro jogadores contra hordas de zumbis."),
        game(2050650, "Resident Evil 4", "Terror, Ação", noSwitch, SOLO, false, "Remake do clássico de survival horror: resgate a filha do presidente em uma vila sinistra."),
        game(782330, "DOOM Eternal", "Tiro, Ação", all, SOLO, false, "FPS frenético contra as hordas do inferno."),
        game(304430, "INSIDE", "Puzzle, Plataforma", all, SOLO, true, "Aventura sombria de plataforma e quebra-cabeças, contada sem nenhuma palavra."),
        game(383870, "Firewatch", "Aventura", all, SOLO, true, "Mistério narrativo sobre um vigia de incêndios nas florestas do Wyoming."),
        game(1551360, "Forza Horizon 5", "Esporte", noSwitch, AMBOS, false, "Corrida em mundo aberto pelas paisagens do México."),
        game(548430, "Deep Rock Galactic", "Tiro", noSwitch, ONLINE, true, "FPS cooperativo com anões mineradores espaciais em cavernas destrutíveis."),
        game(739630, "Phasmophobia", "Terror", noSwitch, ONLINE, true, "Investigação paranormal cooperativa: descubra que tipo de fantasma assombra o local."),
        game(227300, "Euro Truck Simulator 2", "Simulação", setOf("PC"), SOLO, false, "Dirija caminhões entregando cargas por toda a Europa."),
    )
}

# Sculk Contagione

[![Requires Fabric API](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3.1.2/assets/cozy/requires/fabric-api_64h.png)](https://modrinth.com/mod/fabric-api)

A mod that makes Sculk more, lets say, invasive.

This mod was inspired by Sculkhunt & Sculk Horde, and aims to make a vanilla-esque infection mod.

### Features

- Sculk absorbs non-sculk items, and damages entities without boots.
- Sculk catalyst now spreads automatically, has way more range (64 blocks instead of 16) and is more aggresive when spreading, can produce more catalysts, and gives spores to nearby entities, which if not cured with milk, kills the entity and turns it into a catalyst.
- Sculk shriekers spawned by a catalyst can now spawn Wardens.
- When spreading, Sculk catalysts can convert almost any block into Sculk.

You can configure things like timings and the amount of Sculk that spreads using Gamerules (see [this](src/main/resources/assets/sculkcontagione/lang/en_us.json)).

### License

Most of the code is licensed under the MIT License, see the [license file](LICENSE) for more info.

For the rest, these two specific files:
[PlayerEntityRendererMixin](src/main/java/me/clomclem/sculkcontagione/mixin/PlayerEntityRendererMixin.java),
[LivingEntityRendererMixin](src/main/java/me/clomclem/sculkcontagione/mixin/LivingEntityRendererMixin.java)

are licensed by [Ladysnake](https://github.com/Ladysnake) under the GPLv3 license, see the header on both files for more info.
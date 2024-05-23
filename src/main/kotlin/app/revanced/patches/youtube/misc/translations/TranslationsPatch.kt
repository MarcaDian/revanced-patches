package app.revanced.patches.youtube.misc.translations

import app.revanced.patcher.patch.annotation.CompatiblePackage
import app.revanced.patcher.patch.annotation.Patch
import app.revanced.patches.all.misc.resources.AddResourcesPatch
import app.revanced.patches.shared.translations.AbstractTranslationsPatch
import app.revanced.patches.youtube.misc.integrations.IntegrationsPatch

@Patch(
    name = "Translations",
    description = "Add Crowdin translations by MarcaD for YouTube Revanced.",
    dependencies = [IntegrationsPatch::class,AddResourcesPatch::class],
    compatiblePackages = [
        CompatiblePackage(
            "com.google.android.youtube",
            [
                "18.32.39",
                "18.37.36",
                "18.38.44",
                "18.43.45",
                "18.44.41",
                "18.45.43",
                "18.48.39",
                "18.49.37",
                "19.01.34",
                "19.02.39",
                "19.03.36",
                "19.04.38",
                "19.05.36",
                "19.06.39",
                "19.07.40",
                "19.08.36",
                "19.09.38",
                "19.10.39",
                "19.11.43"
            ],
        ),
    ],
)
@Suppress("unused")
object TranslationsPatch : AbstractTranslationsPatch(
    "youtube",
    arrayOf(
        "ru-rRU",
        "uk-rUA"
    )
)

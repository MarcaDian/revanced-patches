package app.revanced.patches.youtube.misc.translations

import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotation.CompatiblePackage
import app.revanced.patcher.patch.annotation.Patch
import app.revanced.patches.shared.translations.TranslationsUtils.copyXml
import app.revanced.patches.youtube.misc.settings.SettingsPatch

@Patch(
    name = "Translations",
    description = "Add Crowdin translations by MarcaD for YouTube Revanced.",
    dependencies = [SettingsPatch::class],
    compatiblePackages = [
        CompatiblePackage(
            "com.google.android.youtube",
            [
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
                "19.11.43",
                "19.12.41",
                "19.13.37",
                "19.14.43",
                "19.15.36",
                "19.16.39",
            ],
        ),
    ],
)

@Suppress("unused")
object TranslationsPatch : ResourcePatch() {
    override fun execute(context: ResourceContext) {

        context.copyXml(
            "youtube",
            arrayOf(
                "ru-rRU",
                "uk-rUA",
            )
        )
    }
}
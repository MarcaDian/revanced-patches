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
            "com.google.android.youtube"
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
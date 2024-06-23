package app.revanced.patches.youtube.layout.actionbuttons

import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotation.CompatiblePackage
import app.revanced.patcher.patch.annotation.Patch
import app.revanced.patcher.patch.options.PatchOption.PatchExtensions.stringPatchOption
import app.revanced.patches.youtube.misc.settings.SettingsPatch
import app.revanced.util.ResourceGroup
import app.revanced.util.copyResources

@Patch(
    name = "Custom Shorts action buttons",
    description = "Changes, at compile time, the icon of the action buttons of the Shorts player.",
    dependencies = [SettingsPatch::class],
    compatiblePackages = [
        CompatiblePackage(
            "com.google.android.youtube"
        ),
    ],
    use = false,
)

@Suppress("unused")
object ShortsActionButtonsPatch : ResourcePatch() {
    private const val DEFAULT_ICON = "round"

    private val IconType = stringPatchOption(
        key = "IconType",
        default = DEFAULT_ICON,
        values = mapOf(
            "Outline" to "outline",
            "OutlineCircle" to "outlinecircle",
            "Round" to DEFAULT_ICON
        ),
        title = "Shorts icon style ",
        description = "The style of the icons for the action buttons in the Shorts player.",
        required = true
    )

    override fun execute(context: ResourceContext) {

        arrayOf(
            "xxxhdpi",
            "xxhdpi",
            "xhdpi",
            "hdpi",
            "mdpi"
        ).forEach { dpi ->
            context.copyResources(
                "actionbuttons/$IconType",
                ResourceGroup(
                    "drawable-$dpi",
                    "ic_remix_filled_white_shadowed.webp",
                    "ic_right_comment_shadowed.webp",
                    "ic_right_dislike_off_shadowed.webp",
                    "ic_right_dislike_on_shadowed.webp",
                    "ic_right_like_off_shadowed.webp",
                    "ic_right_like_on_shadowed.webp",
                    "ic_right_share_shadowed.webp",

                    // for older versions only
                    "ic_remix_filled_white_24.webp",
                    "ic_right_dislike_on_32c.webp",
                    "ic_right_like_on_32c.webp"
                ),
                ResourceGroup(
                    "drawable",
                    "ic_right_comment_32c.xml",
                    "ic_right_dislike_off_32c.xml",
                    "ic_right_like_off_32c.xml",
                    "ic_right_share_32c.xml"
                )
            )
        }

        context.copyResources(
            "actionbuttons/shared",
            ResourceGroup(
                "drawable",
                "reel_camera_bold_24dp.xml",
                "reel_more_vertical_bold_24dp.xml",
                "reel_search_bold_24dp.xml"
            )
        )
    }
}
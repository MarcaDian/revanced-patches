package app.revanced.patches.youtube.interaction.downloads

import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstruction
import app.revanced.patcher.extensions.InstructionExtensions.addInstructionsWithLabels
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.annotation.CompatiblePackage
import app.revanced.patcher.patch.annotation.Patch
import app.revanced.patches.youtube.interaction.downloads.fingerprints.DownloadActionCommandResolverFingerprint
import app.revanced.patches.youtube.interaction.downloads.fingerprints.DownloadActionCommandResolverParentFingerprint
import app.revanced.patches.youtube.interaction.downloads.fingerprints.LegacyDownloadCommandResolverFingerprint
import app.revanced.patches.youtube.misc.playercontrols.PlayerControlsBytecodePatch
import app.revanced.patches.youtube.shared.fingerprints.MainActivityFingerprint
import app.revanced.patches.youtube.video.information.VideoInformationPatch
import app.revanced.util.resultOrThrow

@Patch(
    name = "Downloads",
    description = "Adds support to download videos with an external downloader app" +
        "using the in-app download button or a video player action button.",
    dependencies = [
        DownloadsResourcePatch::class,
        PlayerControlsBytecodePatch::class,
        VideoInformationPatch::class
    ],
    compatiblePackages = [
        CompatiblePackage(
            "com.google.android.youtube",
            [
                "18.48.39",
                "18.49.37",
                "19.01.34",
                "19.02.39",
                "19.03.35",
                "19.03.36",
                "19.04.38",
                "19.05.36"
            ]
        )
    ]
)
@Suppress("unused")
object DownloadsPatch : BytecodePatch(
    setOf(
        DownloadActionCommandResolverParentFingerprint,
        LegacyDownloadCommandResolverFingerprint,
        MainActivityFingerprint
    )
) {
    private const val INTEGRATIONS_CLASS_DESCRIPTOR = "Lapp/revanced/integrations/youtube/patches/DownloadsPatch;"
    private const val BUTTON_DESCRIPTOR = "Lapp/revanced/integrations/youtube/videoplayer/ExternalDownloadButton;"

    override fun execute(context: BytecodeContext) {
        PlayerControlsBytecodePatch.initializeControl("$BUTTON_DESCRIPTOR->initializeButton(Landroid/view/View;)V")
        PlayerControlsBytecodePatch.injectVisibilityCheckCall("$BUTTON_DESCRIPTOR->changeVisibility(Z)V")

        // Main activity is used to launch downloader intent.
        MainActivityFingerprint.resultOrThrow().mutableMethod.apply {
            addInstruction(
                implementation!!.instructions.lastIndex,
                "invoke-static { p0 }, $INTEGRATIONS_CLASS_DESCRIPTOR->activityCreated(Landroid/app/Activity;)V"
            )
        }

        val commonInstructions = """
            move-result v0
            if-eqz v0, :show_native_downloader
            return-void
            :show_native_downloader
            nop
        """

        DownloadActionCommandResolverFingerprint.resolve(context,
            DownloadActionCommandResolverParentFingerprint.resultOrThrow().classDef)
        DownloadActionCommandResolverFingerprint.resultOrThrow().mutableMethod.apply {
            addInstructionsWithLabels(
                0,
                """
                    invoke-static {}, $INTEGRATIONS_CLASS_DESCRIPTOR->inAppDownloadButtonOnClick()Z
                    $commonInstructions
                """
            )
        }

        // Legacy fingerprint is used for old spoofed versions,
        // or if download playlist is pressed on any version.
        // Downloading playlists is not yet supported,
        // as the code this hooks does not easily expost the playlist id.
        LegacyDownloadCommandResolverFingerprint.resultOrThrow().mutableMethod.apply {
            addInstructionsWithLabels(
                0,
                """
                    invoke-static/range {p1 .. p1}, $INTEGRATIONS_CLASS_DESCRIPTOR->inAppDownloadPlaylistLegacyOnClick(Ljava/lang/String;)Z
                    $commonInstructions
                """
            )
        }
    }
}
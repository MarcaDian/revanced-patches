package app.revanced.patches.youtube.misc.keeplandscape

import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstruction
import app.revanced.patcher.extensions.InstructionExtensions.addInstructions
import app.revanced.patcher.extensions.InstructionExtensions.getInstruction
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchException
import app.revanced.patcher.patch.annotation.CompatiblePackage
import app.revanced.patcher.patch.annotation.Patch
import app.revanced.patcher.util.proxy.mutableTypes.MutableMethod
import app.revanced.patches.all.misc.resources.AddResourcesPatch
import app.revanced.patches.shared.misc.settings.preference.InputType
import app.revanced.patches.shared.misc.settings.preference.SwitchPreference
import app.revanced.patches.shared.misc.settings.preference.TextPreference
import app.revanced.patches.youtube.misc.keeplandscape.fingerprints.BroadcastReceiverFingerprint
import app.revanced.patches.youtube.misc.keeplandscape.fingerprints.LandScapeModeConfigFingerprint
import app.revanced.patches.youtube.misc.settings.SettingsPatch
import app.revanced.util.exception
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.builder.instruction.BuilderInstruction21c
import com.android.tools.smali.dexlib2.iface.Method
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction


@Patch(
    name = "Keep landscape mode",
    description = "Adds an option to keep landscape mode when turning the screen off and on in fullscreen.",
    dependencies = [SettingsPatch::class, AddResourcesPatch::class],
    compatiblePackages = [
        CompatiblePackage(
            "com.google.android.youtube",
            [
                "18.42.41",
                "18.43.45",
                "18.44.41",
                "18.45.43",
                "18.46.45",
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
                "19.09.37"
            ],
        ),
    ],
)
@Suppress("unused")
object KeepLandScapeModePatch : BytecodePatch(
    setOf(
        BroadcastReceiverFingerprint,
        LandScapeModeConfigFingerprint
    )
) {
    override fun execute(context: BytecodeContext) {
        AddResourcesPatch(this::class)

        SettingsPatch.PreferenceScreen.MISC.addPreferences(
            SwitchPreference("revanced_keep_landscape_mode"),
            TextPreference("revanced_keep_landscape_mode_timeout", inputType = InputType.NUMBER)
        )

        LandScapeModeConfigFingerprint.result?.let {
            it.mutableMethod.apply {
                val insertIndex = implementation!!.instructions.size - 1
                val insertRegister = getInstruction<OneRegisterInstruction>(insertIndex).registerA

                addInstructions(
                    insertIndex, """
                        invoke-static {v$insertRegister}, Lapp/revanced/integrations/youtube/patches/KeepLandscapePatch;->keepFullscreen(Z)Z
                        move-result v$insertRegister
                        """
                )
            }
        } ?: throw PatchException("This version is not supported. Please use YouTube 18.42.41 or later.")

        BroadcastReceiverFingerprint.result?.let { result ->
            result.mutableMethod.apply {
                val stringIndex = getStringInstructionIndex("android.intent.action.SCREEN_ON")
                val insertIndex = getTargetIndex(stringIndex, Opcode.IF_EQZ) + 1

                addInstruction(
                    insertIndex,
                    "invoke-static {}, Lapp/revanced/integrations/youtube/patches/KeepLandscapePatch;->setScreenStatus()V"
                )
            }
        } ?: throw BroadcastReceiverFingerprint.exception
    }
}

fun Method.getStringInstructionIndex(value: String) =
    implementation?.let {
        it.instructions.indexOfFirst { instruction ->
            instruction.opcode == Opcode.CONST_STRING
                    && (instruction as? BuilderInstruction21c)?.reference.toString() == value
        }
    } ?: -1

fun MutableMethod.getTargetIndex(startIndex: Int, opcode: Opcode) =
    implementation!!.instructions.let {
        startIndex + it.subList(startIndex, it.size - 1).indexOfFirst { instruction ->
            instruction.opcode == opcode
        }
    }
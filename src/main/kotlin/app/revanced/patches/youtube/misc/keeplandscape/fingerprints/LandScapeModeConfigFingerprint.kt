package app.revanced.patches.youtube.misc.keeplandscape.fingerprints

import app.revanced.util.patch.LiteralValueFingerprint

/**
 * This fingerprint is compatible with YouTube v18.42.41+
 */
object LandScapeModeConfigFingerprint : LiteralValueFingerprint(
    returnType = "Z",
    literalSupplier = { 45446428 }
)
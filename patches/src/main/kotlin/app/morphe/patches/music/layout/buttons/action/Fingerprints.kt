// -----------------------------------------------------------------------------
// This file is a verbatim copy of MorpheApp's Fingerprints.kt, taken from:
//   github.com/MorpheApp/morphe-patches @ b142f87
//   path: patches/src/main/kotlin/app/morphe/patches/music/layout/buttons/action/Fingerprints.kt
//
// Included on this PoC branch so any reader can compare it directly with
// anddea's counterpart in his tree. This branch (bump-ytm-vol-2-poc) is a
// proof of concept: its commit metadata claims 2025-07-03, while the GitHub
// events API records the branch first appearing on 2026-07-10 - a ~372-day
// drift. Verify:
//   GET https://api.github.com/repos/MarcaDian/revanced-patches/commits/bump-ytm-vol-2-poc
//   GET https://api.github.com/repos/MarcaDian/revanced-patches/events
//
// Context: MorpheApp/morphe-patches#1983
// -----------------------------------------------------------------------------

/*
 * Copyright 2026 Morphe.
 * https://github.com/MorpheApp/morphe-patches
 *
 * See the included NOTICE file for GPLv3 Section 7 terms that apply to this code.
 */

package app.morphe.patches.music.layout.buttons.action

import app.morphe.patcher.Fingerprint
import app.morphe.patcher.InstructionLocation.MatchAfterImmediately
import app.morphe.patcher.InstructionLocation.MatchAfterWithin
import app.morphe.patcher.InstructionLocation.MatchFirst
import app.morphe.patcher.fieldAccess
import app.morphe.patcher.literal
import app.morphe.patcher.methodCall
import app.morphe.patcher.newInstance
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.Opcode

internal object ButtonProtoBufferGetterFingerprint : Fingerprint(
    accessFlags = listOf(AccessFlags.PUBLIC, AccessFlags.FINAL),
    returnType = "[B",
    parameters = listOf(),
    filters = listOf(
        methodCall(
            opcode = Opcode.INVOKE_VIRTUAL,
            smali = "Lcom/google/android/libraries/elements/adl/UpbMessage;->jniEncode(JJ)[B"
        )
    )
)

internal object TreeNodeListFingerprint : Fingerprint(
    accessFlags = listOf(AccessFlags.PROTECTED, AccessFlags.FINAL),
    returnType = "L",
    parameters = listOf("L"),
    filters = listOf(
        fieldAccess(
            opcode = Opcode.IGET_OBJECT,
            definingClass = "this",
            type = "L",
            location = MatchAfterWithin(5) // Match close to start of method.
        ),
        fieldAccess(
            opcode = Opcode.IGET_OBJECT,
            type = "Ljava/util/concurrent/atomic/AtomicReference;",
            location = MatchAfterWithin(5)
        ),
        fieldAccess(
            opcode = Opcode.IGET_OBJECT,
            type = "Ljava/util/concurrent/atomic/AtomicReference;",
            location = MatchAfterWithin(2)
        ),
        methodCall(
            opcode = Opcode.INVOKE_VIRTUAL,
            smali = "Ljava/util/concurrent/atomic/AtomicReference;->get()Ljava/lang/Object;",
            location = MatchAfterWithin(2)
        )
    )
)

internal object TreeNodeListHelperConstructorFingerprint : Fingerprint(
    classFingerprint = Fingerprint(
        accessFlags = listOf(AccessFlags.PUBLIC, AccessFlags.FINAL),
        returnType = "L",
        parameters = listOf("L", "L"),
        filters = listOf(
            newInstance("Ljava/util/ArrayList;", location = MatchFirst()),
            methodCall(
                opcode = Opcode.INVOKE_DIRECT,
                smali = "Ljava/util/ArrayList;-><init>()V",
                location = MatchAfterImmediately(),
            ),
            literal(0, location = MatchAfterWithin(10))
        ),
        custom = { _, classDef ->
            classDef.methods.count() == 2
        }
    ),
    name = "<init>"
)

package com.example.notably.ui.add

import android.app.Activity.RESULT_OK
import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.speech.RecognizerIntent
import android.view.Window
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.room.util.FileUtil
import com.example.notably.R
import com.example.notably.base.BaseBottomSheet
import com.example.notably.databinding.MoreActionsBottomSheetModalBinding
import com.example.notably.extensions.FileUtils
import com.example.notably.repos.entities.Note
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.ClassCastException
import java.lang.Exception
import java.util.*
import java.util.jar.Manifest

@AndroidEntryPoint
class MoreActionsBottomSheetModal :
    BaseBottomSheet<AddNoteViewModel, MoreActionsBottomSheetModalBinding>() {

    override val layoutId: Int = R.layout.more_actions_bottom_sheet_modal

    override val viewModel: AddNoteViewModel by viewModels()

    private val bundle = Bundle()
    private var presetNote: Note? = null

    private lateinit var onDeleteListener: OnDeleteListener
    private lateinit var onSpeechInputListener: OnSpeechInputListener

    interface OnDeleteListener {
        fun onDeleteListener(requestCode: Int)
    }

    interface OnSpeechInputListener {
        fun onSpeechInputListener(requestCode: Int, text: String)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            onDeleteListener = context as OnDeleteListener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement OnDeleteListener")
        }

        try {
            onSpeechInputListener = context as OnSpeechInputListener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement OnSpeechInputListener")
        }
    }

    override fun initComponents() {
        initPresetNote()
    }

    private fun initPresetNote() {
        presetNote = requireArguments().getSerializable("note_data") as? Note
    }

    override fun initListeners() {
        initArchiveNoteListener()
        initShareNoteListener()
        initDeleteNoteListener()
        initCopyToClipBoardListener()
        initExportAsListener()
        initSpeechToTextListener()
    }

    private fun initSpeechToTextListener() {
        binding.speechToText.setOnClickListener {
            if (presetNote != null) {
                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
                intent.putExtra(
                    RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                )
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.speak_notes))
                try {
                    startActivityForResult(intent, REQUEST_CODE_TEXT_TO_SPEECH)
                } catch (e: Exception) {
                    Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(
                    context,
                    getString(R.string.save_note_before_speech),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun initExportAsListener() {
        binding.exportNoteAs.setOnClickListener {
            if (presetNote != null) {
                showExportAsDialog()
            } else {
                Toast.makeText(
                    context,
                    getString(R.string.save_note_before_export),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun showExportAsDialog() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && requireContext().checkSelfPermission(
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 1000)
        }

        val exportNoteDialog = Dialog(requireContext())
        exportNoteDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        exportNoteDialog.setContentView(R.layout.export_note_as_dialog)

        // enable dialog cancel
        exportNoteDialog.setCancelable(true)
        exportNoteDialog.setOnCancelListener { exportNoteDialog.dismiss() }

        // export as txt
        exportNoteDialog.findViewById<LinearLayout>(R.id.export_as_txt).setOnClickListener {
            val fileName = FileUtils.requestExportFile(presetNote, "txt")
            Toast.makeText(context, fileName, Toast.LENGTH_SHORT).show()
            exportNoteDialog.dismiss()
        }

        // cancel
        exportNoteDialog.findViewById<LinearLayout>(R.id.confirm_deny).setOnClickListener {
            exportNoteDialog.dismiss()
        }

        exportNoteDialog.window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT
            )
        }
        exportNoteDialog.show()
    }

    private fun initCopyToClipBoardListener() {
        binding.copyToClipboard.setOnClickListener {
            if (presetNote != null) {
                // get note details
                val noteTitle = presetNote!!.title
                val noteSubtitle = presetNote!!.subtitle
                val noteDescription = presetNote!!.description

                // format note
                val noteText =
                    "Note title: $noteTitle\n\nNote subtitle: $noteSubtitle\n\nNote description: $noteDescription"
                // Copy to clipboard
                val clipboardManager =
                    context?.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                val data = ClipData.newPlainText("Copy Note", noteText)
                clipboardManager?.setPrimaryClip(data)
                Toast.makeText(context, getString(R.string.copied_to_clipboard), Toast.LENGTH_SHORT)
                    .show()
                dismiss()
            } else {
                Toast.makeText(
                    context,
                    getString(R.string.save_note_before_copy),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun initDeleteNoteListener() {
        binding.deleteNote.setOnClickListener {
            requestPresetTrashNote()
        }
    }

    private fun requestPresetTrashNote() {
        if (presetNote != null) {
            viewModel.insertTrashNote(presetNote!!)
            onDeleteListener.onDeleteListener(REQUEST_DELETE_NOTE_CODE)
        } else {
            onDeleteListener.onDeleteListener(REQUEST_DISCARD_NOTE_CODE)
            Toast.makeText(context, getString(R.string.note_discarded), Toast.LENGTH_SHORT).show()
        }
        dismiss()
    }

    private fun initShareNoteListener() {
        binding.shareNote.setOnClickListener {
            if (presetNote != null) {
                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.type = "text/plain"
                shareIntent.putExtra(Intent.EXTRA_TEXT, presetNote!!.description)
                startActivity(Intent.createChooser(shareIntent, getString(R.string.share_note)))
            } else {
                Toast.makeText(
                    context,
                    getString(R.string.save_note_before_share),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 1000) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(context, getString(R.string.permission_granted), Toast.LENGTH_SHORT)
                    .show()
            } else {
                Toast.makeText(context, getString(R.string.permission_denied), Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun initArchiveNoteListener() {
        binding.archiveNote.setOnClickListener {
            if (presetNote != null) {
                viewModel.insertToArchive(presetNote!!)
                Toast.makeText(context, getString(R.string.note_archived), Toast.LENGTH_SHORT)
                    .show()
                dismiss()
            } else {
                Toast.makeText(
                    context,
                    getString(R.string.save_note_before_archive),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_TEXT_TO_SPEECH) {
            if (resultCode == RESULT_OK && data != null) {
                val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                if (result != null) {
                    onSpeechInputListener.onSpeechInputListener(
                        REQUEST_SPEECH_INPUT_CODE,
                        result[0]
                    )
                    dismiss()
                }
            }
        }
    }

    companion object {
        const val REQUEST_DELETE_NOTE_CODE = 3
        const val REQUEST_DISCARD_NOTE_CODE = 4
        const val REQUEST_CODE_TEXT_TO_SPEECH = 7
        const val REQUEST_SPEECH_INPUT_CODE = 8
    }
}

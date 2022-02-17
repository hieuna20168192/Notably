package com.example.notably.ui.add

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.Dialog
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Patterns
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.*
import androidx.activity.viewModels
import androidx.core.app.ShareCompat
import androidx.core.view.isGone
import androidx.core.widget.doOnTextChanged
import com.example.notably.R
import com.example.notably.base.BaseActivity
import com.example.notably.databinding.ActivityAddNoteBinding
import com.example.notably.extensions.BitmapHelper.requestFilePath
import com.example.notably.receivers.ReminderReceiver
import com.example.notably.repos.entities.Category
import com.example.notably.repos.entities.Note
import com.example.notably.ui.sheets.AttachImageBottomSheetModal
import com.example.notably.ui.sheets.ReminderBottomSheetModal
import com.example.notably.ui.sheets.category.CategoriesBottomSheetModal
import com.example.notably.ui.view.ViewAttachedImageActivity
import com.example.notably.ui.view.ViewAttachedVideoActivity
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
@SuppressLint("ResourceType")
class AddNoteActivity : BaseActivity<AddNoteViewModel, ActivityAddNoteBinding>(),
    AttachImageBottomSheetModal.OnChooseImageListener,
    AttachImageBottomSheetModal.OnChooseVideoListener,
    ReminderBottomSheetModal.OnAddListener,
    ReminderBottomSheetModal.OnRemoveListener,
    CategoriesBottomSheetModal.OnChooseListener,
    MoreActionsBottomSheetModal.OnDeleteListener,
    MoreActionsBottomSheetModal.OnSpeechInputListener {

    override val layoutId: Int = R.layout.activity_add_note

    override val viewModel: AddNoteViewModel by viewModels()

    private var presetNote: Note? = null

    private var selectedNoteColor = ""
    private var selectedImagePath = ""
    private var selectedImageUri: Uri? = null

    private var selectedVideoPath = ""

    private lateinit var attachLinkDialog: Dialog
    private lateinit var link: EditText

    private lateinit var timePicker: TimePicker

    private var alarmStartTime: Long = 0L
    private var reminderSet = ""
    private var reminderIntent: PendingIntent? = null

    private var selectedNoteCategoryId: Int = -1

    private val bundle = Bundle()

    override fun initComponents() {
        initDefaultNoteColor()
        initPresetNote()
        initNoteCreatedTime()
    }

    private fun initPresetNote() {
        if (intent.getBooleanExtra("modifier", false)) {
            presetNote = intent.getSerializableExtra("note") as? Note
            bundle.putSerializable("note_data", presetNote)
            modifyPresetNote()
        }
    }

    private fun modifyPresetNote() {
        presetNote?.apply {
            binding.noteSave.isEnabled = !title.isNullOrEmpty()
            binding.noteTitle.setText(title)
            binding.noteSubtitle.setText(subtitle)
            binding.noteDescription.setText(description)
            binding.noteCreatedAt.text = createdAt
            selectedNoteColor = color
            selectedNoteCategoryId = categoryId
            reminderSet = reminder

            // set preset color
            setNoteColor()

            // set preset attach image
            setPresetImage()

            // set preset attach video
            setPresetVideo()

            // set preset attach link
            setPresetLink()

            // set preset alarm
            setPresetAlarm()
        }
    }

    private fun setPresetAlarm() {
        if (presetNote!!.reminder.isNotEmpty()) {
            binding.includeNoteActions.reminderSet.visibility = View.VISIBLE
            binding.includeNoteActions.reminderSetText.text = presetNote!!.reminder
        }
    }

    private fun setPresetLink() {
        binding.noteWebUrl.text = presetNote!!.webLink
        binding.noteWebUrlContainer.isGone = presetNote!!.webLink.trim().isEmpty()
    }

    private fun setPresetImage() {
        if (presetNote!!.imagePath.trim().isNotEmpty()) {
            binding.noteImage.setImageBitmap(BitmapFactory.decodeFile(presetNote!!.imagePath))
            binding.noteImageContainer.visibility = View.VISIBLE
            selectedImageUri = Uri.parse(presetNote!!.imageUri)
            selectedImagePath = presetNote!!.imagePath
        }
    }

    private fun setPresetVideo() {
        if (presetNote!!.videoPath.trim().isNotEmpty()) {
            val thumbnail = ThumbnailUtils.createVideoThumbnail(
                presetNote!!.videoPath,
                MediaStore.Video.Thumbnails.FULL_SCREEN_KIND
            )
            binding.noteVideo.setImageBitmap(thumbnail)
            binding.noteVideoContainer.visibility = View.VISIBLE
            selectedVideoPath = presetNote!!.videoPath
        }
    }

    private fun initNoteCreatedTime() {
        binding.noteCreatedAt.text = SimpleDateFormat("MM.dd.yyyy, HH:mm a", Locale.getDefault())
            .format(Date())
    }

    private fun initDefaultNoteColor() {
        selectedNoteColor = getString(R.color.note_theme_one)
    }

    override fun initListeners() {
        initTitleListeners()
        initNoteSaveListeners()
        initColorListeners()
        initAttachImageListener()
        initNavUpListener()
        initAttachUrlListener()
        initReminderListener()
        initCategoryChooserListener()
        initMoreOptionsListener()
        initNoteImageShareListener()
        initNoteImageRemoveListener()
        initNoteImageListener()
        initNoteVideoListener()
        initNoteVideoRemoveListener()
        initRemoveLinkListener()
    }

    private fun initRemoveLinkListener() {
        binding.noteWebUrlRemove.setOnClickListener {
            binding.noteWebUrl.text = null
            binding.noteWebUrlContainer.visibility = View.GONE
        }
    }

    private fun initNoteVideoRemoveListener() {
        binding.noteVideoRemove.setOnClickListener {
            val confirmDialog = createConfirmDialog()
            val confirmHeader = confirmDialog.findViewById<TextView>(R.id.confirm_header)
            val confirmText = confirmDialog.findViewById<TextView>(R.id.confirm_text)
            val confirmAllow = confirmDialog.findViewById<TextView>(R.id.confirm_allow)
            val confirmCancel = confirmDialog.findViewById<TextView>(R.id.confirm_deny)

            confirmHeader.text = getString(R.string.remove_video)
            confirmText.text = getString(R.string.remove_video_description)
            confirmAllow.setOnClickListener {
                binding.noteVideo.setImageBitmap(null)
                binding.noteVideoContainer.visibility = View.GONE
                selectedVideoPath = ""
                confirmDialog.dismiss()
            }

            confirmCancel.setOnClickListener { confirmDialog.dismiss() }
            confirmDialog.show()
        }
    }

    private fun initNoteVideoListener() {
        binding.noteVideo.setOnClickListener {
            val intent = Intent(this, ViewAttachedVideoActivity::class.java)
            intent.putExtra("video_path", selectedVideoPath)
            startActivityForResult(intent, REQUEST_VIEW_NOTE_VIDEO)
        }
    }

    private fun initNoteImageListener() {
        binding.noteImage.setOnClickListener {
            val intent = Intent(this, ViewAttachedImageActivity::class.java)
            intent.putExtra("image_path", selectedImagePath)
            startActivityForResult(intent, REQUEST_VIEW_NOTE_IMAGE)
        }
    }

    private fun initNoteImageRemoveListener() {
        binding.noteImageRemove.setOnClickListener {
            val confirmDialog = createConfirmDialog()
            val confirmHeader = confirmDialog.findViewById<TextView>(R.id.confirm_header)
            val confirmText = confirmDialog.findViewById<TextView>(R.id.confirm_text)
            val confirmAllow = confirmDialog.findViewById<TextView>(R.id.confirm_allow)
            val confirmCancel = confirmDialog.findViewById<TextView>(R.id.confirm_deny)

            confirmHeader.text = getString(R.string.remove_image)
            confirmText.text = getString(R.string.remove_image_description)
            confirmAllow.setOnClickListener {
                binding.noteImage.setImageBitmap(null)
                binding.noteImageContainer.visibility = View.GONE
                selectedImagePath = ""
                confirmDialog.dismiss()
            }

            confirmCancel.setOnClickListener { confirmDialog.dismiss() }
            confirmDialog.show()
        }
    }

    private fun createConfirmDialog(): Dialog {
        return Dialog(this).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(R.layout.popup_confirm)
            setCancelable(true)
            setOnCancelListener { dismiss() }
            window?.apply {
                setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                setLayout(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT
                )
            }
        }
    }

    private fun initNoteImageShareListener() {
        binding.noteImageShare.setOnClickListener {
            val share = ShareCompat.IntentBuilder.from(this)
                .setStream(selectedImageUri)
                .setType("text/html")
                .intent
                .setAction(Intent.ACTION_SEND)
                .setDataAndType(selectedImageUri, "image/*")
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivity(Intent.createChooser(share, getString(R.string.share_image)))
        }
    }

    private fun initMoreOptionsListener() {
        binding.includeNoteActions.moreActions.setOnClickListener {
            val moreActionsBottomSheetModal = MoreActionsBottomSheetModal()
            moreActionsBottomSheetModal.arguments = bundle
            moreActionsBottomSheetModal.show(
                supportFragmentManager,
                MoreActionsBottomSheetModal::class.simpleName
            )
        }
    }

    private fun initCategoryChooserListener() {
        binding.includeNoteActions.chooseCategory.setOnClickListener {
            requestCategoryChooser()
        }
    }

    private fun requestCategoryChooser() {
        val categoriesBottomSheetModal = CategoriesBottomSheetModal()
        categoriesBottomSheetModal.show(
            supportFragmentManager,
            CategoriesBottomSheetModal::class.simpleName
        )
    }

    private fun initReminderListener() {
        binding.includeNoteActions.addReminder.setOnClickListener {
            requestReminder()
        }
    }

    private fun requestReminder() {
        bundle.putString("REMINDER_SET", reminderSet)
        val reminderBottomSheetModal = ReminderBottomSheetModal()
        reminderBottomSheetModal.arguments = bundle
        reminderBottomSheetModal.show(
            supportFragmentManager,
            ReminderBottomSheetModal::class.simpleName
        )
    }

    private fun initTitleListeners() {
        binding.noteTitle.doOnTextChanged { text, _, _, _ ->
            binding.noteSave.isEnabled = !text.isNullOrEmpty()
        }
    }

    private fun initNoteSaveListeners() {
        binding.noteSave.setOnClickListener {
            val invalidMsg = msgMandatory()
            if (invalidMsg.isEmpty()) {
                saveAndFinish()
            } else {
                Toast.makeText(this, invalidMsg, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveAndFinish() {
        saveNote()
        setResult(RESULT_OK, Intent())
        finish()
    }

    private fun msgMandatory(): String {
        return when {
            binding.noteTitle.text.isEmpty() -> getString(R.string.note_title_required)
            binding.noteSubtitle.text.isEmpty()
                    || binding.noteDescription.text.isEmpty() -> getString(R.string.note_fields_required)
            else -> ""
        }
    }

    private fun initColorListeners() {
        binding.includeNoteActions.noteThemeOne.setOnClickListener {
            selectedNoteColor = getString(R.color.note_theme_one)
            setNoteColor()
        }

        binding.includeNoteActions.noteThemeTwo.setOnClickListener {
            selectedNoteColor = getString(R.color.note_theme_two)
            setNoteColor()
        }

        binding.includeNoteActions.noteThemeThree.setOnClickListener {
            selectedNoteColor = getString(R.color.note_theme_three)
            setNoteColor()
        }

        binding.includeNoteActions.noteThemeFour.setOnClickListener {
            selectedNoteColor = getString(R.color.note_theme_four)
            setNoteColor()
        }

        binding.includeNoteActions.noteThemeFive.setOnClickListener {
            selectedNoteColor = getString(R.color.note_theme_five)
            setNoteColor()
        }

        binding.includeNoteActions.noteThemeSix.setOnClickListener {
            selectedNoteColor = getString(R.color.note_theme_six)
            setNoteColor()
        }

        binding.includeNoteActions.noteThemeSeven.setOnClickListener {
            selectedNoteColor = getString(R.color.note_theme_seven)
            setNoteColor()
        }

        binding.includeNoteActions.noteThemeEight.setOnClickListener {
            selectedNoteColor = getString(R.color.note_theme_eight)
            setNoteColor()
        }

        binding.includeNoteActions.noteThemeNine.setOnClickListener {
            selectedNoteColor = getString(R.color.note_theme_nine)
            setNoteColor()
        }

        binding.includeNoteActions.noteThemeTen.setOnClickListener {
            selectedNoteColor = getString(R.color.note_theme_ten)
            setNoteColor()
        }
    }

    private fun setNoteColor() {
        val shape = binding.noteDescription.background as GradientDrawable
        shape.setColor(Color.parseColor(selectedNoteColor))
    }

    private fun initAttachImageListener() {
        binding.includeNoteActions.attachImage.setOnClickListener {
            val attachImageBottomSheetModal = AttachImageBottomSheetModal()
            attachImageBottomSheetModal.show(
                supportFragmentManager,
                AttachImageBottomSheetModal::class.simpleName
            )
        }
    }

    private fun initNavUpListener() {
        binding.goBack.setOnClickListener {
            if (msgMandatory().isEmpty()) {
                saveAndFinish()
            } else {
                finish()
            }
        }
    }

    private fun initAttachUrlListener() {
        binding.includeNoteActions.attachLink.setOnClickListener {
            requestAttachLink()
        }
    }

    private fun requestAttachLink() {
        attachLinkDialog = Dialog(this)
        setStyleForAttachLink(attachLinkDialog)

        // attachment link
        link = attachLinkDialog.findViewById(R.id.link)
        val addLink: Button = attachLinkDialog.findViewById(R.id.add_link)

        link.doOnTextChanged { text, _, _, _ ->
            addLink.isEnabled = (text ?: "").isNotEmpty()
        }
        link.requestFocus()

        addLink.setOnClickListener {
            onAddLinkClickListener()
        }

        // cancel link
        val cancelLink: TextView = attachLinkDialog.findViewById(R.id.cancel_link)
        cancelLink.setOnClickListener { attachLinkDialog.dismiss() }

        // show dialog
        attachLinkDialog.show()
    }

    private fun setStyleForAttachLink(attachLinkDialog: Dialog) {
        attachLinkDialog.apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(R.layout.popup_attach_link)
            setCancelable(true)
            setOnCancelListener { dismiss() }

            window?.apply {
                setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                setLayout(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT
                )
                attributes.windowAnimations = R.style.DetailAnimation
                attributes.gravity = Gravity.BOTTOM
            }
        }
    }


    private fun onAddLinkClickListener() {
        when {
            isEmptyLink() -> {
                Toast.makeText(this, getString(R.string.link_url_empty), Toast.LENGTH_SHORT)
                    .show()
            }
            isInValidLinkPattern() -> {
                Toast.makeText(this, getString(R.string.invalid_url), Toast.LENGTH_SHORT).show()
            }
            else -> {
                visibleAttachLink()
                attachLinkDialog.dismiss()
            }
        }
    }

    private fun isEmptyLink(): Boolean {
        return link.text.trim().isEmpty()
    }

    private fun isInValidLinkPattern(): Boolean {
        return !Patterns.WEB_URL.matcher(link.text).matches()
    }

    private fun visibleAttachLink() {
        binding.noteWebUrl.text = link.text
        binding.noteWebUrlContainer.visibility = View.VISIBLE
    }

    private fun saveNote() {
        val newNote = getNewNote()
        setAlarm()
        viewModel.saveNote(newNote)
    }

    private fun getNewNote(): Note {
        return Note(
            noteId = presetNote?.noteId ?: 0,
            title = binding.noteTitle.text.toString(),
            subtitle = binding.noteSubtitle.text.toString(),
            createdAt = binding.noteCreatedAt.text.toString(),
            color = selectedNoteColor,
            description = binding.noteDescription.text.toString(),
            imagePath = selectedImagePath,
            imageUri = selectedImageUri?.toString() ?: "",
            videoPath = selectedVideoPath,
            webLink = getNoteWebUrl(),
            reminder = reminderSet,
            categoryId = selectedNoteCategoryId
        )
    }

    private fun setAlarm() {
        if (alarmStartTime != 0L) {
            val alarm: AlarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
            alarm.set(AlarmManager.RTC_WAKEUP, alarmStartTime, reminderIntent)
        }
    }

    private fun getNoteWebUrl(): String {
        return if (binding.noteWebUrlContainer.visibility == View.VISIBLE) {
            binding.noteWebUrl.text.toString()
        } else ""
    }

    override fun onChooseImageListener(requestCode: Int, bitmap: Bitmap?, uri: Uri?) {
        if (isValidImageCode(requestCode)) {
            // set image
            binding.noteImage.setImageBitmap(bitmap)
            binding.noteImageContainer.visibility = View.VISIBLE

            // request image path
            selectedImagePath = requestFilePath(this, uri)

            // request image uri
            selectedImageUri = uri
        }
    }

    private fun isValidImageCode(requestCode: Int): Boolean {
        return requestCode == AttachImageBottomSheetModal.REQUEST_SELECT_IMAGE_FROM_GALLERY_CODE ||
                requestCode == AttachImageBottomSheetModal.REQUEST_CAMERA_IMAGE_CODE
    }

    override fun onChooseVideoListener(requestCode: Int, uri: Uri?) {
        if (isValidVideoCode(requestCode)) {

            // request video thumbnail
            val thumbnail = ThumbnailUtils.createVideoThumbnail(
                requestFilePath(this, uri),
                MediaStore.Video.Thumbnails.FULL_SCREEN_KIND
            )

            // set video image
            binding.noteVideo.setImageBitmap(thumbnail)
            binding.noteVideoContainer.visibility = View.VISIBLE

            // request video path
            selectedVideoPath = requestFilePath(this, uri)
        }
    }

    private fun isValidVideoCode(requestCode: Int): Boolean {
        return requestCode == AttachImageBottomSheetModal.REQUEST_SELECT_VIDEO_FROM_GALLERY_CODE
    }

    override fun onAddListener(requestCode: Int) {
        if (msgMandatory().isEmpty()) {
            openReminderPicker()
        } else {
            Toast.makeText(this, msgMandatory(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun openReminderPicker() {
        val addReminderDialog = Dialog(this)
        setReminderStyle(addReminderDialog)

        // time picker
        timePicker = addReminderDialog.findViewById(R.id.time_picker)

        // confirm allow
        val confirmAllow = addReminderDialog.findViewById<TextView>(R.id.confirm_allow)
        confirmAllow.setOnClickListener {
            setBroadcastAlarm()
            addReminderDialog.dismiss()
        }

        val confirmCancel = addReminderDialog.findViewById<TextView>(R.id.confirm_deny)
        confirmCancel.setOnClickListener { addReminderDialog.dismiss() }

        addReminderDialog.show()
    }

    private fun setBroadcastAlarm() {
        val intent = Intent(this, ReminderReceiver::class.java)
        intent.putExtra("notificationId", 1)
        intent.putExtra("title", binding.noteTitle.text.toString())
        intent.putExtra("subtitle", binding.noteSubtitle.text.toString())

        reminderIntent =
            PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT)

        val formattedDate = getCurrentDate()

        val hour = timePicker.currentHour
        val minute = timePicker.currentMinute

        // create time
        val startTime = Calendar.getInstance()
        startTime.set(Calendar.HOUR_OF_DAY, hour)
        startTime.set(Calendar.MINUTE, minute)
        startTime.set(Calendar.SECOND, 0)
        alarmStartTime = startTime.timeInMillis

        val amOrPm = if (startTime.get(Calendar.AM_PM) == Calendar.PM) "PM" else "AM"

        val formattedHour =
            if (startTime.get(Calendar.HOUR) == 0) "12" else startTime.get(Calendar.HOUR)

        reminderSet = "$formattedDate $amOrPm $formattedHour:$minute"

        binding.includeNoteActions.reminderSet.visibility = View.VISIBLE
        binding.includeNoteActions.reminderSetText.text = reminderSet

        Toast.makeText(this, getString(R.string.reminder_set_successfully), Toast.LENGTH_SHORT)
            .show()
    }

    private fun getCurrentDate(): String {
        val date = Calendar.getInstance().time
        val simpleDateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        return simpleDateFormat.format(date)
    }

    private fun setReminderStyle(dialog: Dialog) {
        dialog.apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(R.layout.popup_reminder)

            // enable dialog cancel
            setCancelable(true)
            setOnCancelListener { dismiss() }
            window?.apply {
                setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                setLayout(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT
                )
                attributes.windowAnimations = R.style.DetailAnimation
                attributes.gravity = Gravity.BOTTOM
            }
        }
    }

    override fun onRemoveListener(requestCode: Int) {

    }

    override fun onChooseListener(requestCode: Int, category: Category) {
        if (requestCode == CategoriesBottomSheetModal.REQUEST_CHOOSE_CATEGORY_CODE) {
            selectedNoteCategoryId = category.categoryId
            binding.noteCategory.text = category.title
            Toast.makeText(this, category.title + " " + "category is Selected", Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun onDeleteListener(requestCode: Int) {
        if (requestCode == REQUEST_DELETE_NOTE_CODE) {
            val intent = Intent()
            intent.putExtra("is_note_removed", true)
            setResult(RESULT_OK, intent)
            Toast.makeText(this, getString(R.string.note_moved_to_trash), Toast.LENGTH_SHORT).show()
            finish()
        } else if (requestCode == REQUEST_DISCARD_NOTE_CODE) {
            finish()
        }
    }

    override fun onSpeechInputListener(requestCode: Int, text: String) {
        if (requestCode == MoreActionsBottomSheetModal.REQUEST_SPEECH_INPUT_CODE) {
            binding.noteDescription.append(" $text")
        }
    }

    companion object {
        const val REQUEST_DELETE_NOTE_CODE = 3
        const val REQUEST_DISCARD_NOTE_CODE = 4
        const val REQUEST_VIEW_NOTE_IMAGE = 5
        const val REQUEST_VIEW_NOTE_VIDEO = 6
    }
}

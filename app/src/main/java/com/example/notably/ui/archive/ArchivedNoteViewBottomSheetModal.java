package com.example.notably.ui.archive;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ShareCompat;

import com.example.notably.R;
import com.example.notably.repos.entities.ArchiveNote;
import com.example.notably.ui.view.ViewAttachedImageActivity;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.makeramen.roundedimageview.RoundedImageView;

public class ArchivedNoteViewBottomSheetModal extends BottomSheetDialogFragment {

    ArchiveNote presetNote;

    private static final int REQUEST_VIEW_NOTE_VIDEO = 6;

    public ArchivedNoteViewBottomSheetModal() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.archived_note_view_bottom_sheet_modal, container, false);

        // get preset note
        presetNote = (ArchiveNote) getArguments().getSerializable("archive_note_data");

        /*
         * set note data like note title,
         * description and subtitle...
         */
        TextView noteTitle = view.findViewById(R.id.note_title);
        TextView noteSubtitle = view.findViewById(R.id.note_subtitle);
        TextView noteDescription = view.findViewById(R.id.note_description);
        RoundedImageView noteImage = view.findViewById(R.id.note_image);
        RoundedImageView noteVideo = view.findViewById(R.id.note_video);

        noteTitle.setText(presetNote.getTitle());
        noteSubtitle.setText(presetNote.getSubtitle());
        noteDescription.setText(presetNote.getDescription());

        // check if image attachment is set
        if (presetNote.getImagePath() != null && !presetNote.getImagePath().trim().isEmpty()) {
            noteImage.setImageBitmap(BitmapFactory.decodeFile(presetNote.getImagePath()));
            noteImage.setOnClickListener(v -> {
                Intent intent = new Intent(getContext(), ViewAttachedImageActivity.class);
                intent.putExtra("image_path", presetNote.getImagePath());
                intent.putExtra("image_type", "view");
                startActivity(intent);
            });
            // share note image
            view.findViewById(R.id.note_image_share).setOnClickListener(v -> {
                if (presetNote.getImageUri() != null && !TextUtils.isEmpty(presetNote.getImageUri())) {
                    Intent share = ShareCompat.IntentBuilder.from(getActivity())
                            .setStream(Uri.parse(presetNote.getImageUri()))
                            .setType("text/html")
                            .getIntent()
                            .setAction(Intent.ACTION_SEND)
                            .setDataAndType(Uri.parse(presetNote.getImageUri()), "image/*")
                            .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(Intent.createChooser(share, getString(R.string.share_image)));
                }
            });
            // show image, image container
            view.findViewById(R.id.note_image_container).setVisibility(View.VISIBLE);
            noteImage.setVisibility(View.VISIBLE);
        } else {
            view.findViewById(R.id.note_image_container).setVisibility(View.GONE);
        }

        // check if video attachment is set
//        if (presetNote.getVideoPath() != null && !presetNote.getVideoPath().trim().isEmpty()) {
//            Bitmap video_thumbnail = ThumbnailUtils.createVideoThumbnail(presetNote.getVideoPath(), MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);
//            noteVideo.setImageBitmap(video_thumbnail);
//            noteVideo.setOnClickListener(v -> {
//                Intent intent = new Intent(getContext(), ViewAttachedVideoActivity.class);
//                intent.putExtra("video_path", presetNote.getVideoPath());
//                intent.putExtra("video_type", "view");
//                startActivityForResult(intent, REQUEST_VIEW_NOTE_VIDEO);
//            });
//            noteVideo.setVisibility(View.VISIBLE);
//            view.findViewById(R.id.note_video_container).setVisibility(View.VISIBLE);
//        } else {
//            view.findViewById(R.id.note_video_container).setVisibility(View.GONE);
//        }

        // set note color
        GradientDrawable shape = (GradientDrawable) noteDescription.getBackground();
        shape.setColor(Color.parseColor(presetNote.getColor()));

        // note created at
        TextView noteCreatedAt = view.findViewById(R.id.note_created_at);
        noteCreatedAt.setText(presetNote.getCreatedAt());

        // note attached link
        TextView noteWebUrl = view.findViewById(R.id.note_web_url);
        // check if link attachment is set
        if (presetNote.getWebLink() != null && !presetNote.getWebLink().trim().isEmpty()) {
            noteWebUrl.setText(presetNote.getWebLink());
        } else {
            noteWebUrl.setVisibility(View.GONE);
        }

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

}

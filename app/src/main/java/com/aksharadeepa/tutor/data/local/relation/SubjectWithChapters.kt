package com.aksharadeepa.tutor.data.local.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.aksharadeepa.tutor.data.local.entity.ChapterEntity
import com.aksharadeepa.tutor.data.local.entity.SubjectEntity

data class SubjectWithChapters(
    @Embedded val subject: SubjectEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "subjectId"
    )
    val chapters: List<ChapterEntity>
)

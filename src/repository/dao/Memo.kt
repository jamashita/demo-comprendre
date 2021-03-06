package com.comprendre.repository.dao

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object Memos : IntIdTable("memos", "memo_id") {
    val subject = text("subject")
}

data class Memo(val memoId: EntityID<Int>) : IntEntity(memoId) {
    companion object : IntEntityClass<Memo>(Memos)
    val subject by Memos.subject
}

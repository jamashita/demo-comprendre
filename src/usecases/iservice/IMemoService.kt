package com.comprendre.usecases.iservice;

import com.comprendre.repository.dao.Memo;

interface IMemoService {
    fun findById(userId: Int): Memo?

    fun findAllSortedById(): List<Memo>

    fun create(subject: String): Memo

    fun update(id: Int, subject: String): Memo?

    fun deleteById(id: Int): Long
}

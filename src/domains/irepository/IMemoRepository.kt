package com.comprendre.domains.irepository

import com.comprendre.repository.dao.Memo

interface IMemoRepository {
    fun findById(id: Int): Memo?

    fun findAll(): List<Memo>

    fun findAllSortedById(): List<Memo>

    fun create(memo: Memo): Memo

    fun update(memo: Memo): Memo?

    fun deleteById(id: Int): Long
}

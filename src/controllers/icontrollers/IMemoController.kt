interface IMemoController {
    fun getMemo(memoId: MemoId): Memo

    fun getMemos(): List<Memo>

    fun postMemo(memo: MemoContent): Int

    fun putMemo(memo: Memo): Memo

    fun deleteMemo(memoId: MemoId): Unit
}

data class MemoId(val id: Int)

data class MemoContent(val subject: String)

data class Memo(val memo_id: Int, val subject: String)
